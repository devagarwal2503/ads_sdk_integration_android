package com.devagarwal.ads_sdk_integration_android.features.ads.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.sp
import com.devagarwal.ads_sdk_integration_android.core.logger.AppLogger
import com.devagarwal.ads_sdk_integration_android.features.ads.presentation.viewmodel.AdViewModel

/**
 * Main application screen housing the bottom navigation bar and the three core tab views:
 * 1. Ad Simulator View: Performs ad loading, simulated feed scroll and rendering.
 * 2. Ad Verifier View: Displays ad session diagnostics and live tracking status indicators.
 * 3. Event Console View: Streams real-time log activity and system notifications.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AdViewModel,
    logger: AppLogger,
    modifier: Modifier = Modifier
) {
    // Current tab index (0 = Simulator, 1 = Verifier, 2 = Console)
    var currentIndex by remember { mutableStateOf(0) }
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF0F0F1A), // Sleek dark blue theme background
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "OSMOS ADS INTEGRATION",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                },
                actions = {
                    // Reset action button: returns the ad state machine back to InitialState
                    IconButton(onClick = { viewModel.resetBanner() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset State",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF161626)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF111122),
                tonalElevation = 0.dp
            ) {
                // Tab 1 Navigation Item
                NavigationBarItem(
                    selected = currentIndex == 0,
                    onClick = { currentIndex = 0 },
                    icon = { Icon(Icons.Default.AdsClick, contentDescription = null) },
                    label = { Text("Ad Simulator", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0097A7),
                        selectedTextColor = Color(0xFF0097A7),
                        unselectedIconColor = Color.White.copy(alpha = 0.38f),
                        unselectedTextColor = Color.White.copy(alpha = 0.38f),
                        indicatorColor = Color.Transparent
                    )
                )
                // Tab 2 Navigation Item
                NavigationBarItem(
                    selected = currentIndex == 1,
                    onClick = { currentIndex = 1 },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = null) },
                    label = { Text("Ad Verifier", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0097A7),
                        selectedTextColor = Color(0xFF0097A7),
                        unselectedIconColor = Color.White.copy(alpha = 0.38f),
                        unselectedTextColor = Color.White.copy(alpha = 0.38f),
                        indicatorColor = Color.Transparent
                    )
                )
                // Tab 3 Navigation Item
                NavigationBarItem(
                    selected = currentIndex == 2,
                    onClick = { currentIndex = 2 },
                    icon = { Icon(Icons.Default.Terminal, contentDescription = null) },
                    label = { Text("Event Console", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0097A7),
                        selectedTextColor = Color(0xFF0097A7),
                        unselectedIconColor = Color.White.copy(alpha = 0.38f),
                        unselectedTextColor = Color.White.copy(alpha = 0.38f),
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // State Preservation (IndexedStack equivalent):
            // Instead of using conditional statements which dispose views when switching tabs,
            // we keep all three views composed in the hierarchy to retain their scroll states
            // and log histories.
            // When a view is inactive, we collapse its size to 0.dp (so it does not intercept
            // touch inputs) and apply clipToBounds() to prevent its children from rendering.
            
            // Container for Ad Simulator View
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (currentIndex == 0) Modifier else Modifier.requiredSize(0.dp).clipToBounds())
            ) {
                AdSimulatorView(
                    state = state,
                    onLoadAd = { viewModel.loadBannerAd() },
                    onImpression = { viewModel.trackImpression() },
                    onAdClick = { viewModel.trackClick() },
                    onReset = { viewModel.resetBanner() }
                )
            }

            // Container for Ad Verifier View
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (currentIndex == 1) Modifier else Modifier.requiredSize(0.dp).clipToBounds())
            ) {
                AdVerifierView(state = state)
            }

            // Container for Event Console View
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (currentIndex == 2) Modifier else Modifier.requiredSize(0.dp).clipToBounds())
            ) {
                ConsoleLogsView(logger = logger)
            }
        }
    }
}

