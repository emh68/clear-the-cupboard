package com.example.pantry_recipe_finder_android.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeResponse(val results: List<Recipe>, val totalResults: Int)

@Serializable
data class Ingredient(
    val id: Int = 0,
    val name: String = "",
    val original: String = "",
    val image: String? = null
)

@Serializable
data class Recipe(
    val id: Int = 0,
    val title: String = "",
    val image: String = "",
    val usedIngredientCount: Int = 0,
    val missedIngredientCount: Int = 0,
    val usedIngredients: List<Ingredient> = emptyList(),
    val missedIngredients: List<Ingredient> = emptyList(),
    val summary: String? = null,
    val instructions: String? = null
)