package com.devagarwal.ads_sdk_integration_android.features.ads.presentation.component

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.devagarwal.ads_sdk_integration_android.features.ads.domain.model.AdEntity

/**
 * A widget that displays the banner advertisement, handles click actions, and wraps
 * it in [AdVisibilityWrapper] to monitor standard 50%+ visibility impressions.
 */
@Composable
fun BannerAdWidget(
    ad: AdEntity,                            // Domain model containing URLs and dimensions
    onImpression: () -> Unit,                // Action to run when the ad is viewed at 50%+ visibility
    onAdClick: () -> Unit,                   // Action to run when the ad is clicked
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // tracks if the image has finished downloading and is successfully loaded.
    // This state is passed to AdVisibilityWrapper as isEnabled to prevent visibility
    // detection checks during the loading placeholder phase.
    var isImageLoaded by remember(ad.imageUrl) { mutableStateOf(false) }

    // Dynamically calculate aspect ratio from ad metadata. Fallback to standard 16:9 landscape.
    val ratio = if (ad.width != null && ad.height != null && ad.height > 0) {
        (ad.width / ad.height).toFloat()
    } else {
        16f / 9f
    }

    AdVisibilityWrapper(
        adId = ad.imageUrl,
        onImpression = onImpression,
        isEnabled = isImageLoaded,
        expectedRatio = ratio,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 15.dp, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(alpha = 0.24f), shape = RoundedCornerShape(16.dp))
                .clickable {
                    // Click handler: launch destination page in external browser and trigger click tracking
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ad.destinationUrl)).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                        onAdClick()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Could not launch ${ad.destinationUrl}", Toast.LENGTH_SHORT).show()
                    }
                }
        ) {
            AspectRatioBox(ratio = ratio) {
                // Coil subcompose loader to draw loader indicator, image, or error icon states
                SubcomposeAsyncImage(
                    model = ad.imageUrl,
                    contentDescription = "Sponsored Advertisement",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    onSuccess = {
                        // Image downloaded successfully. Set flag to enable visibility checking.
                        isImageLoaded = true
                    },
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF0097A7))
                        }
                    },
                    error = {
                        // Error placeholder layout if the image URL is broken
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BrokenImage,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ad image failed to load",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                )
            }
        }
    }
}

/**
 * Helper container that enforces the calculated aspect ratio on the child content.
 */
@Composable
private fun AspectRatioBox(
    ratio: Float,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio)
    ) {
        content()
    }
}

