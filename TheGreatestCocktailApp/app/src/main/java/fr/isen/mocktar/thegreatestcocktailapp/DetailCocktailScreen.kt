package fr.isen.mocktar.thegreatestcocktailapp

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.gson.Gson

// --- ViewModel (détail) ---
sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val drink: Drink) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
enum class DetailMode { Random, ById }

class DetailViewModel(
    private val repo: CocktailRepository = CocktailRepository()
) : ViewModel() {
    private val _state = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val state: StateFlow<DetailUiState> = _state

    fun loadRandom() {
        _state.value = DetailUiState.Loading
        viewModelScope.launch {
            runCatching { repo.randomDrink() }
                .onSuccess { d -> if (d != null) _state.value = DetailUiState.Success(d) else _state.value = DetailUiState.Error("No data") }
                .onFailure { _state.value = DetailUiState.Error(it.message ?: "Error") }
        }
    }
    fun loadById(id: String) {
        _state.value = DetailUiState.Loading
        viewModelScope.launch {
            runCatching { repo.drinkById(id) }
                .onSuccess { d -> if (d != null) _state.value = DetailUiState.Success(d) else _state.value = DetailUiState.Error("No data") }
                .onFailure { _state.value = DetailUiState.Error(it.message ?: "Error") }
        }
    }
}

// --- Favorites (SharedPreferences) ---
class FavoritesManager(context: Context) {
    private val prefs = context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private fun key(id: String) = "drink_$id"

    fun isFavorite(id: String) = prefs.contains(key(id))
    fun add(drink: Drink) { prefs.edit().putString(key(drink.idDrink), gson.toJson(drink)).apply() }
    fun remove(id: String) { prefs.edit().remove(key(id)).apply() }
    fun toggle(drink: Drink): Boolean =
        if (isFavorite(drink.idDrink)) { remove(drink.idDrink); false } else { add(drink); true }
    fun all(): List<Drink> =
        prefs.all.values.mapNotNull { it as? String }.mapNotNull { runCatching { gson.fromJson(it, Drink::class.java) }.getOrNull() }
}

// --- Écran Détail ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailCocktailScreenApi(
    viewModel: DetailViewModel,
    mode: DetailMode,
    argDrinkId: String? = null,
    onBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val ctx = LocalContext.current
    val favs = remember { FavoritesManager(ctx) }

    LaunchedEffect(mode, argDrinkId) {
        when (mode) {
            DetailMode.Random -> viewModel.loadRandom()
            DetailMode.ById -> argDrinkId?.let { viewModel.loadById(it) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cocktail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state is DetailUiState.Success) {
                        val d = (state as DetailUiState.Success).drink
                        val isFav = remember(d.idDrink) { mutableStateOf(favs.isFavorite(d.idDrink)) }
                        IconButton(onClick = {
                            val nowFav = favs.toggle(d)
                            isFav.value = nowFav
                            Toast.makeText(ctx, if (nowFav) "Added to favorites" else "Removed from favorites", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                imageVector = if (isFav.value) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        when (val s = state) {
            is DetailUiState.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth().padding(padding))
            is DetailUiState.Error -> Text("Error: ${s.message}", Modifier.padding(padding).padding(16.dp))
            is DetailUiState.Success -> {
                val d = s.drink
                Column(
                    Modifier.padding(padding).fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AsyncImage(model = d.strDrinkThumb, contentDescription = d.strDrink, modifier = Modifier.fillMaxWidth().height(220.dp))
                    Text(d.strDrink ?: "No title", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Category: ${d.strCategory ?: "-"}")
                    Text("Glass: ${d.strGlass ?: "-"}")
                    ElevatedCard(shape = RoundedCornerShape(12.dp)) { Column(Modifier.padding(16.dp)) {
                        Text("Ingredients", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        d.ingredients().forEach { (n, m) -> Text("- $n" + if (!m.isNullOrBlank()) " ($m)" else "") }
                    } }
                    ElevatedCard(shape = RoundedCornerShape(12.dp)) { Column(Modifier.padding(16.dp)) {
                        Text("Recipe", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        Text(d.strInstructions ?: "-")
                    } }
                }
            }
        }
    }
}