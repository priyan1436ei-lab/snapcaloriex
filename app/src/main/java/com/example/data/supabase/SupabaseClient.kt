package com.example.data.supabase

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.Meal
import com.example.data.model.UserGoals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SupabaseClient {

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private fun getBaseUrl(): String {
        return try {
            val url = BuildConfig.SUPABASE_URL
            if (url.isNotBlank()) url.trimEnd('/') else "https://seitqpvjeztdnbizxjyg.supabase.co"
        } catch (e: Throwable) {
            "https://seitqpvjeztdnbizxjyg.supabase.co"
        }
    }

    private var runtimeAnonKey: String? = null

    fun setRuntimeKey(key: String) {
        runtimeAnonKey = key.trim()
    }

    private fun getAnonKey(): String {
        if (!runtimeAnonKey.isNullOrBlank()) {
            return runtimeAnonKey!!
        }
        return try {
            val key = BuildConfig.SUPABASE_ANON_KEY
            if (key.isNotBlank() && key != "YOUR_SUPABASE_ANON_KEY") key else ""
        } catch (e: Throwable) {
            ""
        }
    }

    fun isConfigured(): Boolean {
        return getAnonKey().isNotBlank()
    }

    suspend fun syncMealToSupabase(meal: Meal, userId: String): Boolean = withContext(Dispatchers.IO) {
        val apiKey = getAnonKey()
        if (apiKey.isBlank()) {
            Log.d("SupabaseClient", "Supabase anon key not set in Secrets, skipping Supabase sync.")
            return@withContext false
        }

        try {
            val url = "${getBaseUrl()}/rest/v1/meals"
            val payload = JSONObject().apply {
                put("id", meal.id)
                put("user_id", userId)
                put("meal_type", meal.mealType.name)
                put("date_string", meal.dateString)
                put("timestamp", meal.timestamp)
                put("total_calories", meal.totalCalories)
                put("total_protein", meal.totalProtein.toDouble())
                put("total_carbs", meal.totalCarbs.toDouble())
                put("total_fat", meal.totalFat.toDouble())
                put("total_fiber", meal.totalFiber.toDouble())
                put("notes", meal.notes ?: "")
                put("items_json", JSONArray().apply {
                    meal.items.forEach { item ->
                        put(JSONObject().apply {
                            put("name", item.name)
                            put("portion_grams", item.portionGrams.toDouble())
                            put("calories", item.calories)
                            put("protein", item.protein.toDouble())
                            put("carbs", item.carbs.toDouble())
                            put("fat", item.fat.toDouble())
                        })
                    }
                }.toString())
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Prefer", "resolution=merge-duplicates")
                .post(payload.toString().toRequestBody(jsonMediaType))
                .build()

            val response = httpClient.newCall(request).execute()
            val success = response.isSuccessful
            if (success) {
                Log.d("SupabaseClient", "Meal ${meal.id} synced to Supabase successfully")
            } else {
                Log.w("SupabaseClient", "Supabase sync responded with code: ${response.code}")
            }
            return@withContext success
        } catch (e: Exception) {
            Log.e("SupabaseClient", "Failed to sync meal to Supabase", e)
            return@withContext false
        }
    }

    suspend fun syncGoalsToSupabase(goals: UserGoals, userId: String): Boolean = withContext(Dispatchers.IO) {
        val apiKey = getAnonKey()
        if (apiKey.isBlank()) return@withContext false

        try {
            val url = "${getBaseUrl()}/rest/v1/user_goals"
            val payload = JSONObject().apply {
                put("user_id", userId)
                put("daily_calories", goals.dailyCalories)
                put("protein_grams", goals.proteinGrams)
                put("carbs_grams", goals.carbsGrams)
                put("fat_grams", goals.fatGrams)
                put("water_goal_ml", goals.waterGoalMl)
                put("current_water_ml", goals.currentWaterMl)
                put("target_weight_kg", goals.targetWeightKg.toDouble())
                put("goal_type", goals.goalType)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Prefer", "resolution=merge-duplicates")
                .post(payload.toString().toRequestBody(jsonMediaType))
                .build()

            val response = httpClient.newCall(request).execute()
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            Log.e("SupabaseClient", "Failed to sync goals to Supabase", e)
            return@withContext false
        }
    }
}
