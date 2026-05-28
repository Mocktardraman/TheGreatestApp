package fr.isen.mocktar.thegreatestcocktailapp

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

import fr.isen.mocktar.thegreatestcocktailapp.Drink

// ----------------------------
//  FavoritesManager (local)
// ----------------------------



// ----------------------------
//  Écran Favoris
// ----------------------------
@Composable
fun FavoritesScreen(navController: NavController) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val favs = remember { FavoritesManager(ctx) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // ✅ On typpe explicitement la liste pour éviter "Cannot infer type parameter T"
    var list by remember { mutableStateOf<List<Drink>>(emptyList()) }

    // Charger la liste au démarrage de l'écran et à chaque retour sur l'onglet Favoris
    LaunchedEffect(currentRoute) {
        if (currentRoute == "favorites") {
            list = favs.all()
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Favorites")
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(list) { d: Drink ->
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
    }
}