package fr.isen.mocktar.thegreatestcocktailapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
                    onClick = { navController.navigate("random") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
                    label = { Text("List") },
                    selected = currentRoute == "categories",
                    onClick = { navController.navigate("categories") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, null) },
                    label = { Text("Favoris") },
                    selected = currentRoute == "favorites",
                    onClick = { navController.navigate("favorites") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(navController, "random", Modifier.padding(innerPadding)) {
            composable("random") { FeaturedCocktailScreen() }
            composable("categories") { CategoriesScreen() }
            composable("favorites") { FavoritesScreen() }
        }
    }
}

@Composable
fun FeaturedCocktailScreen() {

    val repository = remember { CocktailRepository() }
    var cocktail by remember { mutableStateOf<Drink?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // 2. Lancer l'appel réseau de manière asynchrone dès que l'écran s'affiche
    LaunchedEffect(Unit) {
        isLoading = true
        cocktail = repository.randomDrink()
        isLoading = false
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp), 
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        // Image par défaut (En attendant de mettre Coil pour charger l'URL cocktail?.strDrinkThumb)
        Image(
            painter = painterResource(id = R.drawable.cocktail),
            contentDescription = "Featured",
            modifier = Modifier.size(280.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(32.dp))

        // 3. Gestion de l'affichage de l'état (Chargement vs Données reçues)
        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF3F51B5))
        } else if (cocktail != null) {
            // Affiche le vrai nom du cocktail de l'API !
            Text(
                text = cocktail?.strDrink ?: "Nom inconnu", 
                color = Color.White, 
                fontSize = 32.sp, 
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
               
                cocktail?.strCategory?.let { BadgeItem(it, Color(0xFF3F51B5)) }
                cocktail?.strGlass?.let { BadgeItem(it, Color(0xFF4CAF50)) }
            }
        } else {
         
            Text("Impossible de charger le cocktail", color = Color.Red, fontSize = 16.sp)
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

@Composable fun CategoriesScreen() { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Écran Catégories", color = Color.White) } }
@Composable fun FavoritesScreen() { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Écran Favoris", color = Color.White) } }
