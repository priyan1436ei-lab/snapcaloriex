package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.DailySummary
import com.example.data.model.Meal
import com.example.data.model.MealType
import com.example.data.model.UserGoals
import com.example.ui.components.CalorieRingCard
import com.example.ui.components.MacroSummaryRow
import com.example.ui.components.MealDetailDialog
import com.example.ui.components.SearchFoodDialog
import com.example.ui.components.VoiceLogDialog
import com.example.ui.theme.CarbsAmber
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FatCyan
import com.example.ui.theme.ProteinCoral
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardStroke
import com.example.ui.theme.SlateSubtle
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.StreakOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WaterBlue
import com.example.ui.viewmodel.CalorieViewModel

@Composable
fun DashboardScreen(
    viewModel: CalorieViewModel,
    modifier: Modifier = Modifier
) {
    val dailySummary by viewModel.dailySummary.collectAsStateWithLifecycle()
    val userGoals by viewModel.userGoals.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedMealDetail by viewModel.selectedMealDetail.collectAsStateWithLifecycle()

    var showVoiceDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchDefaultMealType by remember { mutableStateOf(MealType.BREAKFAST) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Date Selector & Streak Badge
            item {
                Spacer(modifier = Modifier.height(8.dp))
                DashboardHeader(
                    selectedDate = selectedDate,
                    streakDays = userGoals.streakDays,
                    onPrevDay = { viewModel.setSelectedDate(adjustDate(selectedDate, -1)) },
                    onNextDay = { viewModel.setSelectedDate(adjustDate(selectedDate, 1)) }
                )
            }

            // Calorie Ring & Target
            item {
                CalorieRingCard(
                    calorieGoal = userGoals.dailyCalories,
                    eatenCalories = dailySummary.totalCalories,
                    burnedCalories = 0
                )
            }

            // Macronutrient Progress Bars (P / C / F)
            item {
                MacroSummaryRow(
                    proteinCurrent = dailySummary.totalProtein,
                    proteinTarget = userGoals.proteinGrams,
                    carbsCurrent = dailySummary.totalCarbs,
                    carbsTarget = userGoals.carbsGrams,
                    fatCurrent = dailySummary.totalFat,
                    fatTarget = userGoals.fatGrams
                )
            }

            // Daily Nutrition Summary Link
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.setTab(2) }
                        .testTag("open_daily_nutrition_summary"),
                    color = SlateCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PieChart,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Daily Nutrition Summary",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Live macro targets, % energy & meal splits",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View Summary",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Water Tracker Card
            item {
                WaterTrackerCard(
                    currentWaterMl = userGoals.currentWaterMl,
                    goalWaterMl = userGoals.waterGoalMl,
                    onAddWater = { viewModel.addWater(250) }
                )
            }

            // Visual Food Timeline Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Food Diary Timeline",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SlateCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .clickable { viewModel.setTab(3) }
                            .testTag("open_past_meal_logs_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = "Past Meal Logs",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Past Logs",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary
                            )
                        }
                    }
                }
            }

            // 4 Meal Sections: Breakfast, Lunch, Dinner, Snack
            items(MealType.values()) { mealType ->
                val mealsForType = dailySummary.meals.filter { it.mealType == mealType }
                MealSectionCard(
                    mealType = mealType,
                    meals = mealsForType,
                    onAddClick = {
                        searchDefaultMealType = mealType
                        showSearchDialog = true
                    },
                    onCameraClick = {
                        viewModel.setTab(1) // Open Camera Scanner
                    },
                    onVoiceClick = {
                        showVoiceDialog = true
                    },
                    onMealClick = { meal ->
                        viewModel.showMealDetail(meal)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }

        // Floating Action Buttons Bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Voice Log FAB
            FloatingActionButton(
                onClick = { showVoiceDialog = true },
                containerColor = SlateCard,
                contentColor = EmeraldPrimary,
                shape = CircleShape,
                modifier = Modifier.size(48.dp).testTag("fab_voice")
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Voice Log", modifier = Modifier.size(22.dp))
            }

            // Primary Scan Plate FAB
            FloatingActionButton(
                onClick = { viewModel.setTab(1) },
                containerColor = EmeraldPrimary,
                contentColor = SlateBackground,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("fab_scan")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Scan Meal")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Snap Plate",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }

    // Dialogs
    if (showVoiceDialog) {
        VoiceLogDialog(
            viewModel = viewModel,
            onDismiss = { showVoiceDialog = false }
        )
    }

    if (showSearchDialog) {
        SearchFoodDialog(
            viewModel = viewModel,
            defaultMealType = searchDefaultMealType,
            onDismiss = { showSearchDialog = false }
        )
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
}

@Composable
fun DashboardHeader(
    selectedDate: String,
    streakDays: Int,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Date switcher
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onPrevDay, modifier = Modifier.size(36.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Day", tint = TextSecondary)
            }
            Text(
                text = formatDisplayDate(selectedDate),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            IconButton(onClick = onNextDay, modifier = Modifier.size(36.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Day", tint = TextSecondary)
            }
        }

        // Streak badge
        Surface(
            color = StreakOrange.copy(alpha = 0.15f),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, StreakOrange.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Whatshot,
                    contentDescription = "Streak",
                    tint = StreakOrange,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$streakDays Day Streak",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = StreakOrange
                )
            }
        }
    }
}

