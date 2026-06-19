package com.devagarwal.ads_sdk_integration_android.features.ads.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devagarwal.ads_sdk_integration_android.analytics.AnalyticsService
import com.devagarwal.ads_sdk_integration_android.features.ads.domain.usecase.*
import com.devagarwal.ads_sdk_integration_android.core.utils.*
import com.devagarwal.ads_sdk_integration_android.features.ads.presentation.state.AdState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel managing the business states and actions for banner advertisements.
 * Manages loading states, impressions tracking, click logging, and analytics hooks.
 */
@HiltViewModel
class AdViewModel @Inject constructor(
    private val fetchBannerAdUseCase: FetchBannerAdUseCase,     // Fetches ad payload data
    private val trackImpressionUseCase: TrackImpressionUseCase, // Logs view impressions
    private val trackClickUseCase: TrackClickUseCase,           // Logs ad clicks
    private val analytics: AnalyticsService                     // Consolidated analytics logger
) : ViewModel() {

    // Internal mutable state flow holding the reactive state of the ad flow
    private val _state = MutableStateFlow<AdState>(AdState.AdInitial)
    
    // Read-only StateFlow exposed to Compose screens to safely observe changes
    val state: StateFlow<AdState> = _state.asStateFlow()

    /**
     * Triggers the async fetch banner ad routine.
     * Transitions the UI state to [AdState.AdLoading] and updates the state
     * reactively based on success/failure usecase returns.
     */
    fun loadBannerAd() {
        viewModelScope.launch {
            _state.value = AdState.AdLoading
            fetchBannerAdUseCase().fold(
                onSuccess = { ad ->
                    analytics.logAdLoaded()
                    _state.value = AdState.AdLoaded(ad = ad)
                },
                onFailure = { failure ->
                    analytics.logFailure(failure.message)
                    _state.value = AdState.AdError(failure.message)
                }
            )
        }
    }

    /**
     * Retries fetching ads on user tap.
     */
    fun retryLoadingAd() {
        loadBannerAd()
    }

    /**
     * Tracks an impression exactly once per ad session when the view criteria is met.
     */
    fun trackImpression() {
        val currentState = _state.value
        // Verify if the ad is loaded and has not been tracked already
        if (currentState is AdState.AdLoaded && !currentState.impressionTracked) {
            val ad = currentState.ad
            analytics.logImpression()
            // Optimistically update local state to lock duplicate impression events
            _state.value = currentState.copy(impressionTracked = true)

            viewModelScope.launch {
                trackImpressionUseCase(
                    TrackImpressionParams(
                        uclid = ad.uclid,
                        impressionTrackingUrl = ad.impressionTrackingUrl
                    )
                )
            }
        }
    }

    /**
     * Tracks an ad click event when the user clicks the banner.
     */
    fun trackClick() {
        val currentState = _state.value
        if (currentState is AdState.AdLoaded) {
            val ad = currentState.ad
            analytics.logClick()
            _state.value = currentState.copy(clickTracked = true)

            viewModelScope.launch {
                trackClickUseCase(
                    TrackClickParams(
                        uclid = ad.uclid,
                        clickTrackingUrl = ad.clickTrackingUrl
                    )
                )
            }
        }
    }

    /**
     * Resets the ad simulator flow back to its initial state.
     */
    fun resetBanner() {
        _state.value = AdState.AdInitial
    }
}

