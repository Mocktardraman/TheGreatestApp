package fr.isen.mocktar.thegreatestcocktailapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import fr.isen.mocktar.thegreatestcocktailapp.ui.theme.TheGreatestCocktailAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "onCreate")
        enableEdgeToEdge()
        setContent { TheGreatestCocktailAppTheme { MainContent() } }
    }
}

@Composable
fun MainContent() {
    val navController = rememberNavController()
    Scaffold(
        containerColor = Color(0xFF1A162D),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF2D2942),
                modifier = Modifier.padding(16.dp).clip(RoundedCornerShape(32.dp))
            ) {
                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry.value?.destination?.route

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Refresh, null) },
                    label = { Text("À la une") },
                    selected = currentRoute == "random",
                    onClick = { navController.navigate("random") { popUpTo("random") { inclusive = true } } }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
                    label = { Text("List") },
                    selected = currentRoute == "list",
                    onClick = { navController.navigate("list") { popUpTo("list") { inclusive = true } } }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, null) },
                    label = { Text("Favoris") },
                    selected = currentRoute == "favorites",
                    onClick = { navController.navigate("favorites") { popUpTo("favorites") { inclusive = true } } }
                )
            }
        }
    ) { innerPadding ->
        NavHost(navController, "random", Modifier.padding(innerPadding)) {
            composable("random") { FeaturedCocktailScreen() }
            composable("list") { ListRootScreen(navController) }
            composable("drinks/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                DrinksListScreen(category, navController)
            }
            composable("detail/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                DetailCocktailScreenWrapper(id, navController)
            }
            composable("favorites") { FavoritesScreen(navController) }
        }
    }
}

@Composable
fun FeaturedCocktailScreen() {
    val repository = remember { CocktailRepository() }
    var cocktail by remember { mutableStateOf<Drink?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        cocktail = repository.randomDrink()
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF220F3D), Color(0xFF31175E), Color(0xFF1A162D))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("The Greatest", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(onClick = { /* refresh action */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                    }
                    IconButton(onClick = { /* favorite action */ }) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorite", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF9C27B0))
                }
            } else if (cocktail != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF321F50).copy(alpha = 0.95f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Box(
                            modifier = Modifier
                                .size(260.dp)
                                .align(Alignment.CenterHorizontally)
                                .clip(CircleShape)
                                .background(Color(0xFF39245B))
                        ) {
                            AsyncImage(
                                model = cocktail?.strDrinkThumb,
                                contentDescription = cocktail?.strDrink,
                                placeholder = painterResource(id = R.drawable.cocktail),
                                error = painterResource(id = R.drawable.cocktail),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = cocktail?.strDrink ?: "Nom inconnu",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            cocktail?.strCategory?.let { BadgeItem(it, Color(0xFF6A4AB0)) }
                            BadgeItem("Unknown", Color(0xFF7C4DFF))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        cocktail?.strGlass?.let {
                            Text("$it", color = Color(0xFFB39DDB), fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF412A65))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Ingrédients", color = Color.White, fontWeight = FontWeight.SemiBold)
                                    Text("${cocktail?.ingredients()?.size ?: 0}", color = Color(0xFFB39DDB))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                cocktail?.ingredients()?.take(4)?.forEach { (name, measure) ->
                                    Text(
                                        "• $name ${measure.orEmpty()}",
                                        color = Color(0xFFDFD4FF),
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text("Impossible de charger le cocktail", color = Color.Red, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun BadgeItem(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
fun DrinksListScreen(category: String, navController: androidx.navigation.NavController) {
    val repository = remember { CocktailRepository() }
    var drinks by remember { mutableStateOf<List<DrinkShort>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(category) {
        isLoading = true
        drinks = repository.drinksByCategory(category)
        isLoading = false
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(category, style = MaterialTheme.typography.headlineSmall, color = Color.White)
        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(drinks) { d ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("detail/${d.idDrink}") }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(d.strDrink, color = Color.White, fontWeight = FontWeight.Bold)
                            if (!d.strDrinkThumb.isNullOrBlank()) {
                                AsyncImage(
                                    model = d.strDrinkThumb,
                                    contentDescription = d.strDrink,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailCocktailScreenWrapper(id: String, navController: androidx.navigation.NavController) {
    val detailViewModel: DetailViewModel = viewModel()
    DetailCocktailScreenApi(
        viewModel = detailViewModel,
        mode = DetailMode.ById,
        argDrinkId = id,
        onBack = { navController.navigateUp() }
    )
}
