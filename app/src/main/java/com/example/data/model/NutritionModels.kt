package com.example.data.model

enum class MealType(val displayName: String, val iconRes: String) {
    BREAKFAST("Breakfast", "🍳"),
    LUNCH("Lunch", "🥗"),
    DINNER("Dinner", "🍲"),
    SNACK("Snacks", "🍎")
}

data class FoodItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val portionGrams: Float,
    val portionUnit: String = "g",
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val fiber: Float = 0f,
    val sodium: Float = 0f, // mg
    val sugar: Float = 0f,
    val confidence: Float = 0.95f,
    val tagColorHex: String = "#00D084"
)

data class Meal(
    val id: Long = System.currentTimeMillis(),
    val mealType: MealType,
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String, // "YYYY-MM-DD"
    val totalCalories: Int,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float,
    val totalFiber: Float = 0f,
    val photoUri: String? = null,
    val notes: String = "",
    val items: List<FoodItem> = emptyList()
)

data class UserGoals(
    val dailyCalories: Int = 2150,
    val proteinGrams: Int = 145,
    val carbsGrams: Int = 215,
    val fatGrams: Int = 65,
    val waterGoalMl: Int = 2500,
    val currentWaterMl: Int = 1500,
    val currentWeightKg: Float = 72.5f,
    val targetWeightKg: Float = 68.0f,
    val streakDays: Int = 7,
    val goalType: String = "Fat Loss"
)

data class DailySummary(
    val dateString: String,
    val totalCalories: Int = 0,
    val totalProtein: Float = 0f,
    val totalCarbs: Float = 0f,
    val totalFat: Float = 0f,
    val totalFiber: Float = 0f,
    val waterIntakeMl: Int = 1500,
    val meals: List<Meal> = emptyList()
)
