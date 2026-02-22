package com.example.pantry_recipe_finder_android.api

import com.example.pantry_recipe_finder_android.BuildConfig
import com.example.pantry_recipe_finder_android.model.RecipeResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class RecipeClient(private val client: HttpClient) {
    // Calls Spoonacular complexSearch endpoint
    // Expects ingredients as comma-separated string (e.g. "milk,eggs,flour")
    // Uses 'min-missing-ingredients' sorting to prioritize recipes with the least missing ingredients first
    suspend fun searchRecipes(ingredients: String): RecipeResponse {
        return client.get("https://api.spoonacular.com/recipes/complexSearch") {
            parameter("includeIngredients", ingredients)
            parameter("apiKey", BuildConfig.SPOONACULAR_KEY)
            // Includes full ingredient lists and instructions in initial payload
            // to minimize API calls when clicking for recipe details
            parameter("fillIngredients", true)
            parameter("addRecipeInformation", true)
            parameter("sort", "min-missing-ingredients")
            // Limits results to 10 recipes to prevent maximum use of quota 50 pts/day
            // each call is 1pt + .01 or .06 or whatever depending on the number of parameters used
            parameter("number", 10)
        }.body<RecipeResponse>()
    }
}