@Composable
fun WaterTrackerCard(
    currentWaterMl: Int,
    goalWaterMl: Int,
    onAddWater: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(WaterBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.LocalDrink,
                        contentDescription = "Water",
                        tint = WaterBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Water Hydration",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "$currentWaterMl / $goalWaterMl ml",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = WaterBlue,
                modifier = Modifier.clickable { onAddWater() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = SlateBackground, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+250 ml",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = SlateBackground
                    )
                }
            }
        }
    }
}

@Composable
fun MealSectionCard(
    mealType: MealType,
    meals: List<Meal>,
    onAddClick: () -> Unit,
    onCameraClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onMealClick: (Meal) -> Unit
) {
    val totalCalories = meals.sumOf { it.totalCalories }
    val totalProtein = meals.sumOf { it.totalProtein.toDouble() }.toInt()
    val totalCarbs = meals.sumOf { it.totalCarbs.toDouble() }.toInt()
    val totalFat = meals.sumOf { it.totalFat.toDouble() }.toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Meal Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = mealType.iconRes, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = mealType.displayName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        if (meals.isNotEmpty()) {
                            Text(
                                text = "P: ${totalProtein}g • C: ${totalCarbs}g • F: ${totalFat}g",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$totalCalories kcal",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (totalCalories > 0) EmeraldPrimary else TextSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onAddClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Add Food", tint = EmeraldPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Logged Items or Empty State
            if (meals.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SlateBackground.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "No items logged yet",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = SlateSubtle.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { onCameraClick() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = "Scan", tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                            Surface(
                                shape = CircleShape,
                                color = SlateSubtle.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { onVoiceClick() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Mic, contentDescription = "Voice", tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    meals.forEach { meal ->
                        MealEntryRow(meal = meal, onClick = { onMealClick(meal) })
                    }
                }
            }
        }
    }
}

@Composable
fun MealEntryRow(meal: Meal, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SlateBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Image Thumbnail if available
            if (meal.photoUri == "sample_food_plate") {
                Image(
                    painter = painterResource(id = R.drawable.sample_food_plate_1794152390034),
                    contentDescription = null,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
            } else if (meal.photoUri != null) {
                AsyncImage(
                    model = meal.photoUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.items.joinToString(", ") { it.name },
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = "${meal.items.size} items • P: ${meal.totalProtein.toInt()}g | C: ${meal.totalCarbs.toInt()}g | F: ${meal.totalFat.toInt()}g",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Text(
                text = "${meal.totalCalories} kcal",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = EmeraldPrimary
            )
        }
    }
}

private fun formatDisplayDate(dateStr: String): String {
    return try {
        val sdfIn = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val date = sdfIn.parse(dateStr) ?: return dateStr
        val todayStr = sdfIn.format(java.util.Date())
        if (dateStr == todayStr) {
            val sdfOut = java.text.SimpleDateFormat("EEEE, MMM d", java.util.Locale.US)
            "Today, " + sdfOut.format(date)
        } else {
            val sdfOut = java.text.SimpleDateFormat("EEE, MMM d", java.util.Locale.US)
            sdfOut.format(date)
        }
    } catch (e: Exception) {
        dateStr
    }
}

private fun adjustDate(dateStr: String, days: Int): String {
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
