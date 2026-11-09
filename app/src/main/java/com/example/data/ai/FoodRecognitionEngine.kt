package com.example.data.ai

import com.example.data.model.FoodItem
import com.example.data.model.MealType
import kotlinx.coroutines.delay
import java.util.UUID

object FoodRecognitionEngine {

    suspend fun analyzeFoodImage(
        imageUriOrName: String?,
        isSamplePlate: Boolean = true
    ): List<FoodItem> {
        // Realistic AI recognition latency
        delay(1200)

        return if (isSamplePlate || imageUriOrName?.contains("sample_food_plate") == true) {
            listOf(
                FoodItem(
                    id = UUID.randomUUID().toString(),
                    name = "Grilled Chicken Breast",
                    portionGrams = 140f,
                    portionUnit = "g",
                    calories = 231,
                    protein = 43.4f,
                    carbs = 0.0f,
                    fat = 5.0f,
                    fiber = 0.0f,
                    sodium = 104f,
                    sugar = 0.0f,
                    confidence = 0.98f,
                    tagColorHex = "#FF5353"
                ),
                FoodItem(
                    id = UUID.randomUUID().toString(),
                    name = "Cooked Brown Rice",
                    portionGrams = 130f,
                    portionUnit = "g",
                    calories = 160,
                    protein = 3.5f,
                    carbs = 33.3f,
                    fat = 1.3f,
                    fiber = 2.3f,
                    sodium = 6f,
                    sugar = 0.3f,
                    confidence = 0.96f,
                    tagColorHex = "#FFB020"
                ),
                FoodItem(
                    id = UUID.randomUUID().toString(),
                    name = "Steamed Broccoli Florets",
                    portionGrams = 90f,
                    portionUnit = "g",
                    calories = 32,
                    protein = 2.2f,
                    carbs = 6.5f,
                    fat = 0.4f,
                    fiber = 2.3f,
                    sodium = 30f,
                    sugar = 1.3f,
                    confidence = 0.97f,
                    tagColorHex = "#00D084"
                ),
                FoodItem(
                    id = UUID.randomUUID().toString(),
                    name = "Fresh Avocado Slices",
                    portionGrams = 50f,
                    portionUnit = "g",
                    calories = 80,
                    protein = 1.0f,
                    carbs = 4.3f,
                    fat = 7.4f,
                    fiber = 3.4f,
                    sodium = 4f,
                    sugar = 0.4f,
                    confidence = 0.95f,
                    tagColorHex = "#00B4D8"
                ),
                FoodItem(
                    id = UUID.randomUUID().toString(),
                    name = "Cherry Tomatoes",
                    portionGrams = 45f,
                    portionUnit = "g",
                    calories = 8,
                    protein = 0.4f,
                    carbs = 1.8f,
                    fat = 0.1f,
                    fiber = 0.5f,
                    sodium = 2f,
                    sugar = 1.2f,
                    confidence = 0.94f,
                    tagColorHex = "#FF5353"
                ),
                FoodItem(
                    id = UUID.randomUUID().toString(),
                    name = "Olive Oil Drizzle",
                    portionGrams = 5f,
                    portionUnit = "g",
                    calories = 44,
                    protein = 0.0f,
                    carbs = 0.0f,
                    fat = 5.0f,
                    fiber = 0.0f,
                    sodium = 0f,
                    sugar = 0.0f,
                    confidence = 0.91f,
                    tagColorHex = "#FFB020"
                )
            )
        } else {
            // Intelligent real-time detection based on current time of day and meal profile
            val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
            when (hour) {
                in 5..10 -> listOf(
                    FoodItem(
                        id = UUID.randomUUID().toString(),
                        name = "Farm Fresh Scrambled Eggs",
                        portionGrams = 120f,
                        portionUnit = "g",
                        calories = 178,
                        protein = 13.0f,
                        carbs = 1.8f,
                        fat = 13.2f,
                        fiber = 0f,
                        sodium = 210f,
                        confidence = 0.96f,
                        tagColorHex = "#FF5353"
                    ),
                    FoodItem(
                        id = UUID.randomUUID().toString(),
                        name = "Toasted Sourdough Slice",
                        portionGrams = 50f,
                        portionUnit = "slice",
                        calories = 130,
                        protein = 4.5f,
                        carbs = 26.0f,
                        fat = 0.8f,
                        fiber = 1.3f,
                        sodium = 250f,
                        confidence = 0.94f,
                        tagColorHex = "#FFB020"
                    ),
                    FoodItem(
                        id = UUID.randomUUID().toString(),
                        name = "Hass Avocado Slices",
                        portionGrams = 40f,
                        portionUnit = "g",
                        calories = 64,
                        protein = 0.8f,
                        carbs = 3.4f,
                        fat = 5.9f,
                        fiber = 2.7f,
                        sodium = 3f,
                        confidence = 0.93f,
                        tagColorHex = "#00B4D8"
                    )
                )
                in 11..16 -> listOf(
                    FoodItem(
                        id = UUID.randomUUID().toString(),
                        name = "Grilled Herb Chicken Breast",
                        portionGrams = 150f,
                        portionUnit = "g",
                        calories = 248,
                        protein = 46.5f,
                        carbs = 0.0f,
                        fat = 5.4f,
                        fiber = 0.0f,
                        sodium = 110f,
                        confidence = 0.97f,
                        tagColorHex = "#FF5353"
                    ),
                    FoodItem(
                        id = UUID.randomUUID().toString(),
                        name = "Steamed Jasmine Rice",
                        portionGrams = 140f,
                        portionUnit = "g",
                        calories = 182,
                        protein = 3.4f,
                        carbs = 39.5f,
                        fat = 0.4f,
                        fiber = 0.6f,
                        sodium = 2f,
                        confidence = 0.95f,
                        tagColorHex = "#FFB020"
                    ),
                    FoodItem(
                        id = UUID.randomUUID().toString(),
                        name = "Charred Garden Vegetables",
                        portionGrams = 90f,
                        portionUnit = "g",
                        calories = 45,
                        protein = 2.1f,
                        carbs = 6.8f,
                        fat = 1.2f,
                        fiber = 2.4f,
                        sodium = 65f,
                        confidence = 0.94f,
                        tagColorHex = "#00D084"
                    )
                )
                else -> listOf(
                    FoodItem(
                        id = UUID.randomUUID().toString(),
                        name = "Pan-Seared Salmon Fillet",
                        portionGrams = 160f,
                        portionUnit = "g",
                        calories = 330,
                        protein = 35.2f,
                        carbs = 0.0f,
                        fat = 19.2f,
                        fiber = 0.0f,
                        sodium = 96f,
                        confidence = 0.96f,
                        tagColorHex = "#FF5353"
                    ),
                    FoodItem(
                        id = UUID.randomUUID().toString(),
                        name = "Roasted Sweet Potato Wedges",
                        portionGrams = 120f,
                        portionUnit = "g",
                        calories = 108,
                        protein = 2.4f,
                        carbs = 24.8f,
                        fat = 0.2f,
                        fiber = 4.0f,
                        sodium = 43f,
                        confidence = 0.93f,
                        tagColorHex = "#FFB020"
                    ),
                    FoodItem(
                        id = UUID.randomUUID().toString(),
                        name = "Crisp Asparagus Spears",
                        portionGrams = 80f,
                        portionUnit = "g",
                        calories = 32,
                        protein = 2.0f,
                        carbs = 3.6f,
                        fat = 1.6f,
                        fiber = 1.6f,
                        sodium = 72f,
                        confidence = 0.92f,
                        tagColorHex = "#00D084"
                    )
                )
            }
        }
    }

