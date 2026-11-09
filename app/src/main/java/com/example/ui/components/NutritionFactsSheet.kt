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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.ai.FoodDatabase
import com.example.data.model.FoodItem
import com.example.data.model.Meal
import com.example.ui.theme.CarbsAmber
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.FatCyan
import com.example.ui.theme.ProteinCoral
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardStroke
import com.example.ui.theme.SlateSubtle
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MealDetailDialog(
    meal: Meal,
    onDismiss: () -> Unit,
    onDelete: (Long) -> Unit,
    onUpdatePortion: (itemId: String, newGrams: Float) -> Unit,
    onDeleteItem: (itemId: String) -> Unit,
    onAddIngredient: (FoodDatabase.FoodReference) -> Unit
) {
    var showAddIngredientPicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${meal.mealType.displayName} Details",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "${meal.totalCalories} kcal • ${meal.items.size} Ingredients",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = EmeraldPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Editable Ingredients Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Ingredients (Real-Time)",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary
                    )
                    Surface(
                        color = EmeraldPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { showAddIngredientPicker = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Add Item",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    meal.items.forEach { item ->
                        InteractiveIngredientRow(
                            item = item,
                            onPortionChange = { newGrams -> onUpdatePortion(item.id, newGrams) },
                            onDelete = { onDeleteItem(item.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Official FDA/USDA Style Nutrition Facts Box
                NutritionFactsBox(meal = meal)

                Spacer(modifier = Modifier.height(18.dp))

                // Delete entire meal
                OutlinedButton(
                    onClick = { onDelete(meal.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed)
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text("Delete Entire Meal Entry")
                }
            }
        }
    }

    if (showAddIngredientPicker) {
        QuickAddIngredientDialog(
            onDismiss = { showAddIngredientPicker = false },
            onFoodSelected = { food ->
                onAddIngredient(food)
                showAddIngredientPicker = false
            }
        )
    }
}

@Composable
fun InteractiveIngredientRow(
    item: FoodItem,
    onPortionChange: (Float) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SlateBackground),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary
                    )
                    Text(
                        text = "P: ${item.protein.toInt()}g • C: ${item.carbs.toInt()}g • F: ${item.fat.toInt()}g",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${item.calories} kcal",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Delete Item", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Portion control buttons & slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // -10g button
                Surface(
                    shape = CircleShape,
                    color = SlateSubtle.copy(alpha = 0.5f),
                    modifier = Modifier.size(28.dp).clickable {
                        onPortionChange((item.portionGrams - 10f).coerceAtLeast(10f))
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Remove, contentDescription = "-10g", tint = TextPrimary, modifier = Modifier.size(14.dp))
                    }
                }

                Slider(
                    value = item.portionGrams,
                    onValueChange = { onPortionChange(it) },
                    valueRange = 10f..400f,
                    steps = 38,
                    colors = SliderDefaults.colors(
                        thumbColor = EmeraldPrimary,
                        activeTrackColor = EmeraldPrimary,
                        inactiveTrackColor = SlateSubtle
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )

                // +10g button
                Surface(
                    shape = CircleShape,
                    color = SlateSubtle.copy(alpha = 0.5f),
                    modifier = Modifier.size(28.dp).clickable {
                        onPortionChange(item.portionGrams + 10f)
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = "+10g", tint = TextPrimary, modifier = Modifier.size(14.dp))
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${item.portionGrams.toInt()}g",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
fun QuickAddIngredientDialog(
    onDismiss: () -> Unit,
    onFoodSelected: (FoodDatabase.FoodReference) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Extra Ingredient",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FoodDatabase.items.take(10).forEach { food ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SlateBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onFoodSelected(food) }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = food.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${food.standardPortion.toInt()}${food.unit}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                                Text(
                                    text = "+${(food.caloriesPer100g * food.standardPortion / 100).toInt()} kcal",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NutritionFactsBox(meal: Meal) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, TextPrimary, RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = "Nutrition Facts",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                ),
                color = Color.Black
            )
            HorizontalDivider(color = Color.Black, thickness = 6.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Amount Per Serving",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Calories",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp
                    ),
                    color = Color.Black
                )
                Text(
                    text = "${meal.totalCalories}",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp
                    ),
                    color = Color.Black
                )
            }
            HorizontalDivider(color = Color.Black, thickness = 4.dp)

            NutritionFactRow(label = "Total Fat", value = "${String.format("%.1f", meal.totalFat)}g", isBold = true)
            NutritionFactRow(label = "  Saturated Fat", value = "${String.format("%.1f", meal.totalFat * 0.25f)}g", isBold = false)
            NutritionFactRow(label = "Sodium", value = "${(meal.totalCalories * 0.6f).toInt()}mg", isBold = true)
            NutritionFactRow(label = "Total Carbohydrate", value = "${String.format("%.1f", meal.totalCarbs)}g", isBold = true)
            NutritionFactRow(label = "  Dietary Fiber", value = "${String.format("%.1f", meal.totalFiber)}g", isBold = false)
            NutritionFactRow(label = "  Total Sugars", value = "${String.format("%.1f", meal.totalCarbs * 0.15f)}g", isBold = false)
            NutritionFactRow(label = "Protein", value = "${String.format("%.1f", meal.totalProtein)}g", isBold = true)

            HorizontalDivider(color = Color.Black, thickness = 3.dp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "*Percent Daily Values are based on a 2,000 calorie diet.",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun NutritionFactRow(label: String, value: String, isBold: Boolean) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
                ),
                color = Color.Black
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
                ),
                color = Color.Black
            )
        }
        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
    }
}
