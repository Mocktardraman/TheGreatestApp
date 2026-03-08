package fr.isen.mocktar.thegreatestcocktailapp

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// --- ApiService (endpoints TheCocktailDB) ---
interface ApiService {
    @GET("random.php")
    suspend fun getRandomDrink(): DrinksResponse

    @GET("list.php?c=list")
    suspend fun listCategories(): CategoriesResponse

    @GET("filter.php")
    suspend fun listDrinksByCategory(@Query("c") category: String): DrinksShortResponse

    @GET("lookup.php")
    suspend fun lookupDrink(@Query("i") drinkId: String): DrinksResponse

    // Partie 6 : recherche par nom / ingrédient
    @GET("search.php")
    suspend fun searchByName(@Query("s") name: String): DrinksResponse

    @GET("filter.php")
    suspend fun filterByIngredient(@Query("i") ingredient: String): DrinksShortResponse
}

// --- Retrofit singleton ---
private const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"

object NetworkModule {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// --- Repository (accès unifié aux données) ---
class CocktailRepository {
    private val api = NetworkModule.api

    suspend fun randomDrink(): Drink? = api.getRandomDrink().drinks?.firstOrNull()
    suspend fun categories(): List<String> =
        api.listCategories().drinks?.map { it.strCategory } ?: emptyList()
    suspend fun drinksByCategory(category: String) =
        api.listDrinksByCategory(category).drinks ?: emptyList()
    suspend fun drinkById(id: String): Drink? =
        api.lookupDrink(id).drinks?.firstOrNull()

    // Partie 6
    suspend fun searchByName(name: String) = api.searchByName(name).drinks ?: emptyList()
    suspend fun filterByIngredient(ingredient: String) =
        api.filterByIngredient(ingredient).drinks ?: emptyList()
}