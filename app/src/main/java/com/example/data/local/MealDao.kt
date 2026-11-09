package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Transaction
    @Query("SELECT * FROM meals WHERE dateString = :date ORDER BY timestamp ASC")
    fun getMealsWithItemsForDate(date: String): Flow<List<MealWithItems>>

    @Transaction
    @Query("SELECT * FROM meals ORDER BY timestamp DESC")
    fun getAllMealsWithItems(): Flow<List<MealWithItems>>

    @Query("SELECT * FROM meals WHERE dateString = :date ORDER BY timestamp ASC")
    fun getMealsForDate(date: String): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals ORDER BY timestamp DESC")
    fun getAllMeals(): Flow<List<MealEntity>>

    @Query("SELECT * FROM food_items WHERE mealId = :mealId")
    suspend fun getFoodItemsForMeal(mealId: Long): List<FoodItemEntity>

    @Query("SELECT * FROM food_items WHERE mealId IN (:mealIds)")
    fun getFoodItemsForMealIds(mealIds: List<Long>): Flow<List<FoodItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItems(items: List<FoodItemEntity>)

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Query("DELETE FROM meals WHERE id = :mealId")
    suspend fun deleteMeal(mealId: Long)

    @Query("DELETE FROM food_items WHERE mealId = :mealId")
    suspend fun deleteFoodItemsForMeal(mealId: Long)

    @Query("UPDATE meals SET totalCalories = :calories, totalProtein = :protein, totalCarbs = :carbs, totalFat = :fat, totalFiber = :fiber WHERE id = :mealId")
    suspend fun updateMealTotals(mealId: Long, calories: Int, protein: Float, carbs: Float, fat: Float, fiber: Float)

    @Transaction
    suspend fun insertMealWithItems(meal: MealEntity, items: List<FoodItemEntity>): Long {
        val mealId = insertMeal(meal)
        val updatedItems = items.map { it.copy(mealId = mealId) }
        insertFoodItems(updatedItems)
        return mealId
    }

    @Transaction
    suspend fun updateMealWithItems(mealId: Long, updatedItems: List<FoodItemEntity>) {
        deleteFoodItemsForMeal(mealId)
        val withMealId = updatedItems.map { it.copy(mealId = mealId) }
        insertFoodItems(withMealId)

        val totalCalories = withMealId.sumOf { it.calories }
        val totalProtein = withMealId.sumOf { it.protein.toDouble() }.toFloat()
        val totalCarbs = withMealId.sumOf { it.carbs.toDouble() }.toFloat()
        val totalFat = withMealId.sumOf { it.fat.toDouble() }.toFloat()
        val totalFiber = withMealId.sumOf { it.fiber.toDouble() }.toFloat()

        updateMealTotals(mealId, totalCalories, totalProtein, totalCarbs, totalFat, totalFiber)
    }

    @Transaction
    suspend fun deleteMealWithItems(mealId: Long) {
        deleteFoodItemsForMeal(mealId)
        deleteMeal(mealId)
    }
}
