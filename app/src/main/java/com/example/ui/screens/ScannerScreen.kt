package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.QuickAddIngredientDialog
import androidx.compose.material.icons.filled.Remove
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.FoodItem
import com.example.data.model.MealType
import com.example.ui.theme.CarbsAmber
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FatCyan
import com.example.ui.theme.ProteinCoral
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardStroke
import com.example.ui.theme.SlateSubtle
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CalorieViewModel
import com.example.ui.viewmodel.ScannerStep

import com.example.ui.camera.CameraXView

@Composable
fun ScannerScreen(
    viewModel: CalorieViewModel,
    modifier: Modifier = Modifier
) {
    val step by viewModel.scannerStep.collectAsStateWithLifecycle()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { viewModel.startScanWithPhotoUri(it.toString()) }
    }

    when (val currentStep = step) {
        is ScannerStep.Viewfinder -> {
            CameraXView(
                onPhotoCaptured = { uri ->
                    viewModel.startScanWithPhotoUri(uri.toString())
                },
                onSampleSelected = {
                    viewModel.startScanSampleBowl()
                },
                onGalleryClicked = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = modifier
            )
        }
        is ScannerStep.Analyzing -> {
            AnalyzingScreen(photoUri = currentStep.photoUri)
        }
        is ScannerStep.Review -> {
            ScannerReviewScreen(
                photoUri = currentStep.photoUri,
                items = currentStep.detectedItems,
                selectedMealType = currentStep.mealType,
                onPortionChanged = { id, grams -> viewModel.updateScannerPortion(id, grams) },
                onRemoveItem = { id -> viewModel.removeScannerItem(id) },
                onMealTypeChanged = { type -> viewModel.setScannerMealType(type) },
                onAddIngredient = { food -> viewModel.addIngredientToScanner(food) },
                onConfirm = { viewModel.confirmLogScannerMeal() },
                onRetake = { viewModel.resetScanner() }
            )
        }
    }
}

