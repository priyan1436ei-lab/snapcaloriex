package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.unit.sp
import com.example.ui.components.SetPersonalizedGoalsDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import android.app.Activity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CarbsAmber
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FatCyan
import com.example.ui.theme.ProteinCoral
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardStroke
import com.example.ui.theme.SlateSubtle
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CalorieViewModel

@Composable
fun ProfileGoalsScreen(
    viewModel: CalorieViewModel,
    modifier: Modifier = Modifier
) {
    val userGoals by viewModel.userGoals.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showCalculatorDialog by remember { mutableStateOf(false) }

    var selectedGoalType by remember(userGoals) { mutableStateOf(userGoals.goalType) }
    var targetCalories by remember(userGoals) { mutableIntStateOf(userGoals.dailyCalories) }
    var targetProtein by remember(userGoals) { mutableIntStateOf(userGoals.proteinGrams) }
    var targetCarbs by remember(userGoals) { mutableIntStateOf(userGoals.carbsGrams) }
    var targetFat by remember(userGoals) { mutableIntStateOf(userGoals.fatGrams) }
    var targetWater by remember(userGoals) { mutableIntStateOf(userGoals.waterGoalMl) }
    var targetWeight by remember(userGoals) { mutableFloatStateOf(userGoals.targetWeightKg) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = EmeraldPrimary.copy(alpha = 0.2f),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Nutrition & Goals",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Personalized energy & macro targets",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SlateCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.clickable { showCalculatorDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Smart Wizard", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = EmeraldPrimary)
                    }
                }
            }
        }

        // Firebase Google Account & Firestore Cloud Sync Card
        item {
            val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
            val coroutineScope = rememberCoroutineScope()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("firebase_account_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(EmeraldPrimary.copy(alpha = 0.4f))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (currentUser != null) EmeraldPrimary.copy(alpha = 0.2f) else SlateSubtle.copy(alpha = 0.4f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (currentUser != null) Icons.Default.Check else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (currentUser != null) EmeraldPrimary else TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (currentUser != null) (currentUser?.displayName ?: "Google User") else "Local Offline Account",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = if (currentUser != null) "Cloud Sync Active • Firestore" else "Tap below to connect Google Account",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (currentUser != null) EmeraldPrimary else TextSecondary
                            )
                        }
                    }

                    if (currentUser != null) {
                        Button(
                            onClick = { viewModel.authManager.signOut(coroutineScope) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SlateSubtle.copy(alpha = 0.5f),
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Sign out", style = MaterialTheme.typography.labelSmall)
                        }
                    } else {
                        Button(
                            onClick = {
                                val activity = context as? Activity
                                if (activity != null) {
                                    viewModel.authManager.signInWithGoogle(activity, coroutineScope)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldPrimary,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Sign in", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }

        // Primary Goal Type
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Current Fitness Objective",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val goalsList = listOf("Fat Loss", "Maintenance", "Muscle Gain", "High Protein", "Low Carb")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        goalsList.take(3).forEach { goal ->
                            FilterChip(
                                selected = selectedGoalType == goal,
                                onClick = {
                                    selectedGoalType = goal
                                    when (goal) {
                                        "Fat Loss" -> {
                                            targetCalories = 1850
                                            targetProtein = 150
                                            targetCarbs = 165
                                            targetFat = 55
                                        }
                                        "Maintenance" -> {
                                            targetCalories = 2150
                                            targetProtein = 145
                                            targetCarbs = 215
                                            targetFat = 65
                                        }
                                        "Muscle Gain" -> {
                                            targetCalories = 2500
                                            targetProtein = 175
                                            targetCarbs = 260
                                            targetFat = 75
                                        }
                                    }
                                },
                                label = { Text(goal, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = SlateBackground
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        goalsList.drop(3).forEach { goal ->
                            FilterChip(
                                selected = selectedGoalType == goal,
                                onClick = {
                                    selectedGoalType = goal
                                    when (goal) {
                                        "High Protein" -> {
                                            targetCalories = 2300
                                            targetProtein = 190
                                            targetCarbs = 205
                                            targetFat = 60
                                        }
                                        "Low Carb" -> {
                                            targetCalories = 1900
                                            targetProtein = 135
                                            targetCarbs = 45
                                            targetFat = 125
                                        }
                                    }
                                },
                                label = { Text(goal, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = SlateBackground
                                )
                            )
                        }
                    }
                }
            }
        }

        // Daily Calorie Target
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Calorie Target",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "$targetCalories kcal",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Slider(
                        value = targetCalories.toFloat(),
                        onValueChange = { targetCalories = it.toInt() },
                        valueRange = 1200f..4000f,
                        steps = 55,
                        colors = SliderDefaults.colors(
                            thumbColor = EmeraldPrimary,
                            activeTrackColor = EmeraldPrimary,
                            inactiveTrackColor = SlateSubtle
                        )
                    )
                }
            }
        }

        // Macronutrient Split Sliders
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Macronutrient Targets",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Protein
                    MacroTargetSlider(
                        name = "Protein",
                        value = targetProtein,
                        color = ProteinCoral,
                        range = 50f..250f,
                        onValueChange = { targetProtein = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Carbs
                    MacroTargetSlider(
                        name = "Carbohydrates",
                        value = targetCarbs,
                        color = CarbsAmber,
                        range = 50f..400f,
                        onValueChange = { targetCarbs = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Fat
                    MacroTargetSlider(
                        name = "Fat",
                        value = targetFat,
                        color = FatCyan,
                        range = 20f..150f,
                        onValueChange = { targetFat = it }
                    )
                }
            }
        }

        // Water Goal
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Hydration Goal",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "$targetWater ml",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )
                    }

                    Slider(
                        value = targetWater.toFloat(),
                        onValueChange = { targetWater = it.toInt() },
                        valueRange = 1000f..5000f,
                        steps = 15,
                        colors = SliderDefaults.colors(
                            thumbColor = EmeraldPrimary,
                            activeTrackColor = EmeraldPrimary,
                            inactiveTrackColor = SlateSubtle
                        )
                    )
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    viewModel.updateGoals(
                        calories = targetCalories,
                        protein = targetProtein,
                        carbs = targetCarbs,
                        fat = targetFat,
                        waterMl = targetWater,
                        targetWeight = targetWeight,
                        goalType = selectedGoalType
                    )
                    Toast.makeText(context, "Nutrition goals updated!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_goals_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = SlateBackground
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Goals",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(90.dp))
        }
    }

    if (showCalculatorDialog) {
        SetPersonalizedGoalsDialog(
            currentGoals = userGoals,
            onDismiss = { showCalculatorDialog = false },
            onSaveGoals = { cals, p, c, f, water, weight, objective ->
                viewModel.updateGoals(cals, p, c, f, water, weight, objective)
                showCalculatorDialog = false
            }
        )
    }
}

@Composable
fun MacroTargetSlider(
    name: String,
    value: Int,
    color: androidx.compose.ui.graphics.Color,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = color
            )
            Text(
                text = "${value}g",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = SlateSubtle
            )
        )
    }
}
