package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Whatshot
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.Meal
import com.example.data.model.MealType
import com.example.ui.components.MealDetailDialog
import com.example.ui.theme.CarbsAmber
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FatCyan
import com.example.ui.theme.ProteinCoral
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardStroke
import com.example.ui.theme.SlateSubtle
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CalorieViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PastMealLogsScreen(
    viewModel: CalorieViewModel,
    modifier: Modifier = Modifier
) {
    val allPastMeals by viewModel.allPastMeals.collectAsStateWithLifecycle()
    val selectedMealDetail by viewModel.selectedMealDetail.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedMealTypeFilter by remember { mutableStateOf<MealType?>(null) }

    // Filter meals by search query and meal type
    val filteredMeals = remember(allPastMeals, searchQuery, selectedMealTypeFilter) {
        allPastMeals.filter { meal ->
            val matchesType = selectedMealTypeFilter == null || meal.mealType == selectedMealTypeFilter
            val matchesQuery = searchQuery.isBlank() ||
                meal.items.any { it.name.contains(searchQuery, ignoreCase = true) } ||
                meal.mealType.displayName.contains(searchQuery, ignoreCase = true) ||
                meal.notes.contains(searchQuery, ignoreCase = true) ||
                meal.dateString.contains(searchQuery, ignoreCase = true)
            matchesType && matchesQuery
        }
    }

    // Group meals by date
    val groupedMeals = remember(filteredMeals) {
        filteredMeals.groupBy { it.dateString }
    }

    val totalCaloriesLogged = remember(filteredMeals) {
        filteredMeals.sumOf { it.totalCalories }
    }
    val avgCaloriesPerMeal = remember(filteredMeals) {
        if (filteredMeals.isNotEmpty()) totalCaloriesLogged / filteredMeals.size else 0
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 16.dp)
            .testTag("past_meal_logs_list"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Header
        item {
            Spacer(modifier = Modifier.height(4.dp))
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
                                Icons.Default.History,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Past Meal Logs",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Room database historical intake records",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EmeraldPrimary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "${filteredMeals.size} Meals",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Summary Stats Banner
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
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PastStatColumn(
                        label = "Total Logged",
                        value = "${filteredMeals.size}",
                        unit = "meals",
                        color = TextPrimary
                    )
                    PastStatDivider()
                    PastStatColumn(
                        label = "Total Energy",
                        value = "$totalCaloriesLogged",
                        unit = "kcal",
                        color = EmeraldPrimary
                    )
                    PastStatDivider()
                    PastStatColumn(
                        label = "Average / Meal",
                        value = "$avgCaloriesPerMeal",
                        unit = "kcal",
                        color = CarbsAmber
                    )
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_past_meals_input"),
                placeholder = { Text("Search by food, ingredient or date...", color = TextSecondary, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SlateCard,
                    unfocusedContainerColor = SlateCard,
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = SlateCardStroke,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }

        // Meal Type Filter Chips Row
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedMealTypeFilter == null,
                        onClick = { selectedMealTypeFilter = null },
                        label = { Text("All (${allPastMeals.size})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = SlateBackground,
                            containerColor = SlateCard,
                            labelColor = TextPrimary
                        )
                    )
                }
                items(MealType.values()) { type ->
                    val countForType = allPastMeals.count { it.mealType == type }
                    FilterChip(
                        selected = selectedMealTypeFilter == type,
                        onClick = {
                            selectedMealTypeFilter = if (selectedMealTypeFilter == type) null else type
                        },
                        leadingIcon = {
                            Text(type.iconRes, fontSize = 14.sp)
                        },
                        label = { Text("${type.displayName} ($countForType)", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = SlateBackground,
                            containerColor = SlateCard,
                            labelColor = TextPrimary
                        )
                    )
                }
            }
        }

        // Empty State
        if (filteredMeals.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SlateCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SlateBackground,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Fastfood,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || selectedMealTypeFilter != null) {
                                "No meals match the selected filters"
                            } else {
                                "No meal logs in database yet"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Capture meals using the AI Lens camera to automatically populate nutrition logs.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // Grouped By Date Items
            groupedMeals.forEach { (dateStr, mealsOnDate) ->
                item(key = "header_$dateStr") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = formatPastLogDateHeader(dateStr),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }

                        val dayCalories = mealsOnDate.sumOf { it.totalCalories }
                        Surface(
                            color = SlateCard,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke)
                        ) {
                            Text(
                                text = "$dayCalories kcal total",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                items(mealsOnDate, key = { it.id }) { meal ->
                    PastMealLogItemCard(
                        meal = meal,
                        onClick = { viewModel.showMealDetail(meal) }
                    )
                }
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
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PastMealLogItemCard(
    meal: Meal,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("past_meal_card_${meal.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Food Photo Thumbnail
                if (meal.photoUri == "sample_food_plate") {
                    Image(
                        painter = painterResource(id = R.drawable.sample_food_plate_1794152390034),
                        contentDescription = "Meal photo",
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, SlateCardStroke, RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else if (meal.photoUri != null) {
                    AsyncImage(
                        model = meal.photoUri,
                        contentDescription = "Captured meal photo",
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, SlateCardStroke, RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SlateBackground,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke),
                        modifier = Modifier.size(76.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = meal.mealType.iconRes,
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Meal Details & Calculated Nutrition Data
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = meal.mealType.displayName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            if (meal.photoUri != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = "Photo attached",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        // Calculated Calories Pill
                        Surface(
                            color = EmeraldPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "${meal.totalCalories} kcal",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Time & Date Subtitle
                    Text(
                        text = formatPastMealTimestamp(meal.timestamp, meal.dateString),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calculated Macronutrients Row (P / C / F / Fiber)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MacroBadge(label = "P", value = "${meal.totalProtein.toInt()}g", color = ProteinCoral)
                        MacroBadge(label = "C", value = "${meal.totalCarbs.toInt()}g", color = CarbsAmber)
                        MacroBadge(label = "F", value = "${meal.totalFat.toInt()}g", color = FatCyan)
                        if (meal.totalFiber > 0f) {
                            MacroBadge(label = "Fiber", value = "${String.format("%.1f", meal.totalFiber)}g", color = SuccessGreen)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = SlateCardStroke.copy(alpha = 0.6f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Food Items Tags & Portion Summary
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                meal.items.forEach { item ->
                    Surface(
                        color = SlateBackground,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, SlateCardStroke)
                    ) {
                        Text(
                            text = "${item.name} (${item.portionGrams.toInt()}g)",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            if (meal.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "“${meal.notes}”",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun MacroBadge(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

@Composable
fun PastStatColumn(label: String, value: String, unit: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = color
            )
            Spacer(modifier = Modifier.width(3.dp))
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
fun PastStatDivider() {
    Box(
        modifier = Modifier
            .height(28.dp)
            .width(1.dp)
            .background(SlateCardStroke)
    )
}

private fun formatPastLogDateHeader(dateStr: String): String {
    return try {
        val sdfIn = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = sdfIn.parse(dateStr) ?: return dateStr
        val todayStr = sdfIn.format(Date())

        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = sdfIn.format(cal.time)

        when (dateStr) {
            todayStr -> "Today • ${SimpleDateFormat("MMM d, yyyy", Locale.US).format(date)}"
            yesterdayStr -> "Yesterday • ${SimpleDateFormat("MMM d, yyyy", Locale.US).format(date)}"
            else -> SimpleDateFormat("EEEE, MMM d, yyyy", Locale.US).format(date)
        }
    } catch (e: Exception) {
        dateStr
    }
}

private fun formatPastMealTimestamp(timestamp: Long, dateStr: String): String {
    return try {
        val sdfTime = SimpleDateFormat("h:mm a", Locale.US)
        val timeFormatted = sdfTime.format(Date(timestamp))
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = sdfDate.parse(dateStr)
        val todayStr = sdfDate.format(Date())

        if (dateStr == todayStr) {
            "Today at $timeFormatted"
        } else {
            val dateFormatted = SimpleDateFormat("MMM d", Locale.US).format(date ?: Date(timestamp))
            "$dateFormatted at $timeFormatted"
        }
    } catch (e: Exception) {
        dateStr
    }
}
