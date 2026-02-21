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
                    PantryApp(recipeService)
                }
            }
        }
    }
}

@Composable
fun PantryApp(service: RecipeClient) {
    var ingredientList by remember { mutableStateOf(listOf<String>()) }
    var currentInput by remember { mutableStateOf("") }
    var recipes by remember { mutableStateOf(listOf<Recipe>()) }
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
                        val query = ingredientList.joinToString(",") { it.trim() }
                        val result = service.searchRecipes(query)
                        recipes = result.results
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
        // Temporary Testing Recipe List
        LazyColumn {
            items(recipes) { recipe ->
                Card(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = recipe.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Have: ${recipe.usedIngredientCount} | Missing: ${recipe.missedIngredientCount}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
