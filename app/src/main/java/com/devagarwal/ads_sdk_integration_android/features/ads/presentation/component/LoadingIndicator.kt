package com.devagarwal.ads_sdk_integration_android.features.ads.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Centered progress loader screen shown to users during active network operations.
 */
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = Color(0xFF0097A7))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Fetching ad content...",
            color = Color.LightGray,
            fontSize = 14.sp
        )
    }
}
