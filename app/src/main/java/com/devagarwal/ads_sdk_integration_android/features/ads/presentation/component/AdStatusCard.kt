package com.devagarwal.ads_sdk_integration_android.features.ads.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * AdStatusCard shows the state of our ad analytics trackers (Impression and Click) in a
 * clean dashboard-like panel.
 *
 * It turns green as soon as events are tracked, giving visual validation that the background
 * calls to the mock servers or SDK trackers are succeeding.
 */
@Composable
fun AdStatusCard(
    impressionTracked: Boolean,
    clickTracked: Boolean,
    uclid: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
        shape = RoundedCornerShape(16.dp),
        border = borderStroke()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Title and Icon
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = Color(0xFF0097A7),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ad Tracking Status",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            // Displays the unique click tracking ID
            Text(
                text = "UCLID: $uclid",
                color = Color.Gray,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            HorizontalDivider(
                color = Color.White.copy(alpha = 0.1f),
                modifier = Modifier.padding(vertical = 12.dp)
            )
            
            // Side-by-side status items for Impression & Click
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatusItem(
                    label = "Impression",
                    description = "50%+ visible",
                    status = impressionTracked,
                    modifier = Modifier.weight(1f)
                )
                StatusItem(
                    label = "Click",
                    description = "Destination URL",
                    status = clickTracked,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * An individual tracking indicator card.
 *
 * Dynamically updates its color scheme:
 * - Green (when event is tracked successfully).
 * - Gray (when event is pending/untracked).
 */
@Composable
private fun StatusItem(
    label: String,
    description: String,
    status: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (status) Color.Green.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.04f)
    val borderColor = if (status) Color.Green.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f)
    val tintColor = if (status) Color.Green else Color.Gray

    Box(
        modifier = modifier
            .background(bgColor, shape = RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (status) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                contentDescription = null,
                tint = tintColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    color = if (status) Color.Green else Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Returns a thin bordered line for the parent Card outline.
 */
@Composable
private fun borderStroke() = CardDefaults.outlinedCardBorder().copy(
    brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.1f))
)