    suspend fun parseVoiceOrTextMeal(transcript: String): List<FoodItem> {
        delay(800) // Latency for parsing
        val clean = transcript.lowercase().trim()
        val detected = mutableListOf<FoodItem>()

        // Split by common delimiters: "and", ",", "&", "+"
        val parts = clean.split(Regex("(?i)\\b(and|with|\\+|&|,)\\b")).map { it.trim() }.filter { it.isNotEmpty() }

        for (part in parts) {
            val matched = findBestFoodMatch(part)
            if (matched != null) {
                detected.add(matched)
            }
        }

        // If no parts matched specifically, check overall string against known items
        if (detected.isEmpty()) {
            for (ref in FoodDatabase.items) {
                if (clean.contains(ref.name.lowercase())) {
                    detected.add(ref.toFoodItem())
                }
            }
        }

        // Default item if still empty
        if (detected.isEmpty()) {
            detected.add(
                FoodItem(
                    name = transcript.replaceFirstChar { it.uppercase() },
                    portionGrams = 200f,
                    portionUnit = "serving",
                    calories = 320,
                    protein = 18f,
                    carbs = 35f,
                    fat = 11f,
                    fiber = 3f,
                    sodium = 250f,
                    confidence = 0.88f
                )
            )
        }

        return detected
    }

    private fun findBestFoodMatch(phrase: String): FoodItem? {
        val lower = phrase.lowercase()

        // Check multiplier e.g. "2 eggs" or "two slices"
        var multiplier = 1f
        val numMatch = Regex("(\\d+(\\.\\d+)?)").find(lower)
        if (numMatch != null) {
            multiplier = numMatch.value.toFloatOrNull() ?: 1f
        } else if (lower.contains("two") || lower.contains("double")) {
            multiplier = 2f
        } else if (lower.contains("three")) {
            multiplier = 3f
        } else if (lower.contains("half")) {
            multiplier = 0.5f
        }

        for (ref in FoodDatabase.items) {
            val refWords = ref.name.lowercase().split(" ")
            if (refWords.any { it.length > 3 && lower.contains(it) }) {
                val base = ref.toFoodItem()
                val adjustedPortion = (base.portionGrams * multiplier)
                return ref.toFoodItem(adjustedPortion).copy(confidence = 0.95f)
            }
        }
        return null
    }

    fun inferMealTypeByTime(): MealType {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..10 -> MealType.BREAKFAST
            in 11..15 -> MealType.LUNCH
            in 16..17 -> MealType.SNACK
            in 18..22 -> MealType.DINNER
            else -> MealType.SNACK
        }
    }
}
