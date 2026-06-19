package com.devagarwal.ads_sdk_integration_android.features.ads.presentation.state

import com.devagarwal.ads_sdk_integration_android.features.ads.domain.model.AdEntity

/**
 * AdState defines the states of our main view flow.
 *
 * Using a sealed interface guarantees compile-time safety when we evaluate the state
 * inside our Compose screens (the 'when' block is exhaustive).
 */
sealed interface AdState {
    
    // Idle/Initial state before any ad is fetched. Prompts the user to trigger the fetch action.
    object AdInitial : AdState
    
    // Loading state indicating the HTTP request or SDK processing is actively running.
    object AdLoading : AdState
    
    // Loaded state when the ad has been fetched. Tracks impression and click status locally.
    data class AdLoaded(
        val ad: AdEntity,
        val impressionTracked: Boolean = false,
        val clickTracked: Boolean = false
    ) : AdState

    // Empty state indicating the request succeeded but no ads were returned.
    data class AdEmpty(
        val message: String = "Ad not available"
    ) : AdState

    // Error state when an exception occurs (network issue, parsing failure, etc.) during loading.
    data class AdError(
        val message: String
    ) : AdState
}
