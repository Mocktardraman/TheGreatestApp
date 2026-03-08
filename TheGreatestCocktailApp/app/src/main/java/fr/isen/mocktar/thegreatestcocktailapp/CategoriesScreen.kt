package fr.isen.mocktar.thegreatestcocktailapp

import androidx.compose.foundation.clickable2
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ---------- ViewModel : catégories ----------
class CategoriesViewModel(
    private val repo: CocktailRepository = CocktailRepository()
) : ViewModel() {
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories

    fun load() {
        viewModelScope.launch {
            runCatching { repo.categories() }
                .onSuccess { _categories.value = it }
                .onFailure { _categories.value = emptyList() }
        }
    }
}

// ---------- ViewModel : recherche par nom (Partie 6) ----------
class SearchViewModel(
    private val repo: CocktailRepository = CocktailRepository()
) : ViewModel() {
    private val _results = MutableStateFlow<List<Drink>>(emptyList())
    val results: StateFlow<List<Drink>> = _results

    fun searchByName(q: String) {
        if (q.isBlank()) { _results.value = emptyList(); return }
        viewModelScope.launch {
            runCatching { repo.searchByName(q.trim()) }
                .onSuccess { _results.value = it }
                .onFailure { _results.value = emptyList() }
        }
    }
}

// ---------- Écran racine de l’onglet "List" ----------
@Composable
fun ListRootScreen(navController: NavController) {
    val catVm: CategoriesViewModel = viewModel()
    val searchVm: SearchViewModel = viewModel()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // Barre de recherche
        var query by remember { mutableStateOf("") }
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search by name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = { searchVm.searchByName(query) }) {
            Text("Search")
        }

        val results by searchVm.results.collectAsState()
        if (results.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text("Results", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(results) { d ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("detail/${d.idDrink}") }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(d.strDrink ?: "-")
                            if (!d.strCategory.isNullOrBlank()) Text("Category: ${d.strCategory}")
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // Catégories
        Text("Categories", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        CategoriesScreenApi(navController, catVm)
    }
}

// ---------- Écran Catégories ----------
@Composable
fun CategoriesScreenApi(navController: NavController, viewModel: CategoriesViewModel) {
    val categories by viewModel.categories.collectAsState()
    LaunchedEffect(Unit) { viewModel.load() }

    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { cat ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("drinks/$cat") }
            ) {
                Text(cat, Modifier.padding(16.dp))
            }
        }
    }
}