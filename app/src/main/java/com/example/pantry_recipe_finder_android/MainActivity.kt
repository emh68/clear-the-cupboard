package com.example.pantry_recipe_finder_android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pantry_recipe_finder_android.api.RecipeClient
import com.example.pantry_recipe_finder_android.model.Recipe
import com.example.pantry_recipe_finder_android.ui.theme.ClearTheCupboardTheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                // Prevent crashes if API adds unexpected fields
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }
    private val recipeService = RecipeClient(client)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClearTheCupboardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(recipeService)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(service: RecipeClient) {
    val navController = rememberNavController()
    // recipesState is defined for persistence when navigating from 'pantry' to 'recipes'
    var recipesState by remember { mutableStateOf<List<Recipe>>(emptyList()) }

    NavHost(
        navController = navController,
        startDestination = "pantry"
    ) {
        composable("pantry") {
            PantryScreen(
                service = service,
                onRecipesLoaded = { loadedRecipes ->
                    recipesState = loadedRecipes
                    navController.navigate("recipes") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("recipes") {
            RecipeListScreen(
                recipes = recipesState,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun PantryScreen(service: RecipeClient, onRecipesLoaded: (List<Recipe>) -> Unit) {
    var ingredientList by remember { mutableStateOf(listOf<String>()) }
    var currentInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "My Pantry",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            OutlinedTextField(
                value = currentInput, onValueChange = { currentInput = it },
                label = { Text("Add ingredient") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                if (currentInput.isNotBlank()) {
                    ingredientList = ingredientList + currentInput
                    currentInput = ""
                }
            })
            {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(ingredientList) { ingredient ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ingredient,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        IconButton(onClick = {
                            ingredientList = ingredientList.filter { it != ingredient }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove ingredient",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
        Button(
            onClick = {
                scope.launch {
                    try {
                        val ingredientsQuery = ingredientList
                            .filter { it.isNotBlank() }
                            .joinToString(",") { it.trim() }
                        val result = service.searchRecipes(ingredientsQuery)
                        Log.d("pantryApp", "Loaded recipes count: ${result.results.size}")
                        onRecipesLoaded(result.results)
                    } catch (e: Exception) {
                        Log.e("PantryApp", "Error fetching recipes", e)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Find Recipes")
        }
    }
}

@Composable
fun RecipeListScreen(
    recipes: List<Recipe>,
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Recipes",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(all = 12.dp)

        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(recipes) { recipe ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = recipe.title,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        if (recipe.usedIngredients.isNotEmpty()) {
                            val usedNames = recipe.usedIngredients.joinToString(", ") { it.name }
                            Text(
                                text = "Have: $usedNames",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        if (recipe.missedIngredients.isNotEmpty()) {
                            val missedNames =
                                recipe.missedIngredients.joinToString(", ") { it.name }
                            Text(
                                text = "Missing: $missedNames",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Pantry")
        }
    }
}