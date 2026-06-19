package com.devagarwal.ads_sdk_integration_android.features.ads.domain.repository

import com.devagarwal.ads_sdk_integration_android.core.error.Failure
import com.devagarwal.ads_sdk_integration_android.core.utils.Result
import com.devagarwal.ads_sdk_integration_android.features.ads.domain.model.AdEntity

/**
 * Interface defining the API contract for fetching and tracking advertisements.
 *
 * This contract belongs to the Domain layer, while its concrete implementation resides in the
 * Data layer. This strict decoupling (Dependency Inversion Principle) allows us to switch from a
 * mock data source to a local database or real web APIs without modifying any Domain/UI logic.
 */
interface AdsRepository {
    
    // Fetches the banner advertisement from a data source.
    suspend fun fetchBannerAd(): Result<AdEntity, Failure>
    
    // Triggers tracking for the impression event when visibility thresholds are met.
    suspend fun trackImpression(uclid: String, impressionTrackingUrl: String?): Result<Unit, Failure>
    
    // Triggers tracking for the click event when user redirects to destination.
    suspend fun trackClick(uclid: String, clickTrackingUrl: String?): Result<Unit, Failure>
}
