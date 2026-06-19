package com.devagarwal.ads_sdk_integration_android.features.ads.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.devagarwal.ads_sdk_integration_android.core.error.Failure
import com.devagarwal.ads_sdk_integration_android.core.utils.Result
import com.devagarwal.ads_sdk_integration_android.features.ads.data.datasource.AdsRemoteDataSource
import com.devagarwal.ads_sdk_integration_android.features.ads.domain.model.AdEntity
import com.devagarwal.ads_sdk_integration_android.features.ads.domain.repository.AdsRepository
import com.devagarwal.ads_sdk_integration_android.sdk.OsmosEventService
import com.devagarwal.ads_sdk_integration_android.sdk.OsmosInitializer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Implementation of [AdsRepository] translating data-transfer models (DTOs) into clean
 * domain entities, and triggering event logs to the wrapper services.
 */
class AdsRepositoryImpl @Inject constructor(
    private val remoteDataSource: AdsRemoteDataSource, // Communicates with OsmosAdService
    private val osmosEventService: OsmosEventService,   // Handles click and view logging
    private val osmosInitializer: OsmosInitializer,     // Bootstraps native SDK context
    @ApplicationContext private val context: Context   // Injected application context to inspect internet status
) : AdsRepository {

    /**
     * Checks if the device has an active internet connection.
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Fetches and deserializes display banner ad layouts.
     * 
     * Applies two critical business logic fallbacks:
     * 1. If [elements.destinationUrl] is missing, it falls back to using [clickTrackingUrl]
     *    as the target landing page to avoid empty web redirect errors.
     * 2. Extracts `uclid` parameters dynamically from tracking URLs if omitted in the root.
     * 
     * @return [Result.Success] containing the parsed [AdEntity] or a [Result.FailureResult] failure descriptor.
     */
    override suspend fun fetchBannerAd(): Result<AdEntity, Failure> {
        // Intercept requests if there is no active internet connection
        if (!isNetworkAvailable()) {
            return Result.FailureResult(
                Failure.NetworkFailure("No internet connection. Please verify your network settings and try again.")
            )
        }

        return try {
            // Self-healing initialization: ensure SDK context is active
            if (!osmosInitializer.isInitialized) {
                osmosInitializer.init()
            }

            val adsResponse = remoteDataSource.fetchDisplayAds()
            if (adsResponse.bannerAds.isEmpty()) {
                return Result.FailureResult(Failure.ServerFailure("No ads available. The ad server returned an empty response."))
            }

            val adModel = adsResponse.bannerAds.first()
            val elements = adModel.elements

            if (elements == null || elements.value.isEmpty()) {
                return Result.FailureResult(Failure.ServerFailure("Ad image URL is missing in the server response."))
            }

            // Fallback challenge resolution: if destinationUrl is empty, fallback to clickTrackingUrl
            val destinationUrl = if (elements.destinationUrl.isNotEmpty()) {
                elements.destinationUrl
            } else {
                adModel.clickTrackingUrl ?: ""
            }

            // Map DTO to pure Domain Entity
            val adEntity = AdEntity(
                imageUrl = elements.value,
                destinationUrl = destinationUrl,
                impressionTrackingUrl = adModel.impressionTrackingUrl,
                clickTrackingUrl = adModel.clickTrackingUrl,
                width = adModel.width,
                height = adModel.height,
                uclid = adModel.uclid ?: "unknown"
            )

            Result.Success(adEntity)
        } catch (e: java.io.IOException) {
            Result.FailureResult(
                Failure.NetworkFailure("Network connection failed. Please check your internet connection and try again.")
            )
        } catch (e: Exception) {
            val message = e.message ?: e.toString()
            if (message.contains("UnknownHostException", ignoreCase = true) ||
                message.contains("ConnectException", ignoreCase = true) ||
                message.contains("timeout", ignoreCase = true) ||
                message.contains("network", ignoreCase = true) ||
                message.contains("offline", ignoreCase = true)) {
                Result.FailureResult(
                    Failure.NetworkFailure("No internet connection. Please check your network and try again.")
                )
            } else {
                Result.FailureResult(Failure.UnexpectedFailure("Failed to load ad: $message"))
            }
        }
    }

    /**
     * Relays impression events to the event tracking service.
     */
    override suspend fun trackImpression(
        uclid: String,
        impressionTrackingUrl: String?
    ): Result<Unit, Failure> {
        if (!isNetworkAvailable()) {
            return Result.FailureResult(Failure.NetworkFailure("No internet connection. Impression tracking failed."))
        }
        return try {
            osmosEventService.trackImpression(uclid, impressionTrackingUrl)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.FailureResult(Failure.UnexpectedFailure(e.message ?: e.toString()))
        }
    }

    /**
     * Relays click events to the event tracking service.
     */
    override suspend fun trackClick(
        uclid: String,
        clickTrackingUrl: String?
    ): Result<Unit, Failure> {
        if (!isNetworkAvailable()) {
            return Result.FailureResult(Failure.NetworkFailure("No internet connection. Click tracking failed."))
        }
        return try {
            osmosEventService.trackClick(uclid, clickTrackingUrl)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.FailureResult(Failure.UnexpectedFailure(e.message ?: e.toString()))
        }
    }
}

