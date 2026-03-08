package fr.isen.mocktar.thegreatestcocktailapp

import com.google.gson.annotations.SerializedName

data class DrinksResponse(@SerializedName("drinks") val drinks: List<Drink>?)
data class CategoriesResponse(@SerializedName("drinks") val drinks: List<CategoryItem>?)
data class CategoryItem(@SerializedName("strCategory") val strCategory: String)

data class DrinksShortResponse(@SerializedName("drinks") val drinks: List<DrinkShort>?)
data class DrinkShort(
    @SerializedName("idDrink") val idDrink: String,
    @SerializedName("strDrink") val strDrink: String,
    @SerializedName("strDrinkThumb") val strDrinkThumb: String?
)

data class Drink(
    @SerializedName("idDrink") val idDrink: String,
    @SerializedName("strDrink") val strDrink: String?,
    @SerializedName("strCategory") val strCategory: String?,
    @SerializedName("strGlass") val strGlass: String?,
    @SerializedName("strDrinkThumb") val strDrinkThumb: String?,
    @SerializedName("strInstructions") val strInstructions: String?,
    // Ingrédients/mesures 1..15
    @SerializedName("strIngredient1") val strIngredient1: String?, @SerializedName("strMeasure1") val strMeasure1: String?,
    @SerializedName("strIngredient2") val strIngredient2: String?, @SerializedName("strMeasure2") val strMeasure2: String?,
    @SerializedName("strIngredient3") val strIngredient3: String?, @SerializedName("strMeasure3") val strMeasure3: String?,
    @SerializedName("strIngredient4") val strIngredient4: String?, @SerializedName("strMeasure4") val strMeasure4: String?,
    @SerializedName("strIngredient5") val strIngredient5: String?, @SerializedName("strMeasure5") val strMeasure5: String?,
    @SerializedName("strIngredient6") val strIngredient6: String?, @SerializedName("strMeasure6") val strMeasure6: String?,
    @SerializedName("strIngredient7") val strIngredient7: String?, @SerializedName("strMeasure7") val strMeasure7: String?,
    @SerializedName("strIngredient8") val strIngredient8: String?, @SerializedName("strMeasure8") val strMeasure8: String?,
    @SerializedName("strIngredient9") val strIngredient9: String?, @SerializedName("strMeasure9") val strMeasure9: String?,
    @SerializedName("strIngredient10") val strIngredient10: String?, @SerializedName("strMeasure10") val strMeasure10: String?,
    @SerializedName("strIngredient11") val strIngredient11: String?, @SerializedName("strMeasure11") val strMeasure11: String?,
    @SerializedName("strIngredient12") val strIngredient12: String?, @SerializedName("strMeasure12") val strMeasure12: String?,
    @SerializedName("strIngredient13") val strIngredient13: String?, @SerializedName("strMeasure13") val strMeasure13: String?,
    @SerializedName("strIngredient14") val strIngredient14: String?, @SerializedName("strMeasure14") val strMeasure14: String?,
    @SerializedName("strIngredient15") val strIngredient15: String?, @SerializedName("strMeasure15") val strMeasure15: String?
) {
    fun ingredients(): List<Pair<String, String?>> {
        val list = listOf(
            strIngredient1 to strMeasure1, strIngredient2 to strMeasure2, strIngredient3 to strMeasure3,
            strIngredient4 to strMeasure4, strIngredient5 to strMeasure5, strIngredient6 to strMeasure6,
            strIngredient7 to strMeasure7, strIngredient8 to strMeasure8, strIngredient9 to strMeasure9,
            strIngredient10 to strMeasure10, strIngredient11 to strMeasure11, strIngredient12 to strMeasure12,
            strIngredient13 to strMeasure13, strIngredient14 to strMeasure14, strIngredient15 to strMeasure15
        )
        return list.filter { it.first?.isNotBlank() == true }
            .map { Pair(it.first!!.trim(), it.second?.trim()) }
    }
}