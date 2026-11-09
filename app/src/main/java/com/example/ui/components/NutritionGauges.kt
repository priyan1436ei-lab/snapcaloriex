package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CarbsAmber
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FatCyan
import com.example.ui.theme.ProteinCoral
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardStroke
import com.example.ui.theme.SlateSubtle
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CalorieRingCard(
    calorieGoal: Int,
    eatenCalories: Int,
    burnedCalories: Int = 0,
    modifier: Modifier = Modifier
) {
    val remaining = (calorieGoal - eatenCalories + burnedCalories).coerceAtLeast(0)
    val progress = if (calorieGoal > 0) (eatenCalories.toFloat() / calorieGoal.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000),
        label = "calorie_progress"
    )

    val gradientBrush = remember {
        androidx.compose.ui.graphics.Brush.sweepGradient(
            listOf(
                com.example.ui.theme.EmeraldDark,
                com.example.ui.theme.EmeraldPrimary,
                com.example.ui.theme.MintGlow,
                com.example.ui.theme.EmeraldDark
            )
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                listOf(SlateCardStroke.copy(alpha = 0.9f), SlateCardStroke.copy(alpha = 0.3f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                Canvas(modifier = Modifier.size(175.dp)) {
                    val strokeWidth = 15.dp.toPx()
                    // Track circle
                    drawCircle(
                        color = SlateSubtle.copy(alpha = 0.35f),
                        style = Stroke(width = strokeWidth)
                    )
                    // Progress arc with glowing gradient
                    drawArc(
                        brush = gradientBrush,
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$remaining",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 42.sp,
                            letterSpacing = (-1).sp
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = "kcal remaining",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = EmeraldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CalorieMetricItem(label = "Daily Goal", value = "$calorieGoal", color = TextPrimary)
                CalorieMetricItem(label = "Food", value = "$eatenCalories", color = EmeraldPrimary)
                CalorieMetricItem(label = "Exercise", value = "$burnedCalories", color = CarbsAmber)
            }
        }
    }
}

@Composable
fun CalorieMetricItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

@Composable
fun MacroProgressBar(
    name: String,
    currentGrams: Float,
    targetGrams: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (targetGrams > 0) (currentGrams / targetGrams).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800),
        label = "macro_progress"
    )

    Column(modifier = modifier) {
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
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary
                )
            }
            Text(
                text = "${currentGrams.toInt()} / ${targetGrams}g",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = SlateSubtle.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun MacroSummaryRow(
    proteinCurrent: Float,
    proteinTarget: Int,
    carbsCurrent: Float,
    carbsTarget: Int,
    fatCurrent: Float,
    fatTarget: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MacroProgressBar(
                name = "Protein",
                currentGrams = proteinCurrent,
                targetGrams = proteinTarget,
                color = ProteinCoral,
                modifier = Modifier.weight(1f)
            )
            MacroProgressBar(
                name = "Carbs",
                currentGrams = carbsCurrent,
                targetGrams = carbsTarget,
                color = CarbsAmber,
                modifier = Modifier.weight(1f)
            )
            MacroProgressBar(
                name = "Fat",
                currentGrams = fatCurrent,
                targetGrams = fatTarget,
                color = FatCyan,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