@Composable
fun ViewfinderScreen(
    onCaptureSample: () -> Unit,
    onPhotoPicked: (String) -> Unit,
    onVoiceClick: () -> Unit
) {
    var isFlashOn by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { onPhotoPicked(it.toString()) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Simulated Camera Viewfinder with dark subtle texture
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF141920), Color(0xFF0A0D11))
                    )
                )
        )

        // Viewfinder reticle & frame in center
        Box(
            modifier = Modifier
                .size(310.dp)
                .align(Alignment.Center)
                .border(2.dp, EmeraldPrimary.copy(alpha = 0.6f), RoundedCornerShape(28.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Reticle crosshair / guide
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = EmeraldPrimary.copy(alpha = 0.4f),
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Center your food in frame",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "AI will detect items & portion volume",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldPrimary
                )
            }
        }

        // Top HUD Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AI Plate Scanner",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            IconButton(
                onClick = { isFlashOn = !isFlashOn },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
            ) {
                Icon(
                    Icons.Default.FlashOn,
                    contentDescription = "Flash",
                    tint = if (isFlashOn) CarbsAmber else Color.White
                )
            }
        }

        // Quick action: "Try Demo Bowl" Banner
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp)
                .clickable { onCaptureSample() },
            shape = RoundedCornerShape(20.dp),
            color = SlateCard.copy(alpha = 0.92f),
            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sample_food_plate_1794152390034),
                    contentDescription = "Sample plate",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "⚡ Try Sample Food Plate",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPrimary
                    )
                    Text(
                        text = "Scan Chicken, Brown Rice & Avocado Bowl",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Bottom Camera Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp, start = 32.dp, end = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gallery Picker
            IconButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(SlateCard)
                    .testTag("gallery_button")
            ) {
                Icon(
                    Icons.Default.PhotoLibrary,
                    contentDescription = "Choose from gallery",
                    tint = Color.White
                )
            }

            // Big Glowing Shutter Button
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(EmeraldPrimary, EmeraldDark)
                        )
                    )
                    .border(4.dp, Color.White, CircleShape)
                    .clickable { onCaptureSample() }
                    .testTag("shutter_button"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary)
                )
            }

            // Voice or Camera Mode Toggle
            IconButton(
                onClick = onCaptureSample,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(SlateCard)
                    .testTag("demo_snap_button")
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Switch Camera",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun AnalyzingScreen(photoUri: String?) {
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pos"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Food Image with Laser Scan Line
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(2.dp, EmeraldPrimary, RoundedCornerShape(28.dp))
            ) {
                if (photoUri == "sample_food_plate" || photoUri == null) {
                    Image(
                        painter = painterResource(id = R.drawable.sample_food_plate_1794152390034),
                        contentDescription = "Analyzing plate",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Analyzing plate",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Laser scan bar with animated vertical translation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .padding(top = (320 * laserY).dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, EmeraldPrimary, Color.White, EmeraldPrimary, Color.Transparent)
                            )
                        )
                )

                // Pulsing AI detected food tags over plate
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "🍗 Grilled Chicken • 🍚 Brown Rice",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                color = EmeraldPrimary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AI Analyzing Plate & Depth...",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Identifying food items, volume & USDA nutrients",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerReviewScreen(
    photoUri: String?,
    items: List<FoodItem>,
    selectedMealType: MealType,
    onPortionChanged: (String, Float) -> Unit,
    onRemoveItem: (String) -> Unit,
    onMealTypeChanged: (MealType) -> Unit,
    onAddIngredient: (com.example.data.ai.FoodDatabase.FoodReference) -> Unit,
    onConfirm: () -> Unit,
    onRetake: () -> Unit
) {
    var showAddIngredientPicker by remember { mutableStateOf(false) }
    val totalCalories = items.sumOf { it.calories }
    val totalProtein = items.sumOf { it.protein.toDouble() }.toFloat()
    val totalCarbs = items.sumOf { it.carbs.toDouble() }.toFloat()
    val totalFat = items.sumOf { it.fat.toDouble() }.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .statusBarsPadding()
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onRetake) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retake", tint = TextPrimary)
            }
            Text(
                text = "Detected Meal Review",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            IconButton(onClick = onRetake) {
                Icon(Icons.Default.Refresh, contentDescription = "Scan Again", tint = TextSecondary)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Food Photo Banner with Badges
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, SlateCardStroke, RoundedCornerShape(20.dp))
                ) {
                    if (photoUri == "sample_food_plate") {
                        Image(
                            painter = painterResource(id = R.drawable.sample_food_plate_1794152390034),
                            contentDescription = "Meal photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (photoUri != null) {
                        AsyncImage(
                            model = photoUri,
                            contentDescription = "Meal photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Overlay banner
                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✨ High AI Confidence (96%)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary
                            )
                            Text(
                                text = "${items.size} Items Identified",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Total Nutrition Card
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
                            Column {
                                Text(
                                    text = "Total Estimated Energy",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "$totalCalories kcal",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldPrimary
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                MacroPill(label = "P", value = "${totalProtein.toInt()}g", color = ProteinCoral)
                                MacroPill(label = "C", value = "${totalCarbs.toInt()}g", color = CarbsAmber)
                                MacroPill(label = "F", value = "${totalFat.toInt()}g", color = FatCyan)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Meal Type Selection Chips
                        Text(
                            text = "Log as:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MealType.values().forEach { type ->
                                val isSelected = selectedMealType == type
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onMealTypeChanged(type) },
                                    label = { Text(type.displayName) },
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

            // Detected Ingredients Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Ingredients & Portions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Adjust grams or add missing foods",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }

                    Surface(
                        color = EmeraldPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { showAddIngredientPicker = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
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
            }

            // Detected Items List with Portion Sliders
            items(items) { item ->
                IngredientAdjustmentCard(
                    item = item,
                    onPortionChanged = { newGrams -> onPortionChanged(item.id, newGrams) },
                    onDelete = { onRemoveItem(item.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom Confirm Button
        Surface(
            color = SlateSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardStroke),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("confirm_log_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = SlateBackground
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log Meal ($totalCalories kcal)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
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
fun IngredientAdjustmentCard(
    item: FoodItem,
    onPortionChanged: (Float) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardStroke))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary
                    )
                    Text(
                        text = "${item.calories} kcal • P: ${item.protein.toInt()}g | C: ${item.carbs.toInt()}g | F: ${item.fat.toInt()}g",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = SlateSubtle.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${item.portionGrams.toInt()}g",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Grams Slider with -10g and +10g fine-tuning buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = CircleShape,
                    color = SlateSubtle.copy(alpha = 0.5f),
                    modifier = Modifier.size(28.dp).clickable {
                        onPortionChanged((item.portionGrams - 10f).coerceAtLeast(10f))
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Remove, contentDescription = "-10g", tint = TextPrimary, modifier = Modifier.size(14.dp))
                    }
                }

                Slider(
                    value = item.portionGrams,
                    onValueChange = { onPortionChanged(it) },
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

                Surface(
                    shape = CircleShape,
                    color = SlateSubtle.copy(alpha = 0.5f),
                    modifier = Modifier.size(28.dp).clickable {
                        onPortionChanged(item.portionGrams + 10f)
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = "+10g", tint = TextPrimary, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MacroPill(label: String, value: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label: ",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = TextPrimary
            )
        }
    }
}
