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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devagarwal.ads_sdk_integration_android.features.ads.presentation.component.AdStatusCard
import com.devagarwal.ads_sdk_integration_android.features.ads.presentation.state.AdState

/**
 * AdVerifierView displays diagnostic details and tracking info for the active ad session.
 *
 * It validates that the integration works in real time, displaying details about:
 * 1. Impression and Click event attribution status.
 * 2. Underlying tracking and destination landing page URLs.
 * 3. Unique Click Identifier (UCLID).
 */
@Composable
fun AdVerifierView(
    state: AdState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // If an ad is successfully loaded, show the diagnostics console; otherwise show a fallback prompt.
        if (state is AdState.AdLoaded) {
            DiagnosticsContent(state)
        } else {
            EmptyDiagnosticsState(state)
        }
    }
}

/**
 * Renders the state when there's no active banner ad loaded.
 * Adapts the message depending on whether the app is currently loading or in error state.
 */
@Composable
private fun EmptyDiagnosticsState(state: AdState) {
    val message = when (state) {
        is AdState.AdLoading -> "Ad session is loading... Please wait."
        is AdState.AdError -> "Active ad session failed: ${state.message}"
        is AdState.AdEmpty -> "Ad session completed with empty response: ${state.message}"
        else -> "Please load a display ad in the Simulator tab to activate tracking diagnostics."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.02f), shape = RoundedCornerShape(50.dp))
                .border(1.dp, Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(50.dp))
                .padding(20.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Analytics,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.24f),
                modifier = Modifier.size(64.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No Active Ad Session",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            color = Color.White.copy(alpha = 0.38f),
            fontSize = 12.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * DiagnosticsContent displays the specific tracking properties of the loaded ad.
 */
@Composable
private fun DiagnosticsContent(state: AdState.AdLoaded) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "AD SESSION DIAGNOSTICS",
            color = Color.White.copy(alpha = 0.38f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Ad Status Card tracks real-time attribution (checkmarks for Impression & Click)
        AdStatusCard(
            impressionTracked = state.impressionTracked,
            clickTracked = state.clickTracked,
            uclid = state.ad.uclid
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "SDK ANALYTICS DATA",
            color = Color.White.copy(alpha = 0.38f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Individual information blocks to display SDK URLs
        InfoTile(
            title = "Image URL",
            value = state.ad.imageUrl,
            icon = Icons.Outlined.Image
        )
        Spacer(modifier = Modifier.height(12.dp))
        InfoTile(
            title = "Target Landing Page",
            value = state.ad.destinationUrl,
            icon = Icons.Outlined.Launch
        )
        Spacer(modifier = Modifier.height(12.dp))
        InfoTile(
            title = "Impression Request URL (SDK & Direct Ping)",
            value = state.ad.impressionTrackingUrl ?: "Not specified by SDK",
            icon = Icons.Outlined.Visibility
        )
        Spacer(modifier = Modifier.height(12.dp))
        InfoTile(
            title = "Click Request URL (SDK & Direct Ping)",
            value = state.ad.clickTrackingUrl ?: "Not specified by SDK",
            icon = Icons.Outlined.Mouse
        )
    }
}

/**
 * Helper card composable to print URL and metadata strings in monospace format.
 */
@Composable
private fun InfoTile(
    title: String,
    value: String,
    icon: ImageVector
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF161626), shape = RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.04f), shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF0097A7),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    color = Color.White.copy(alpha = 0.38f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
