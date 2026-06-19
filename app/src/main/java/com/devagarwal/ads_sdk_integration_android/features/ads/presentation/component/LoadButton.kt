package com.devagarwal.ads_sdk_integration_android.features.ads.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A beautiful, pill-shaped action button with a sleek linear gradient background.
 *
 * Automatically handles loading states:
 * - Disables clicks while loading to prevent concurrent fetches.
 * - Shows a circular progress loader in place of the action icon.
 */
@Composable
fun LoadButton(
    label: String,
    icon: ImageVector,
    onPressed: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0097A7), Color(0xFF006974))
                )
            )
            // Prevent clicks when loading is in progress
            .clickable(enabled = !isLoading) { onPressed() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Swap standard icon for loading indicator when fetching is active
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isLoading) "Loading Ad..." else label,
                color = Color.White,
                fontSize = 16.sp,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
