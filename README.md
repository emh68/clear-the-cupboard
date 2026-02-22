# Overview

This app is designed to help users explore recipes based on the ingredients they already have in
their pantry. My goal in building it was to practice building a complete Android application with
Kotlin and Jetpack Compose, integrating a real-world API, and handling dynamic data in a structured
and user-friendly way.

Users can add ingredients to their pantry, remove them as needed, and then search for recipes that
match what they have. The app displays a list of recipes, highlighting which ingredients the user
has and which are missing. Clicking on a recipe shows cooking instructions when available.

[Clear The Cupboard Demo Video](https://youtu.be/QPPZfDOpIzs)

# Development Environment

- **IDE:** Android Studio (Panda 1 | 2025.3.1)
- **Language:** Kotlin
- **Libraries/Tools:**
    - Jetpack Compose for UI
    - Ktor Client for API requests
    - kotlinx.serialization for JSON parsing
    - Android Material3 components for theming and layout
    - Kotlin coroutines for asynchronous operations
- **API Key:** &nbsp;[Spoonacular API Key](https://spoonacular.com/food-api)

---

## Setup

1. **Clone the Repository**

```bash
git clone https://github.com/emh68/clear-the-cupboard-android
cd clear-the-cupboard-android
```

2. **Add the API Key**
    - The app reads the Spoonacular API key from a local gradle.properties file for security:
    - Create a file in your project root: gradle.properties (if it doesn’t exist).

Add the following line:

```bash
SPOONACULAR_API_KEY=your_actual_api_key_here
```

- In RecipeClient.kt (or wherever you initialize the client), the app reads this key using:

```bash
val apiKey = BuildConfig.SPOONACULAR_API_KEY
```

3. **Sync Project with Gradle**

    - Open Android Studio, click File → Sync Project with Gradle Files to ensure dependencies are
      resolved.

---

## Execution

1. Open the project in Android Studio.
2. Connect an Android device or start an emulator.
3. Click the **Run** button (green triangle) to launch the app.
4. Use the app by adding ingredients to your pantry and exploring recipes.

---

## Usage

1. Open the app to see the **Pantry screen**.
2. Add ingredients using the input field and **+** button.
3. Tap **Find Recipes** to see recipes that match your pantry items.
4. Tap a recipe to view its full instructions.
5. Use the **Back** button to navigate between screens.

---

# Useful Websites

* [Spoonacular API Documentation](https://spoonacular.com/food-api)
* [Jetpack Compose Guide](https://developer.android.com/jetpack/compose)
* [Ktor Client Guide](https://ktor.io/docs/getting-started-ktor-client.html)
* [Kotlin Serialization Guide](https://kotlinlang.org/docs/serialization.html)
* [Android Material3 Guide](https://m3.material.io/components/material3/overview)
* [Android Jetpack Compose Navigation Guide](https://developer.android.com/guide/navigation)
* [Android Jetpack Compose State Guide](https://developer.android.com/jetpack/compose/state)
* [Android Basics - Philipp Lackner](https://developer.android.com/jetpack/compose/lifecycle)
* [Android Codelabs](https://developer.android.com/get-started/codelabs)
* [Android & Kotlin Development Masterclass](https://www.youtube.com/watch?v=blKkRoZPxLc)

---

# Future Work

* Improve error handling for API failures and no internet connection.
* Parse and format HTML instructions from recipes for better readability.
* Add a local database to save pantry items and past searches.
* Improve UI styling and responsiveness for different screen sizes.
* Allow filtering or sorting recipes by preparation time, dietary restrictions, or popularity.
* Implement local caching for offline usage.

# AI Disclosure

I used AI to help with the initial project setup, specifically for Gradle settings and dependency
management. I also used it as a debugging tool along with Logcat when I hit walls with the
Spoonacular API documentation, which I found to be lacking in some areas. For example, I used AI to
help me figure out why I was getting data back for "apples" but nothing for "chicken," even though
my logs showed the API call was working. This helped me realize the new endpoint was just much more
strict about search terms. While AI was a big help for troubleshooting documentation gaps and
formatting, I wrote all the application logic and source code myself.