package com.example.data.repository

import android.content.Context
import com.example.data.local.CalorieLensDatabase
import com.example.data.local.FoodItemEntity
import com.example.data.local.MealEntity
import com.example.data.model.DailySummary
import com.example.data.model.FoodItem
import com.example.data.model.Meal
import com.example.data.model.MealType
import com.example.data.model.UserGoals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalorieRepository(private val context: Context) {
    private val database = CalorieLensDatabase.getDatabase(context)
    private val mealDao = database.mealDao()

    private val prefs = context.getSharedPreferences("calorie_lens_user_prefs", Context.MODE_PRIVATE)

    private val _userGoals = MutableStateFlow(
        UserGoals(
            dailyCalories = prefs.getInt("daily_calories", 2150),
            proteinGrams = prefs.getInt("protein_grams", 145),
            carbsGrams = prefs.getInt("carbs_grams", 215),
            fatGrams = prefs.getInt("fat_grams", 65),
            waterGoalMl = prefs.getInt("water_goal_ml", 2500),
            currentWaterMl = prefs.getInt("current_water_ml", 1750),
            currentWeightKg = prefs.getFloat("current_weight_kg", 72.5f),
            targetWeightKg = prefs.getFloat("target_weight_kg", 68.0f),
            streakDays = prefs.getInt("streak_days", 7),
            goalType = prefs.getString("goal_type", "Fat Loss") ?: "Fat Loss"
        )
    )
    val userGoals = _userGoals.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfEmpty()
        }
    }

    fun getAllPastMeals(): Flow<List<Meal>> {
        return mealDao.getAllMealsWithItems().map { list ->
            list.map { itemRel ->
                val mealEntity = itemRel.meal
                val domainItems = itemRel.items.map { itemEntity ->
                    FoodItem(
                        id = itemEntity.id,
                        name = itemEntity.name,
                        portionGrams = itemEntity.portionGrams,
                        portionUnit = itemEntity.portionUnit,
                        calories = itemEntity.calories,
                        protein = itemEntity.protein,
                        carbs = itemEntity.carbs,
                        fat = itemEntity.fat,
                        fiber = itemEntity.fiber,
                        sodium = itemEntity.sodium,
                        sugar = itemEntity.sugar,
                        confidence = itemEntity.confidence
                    )
                }
                Meal(
                    id = mealEntity.id,
                    mealType = mealEntity.mealType,
                    timestamp = mealEntity.timestamp,
                    dateString = mealEntity.dateString,
                    totalCalories = mealEntity.totalCalories,
                    totalProtein = mealEntity.totalProtein,
                    totalCarbs = mealEntity.totalCarbs,
                    totalFat = mealEntity.totalFat,
                    totalFiber = mealEntity.totalFiber,
                    photoUri = mealEntity.photoUri,
                    notes = mealEntity.notes,
                    items = domainItems
                )
            }
        }
    }

    fun getDailySummary(dateString: String): Flow<DailySummary> {
        return combine(
            mealDao.getMealsWithItemsForDate(dateString),
            _userGoals
        ) { mealsWithItems, goals ->
            val domainMeals = mealsWithItems.map { itemRel ->
                val mealEntity = itemRel.meal
                val domainItems = itemRel.items.map { itemEntity ->
                    FoodItem(
                        id = itemEntity.id,
                        name = itemEntity.name,
                        portionGrams = itemEntity.portionGrams,
                        portionUnit = itemEntity.portionUnit,
                        calories = itemEntity.calories,
                        protein = itemEntity.protein,
                        carbs = itemEntity.carbs,
                        fat = itemEntity.fat,
                        fiber = itemEntity.fiber,
                        sodium = itemEntity.sodium,
                        sugar = itemEntity.sugar,
                        confidence = itemEntity.confidence
                    )
                }
                Meal(
                    id = mealEntity.id,
                    mealType = mealEntity.mealType,
                    timestamp = mealEntity.timestamp,
                    dateString = mealEntity.dateString,
                    totalCalories = mealEntity.totalCalories,
                    totalProtein = mealEntity.totalProtein,
                    totalCarbs = mealEntity.totalCarbs,
                    totalFat = mealEntity.totalFat,
                    totalFiber = mealEntity.totalFiber,
                    photoUri = mealEntity.photoUri,
                    notes = mealEntity.notes,
                    items = domainItems
                )
            }

            DailySummary(
                dateString = dateString,
                totalCalories = domainMeals.sumOf { it.totalCalories },
                totalProtein = domainMeals.sumOf { it.totalProtein.toDouble() }.toFloat(),
                totalCarbs = domainMeals.sumOf { it.totalCarbs.toDouble() }.toFloat(),
                totalFat = domainMeals.sumOf { it.totalFat.toDouble() }.toFloat(),
                totalFiber = domainMeals.sumOf { it.totalFiber.toDouble() }.toFloat(),
                waterIntakeMl = goals.currentWaterMl,
                meals = domainMeals
            )
        }
    }

    suspend fun logMeal(
        mealType: MealType,
        items: List<FoodItem>,
        photoUri: String? = null,
        notes: String = "",
        dateString: String = getTodayDateString(),
        timestamp: Long = System.currentTimeMillis()
    ): Long {
        val totalCalories = items.sumOf { it.calories }
        val totalProtein = items.sumOf { it.protein.toDouble() }.toFloat()
        val totalCarbs = items.sumOf { it.carbs.toDouble() }.toFloat()
        val totalFat = items.sumOf { it.fat.toDouble() }.toFloat()
        val totalFiber = items.sumOf { it.fiber.toDouble() }.toFloat()

        val mealEntity = MealEntity(
            mealType = mealType,
            timestamp = timestamp,
            dateString = dateString,
            totalCalories = totalCalories,
            totalProtein = totalProtein,
            totalCarbs = totalCarbs,
            totalFat = totalFat,
            totalFiber = totalFiber,
            photoUri = photoUri,
            notes = notes
        )

        val foodEntities = items.map {
            FoodItemEntity(
                id = it.id,
                mealId = 0,
                name = it.name,
                portionGrams = it.portionGrams,
                portionUnit = it.portionUnit,
                calories = it.calories,
                protein = it.protein,
                carbs = it.carbs,
                fat = it.fat,
                fiber = it.fiber,
                sodium = it.sodium,
                sugar = it.sugar,
                confidence = it.confidence
            )
        }

        return mealDao.insertMealWithItems(mealEntity, foodEntities)
    }

    suspend fun updateMealItems(mealId: Long, items: List<FoodItem>) {
        val foodEntities = items.map {
            FoodItemEntity(
                id = it.id,
                mealId = mealId,
                name = it.name,
                portionGrams = it.portionGrams,
                portionUnit = it.portionUnit,
                calories = it.calories,
                protein = it.protein,
                carbs = it.carbs,
                fat = it.fat,
                fiber = it.fiber,
                sodium = it.sodium,
                sugar = it.sugar,
                confidence = it.confidence
            )
        }
        mealDao.updateMealWithItems(mealId, foodEntities)
    }

    suspend fun deleteMeal(mealId: Long) {
        mealDao.deleteMealWithItems(mealId)
    }

    fun addWater(amountMl: Int) {
        val newWater = (_userGoals.value.currentWaterMl + amountMl).coerceAtLeast(0)
        _userGoals.value = _userGoals.value.copy(currentWaterMl = newWater)
        prefs.edit().putInt("current_water_ml", newWater).apply()
    }

    fun updateGoals(
        calories: Int,
        protein: Int,
        carbs: Int,
        fat: Int,
        waterMl: Int,
        targetWeightKg: Float,
        goalType: String = _userGoals.value.goalType
    ) {
        _userGoals.value = _userGoals.value.copy(
            dailyCalories = calories,
            proteinGrams = protein,
            carbsGrams = carbs,
            fatGrams = fat,
            waterGoalMl = waterMl,
            targetWeightKg = targetWeightKg,
            goalType = goalType
        )
        prefs.edit()
            .putInt("daily_calories", calories)
            .putInt("protein_grams", protein)
            .putInt("carbs_grams", carbs)
            .putInt("fat_grams", fat)
            .putInt("water_goal_ml", waterMl)
            .putFloat("target_weight_kg", targetWeightKg)
            .putString("goal_type", goalType)
            .apply()
    }

    companion object {
        fun getTodayDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return sdf.format(Date())
        }

        fun getPastDateString(daysAgo: Int): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            return sdf.format(cal.time)
        }
    }

    private suspend fun seedInitialDataIfEmpty() {
        val today = getTodayDateString()
        val yesterday = getPastDateString(1)
        val twoDaysAgo = getPastDateString(2)

        val existing = mealDao.getFoodItemsForMeal(1L)
        if (existing.isNotEmpty()) return

        // Seed realistic breakfast (Today)
        val breakfastItems = listOf(
            FoodItem(
                name = "Rolled Oatmeal with Blueberries",
                portionGrams = 200f,
                portionUnit = "bowl",
                calories = 245,
                protein = 8.5f,
                carbs = 42.0f,
                fat = 4.5f,
                fiber = 6.2f,
                sodium = 12f
            ),
            FoodItem(
                name = "Boiled Eggs (2 large)",
                portionGrams = 100f,
                portionUnit = "eggs",
                calories = 143,
                protein = 12.6f,
                carbs = 0.7f,
                fat = 9.5f,
                fiber = 0f,
                sodium = 142f
            ),
            FoodItem(
                name = "Black Coffee",
                portionGrams = 240f,
                portionUnit = "cup",
                calories = 2,
                protein = 0.1f,
                carbs = 0f,
                fat = 0f,
                fiber = 0f,
                sodium = 5f
            )
        )
        logMeal(MealType.BREAKFAST, breakfastItems, null, "Pre-workout energy", today, System.currentTimeMillis() - 14400000)

        // Seed realistic photo meal bowl (Today Lunch)
        val lunchItems = listOf(
            FoodItem(
                name = "Grilled Chicken Breast",
                portionGrams = 150f,
                portionUnit = "g",
                calories = 247,
                protein = 46.5f,
                carbs = 0f,
                fat = 5.4f,
                fiber = 0f,
                sodium = 110f
            ),
            FoodItem(
                name = "Steamed Brown Rice",
                portionGrams = 140f,
                portionUnit = "cup",
                calories = 155,
                protein = 3.6f,
                carbs = 33.0f,
                fat = 1.2f,
                fiber = 2.4f,
                sodium = 5f
            ),
            FoodItem(
                name = "Avocado Hass (Fresh sliced)",
                portionGrams = 60f,
                portionUnit = "g",
                calories = 96,
                protein = 1.2f,
                carbs = 5.1f,
                fat = 8.8f,
                fiber = 4.0f,
                sodium = 4f
            ),
            FoodItem(
                name = "Steamed Broccoli Florets",
                portionGrams = 80f,
                portionUnit = "g",
                calories = 28,
                protein = 2.2f,
                carbs = 5.6f,
                fat = 0.3f,
                fiber = 2.1f,
                sodium = 26f
            )
        )
        logMeal(MealType.LUNCH, lunchItems, "sample_food_plate", "Camera AI Lens Meal Log", today, System.currentTimeMillis() - 7200000)

        // Seed realistic snack (Today)
        val snackItems = listOf(
            FoodItem(
                name = "Greek Yogurt (0% Fat)",
                portionGrams = 170f,
                portionUnit = "g",
                calories = 100,
                protein = 17.3f,
                carbs = 6.1f,
                fat = 0.7f,
                fiber = 0f,
                sodium = 61f
            ),
            FoodItem(
                name = "Raw Almonds",
                portionGrams = 28f,
                portionUnit = "handful",
                calories = 162,
                protein = 6.0f,
                carbs = 6.0f,
                fat = 14.0f,
                fiber = 3.5f,
                sodium = 1f
            )
        )
        logMeal(MealType.SNACK, snackItems, null, "Mid-morning bite", today, System.currentTimeMillis() - 3600000)

        // Seed Past Meal 1 (Yesterday Lunch)
        val yestLunchItems = listOf(
            FoodItem(
                name = "Atlantic Salmon Fillet",
                portionGrams = 160f,
                portionUnit = "fillet",
                calories = 332,
                protein = 32.0f,
                carbs = 0f,
                fat = 20.8f,
                fiber = 0f,
                sodium = 95f
            ),
            FoodItem(
                name = "Organic Quinoa (Cooked)",
                portionGrams = 150f,
                portionUnit = "g",
                calories = 180,
                protein = 6.6f,
                carbs = 32.0f,
                fat = 2.9f,
                fiber = 4.2f,
                sodium = 10f
            ),
            FoodItem(
                name = "Steamed Asparagus Spears",
                portionGrams = 90f,
                portionUnit = "g",
                calories = 18,
                protein = 2.0f,
                carbs = 3.5f,
                fat = 0.2f,
                fiber = 1.9f,
                sodium = 2f
            )
        )
        logMeal(MealType.LUNCH, yestLunchItems, "sample_food_plate", "Omega-3 rich recovery lunch", yesterday, System.currentTimeMillis() - 93600000)

        // Seed Past Meal 2 (Yesterday Dinner)
        val yestDinnerItems = listOf(
            FoodItem(
                name = "Grass-Fed Beef Sirloin Steak",
                portionGrams = 180f,
                portionUnit = "steak",
                calories = 370,
                protein = 48.0f,
                carbs = 0f,
                fat = 18.0f,
                fiber = 0f,
                sodium = 105f
            ),
            FoodItem(
                name = "Roasted Sweet Potato",
                portionGrams = 150f,
                portionUnit = "medium",
                calories = 135,
                protein = 3.0f,
                carbs = 31.0f,
                fat = 0.2f,
                fiber = 4.5f,
                sodium = 72f
            ),
            FoodItem(
                name = "Mixed Garden Greens with Olive Oil",
                portionGrams = 85f,
                portionUnit = "bowl",
                calories = 95,
                protein = 1.4f,
                carbs = 3.0f,
                fat = 9.0f,
                fiber = 1.8f,
                sodium = 45f
            )
        )
        logMeal(MealType.DINNER, yestDinnerItems, null, "High-protein recovery dinner", yesterday, System.currentTimeMillis() - 86400000)

        // Seed Past Meal 3 (2 Days Ago Lunch)
        val twoDaysLunchItems = listOf(
            FoodItem(
                name = "Grilled Chicken Breast",
                portionGrams = 140f,
                portionUnit = "g",
                calories = 231,
                protein = 43.4f,
                carbs = 0f,
                fat = 5.0f,
                fiber = 0f,
                sodium = 102f
            ),
            FoodItem(
                name = "Whole Wheat Pasta",
                portionGrams = 140f,
                portionUnit = "cup",
                calories = 174,
                protein = 7.5f,
                carbs = 37.0f,
                fat = 0.8f,
                fiber = 5.5f,
                sodium = 6f
            ),
            FoodItem(
                name = "Olive Oil Marinara Sauce",
                portionGrams = 100f,
                portionUnit = "g",
                calories = 65,
                protein = 1.5f,
                carbs = 8.5f,
                fat = 3.2f,
                fiber = 2.0f,
                sodium = 280f
            )
        )
        logMeal(MealType.LUNCH, twoDaysLunchItems, "sample_food_plate", "Carb loading meal", twoDaysAgo, System.currentTimeMillis() - 172800000)
    }
}
