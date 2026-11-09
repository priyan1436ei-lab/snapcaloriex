package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserGoals
import com.example.ui.theme.CarbsAmber
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FatCyan
import com.example.ui.theme.ProteinCoral
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardStroke
import com.example.ui.theme.SlateSubtle
import com.example.ui.theme.StreakOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WaterBlue

@Composable
fun SetPersonalizedGoalsDialog(
    currentGoals: UserGoals,
    onDismiss: () -> Unit,
    onSaveGoals: (calories: Int, protein: Int, carbs: Int, fat: Int, waterMl: Int, targetWeightKg: Float, goalType: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Quick Targets & Presets, 1: Smart BMR/TDEE Calculator

    // Goal values
    var selectedGoalType by remember { mutableStateOf(currentGoals.goalType) }
    var calories by remember { mutableIntStateOf(currentGoals.dailyCalories) }
    var protein by remember { mutableIntStateOf(currentGoals.proteinGrams) }
    var carbs by remember { mutableIntStateOf(currentGoals.carbsGrams) }
    var fat by remember { mutableIntStateOf(currentGoals.fatGrams) }
    var waterMl by remember { mutableIntStateOf(currentGoals.waterGoalMl) }
    var targetWeightKg by remember { mutableFloatStateOf(currentGoals.targetWeightKg) }

    // Smart Calculator fields
    var calcWeightKg by remember { mutableStateOf(currentGoals.currentWeightKg.toString()) }
    var calcHeightCm by remember { mutableStateOf("175") }
    var calcAgeYears by remember { mutableStateOf("28") }
    var calcIsMale by remember { mutableStateOf(true) }
    var calcActivityIndex by remember { mutableIntStateOf(1) } // 0: Sedentary, 1: Light, 2: Moderate, 3: Very Active

    // Computed macro calories
    val macroTotalKcal = (protein * 4) + (carbs * 4) + (fat * 9)
    val proteinPercent = if (macroTotalKcal > 0) ((protein * 4f / macroTotalKcal) * 100).toInt() else 0
    val carbsPercent = if (macroTotalKcal > 0) ((carbs * 4f / macroTotalKcal) * 100).toInt() else 0
    val fatPercent = if (macroTotalKcal > 0) ((fat * 9f / macroTotalKcal) * 100).toInt() else 0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(28.dp))
                .border(1.dp, SlateCardStroke, RoundedCornerShape(28.dp)),
            color = SlateCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldPrimary.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Personalize Targets",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "Set tailored daily calorie & macro goals",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SlateBackground,
                    contentColor = EmeraldPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp)),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = EmeraldPrimary,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "Target Sliders",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) EmeraldPrimary else TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = if (selectedTab == 1) EmeraldPrimary else TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Smart Calculator",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == 1) EmeraldPrimary else TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // Quick Presets
                    Text(
                        text = "Fitness Objective Preset",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val presets = listOf(
                        GoalPreset("Fat Loss", 1850, 150, 165, 55, "Deficit for steady fat loss"),
                        GoalPreset("Maintenance", 2150, 145, 215, 65, "Energy balance & vitality"),
                        GoalPreset("Muscle Gain", 2500, 175, 260, 75, "Hypertrophy lean bulk"),
                        GoalPreset("High Protein", 2300, 190, 205, 60, "Athletic muscle retention"),
                        GoalPreset("Low Carb", 1900, 135, 45, 125, "Ketogenic / Low-carb burn")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presets.take(3).forEach { preset ->
                            FilterChip(
                                selected = selectedGoalType == preset.name,
                                onClick = {
                                    selectedGoalType = preset.name
                                    calories = preset.calories
                                    protein = preset.protein
                                    carbs = preset.carbs
                                    fat = preset.fat
                                },
                                label = { Text(preset.name, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = SlateBackground,
                                    containerColor = SlateBackground,
                                    labelColor = TextPrimary
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presets.drop(3).forEach { preset ->
                            FilterChip(
                                selected = selectedGoalType == preset.name,
                                onClick = {
                                    selectedGoalType = preset.name
                                    calories = preset.calories
                                    protein = preset.protein
                                    carbs = preset.carbs
                                    fat = preset.fat
                                },
                                label = { Text(preset.name, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = SlateBackground,
                                    containerColor = SlateBackground,
                                    labelColor = TextPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Daily Calories Slider & Text
                    GoalTargetControl(
                        label = "Daily Calorie Target",
                        value = calories,
                        unit = "kcal",
                        color = EmeraldPrimary,
                        min = 1200,
                        max = 4500,
                        step = 25,
                        onValueChange = { calories = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Protein Slider & Text
                    GoalTargetControl(
                        label = "Daily Protein Target",
                        value = protein,
                        unit = "g",
                        color = ProteinCoral,
                        min = 40,
                        max = 280,
                        step = 5,
                        subtitle = "${protein * 4} kcal • $proteinPercent% energy",
                        onValueChange = { protein = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Carbs Slider & Text
                    GoalTargetControl(
                        label = "Daily Carbohydrates Target",
                        value = carbs,
                        unit = "g",
                        color = CarbsAmber,
                        min = 20,
                        max = 450,
                        step = 5,
                        subtitle = "${carbs * 4} kcal • $carbsPercent% energy",
                        onValueChange = { carbs = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Fat Slider & Text
                    GoalTargetControl(
                        label = "Daily Fat Target",
                        value = fat,
                        unit = "g",
                        color = FatCyan,
                        min = 20,
                        max = 160,
                        step = 2,
                        subtitle = "${fat * 9} kcal • $fatPercent% energy",
                        onValueChange = { fat = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Water Goal
                    GoalTargetControl(
                        label = "Daily Water Goal",
                        value = waterMl,
                        unit = "ml",
                        color = WaterBlue,
                        min = 1000,
                        max = 5000,
                        step = 100,
                        subtitle = "${String.format("%.1f", waterMl / 1000f)} liters / day",
                        onValueChange = { waterMl = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Macro balance summary card
                    Surface(
                        color = SlateBackground,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Macro Energy Split",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "$macroTotalKcal / $calories kcal",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (kotlin.math.abs(macroTotalKcal - calories) <= 50) EmeraldPrimary else StreakOrange
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight((proteinPercent.coerceAtLeast(5)).toFloat())
                                        .height(8.dp)
                                        .background(ProteinCoral)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight((carbsPercent.coerceAtLeast(5)).toFloat())
                                        .height(8.dp)
                                        .background(CarbsAmber)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight((fatPercent.coerceAtLeast(5)).toFloat())
                                        .height(8.dp)
                                        .background(FatCyan)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("P: $proteinPercent%", style = MaterialTheme.typography.labelSmall, color = ProteinCoral)
                                Text("C: $carbsPercent%", style = MaterialTheme.typography.labelSmall, color = CarbsAmber)
                                Text("F: $fatPercent%", style = MaterialTheme.typography.labelSmall, color = FatCyan)
                            }
                        }
                    }
                } else {
                    // Smart BMR & TDEE Calculator Tab
                    Text(
                        text = "Personal Body Telemetry",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Uses clinical Mifflin-St Jeor metabolic equations to establish your exact maintenance and goal targets.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Weight & Height
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = calcWeightKg,
                            onValueChange = { calcWeightKg = it },
                            label = { Text("Weight (kg)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = SlateCardStroke,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        OutlinedTextField(
                            value = calcHeightCm,
                            onValueChange = { calcHeightCm = it },
                            label = { Text("Height (cm)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = SlateCardStroke,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Age & Sex
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = calcAgeYears,
                            onValueChange = { calcAgeYears = it },
                            label = { Text("Age (yrs)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = SlateCardStroke,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Row(modifier = Modifier.weight(1f)) {
                            FilterChip(
                                selected = calcIsMale,
                                onClick = { calcIsMale = true },
                                label = { Text("Male", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = SlateBackground,
                                    containerColor = SlateBackground,
                                    labelColor = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            FilterChip(
                                selected = !calcIsMale,
                                onClick = { calcIsMale = false },
                                label = { Text("Female", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = SlateBackground,
                                    containerColor = SlateBackground,
                                    labelColor = TextPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Activity Level",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val activityLevels = listOf(
                        "Sedentary (Desk work, little exercise)",
                        "Light (1-3 workouts / week)",
                        "Moderate (3-5 workouts / week)",
                        "Very Active (6+ intense workouts)"
                    )
                    activityLevels.forEachIndexed { index, levelDesc ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (calcActivityIndex == index) EmeraldPrimary.copy(alpha = 0.15f) else SlateBackground,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (calcActivityIndex == index) EmeraldPrimary else SlateCardStroke
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable { calcActivityIndex = index }
                        ) {
                            Text(
                                text = levelDesc,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (calcActivityIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (calcActivityIndex == index) EmeraldPrimary else TextPrimary
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val w = calcWeightKg.toFloatOrNull() ?: 72f
                            val h = calcHeightCm.toFloatOrNull() ?: 175f
                            val a = calcAgeYears.toIntOrNull() ?: 28
                            val activityMultiplier = when (calcActivityIndex) {
                                0 -> 1.2f
                                1 -> 1.375f
                                2 -> 1.55f
                                else -> 1.725f
                            }
                            // BMR = 10*W + 6.25*H - 5*A + (5 for male / -161 for female)
                            val bmr = (10f * w) + (6.25f * h) - (5f * a) + (if (calcIsMale) 5f else -161f)
                            val tdee = bmr * activityMultiplier

                            val calculatedCals = when (selectedGoalType) {
                                "Fat Loss" -> (tdee - 450f).toInt().coerceAtLeast(1300)
                                "Muscle Gain" -> (tdee + 350f).toInt()
                                "High Protein" -> (tdee + 150f).toInt()
                                "Low Carb" -> (tdee - 300f).toInt().coerceAtLeast(1300)
                                else -> tdee.toInt()
                            }

                            // Macros based on objective & weight
                            val calculatedProtein = (w * 2.1f).toInt().coerceIn(80, 240)
                            val calculatedFat = ((calculatedCals * 0.25f) / 9f).toInt().coerceIn(35, 120)
                            val remainingCals = (calculatedCals - (calculatedProtein * 4) - (calculatedFat * 9)).coerceAtLeast(100)
                            val calculatedCarbs = (remainingCals / 4).coerceAtLeast(30)

                            calories = calculatedCals
                            protein = calculatedProtein
                            fat = calculatedFat
                            carbs = calculatedCarbs
                            waterMl = (w * 35f).toInt().coerceIn(2000, 4500)
                            targetWeightKg = w

                            // Switch to review tab
                            selectedTab = 0
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = SlateBackground
                        )
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calculate & Apply Body Targets", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Save Action
                Button(
                    onClick = {
                        onSaveGoals(calories, protein, carbs, fat, waterMl, targetWeightKg, selectedGoalType)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("apply_personalized_goals_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = SlateBackground
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Personalized Targets",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun GoalTargetControl(
    label: String,
    value: Int,
    unit: String,
    color: Color,
    min: Int,
    max: Int,
    step: Int,
    subtitle: String? = null,
    onValueChange: (Int) -> Unit
) {
    Surface(
        color = SlateBackground,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = color
                        )
                    }
                }
                Surface(
                    color = color.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "$value $unit",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = color,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(((it / step).toInt()) * step) },
                valueRange = min.toFloat()..max.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = color,
                    activeTrackColor = color,
                    inactiveTrackColor = SlateSubtle.copy(alpha = 0.6f)
                )
            )
        }
    }
}

private data class GoalPreset(
    val name: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val description: String
)
