package com.devagarwal.ads_sdk_integration_android.features.ads.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devagarwal.ads_sdk_integration_android.features.ads.presentation.component.BannerAdWidget
import com.devagarwal.ads_sdk_integration_android.features.ads.presentation.component.LoadButton
import com.devagarwal.ads_sdk_integration_android.features.ads.presentation.component.LoadingIndicator
import com.devagarwal.ads_sdk_integration_android.features.ads.presentation.state.AdState

/**
 * AdSimulatorView serves as the user-facing screen where ads can be requested and loaded.
 * It simulates a standard user feed (like a news application) where the advertisement is
 * placed lower in the scrollable content.
 *
 * This design is crucial for validating our 50% visibility tracking logic, since the user
 * must explicitly scroll down to see the ad before an impression event is generated.
 */
@Composable
fun AdSimulatorView(
    state: AdState,
    onLoadAd: () -> Unit,
    onImpression: () -> Unit,
    onAdClick: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App header description explaining what the user is interacting with
        Text(
            text = "Demo showcasing API-driven display banner ads, visibility-based impression triggers (50%+), and click attribution.",
            color = Color.LightGray.copy(alpha = 0.6f),
            fontSize = 11.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Card container that dynamically renders layout based on current UI state
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF161626))
                .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(20.dp))
        ) {
            when (state) {
                is AdState.AdInitial -> {
                    InitialState(onLoadAd)
                }
                is AdState.AdLoading -> {
                    LoadingIndicator()
                }
                is AdState.AdLoaded -> {
                    LoadedState(
                        state = state,
                        onImpression = onImpression,
                        onAdClick = onAdClick,
                        onReset = onReset
                    )
                }
                is AdState.AdEmpty -> {
                    EmptyState(state.message, onLoadAd)
                }
                is AdState.AdError -> {
                    ErrorState(state.message, onLoadAd)
                }
            }
        }
    }
}

/**
 * Initial view prompt shown to the user when they first launch the app.
 * Prompts them to fetch and load a banner advertisement.
 */
@Composable
private fun InitialState(onLoadAd: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.04f), shape = RoundedCornerShape(50.dp))
                .padding(20.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.AdsClick,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(56.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ready to Load Ad",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Click below to fetch the banner ad.",
            color = Color.White.copy(alpha = 0.38f),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        LoadButton(
            label = "Load Display Ad",
            icon = Icons.Outlined.CloudDownload,
            onPressed = onLoadAd
        )
    }
}

/**
 * Renders the primary simulated feed layout once the banner ad is fetched successfully.
 * Contains article snippet cards, followed by the ad widget, followed by additional content.
 */
@Composable
private fun LoadedState(
    state: AdState.AdLoaded,
    onImpression: () -> Unit,
    onAdClick: () -> Unit,
    onReset: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "DEMO FEED CONTENT (SCROLL DOWN)",
            color = Color.White.copy(alpha = 0.38f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Set of mock feed article cards to push the ad below the initial viewport folding line
        DummyArticleCard(
            title = "Exploring Clean Architecture in Flutter",
            snippet = "Discover how isolating domain, data, and presentation layers leads to highly maintainable, testable, and robust codebases in large-scale mobile apps..."
        )
        Spacer(modifier = Modifier.height(12.dp))

        DummyArticleCard(
            title = "State Management with BLoC",
            snippet = "A deep dive into event-driven state transitions, asynchronous streams, and reactive UI elements to separate business logic from rendering code..."
        )
        Spacer(modifier = Modifier.height(12.dp))

        DummyArticleCard(
            title = "Testing Flutter Applications",
            snippet = "Learn how to write unit, widget, and integration tests to verify functionality and prevent code regressions during fast iteration cycles..."
        )
        Spacer(modifier = Modifier.height(40.dp))

        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SPONSORED ADVERTISEMENT",
            color = Color(0xFF0097A7),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))

        // The actual ad widget wrapped with visibility checks and redirection/click actions
        BannerAdWidget(
            ad = state.ad,
            onImpression = onImpression,
            onAdClick = onAdClick
        )
        Spacer(modifier = Modifier.height(32.dp))

        DummyArticleCard(
            title = "Responsive Layouts in Flutter",
            snippet = "Best practices for handling overflows, adapting sizes, and supporting multiple screen profiles..."
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Option to reset screen state back to initial and load another ad
        TextButton(
            onClick = onReset,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    tint = Color(0xFF0097A7),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reset & Load Another",
                    color = Color(0xFF0097A7),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Renders a lightweight mockup card simulating an article list item in the scrollable feed.
 */
@Composable
private fun DummyArticleCard(title: String, snippet: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.02f)),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.05f))
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = snippet,
                color = Color.White.copy(alpha = 0.38f),
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

/**
 * Fallback view shown if the ad server returns an empty list or indicates no ads are filled.
 */
@Composable
private fun EmptyState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.HourglassEmpty,
            contentDescription = null,
            tint = Color.Yellow,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Ads Available",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            color = Color.LightGray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        LoadButton(
            label = "Retry Fetch",
            icon = Icons.Outlined.Refresh,
            onPressed = onRetry
        )
    }
}

/**
 * Fallback view shown when network errors, HTTP failures, or exception occurrences block ad loads.
 */
@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ad Loading Failed",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            color = Color.Red.copy(alpha = 0.8f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        LoadButton(
            label = "Retry",
            icon = Icons.Outlined.Refresh,
            onPressed = onRetry
        )
    }
}
