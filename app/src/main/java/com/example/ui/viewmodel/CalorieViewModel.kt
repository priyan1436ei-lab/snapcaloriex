package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.DailyNutritionInsight
import com.example.data.ai.FoodDatabase
import com.example.data.ai.FoodRecognitionEngine
import com.example.data.ai.GeminiNutritionInsightEngine
import com.example.data.model.DailySummary
import com.example.data.model.FoodItem
import com.example.data.model.MealType
import com.example.data.model.UserGoals
import com.example.data.repository.CalorieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed class ScannerStep {
    data object Viewfinder : ScannerStep()
    data class Analyzing(val photoUri: String?) : ScannerStep()
    data class Review(
        val photoUri: String?,
        val detectedItems: List<FoodItem>,
        val mealType: MealType
    ) : ScannerStep()
}

class CalorieViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CalorieRepository(application)
    val authManager = com.example.data.firebase.FirebaseAuthManager(application)
    val firestoreSyncService = com.example.data.firebase.FirestoreSyncService(application)

    val currentUser = authManager.currentUser
    val isAuthLoading = authManager.isLoading
    val authErrorMessage = authManager.errorMessage

    init {
        // Attempt silent auto-sign in on startup if previous Google session exists
        authManager.attemptAutoSignIn(viewModelScope)
    }

    private val _selectedDate = MutableStateFlow(CalorieRepository.getTodayDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    val dailySummary: StateFlow<DailySummary> = _selectedDate
        .flatMapLatest { date -> repository.getDailySummary(date) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            DailySummary(dateString = CalorieRepository.getTodayDateString())
        )

    val userGoals: StateFlow<UserGoals> = repository.userGoals

    val allPastMeals: StateFlow<List<com.example.data.model.Meal>> = repository.getAllPastMeals()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Scanner state
    private val _scannerStep = MutableStateFlow<ScannerStep>(ScannerStep.Viewfinder)
    val scannerStep: StateFlow<ScannerStep> = _scannerStep.asStateFlow()

    // Active bottom navigation destination
    private val _currentTab = MutableStateFlow(0) // 0: Diary, 1: Scan, 2: Analytics, 3: Profile
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // Voice Logger State
    private val _voiceTranscript = MutableStateFlow("")
    val voiceTranscript: StateFlow<String> = _voiceTranscript.asStateFlow()

    private val _voiceParsedItems = MutableStateFlow<List<FoodItem>>(emptyList())
    val voiceParsedItems: StateFlow<List<FoodItem>> = _voiceParsedItems.asStateFlow()

    private val _isVoiceProcessing = MutableStateFlow(false)
    val isVoiceProcessing: StateFlow<Boolean> = _isVoiceProcessing.asStateFlow()

    // Search & Barcode
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<FoodDatabase.FoodReference>>(FoodDatabase.items.take(12))
    val searchResults: StateFlow<List<FoodDatabase.FoodReference>> = _searchResults.asStateFlow()

    // Selected meal for detail sheet
    private val _selectedMealDetail = MutableStateFlow<com.example.data.model.Meal?>(null)
    val selectedMealDetail: StateFlow<com.example.data.model.Meal?> = _selectedMealDetail.asStateFlow()

    // Daily Nutrition Insight State (Gemini AI powered)
    private val _nutritionInsight = MutableStateFlow<DailyNutritionInsight?>(null)
    val nutritionInsight: StateFlow<DailyNutritionInsight?> = _nutritionInsight.asStateFlow()

    private val _isGeneratingInsight = MutableStateFlow(false)
    val isGeneratingInsight: StateFlow<Boolean> = _isGeneratingInsight.asStateFlow()

    init {
        // Automatically generate / update insight when daily summary or goals change
        viewModelScope.launch {
            dailySummary.collect { summary ->
                refreshNutritionInsight(summary, userGoals.value)
            }
        }
    }

    fun refreshNutritionInsight(
        summary: DailySummary = dailySummary.value,
        goals: UserGoals = userGoals.value
    ) {
        viewModelScope.launch {
            _isGeneratingInsight.value = true
            try {
                val insight = GeminiNutritionInsightEngine.generateDailyInsight(summary, goals)
                _nutritionInsight.value = insight
            } catch (e: Exception) {
                _nutritionInsight.value = GeminiNutritionInsightEngine.buildClinicalNutritionInsight(summary, goals)
            } finally {
                _isGeneratingInsight.value = false
            }
        }
    }

    fun setTab(tabIndex: Int) {
        _currentTab.value = tabIndex
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun showMealDetail(meal: com.example.data.model.Meal?) {
        _selectedMealDetail.value = meal
    }

    // Scanner Actions
    fun startScanSampleBowl() {
        viewModelScope.launch {
            _scannerStep.value = ScannerStep.Analyzing("sample_food_plate")
            val items = FoodRecognitionEngine.analyzeFoodImage("sample_food_plate", isSamplePlate = true)
            _scannerStep.value = ScannerStep.Review(
                photoUri = "sample_food_plate",
                detectedItems = items,
                mealType = FoodRecognitionEngine.inferMealTypeByTime()
            )
        }
    }

    fun startScanWithPhotoUri(uri: String) {
        viewModelScope.launch {
            _scannerStep.value = ScannerStep.Analyzing(uri)
            val items = FoodRecognitionEngine.analyzeFoodImage(uri, isSamplePlate = false)
            _scannerStep.value = ScannerStep.Review(
                photoUri = uri,
                detectedItems = items,
                mealType = FoodRecognitionEngine.inferMealTypeByTime()
            )
        }
    }

    fun updateScannerPortion(itemId: String, newGrams: Float) {
        val current = _scannerStep.value
        if (current is ScannerStep.Review) {
            val updated = current.detectedItems.map { item ->
                if (item.id == itemId) {
                    val ratio = (newGrams / item.portionGrams).coerceAtLeast(0.1f)
                    item.copy(
                        portionGrams = newGrams,
                        calories = (item.calories * ratio).toInt(),
                        protein = item.protein * ratio,
                        carbs = item.carbs * ratio,
                        fat = item.fat * ratio,
                        fiber = item.fiber * ratio
                    )
                } else item
            }
            _scannerStep.value = current.copy(detectedItems = updated)
        }
    }

    fun removeScannerItem(itemId: String) {
        val current = _scannerStep.value
        if (current is ScannerStep.Review) {
            val updated = current.detectedItems.filter { it.id != itemId }
            _scannerStep.value = current.copy(detectedItems = updated)
        }
    }

    fun setScannerMealType(type: MealType) {
        val current = _scannerStep.value
        if (current is ScannerStep.Review) {
            _scannerStep.value = current.copy(mealType = type)
        }
    }

    fun confirmLogScannerMeal() {
        val current = _scannerStep.value
        if (current is ScannerStep.Review) {
            viewModelScope.launch {
                repository.logMeal(
                    mealType = current.mealType,
                    items = current.detectedItems,
                    photoUri = current.photoUri,
                    dateString = _selectedDate.value
                )
                _scannerStep.value = ScannerStep.Viewfinder
                _currentTab.value = 0 // Return to Diary
            }
        }
    }

    fun resetScanner() {
        _scannerStep.value = ScannerStep.Viewfinder
    }

    // Voice Logging
    fun onVoiceTranscriptChanged(text: String) {
        _voiceTranscript.value = text
        if (text.length > 3) {
            viewModelScope.launch {
                _isVoiceProcessing.value = true
                _voiceParsedItems.value = FoodRecognitionEngine.parseVoiceOrTextMeal(text)
                _isVoiceProcessing.value = false
            }
        } else {
            _voiceParsedItems.value = emptyList()
        }
    }

    fun confirmLogVoiceMeal(mealType: MealType) {
        val items = _voiceParsedItems.value
        if (items.isNotEmpty()) {
            viewModelScope.launch {
                repository.logMeal(
                    mealType = mealType,
                    items = items,
                    photoUri = null,
                    notes = _voiceTranscript.value,
                    dateString = _selectedDate.value
                )
                _voiceTranscript.value = ""
                _voiceParsedItems.value = emptyList()
                _currentTab.value = 0
            }
        }
    }

    // Food Search
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = FoodDatabase.items.take(12)
        } else {
            _searchResults.value = FoodDatabase.items.filter {
                it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
            }
        }
    }

    fun logDirectFood(food: FoodDatabase.FoodReference, mealType: MealType, portionGrams: Float) {
        viewModelScope.launch {
            val item = food.toFoodItem(portionGrams)
            repository.logMeal(
                mealType = mealType,
                items = listOf(item),
                photoUri = null,
                dateString = _selectedDate.value
            )
            _currentTab.value = 0
        }
    }

    fun logQuickCalories(calories: Int, mealType: MealType, label: String) {
        viewModelScope.launch {
            val item = FoodItem(
                id = UUID.randomUUID().toString(),
                name = label.ifBlank { "Quick Add ($calories kcal)" },
                portionGrams = 100f,
                portionUnit = "serving",
                calories = calories,
                protein = (calories * 0.15f / 4f),
                carbs = (calories * 0.55f / 4f),
                fat = (calories * 0.30f / 9f)
            )
            repository.logMeal(
                mealType = mealType,
                items = listOf(item),
                photoUri = null,
                dateString = _selectedDate.value
            )
            _currentTab.value = 0
        }
    }

    fun addWater(ml: Int) {
        repository.addWater(ml)
    }

    fun deleteMeal(mealId: Long) {
        viewModelScope.launch {
            repository.deleteMeal(mealId)
            firestoreSyncService.deleteMeal(mealId)
            if (_selectedMealDetail.value?.id == mealId) {
                _selectedMealDetail.value = null
            }
        }
    }

    fun updateGoals(
        calories: Int,
        protein: Int,
        carbs: Int,
        fat: Int,
        waterMl: Int,
        targetWeight: Float = 70f,
        goalType: String = userGoals.value.goalType
    ) {
        repository.updateGoals(calories, protein, carbs, fat, waterMl, targetWeight, goalType)
        viewModelScope.launch {
            firestoreSyncService.syncUserGoals(userGoals.value)
        }
    }

    // Real-Time Editing for Logged Meals
    fun updateLoggedMealPortion(mealId: Long, itemId: String, newGrams: Float) {
        val currentMeal = _selectedMealDetail.value ?: return
        if (currentMeal.id != mealId) return

        val updatedItems = currentMeal.items.map { item ->
            if (item.id == itemId) {
                val ratio = (newGrams / item.portionGrams).coerceAtLeast(0.05f)
                item.copy(
                    portionGrams = newGrams,
                    calories = (item.calories * ratio).toInt(),
                    protein = item.protein * ratio,
                    carbs = item.carbs * ratio,
                    fat = item.fat * ratio,
                    fiber = item.fiber * ratio
                )
            } else item
        }

        viewModelScope.launch {
            repository.updateMealItems(mealId, updatedItems)
            _selectedMealDetail.value = currentMeal.copy(
                items = updatedItems,
                totalCalories = updatedItems.sumOf { it.calories },
                totalProtein = updatedItems.sumOf { it.protein.toDouble() }.toFloat(),
                totalCarbs = updatedItems.sumOf { it.carbs.toDouble() }.toFloat(),
                totalFat = updatedItems.sumOf { it.fat.toDouble() }.toFloat(),
                totalFiber = updatedItems.sumOf { it.fiber.toDouble() }.toFloat()
            )
        }
    }

    fun deleteLoggedMealItem(mealId: Long, itemId: String) {
        val currentMeal = _selectedMealDetail.value ?: return
        if (currentMeal.id != mealId) return

        val updatedItems = currentMeal.items.filter { it.id != itemId }
        viewModelScope.launch {
            if (updatedItems.isEmpty()) {
                repository.deleteMeal(mealId)
                _selectedMealDetail.value = null
            } else {
                repository.updateMealItems(mealId, updatedItems)
                _selectedMealDetail.value = currentMeal.copy(
                    items = updatedItems,
                    totalCalories = updatedItems.sumOf { it.calories },
                    totalProtein = updatedItems.sumOf { it.protein.toDouble() }.toFloat(),
                    totalCarbs = updatedItems.sumOf { it.carbs.toDouble() }.toFloat(),
                    totalFat = updatedItems.sumOf { it.fat.toDouble() }.toFloat(),
                    totalFiber = updatedItems.sumOf { it.fiber.toDouble() }.toFloat()
                )
            }
        }
    }

    fun addIngredientToScanner(food: FoodDatabase.FoodReference, portionGrams: Float = food.standardPortion) {
        val current = _scannerStep.value
        if (current is ScannerStep.Review) {
            val newItem = food.toFoodItem(portionGrams)
            _scannerStep.value = current.copy(detectedItems = current.detectedItems + newItem)
        }
    }

    fun addIngredientToLoggedMeal(mealId: Long, food: FoodDatabase.FoodReference, portionGrams: Float = food.standardPortion) {
        val currentMeal = _selectedMealDetail.value ?: return
        if (currentMeal.id != mealId) return

        val newItem = food.toFoodItem(portionGrams)
        val updatedItems = currentMeal.items + newItem
        viewModelScope.launch {
            repository.updateMealItems(mealId, updatedItems)
            _selectedMealDetail.value = currentMeal.copy(
                items = updatedItems,
                totalCalories = updatedItems.sumOf { it.calories },
                totalProtein = updatedItems.sumOf { it.protein.toDouble() }.toFloat(),
                totalCarbs = updatedItems.sumOf { it.carbs.toDouble() }.toFloat(),
                totalFat = updatedItems.sumOf { it.fat.toDouble() }.toFloat(),
                totalFiber = updatedItems.sumOf { it.fiber.toDouble() }.toFloat()
            )
        }
    }

    fun addCustomFood(
        name: String,
        calories: Int,
        protein: Float,
        carbs: Float,
        fat: Float,
        fiber: Float,
        standardGrams: Float
    ) {
        val validPortion = standardGrams.coerceAtLeast(10f)
        val newFood = FoodDatabase.FoodReference(
            name = name,
            standardPortion = validPortion,
            unit = "g",
            caloriesPer100g = ((calories / validPortion) * 100).toInt(),
            proteinPer100g = (protein / validPortion) * 100,
            carbsPer100g = (carbs / validPortion) * 100,
            fatPer100g = (fat / validPortion) * 100,
            fiberPer100g = (fiber / validPortion) * 100,
            category = "Custom Foods"
        )
        FoodDatabase.addCustomFood(newFood)
        onSearchQueryChanged(_searchQuery.value)
    }
}
