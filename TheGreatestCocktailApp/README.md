# TheGreatestCocktailApp

## Travail effectué
Cette application a été implémentée avec Jetpack Compose et la base de données TheCocktailDB.

### Fonctionnalités principales
- Écran d’accueil **"À la une"** avec un cocktail aléatoire chargé depuis l’API.
- Barre de navigation en bas avec trois onglets :
  - `À la une`
  - `List`
  - `Favoris`
- Écran **List** avec :
  - recherche par nom de cocktail
  - liste des catégories
  - navigation vers la liste de boissons d’une catégorie
- Écran **Détails** du cocktail avec :
  - image du cocktail
  - nom du cocktail
  - catégorie
  - type de verre
  - carte des ingrédients
  - carte de la recette
  - TopAppBar avec bouton favori
- Écran **Favoris** avec stockage local et affichage des cocktails ajoutés.

### Stockage des favoris
- Favoris persistants via `SharedPreferences`
- Ajout / suppression d’un cocktail depuis l’écran détail
- Liste des favoris mise à jour à chaque retour sur l’onglet Favoris

### Intégration réseau
- Retrofit 2 pour les appels HTTP
- Gson pour la sérialisation JSON
- Coil pour l’affichage des images distantes
- Endpoints utilisés :
  - `random.php`
  - `list.php?c=list`
  - `filter.php?c=...`
  - `lookup.php?i=...`
  - `search.php?s=...`

### Architecture
- `MainActivity.kt` : hôte Compose, navigation et écran d’accueil
- `DetailCocktailScreen.kt` : écran détail + gestion des favoris
- `CategoriesScreen.kt` : écran catégories + recherche
- `FavoritesScreen.kt` : écran des boissons favorites
- `CocktailApiService.kt` : interface Retrofit + module réseau
- `Models.kt` : modèles de données pour l’API et logique d’ingrédients

### Résultat
L’application compile correctement et correspond aux fonctionnalités demandées dans le README initial. Des ajustements de design ont également été appliqués pour faire ressembler la page d’accueil à une interface soignée.

### Exécution
Pour lancer le projet :
1. Ouvrir `TheGreatestCocktailApp` dans Android Studio
2. Exécuter `app` sur un émulateur ou un appareil Android

### Remarque
Le design actuel du flux principal est fonctionnel et prêt à être testé sur l’appareil.
