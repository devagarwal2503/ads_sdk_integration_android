package com.devagarwal.ads_sdk_integration_android.features.ads.presentation.component

import android.graphics.Rect
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView

/**
 * A wrapper composable that monitors the on-screen visibility of its child component.
 *
 * It triggers the [onImpression] callback exactly once when at least 50% of the child's
 * area is visible within the viewport bounds.
 *
 * To avoid race conditions where the child component's layout height starts off collapsed
 * (e.g., when a network image is still loading) and temporarily registers as 100% visible:
 * 1. [isEnabled] controls when visibility tracking becomes active (usually set to true only after the image loads).
 * 2. [expectedRatio] is checked against the laid-out dimensions to ensure the layout height has settled to its final proportions.
 */
@Composable
fun AdVisibilityWrapper(
    adId: String,                           // Unique ID of the ad (e.g. image URL) to reset state when the ad changes
    onImpression: () -> Unit,                // Callback triggered once when 50%+ visibility is achieved
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,               // Prevents tracking while the ad is in an initial loading state
    expectedRatio: Float = 16f / 9f,         // The target aspect ratio used to verify if layout dimensions have settled
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    
    // Session lock: Ensures an impression event fires exactly once for a given adId.
    // If the adId changes, remember(adId) resets this flag to false for the new ad session.
    var impressionFired by remember(adId) { mutableStateOf(false) }

    Box(
        modifier = modifier.onGloballyPositioned { coordinates ->
            // Obtain the bounds of this layout box in window coordinates
            val bounds = coordinates.boundsInWindow()
            val compWidth = bounds.width
            val compHeight = bounds.height
            val compArea = compWidth * compHeight

            if (compArea > 0) {
                // Determine the final expected height based on the aspect ratio and current width
                val expectedHeight = compWidth / expectedRatio
                
                // Layout settlement check: Make sure the composable's height is at least 50%
                // of its final expected size to prevent early impression firing during loading.
                val isSizeResolved = compHeight >= expectedHeight * 0.5f

                // Track only if tracking is active, the size is fully resolved, and we haven't tracked yet
                if (isEnabled && isSizeResolved && !impressionFired) {
                    // Fetch the visible display rect of the window (taking system bars, status bar into account)
                    val visibleFrame = Rect()
                    view.getWindowVisibleDisplayFrame(visibleFrame)

                    // Find the overlapping bounds between the composable and the visible window frame
                    val overlapLeft = maxOf(bounds.left, visibleFrame.left.toFloat())
                    val overlapRight = minOf(bounds.right, visibleFrame.right.toFloat())
                    val overlapTop = maxOf(bounds.top, visibleFrame.top.toFloat())
                    val overlapBottom = minOf(bounds.bottom, visibleFrame.bottom.toFloat())

                    // Calculate overlap width and height
                    val overlapWidth = overlapRight - overlapLeft
                    val overlapHeight = overlapBottom - overlapTop

                    // Calculate the visibility fraction of the composable area
                    val visibleRatio = if (overlapWidth > 0 && overlapHeight > 0) {
                        (overlapWidth * overlapHeight) / compArea
                    } else {
                        0f
                    }

                    Log.d("AdVisibility", "adId: $adId, bounds: $bounds, visibleFrame: $visibleFrame, visibleRatio: $visibleRatio, overlapWidth: $overlapWidth, overlapHeight: $overlapHeight")

                    // If the overlapping visible area meets or exceeds 50%, trigger the callback and lock it
                    if (visibleRatio >= 0.5f) {
                        impressionFired = true
                        onImpression()
                    }
                }
            }
        }
    ) {
        content()
    }
}

