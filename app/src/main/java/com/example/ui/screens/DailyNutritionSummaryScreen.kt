package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.DailySummary
import com.example.data.model.Meal
import com.example.data.model.MealType
import com.example.data.model.UserGoals
import com.example.ui.components.MealDetailDialog
import com.example.ui.components.SetPersonalizedGoalsDialog
import com.example.ui.theme.CarbsAmber
import com.example.ui.theme.CarbsAmberLight
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FatCyan
import com.example.ui.theme.FatCyanLight
import com.example.ui.theme.ProteinCoral
import com.example.ui.theme.ProteinCoralLight
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardStroke
import com.example.ui.theme.SlateSubtle
import com.example.ui.theme.StreakOrange
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WaterBlue
import com.example.ui.viewmodel.CalorieViewModel

@Composable
fun DailyNutritionSummaryScreen(
    viewModel: CalorieViewModel,
    modifier: Modifier = Modifier
) {
    val dailySummary by viewModel.dailySummary.collectAsStateWithLifecycle()
    val userGoals by viewModel.userGoals.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedMealDetail by viewModel.selectedMealDetail.collectAsStateWithLifecycle()
    val nutritionInsight by viewModel.nutritionInsight.collectAsStateWithLifecycle()
    val isGeneratingInsight by viewModel.isGeneratingInsight.collectAsStateWithLifecycle()

    var showGoalsDialog by remember { mutableStateOf(false) }

    val totalCalories = dailySummary.totalCalories
    val calorieGoal = userGoals.dailyCalories
    val remainingCalories = (calorieGoal - totalCalories).coerceAtLeast(0)
    val isOverBudget = totalCalories > calorieGoal

    val totalProtein = dailySummary.totalProtein
    val totalCarbs = dailySummary.totalCarbs
    val totalFat = dailySummary.totalFat
    val totalFiber = dailySummary.totalFiber

    // Calories from each macro
    val proteinCals = (totalProtein * 4f).toInt()
    val carbsCals = (totalCarbs * 4f).toInt()
    val fatCals = (totalFat * 9f).toInt()
    val totalMacroCals = (proteinCals + carbsCals + fatCals).coerceAtLeast(1)

    val proteinCalPercent = ((proteinCals.toFloat() / totalMacroCals) * 100).toInt()
    val carbsCalPercent = ((carbsCals.toFloat() / totalMacroCals) * 100).toInt()
    val fatCalPercent = ((fatCals.toFloat() / totalMacroCals) * 100).toInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header & Date Selector
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Live Real-Time Sync",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Nutrition Summary",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Date Navigator
                    Surface(
                        color = SlateCard,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.setSelectedDate(adjustSummaryDate(selectedDate, -1)) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Day", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = formatSummaryDate(selectedDate),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            IconButton(
                                onClick = { viewModel.setSelectedDate(adjustSummaryDate(selectedDate, 1)) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Day", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Quick Goal Setting Icon Button
                    IconButton(
                        onClick = { showGoalsDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SlateCard)
                            .border(1.dp, EmeraldPrimary.copy(alpha = 0.4f), CircleShape)
                            .testTag("set_goals_header_btn")
                    ) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = "Personalize Goals",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Daily Gemini Nutrition Insight Card (Personalized, Health-Focused AI Tips)
        item {
            DailyNutritionInsightCard(
                insight = nutritionInsight,
                isLoading = isGeneratingInsight,
                onRefresh = { viewModel.refreshNutritionInsight() }
            )
        }

        // Personalized Target Goals & Progress Bars Section (Hero Proximity)
        item {
            PersonalizedTargetGoalsProgressCard(
                totalCalories = totalCalories,
                calorieGoal = calorieGoal,
                totalProtein = totalProtein,
                proteinGoal = userGoals.proteinGrams,
                totalCarbs = totalCarbs,
                carbsGoal = userGoals.carbsGrams,
                totalFat = totalFat,
                fatGoal = userGoals.fatGrams,
                goalType = userGoals.goalType,
                onEditGoals = { showGoalsDialog = true }
            )
        }

        // Hero Real-Time Calorie Balance Gauge
        item {
            RealTimeCalorieHeroCard(
                totalCalories = totalCalories,
                calorieGoal = calorieGoal,
                remainingCalories = remainingCalories,
                isOverBudget = isOverBudget,
                loggedMealsCount = dailySummary.meals.size,
                onEditGoals = { showGoalsDialog = true }
            )
        }

        // Macronutrients Consumed vs Targets Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Macronutrient Breakdown",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Surface(
                            color = SlateSubtle.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${totalMacroCals} kcal from macros",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Multi-segment Calorie Split Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .weight((proteinCalPercent.coerceAtLeast(5)).toFloat())
                                .height(12.dp)
                                .background(ProteinCoral)
                        )
                        Box(
                            modifier = Modifier
                                .weight((carbsCalPercent.coerceAtLeast(5)).toFloat())
                                .height(12.dp)
                                .background(CarbsAmber)
                        )
                        Box(
                            modifier = Modifier
                                .weight((fatCalPercent.coerceAtLeast(5)).toFloat())
                                .height(12.dp)
                                .background(FatCyan)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Detailed Cards for Protein, Carbs, Fat
                    DetailedMacroItem(
                        name = "Protein",
                        consumedGrams = totalProtein,
                        goalGrams = userGoals.proteinGrams,
                        caloriesFromMacro = proteinCals,
                        caloriePercent = proteinCalPercent,
                        color = ProteinCoral,
                        lightColor = ProteinCoralLight
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailedMacroItem(
                        name = "Carbohydrates",
                        consumedGrams = totalCarbs,
                        goalGrams = userGoals.carbsGrams,
                        caloriesFromMacro = carbsCals,
                        caloriePercent = carbsCalPercent,
                        color = CarbsAmber,
                        lightColor = CarbsAmberLight
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailedMacroItem(
                        name = "Fats",
                        consumedGrams = totalFat,
                        goalGrams = userGoals.fatGrams,
                        caloriesFromMacro = fatCals,
                        caloriePercent = fatCalPercent,
                        color = FatCyan,
                        lightColor = FatCyanLight
                    )
                }
            }
        }

        // Meal-by-Meal Calorie Distribution (Breakfast, Lunch, Dinner, Snack)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Meal-by-Meal Intake",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Energy and nutrient split across today's eating window",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    MealType.values().forEach { mealType ->
                        val mealsForType = dailySummary.meals.filter { it.mealType == mealType }
                        val mealCals = mealsForType.sumOf { it.totalCalories }
                        val mealProtein = mealsForType.sumOf { it.totalProtein.toDouble() }.toInt()
                        val mealCarbs = mealsForType.sumOf { it.totalCarbs.toDouble() }.toInt()
                        val mealFat = mealsForType.sumOf { it.totalFat.toDouble() }.toInt()
                        val mealPercent = if (totalCalories > 0) ((mealCals.toFloat() / totalCalories) * 100).toInt() else 0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = mealType.iconRes, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = mealType.displayName,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = if (mealsForType.isNotEmpty()) {
                                            "P: ${mealProtein}g • C: ${mealCarbs}g • F: ${mealFat}g"
                                        } else {
                                            "Not logged"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$mealCals kcal",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = if (mealCals > 0) EmeraldPrimary else TextSecondary
                                )
                                Text(
                                    text = "$mealPercent% of day",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }
                        HorizontalDivider(color = SlateCardStroke.copy(alpha = 0.5f), thickness = 0.5.dp)
                    }
                }
            }
        }

        // Micronutrients & Hydration Quality
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Micronutrients & Hydration",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Fiber Card
                        MicroMetricTile(
                            icon = Icons.Default.Bolt,
                            label = "Dietary Fiber",
                            currentValue = "${String.format("%.1f", totalFiber)}g",
                            targetValue = "30g",
                            progress = (totalFiber / 30f).coerceIn(0f, 1f),
                            color = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )

                        // Water Card
                        MicroMetricTile(
                            icon = Icons.Default.WaterDrop,
                            label = "Water Intake",
                            currentValue = "${userGoals.currentWaterMl}ml",
                            targetValue = "${userGoals.waterGoalMl}ml",
                            progress = (userGoals.currentWaterMl.toFloat() / userGoals.waterGoalMl.toFloat()).coerceIn(0f, 1f),
                            color = WaterBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Today's Detailed Food Log Items List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Logged Meals (${dailySummary.meals.size})",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SlateCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f)),
                    modifier = Modifier.clickable { viewModel.setTab(3) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "All Past Logs",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "All Past Logs",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )
                    }
                }
            }
        }

        if (dailySummary.meals.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SlateCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No meals logged for today yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(dailySummary.meals) { meal ->
                DailyMealSummaryCard(
                    meal = meal,
                    onClick = { viewModel.showMealDetail(meal) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    selectedMealDetail?.let { meal ->
        MealDetailDialog(
            meal = meal,
            onDismiss = { viewModel.showMealDetail(null) },
            onDelete = { id -> viewModel.deleteMeal(id) },
            onUpdatePortion = { itemId, newGrams ->
                viewModel.updateLoggedMealPortion(meal.id, itemId, newGrams)
            },
            onDeleteItem = { itemId ->
                viewModel.deleteLoggedMealItem(meal.id, itemId)
            },
            onAddIngredient = { food ->
                viewModel.addIngredientToLoggedMeal(meal.id, food)
            }
        )
    }

    if (showGoalsDialog) {
        SetPersonalizedGoalsDialog(
            currentGoals = userGoals,
            onDismiss = { showGoalsDialog = false },
            onSaveGoals = { cals, p, c, f, water, weight, objective ->
                viewModel.updateGoals(cals, p, c, f, water, weight, objective)
                showGoalsDialog = false
            }
        )
    }
}

@Composable
fun RealTimeCalorieHeroCard(
    totalCalories: Int,
    calorieGoal: Int,
    remainingCalories: Int,
    isOverBudget: Boolean,
    loggedMealsCount: Int,
    onEditGoals: (() -> Unit)? = null
) {
    val calorieProgress = if (calorieGoal > 0) (totalCalories.toFloat() / calorieGoal.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = calorieProgress,
        animationSpec = tween(900),
        label = "hero_progress"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(190.dp)
            ) {
                Canvas(modifier = Modifier.size(170.dp)) {
                    val stroke = 15.dp.toPx()
                    // Track
                    drawCircle(
                        color = SlateSubtle.copy(alpha = 0.5f),
                        style = Stroke(width = stroke)
                    )
                    // Progress
                    drawArc(
                        color = if (isOverBudget) StreakOrange else EmeraldPrimary,
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalCalories",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 38.sp
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = "consumed today",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3-Metric status row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HeroMetricItem(label = "Target Goal", value = "$calorieGoal", unit = "kcal", color = TextPrimary)
                HeroMetricItem(
                    label = if (isOverBudget) "Over Budget" else "Remaining",
                    value = if (isOverBudget) "+${totalCalories - calorieGoal}" else "$remainingCalories",
                    unit = "kcal",
                    color = if (isOverBudget) StreakOrange else EmeraldPrimary
                )
                HeroMetricItem(label = "Logged", value = "$loggedMealsCount", unit = "meals", color = CarbsAmber)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Satiety and Deficit Status Pill
            Surface(
                color = if (isOverBudget) StreakOrange.copy(alpha = 0.15f) else EmeraldPrimary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isOverBudget) StreakOrange.copy(alpha = 0.4f) else EmeraldPrimary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (isOverBudget) Icons.Default.Bolt else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isOverBudget) StreakOrange else EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isOverBudget) {
                            "${totalCalories - calorieGoal} kcal above target today"
                        } else {
                            "$remainingCalories kcal deficit • On track with nutrition plan"
                        },
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isOverBudget) StreakOrange else EmeraldPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun HeroMetricItem(label: String, value: String, unit: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}

@Composable
fun DetailedMacroItem(
    name: String,
    consumedGrams: Float,
    goalGrams: Int,
    caloriesFromMacro: Int,
    caloriePercent: Int,
    color: Color,
    lightColor: Color
) {
    val progress = if (goalGrams > 0) (consumedGrams / goalGrams).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(700),
        label = "macro_$name"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SlateBackground)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }

            Text(
                text = "${consumedGrams.toInt()} / ${goalGrams}g (${(progress * 100).toInt()}%)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = SlateSubtle.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$caloriesFromMacro kcal provided",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Text(
                text = "$caloriePercent% of daily energy",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun MicroMetricTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    currentValue: String,
    targetValue: String,
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = SlateBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentValue,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = "Target: $targetValue",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = SlateSubtle.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun DailyMealSummaryCard(
    meal: Meal,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Thumbnail if available
            if (meal.photoUri == "sample_food_plate") {
                Image(
                    painter = painterResource(id = R.drawable.sample_food_plate_1794152390034),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            } else if (meal.photoUri != null) {
                AsyncImage(
                    model = meal.photoUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SlateBackground,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = meal.mealType.iconRes, style = MaterialTheme.typography.titleLarge)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.mealType.displayName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = meal.items.joinToString(", ") { "${it.name} (${it.portionGrams.toInt()}g)" },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1
                )
                Text(
                    text = "P: ${meal.totalProtein.toInt()}g • C: ${meal.totalCarbs.toInt()}g • F: ${meal.totalFat.toInt()}g",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldPrimary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${meal.totalCalories} kcal",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = "Tap to edit",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

private fun formatSummaryDate(dateStr: String): String {
    return try {
        val sdfIn = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val date = sdfIn.parse(dateStr) ?: return dateStr
        val todayStr = sdfIn.format(java.util.Date())
        if (dateStr == todayStr) {
            "Today"
        } else {
            val sdfOut = java.text.SimpleDateFormat("MMM d", java.util.Locale.US)
            sdfOut.format(date)
        }
    } catch (e: Exception) {
        dateStr
    }
}

private fun adjustSummaryDate(dateStr: String, days: Int): String {
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val cal = java.util.Calendar.getInstance()
        cal.time = sdf.parse(dateStr) ?: java.util.Date()
        cal.add(java.util.Calendar.DAY_OF_YEAR, days)
        sdf.format(cal.time)
    } catch (e: Exception) {
        dateStr
    }
}

@Composable
fun PersonalizedTargetGoalsProgressCard(
    totalCalories: Int,
    calorieGoal: Int,
    totalProtein: Float,
    proteinGoal: Int,
    totalCarbs: Float,
    carbsGoal: Int,
    totalFat: Float,
    fatGoal: Int,
    goalType: String,
    onEditGoals: () -> Unit
) {
    val calorieProgress = if (calorieGoal > 0) (totalCalories.toFloat() / calorieGoal.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedCalorieProgress by animateFloatAsState(
        targetValue = calorieProgress,
        animationSpec = tween(800),
        label = "anim_cal_progress"
    )

    val caloriePercent = if (calorieGoal > 0) ((totalCalories.toFloat() / calorieGoal.toFloat()) * 100).toInt() else 0
    val calorieDiff = totalCalories - calorieGoal
    val isOverBudget = calorieDiff > 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("target_goals_progress_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header with Goal Tag and "Set Goals" Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldPrimary.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "Goal: $goalType",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Target Goals & Proximity",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SlateBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke),
                    modifier = Modifier
                        .clickable { onEditGoals() }
                        .testTag("edit_personalized_goals_card_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = "Personalize Goals",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Set Goals",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Overall Daily Calorie Target Progress Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SlateBackground)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Calorie Target",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "$totalCalories / $calorieGoal kcal ($caloriePercent%)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isOverBudget) StreakOrange else EmeraldPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sleek animated progress bar
                LinearProgressIndicator(
                    progress = { animatedCalorieProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (isOverBudget) StreakOrange else EmeraldPrimary,
                    trackColor = SlateSubtle.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Proximity feedback badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when {
                            isOverBudget -> "⚠️ ${calorieDiff} kcal over your daily target"
                            caloriePercent >= 90 -> "🎯 Almost there! ${(calorieGoal - totalCalories)} kcal left to hit target"
                            caloriePercent == 0 -> "0% completed • Log meals to track progress"
                            else -> "🏃 ${(calorieGoal - totalCalories)} kcal remaining to reach goal"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isOverBudget) StreakOrange else TextSecondary
                    )
                    Text(
                        text = if (isOverBudget) "+$caloriePercent%" else "${(100 - caloriePercent).coerceAtLeast(0)}% left",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Macro Targets Proximity Section
            Text(
                text = "Macronutrient Targets Proximity",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Protein Progress Bar
            MacroTargetProgressItem(
                name = "Protein",
                consumedGrams = totalProtein,
                goalGrams = proteinGoal,
                color = ProteinCoral,
                unit = "g"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Carbs Progress Bar
            MacroTargetProgressItem(
                name = "Carbohydrates",
                consumedGrams = totalCarbs,
                goalGrams = carbsGoal,
                color = CarbsAmber,
                unit = "g"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Fat Progress Bar
            MacroTargetProgressItem(
                name = "Fat",
                consumedGrams = totalFat,
                goalGrams = fatGoal,
                color = FatCyan,
                unit = "g"
            )
        }
    }
}

@Composable
fun MacroTargetProgressItem(
    name: String,
    consumedGrams: Float,
    goalGrams: Int,
    color: Color,
    unit: String
) {
    val progress = if (goalGrams > 0) (consumedGrams / goalGrams).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(700),
        label = "macro_prog_$name"
    )
    val percent = if (goalGrams > 0) ((consumedGrams / goalGrams) * 100).toInt() else 0
    val remaining = (goalGrams - consumedGrams.toInt()).coerceAtLeast(0)
    val exceeded = (consumedGrams.toInt() - goalGrams).coerceAtLeast(0)

    Surface(
        color = SlateBackground,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke.copy(alpha = 0.7f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }
                Text(
                    text = "${consumedGrams.toInt()} / ${goalGrams}$unit ($percent%)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = SlateSubtle.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (exceeded > 0) {
                        "Target reached! (+${exceeded}$unit over goal)"
                    } else if (remaining == 0) {
                        "100% of target reached!"
                    } else {
                        "$remaining$unit left to hit target"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (exceeded > 0) EmeraldPrimary else TextSecondary
                )
                Text(
                    text = if (goalGrams > 0) "${(progress * 100).toInt()}%" else "0%",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun DailyNutritionInsightCard(
    insight: com.example.data.ai.DailyNutritionInsight?,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("nutrition_insight_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                listOf(
                    EmeraldPrimary.copy(alpha = 0.6f),
                    CarbsAmber.copy(alpha = 0.3f),
                    SlateCardStroke
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header with Gemini AI Badge and Refresh button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = EmeraldPrimary.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Gemini Nutrition Intelligence",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary
                            )
                        }
                    }

                    insight?.focusArea?.let { focus ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SlateSubtle.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = focus,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                maxLines = 1
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onRefresh,
                    enabled = !isLoading,
                    modifier = Modifier.size(34.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = EmeraldPrimary
                        )
                    } else {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh Insight",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isLoading && insight == null) {
                // Skeleton loading state
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.5.dp,
                        color = EmeraldPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Analyzing your meal logs and macro ratios with Gemini...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            } else if (insight != null) {
                // Insight Title
                Text(
                    text = insight.headline,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    ),
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Summary analysis paragraph
                Text(
                    text = insight.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Actionable Tips Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = CarbsAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Actionable Health Tips",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = CarbsAmber
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Actionable tips list
                insight.actionableTips.forEachIndexed { index, tip ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldPrimary.copy(alpha = 0.2f),
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldPrimary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = tip,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            lineHeight = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (insight.motivationalNote.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SlateBackground.copy(alpha = 0.8f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "💡", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = insight.motivationalNote,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
