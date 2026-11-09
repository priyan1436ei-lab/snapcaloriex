package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DailyNutritionSummaryScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ProfileGoalsScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateCardStroke
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CalorieViewModel
import com.example.ui.viewmodel.ScannerStep

class MainActivity : ComponentActivity() {
    private val viewModel: CalorieViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                CalorieLensApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CalorieLensApp(viewModel: CalorieViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var continueAsGuest by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    if (currentUser == null && !continueAsGuest) {
        com.example.ui.screens.AuthGateScreen(
            viewModel = viewModel,
            onContinueAsGuest = { continueAsGuest = true }
        )
        return
    }

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val scannerStep by viewModel.scannerStep.collectAsStateWithLifecycle()

    // Handle system back navigation
    BackHandler(enabled = currentTab != 0 || scannerStep !is ScannerStep.Viewfinder) {
        if (scannerStep !is ScannerStep.Viewfinder) {
            viewModel.resetScanner()
        } else {
            viewModel.setTab(0)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (scannerStep is ScannerStep.Viewfinder || currentTab != 1) {
                CalorieLensBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { index ->
                        if (index != 1 && scannerStep !is ScannerStep.Viewfinder) {
                            viewModel.resetScanner()
                        }
                        viewModel.setTab(index)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (currentTab) {
                0 -> DashboardScreen(viewModel = viewModel)
                1 -> ScannerScreen(viewModel = viewModel)
                2 -> DailyNutritionSummaryScreen(viewModel = viewModel)
                3 -> AnalyticsScreen(viewModel = viewModel)
                4 -> ProfileGoalsScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CalorieLensBottomBar(
    currentTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = SlateSurface,
        tonalElevation = 8.dp,
        modifier = Modifier
            .navigationBarsPadding()
            .testTag("main_bottom_nav")
    ) {
        // Tab 0: Diary
        NavigationBarItem(
            selected = currentTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Icon(
                    if (currentTab == 0) Icons.Filled.RestaurantMenu else Icons.Outlined.RestaurantMenu,
                    contentDescription = "Food Diary"
                )
            },
            label = {
                Text(
                    text = "Diary",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (currentTab == 0) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldPrimary,
                selectedTextColor = EmeraldPrimary,
                indicatorColor = EmeraldPrimary.copy(alpha = 0.15f),
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )

        // Tab 1: AI Lens / Scanner (Prominent center item)
        NavigationBarItem(
            selected = currentTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            if (currentTab == 1) EmeraldPrimary else EmeraldPrimary.copy(alpha = 0.2f)
                        ),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Icon(
                        if (currentTab == 1) Icons.Filled.CameraAlt else Icons.Outlined.CameraAlt,
                        contentDescription = "AI Scanner",
                        tint = if (currentTab == 1) SlateBackground else EmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            label = {
                Text(
                    text = "AI Lens",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SlateBackground,
                selectedTextColor = EmeraldPrimary,
                indicatorColor = Color.Transparent,
                unselectedIconColor = EmeraldPrimary,
                unselectedTextColor = TextSecondary
            )
        )

        // Tab 2: Summary (Daily Nutrition Summary)
        NavigationBarItem(
            selected = currentTab == 2,
            onClick = { onTabSelected(2) },
            icon = {
                Icon(
                    if (currentTab == 2) Icons.Filled.PieChart else Icons.Outlined.PieChart,
                    contentDescription = "Nutrition Summary"
                )
            },
            label = {
                Text(
                    text = "Summary",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (currentTab == 2) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldPrimary,
                selectedTextColor = EmeraldPrimary,
                indicatorColor = EmeraldPrimary.copy(alpha = 0.15f),
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )

        // Tab 3: Past Logs (Room DB Historical Food Logs)
        NavigationBarItem(
            selected = currentTab == 3,
            onClick = { onTabSelected(3) },
            icon = {
                Icon(
                    if (currentTab == 3) Icons.Filled.History else Icons.Outlined.History,
                    contentDescription = "Past Meal Logs"
                )
            },
            label = {
                Text(
                    text = "Past Logs",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (currentTab == 3) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldPrimary,
                selectedTextColor = EmeraldPrimary,
                indicatorColor = EmeraldPrimary.copy(alpha = 0.15f),
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )

        // Tab 4: Goals / Profile
        NavigationBarItem(
            selected = currentTab == 4,
            onClick = { onTabSelected(4) },
            icon = {
                Icon(
                    if (currentTab == 4) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Goals & Profile"
                )
            },
            label = {
                Text(
                    text = "Goals",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (currentTab == 4) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldPrimary,
                selectedTextColor = EmeraldPrimary,
                indicatorColor = EmeraldPrimary.copy(alpha = 0.15f),
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
    }
}
