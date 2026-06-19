package com.devagarwal.ads_sdk_integration_android.sdk

import com.ai.osmos.models.events.TrackingParams
import com.ai.osmos.utils.error.ErrorCallback
import com.devagarwal.ads_sdk_integration_android.core.constants.ApiConstants
import com.devagarwal.ads_sdk_integration_android.core.logger.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service class that handles impression and click logging.
 *
 * Implements a dual-dispatch event pipeline:
 * 1. Native SDK attribution calls ([registerAdImpressionEvent] and [registerAdClickEvent]).
 * 2. Redundant, direct HTTP background pings using [OkHttpClient] to bypass native channel blockages.
 */
@Singleton
class OsmosEventService @Inject constructor(
    private val initializer: OsmosInitializer, // Singleton SDK bootstrapping manager
    private val okHttpClient: OkHttpClient,       // HTTP client for raw tracking url pings
    private val logger: AppLogger                // Log streaming event manager
) {
    /**
     * Registers an ad impression event.
     *
     * @param uclid Unique click-attribution tracking ID.
     * @param impressionTrackingUrl Direct impression ping URL from ad metadata.
     */
    suspend fun trackImpression(
        uclid: String,
        impressionTrackingUrl: String?
    ) = withContext(Dispatchers.IO) {
        logger.info("Registering impression event for uclid: $uclid")
        try {
            if (!initializer.isInitialized) {
                initializer.init()
            }
            val sdk = initializer.sdk
            val registerEvent = sdk.registerEvent()

            // Build impression tracking parameters configuration
            val trackingParams = TrackingParams.builder()
                .uclid(uclid)
                .position(1) // Mandatory slot position index
                .build()

            val errorCallback = object : ErrorCallback {
                override fun onError(errorCode: String, message: String, exception: Throwable?) {
                    logger.error("Osmos Register Impression Callback Error ($errorCode): $message", exception)
                }
            }

            // Fire native SDK impression call
            val response = registerEvent.registerAdImpressionEvent(
                ApiConstants.CLI_UBID, // Client ubid string ("Any")
                uclid,                 // Unique attribution click id
                1,                     // Screen placement position index
                trackingParams,        // Detailed tracking options configuration
                errorCallback          // Logging error listener
            )
            logger.info("Impression registered with SDK. Response: $response")
        } catch (e: Exception) {
            logger.error("Error registering impression with SDK", e)
        }

        // Direct HTTP ping fallback: Fired concurrently to ensure tracking redundancy
        if (!impressionTrackingUrl.isNullOrEmpty()) {
            logger.info("Pinging raw impression tracking URL: $impressionTrackingUrl")
            try {
                val request = Request.Builder()
                    .url(impressionTrackingUrl)
                    .build()
                okHttpClient.newCall(request).execute().use { response ->
                    logger.info("Impression URL ping status: ${response.code}")
                }
            } catch (e: Exception) {
                logger.error("Failed to ping raw impression URL", e)
            }
        }
    }

    /**
     * Registers an ad click event.
     *
     * @param uclid Unique click-attribution tracking ID.
     * @param clickTrackingUrl Direct click ping URL from ad metadata.
     */
    suspend fun trackClick(
        uclid: String,
        clickTrackingUrl: String?
    ) = withContext(Dispatchers.IO) {
        logger.info("Registering click event for uclid: $uclid")
        try {
            if (!initializer.isInitialized) {
                initializer.init()
            }
            val sdk = initializer.sdk
            val registerEvent = sdk.registerEvent()

            // Build click parameters config
            val trackingParams = TrackingParams.builder()
                .uclid(uclid)
                .build()

            val errorCallback = object : ErrorCallback {
                override fun onError(errorCode: String, message: String, exception: Throwable?) {
                    logger.error("Osmos Register Click Callback Error ($errorCode): $message", exception)
                }
            }

            // Invoke native SDK click registry
            val response = registerEvent.registerAdClickEvent(
                ApiConstants.CLI_UBID,
                uclid,
                trackingParams,
                errorCallback
            )
            logger.info("Click registered with SDK. Response: $response")
        } catch (e: Exception) {
            logger.error("Error registering click with SDK", e)
        }

        // Direct HTTP click ping fallback: Confirms user redirection
        if (!clickTrackingUrl.isNullOrEmpty()) {
            logger.info("Pinging raw click tracking URL: $clickTrackingUrl")
            try {
                val request = Request.Builder()
                    .url(clickTrackingUrl)
                    .build()
                okHttpClient.newCall(request).execute().use { response ->
                    logger.info("Click URL ping status: ${response.code}")
                }
            } catch (e: Exception) {
                logger.error("Failed to ping raw click URL", e)
            }
        }
    }
}

