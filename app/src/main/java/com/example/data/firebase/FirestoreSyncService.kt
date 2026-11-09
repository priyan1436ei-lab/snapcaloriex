package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.DailySummary
import com.example.data.model.FoodItem
import com.example.data.model.Meal
import com.example.data.model.MealType
import com.example.data.model.UserGoals
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

class FirestoreSyncService(context: Context) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(
        context.getString(R.string.firestore_database_id)
    )

    private fun currentUid(): String? = Firebase.auth.currentUser?.uid

    suspend fun syncUserGoals(goals: UserGoals) {
        val uid = currentUid() ?: return
        try {
            val userDoc = db.collection("users").document(uid)
            val data = mapOf(
                "userId" to uid,
                "dailyCalories" to goals.dailyCalories,
                "proteinGrams" to goals.proteinGrams,
                "carbsGrams" to goals.carbsGrams,
                "fatGrams" to goals.fatGrams,
                "waterGoalMl" to goals.waterGoalMl,
                "currentWaterMl" to goals.currentWaterMl,
                "targetWeightKg" to goals.targetWeightKg.toDouble(),
                "goalType" to goals.goalType,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            userDoc.set(data, SetOptions.merge()).await()
            Log.d("FirestoreSyncService", "User goals synced to Firestore successfully")
        } catch (e: Exception) {
            Log.e("FirestoreSyncService", "Error syncing goals to Firestore", e)
        }
    }

    suspend fun syncMeal(meal: Meal) {
        val uid = currentUid() ?: return
        try {
            val mealsCollection = db.collection("users").document(uid).collection("meals")
            val itemsJson = JSONArray().apply {
                meal.items.forEach { item ->
                    put(JSONObject().apply {
                        put("id", item.id)
                        put("name", item.name)
                        put("portionGrams", item.portionGrams.toDouble())
                        put("calories", item.calories)
                        put("protein", item.protein.toDouble())
                        put("carbs", item.carbs.toDouble())
                        put("fat", item.fat.toDouble())
                        put("fiber", item.fiber.toDouble())
                    })
                }
            }.toString()

            val mealDoc = mealsCollection.document(meal.id.toString())
            val data = mapOf(
                "userId" to uid,
                "mealType" to meal.mealType.name,
                "dateString" to meal.dateString,
                "timestamp" to meal.timestamp,
                "totalCalories" to meal.totalCalories,
                "totalProtein" to meal.totalProtein.toDouble(),
                "totalCarbs" to meal.totalCarbs.toDouble(),
                "totalFat" to meal.totalFat.toDouble(),
                "totalFiber" to meal.totalFiber.toDouble(),
                "photoUri" to (meal.photoUri ?: ""),
                "notes" to (meal.notes ?: ""),
                "itemsJson" to itemsJson,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            mealDoc.set(data, SetOptions.merge()).await()
            Log.d("FirestoreSyncService", "Meal ${meal.id} synced to Firestore successfully")
        } catch (e: Exception) {
            Log.e("FirestoreSyncService", "Error syncing meal to Firestore", e)
        }
    }

    suspend fun deleteMeal(mealId: Long) {
        val uid = currentUid() ?: return
        try {
            db.collection("users").document(uid).collection("meals").document(mealId.toString()).delete().await()
            Log.d("FirestoreSyncService", "Meal $mealId deleted from Firestore")
        } catch (e: Exception) {
            Log.e("FirestoreSyncService", "Error deleting meal from Firestore", e)
        }
    }
}
