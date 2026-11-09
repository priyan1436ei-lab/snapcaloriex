package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CarbsAmber
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FatCyan
import com.example.ui.theme.ProteinCoral
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardStroke
import com.example.ui.theme.SlateSubtle
import com.example.ui.theme.StreakOrange
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CalorieViewModel

@Composable
fun AnalyticsScreen(
    viewModel: CalorieViewModel,
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Past Meal Logs (Room DB list), 1: 7-Day Trends

    val dailySummary by viewModel.dailySummary.collectAsStateWithLifecycle()
    val userGoals by viewModel.userGoals.collectAsStateWithLifecycle()
    val allPastMeals by viewModel.allPastMeals.collectAsStateWithLifecycle()

    val calorieDeficit = (userGoals.dailyCalories - dailySummary.totalCalories).coerceAtLeast(0)

    val totalMacroGrams = (dailySummary.totalProtein + dailySummary.totalCarbs + dailySummary.totalFat).coerceAtLeast(1f)
    val proteinPercent = ((dailySummary.totalProtein / totalMacroGrams) * 100).toInt()
    val carbsPercent = ((dailySummary.totalCarbs / totalMacroGrams) * 100).toInt()
    val fatPercent = ((dailySummary.totalFat / totalMacroGrams) * 100).toInt()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        // Top SubTab Switcher (Past Logs vs Trends)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            TabRow(
                selectedTabIndex = activeSubTab,
                containerColor = SlateCard,
                contentColor = EmeraldPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, SlateCardStroke, RoundedCornerShape(14.dp)),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                        color = EmeraldPrimary,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = activeSubTab == 0,
                    onClick = { activeSubTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                tint = if (activeSubTab == 0) EmeraldPrimary else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Past Meal Logs",
                                fontWeight = if (activeSubTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (activeSubTab == 0) EmeraldPrimary else TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                )
                Tab(
                    selected = activeSubTab == 1,
                    onClick = { activeSubTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AutoGraph,
                                contentDescription = null,
                                tint = if (activeSubTab == 1) EmeraldPrimary else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "7-Day Trends",
                                fontWeight = if (activeSubTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (activeSubTab == 1) EmeraldPrimary else TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                )
            }
        }

        if (activeSubTab == 0) {
            PastMealLogsScreen(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SlateBackground)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Nutrition Analytics",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Text(
                text = "7-Day trends, macro split & diet quality",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        // Diet Quality Score Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = CarbsAmber, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Diet Quality Grade",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "High Protein density & great micronutrient diversity. Excellent satiety score!",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.2f))
                            .border(2.dp, EmeraldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A-",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28.sp
                            ),
                            color = EmeraldPrimary
                        )
                    }
                }
            }
        }

        // 7-Day Calorie Intake Bar Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Calorie History (Last 7 Days)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Goal: ${userGoals.dailyCalories} kcal",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldPrimary
                        )
                    }

                    val calendar = java.util.Calendar.getInstance()
                    val sdfDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                    val sdfDay = java.text.SimpleDateFormat("EEE", java.util.Locale.US)
                    val todayStr = sdfDate.format(calendar.time)

                    val weekDays = (6 downTo 0).map { daysAgo ->
                        val cal = java.util.Calendar.getInstance()
                        cal.add(java.util.Calendar.DAY_OF_YEAR, -daysAgo)
                        val dateKey = sdfDate.format(cal.time)
                        val label = if (daysAgo == 0) "Today" else sdfDay.format(cal.time)
                        val calories = if (dateKey == todayStr) {
                            dailySummary.totalCalories
                        } else {
                            allPastMeals.filter { it.dateString == dateKey }.sumOf { it.totalCalories }
                        }
                        Pair(label, calories)
                    }

                    val maxCal = (weekDays.maxOfOrNull { it.second } ?: 2000).coerceAtLeast(userGoals.dailyCalories).coerceAtLeast(1)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        weekDays.forEach { (day, calories) ->
                            val heightFraction = (calories.toFloat() / maxCal.toFloat()).coerceIn(0.08f, 1f)
                            val isToday = day == "Today"
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "$calories",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = if (isToday) EmeraldPrimary else TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .height((100 * heightFraction).dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isToday) EmeraldPrimary else SlateSubtle
                                        )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isToday) TextPrimary else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Macronutrient Ratio Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Macronutrient Distribution",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Multi-color segmented progress bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .weight((proteinPercent.coerceAtLeast(10)).toFloat())
                                .height(14.dp)
                                .background(ProteinCoral)
                        )
                        Box(
                            modifier = Modifier
                                .weight((carbsPercent.coerceAtLeast(10)).toFloat())
                                .height(14.dp)
                                .background(CarbsAmber)
                        )
                        Box(
                            modifier = Modifier
                                .weight((fatPercent.coerceAtLeast(10)).toFloat())
                                .height(14.dp)
                                .background(FatCyan)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MacroLegendItem(name = "Protein", percent = "$proteinPercent%", color = ProteinCoral)
                        MacroLegendItem(name = "Carbs", percent = "$carbsPercent%", color = CarbsAmber)
                        MacroLegendItem(name = "Fat", percent = "$fatPercent%", color = FatCyan)
                    }
                }
            }
        }

        // Goal & Calorie Deficit Projection Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Weight & Deficit Projection",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Current Weight: ${userGoals.currentWeightKg} kg • Target: ${userGoals.targetWeightKg} kg",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "With a daily ~500 kcal deficit, you are projected to reach ${userGoals.targetWeightKg} kg in 6 weeks.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
}
}

@Composable
fun MacroLegendItem(name: String, percent: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(text = name, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(text = percent, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
        }
    }
}
