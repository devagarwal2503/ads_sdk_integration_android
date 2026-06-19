package com.devagarwal.ads_sdk_integration_android.sdk

import com.ai.osmos.utils.error.ErrorCallback
import com.devagarwal.ads_sdk_integration_android.core.constants.ApiConstants
import com.devagarwal.ads_sdk_integration_android.core.logger.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service class that acts as a wrapper around the native Osmos SDK ad-fetching APIs.
 * It separates the third-party SDK dependencies from the presentation and data layers.
 */
@Singleton
class OsmosAdService @Inject constructor(
    private val initializer: OsmosInitializer, // Responsible for boot-strapping the SDK instance
    private val logger: AppLogger                // Streams info and error logs to Logcat/Console
) {
    /**
     * Fetches display banner advertisements from the Osmos SDK using configured Ad Unit parameters.
     * 
     * Runs asynchronously on the [Dispatchers.IO] thread pool since it performs network requests.
     *
     * @return The raw JSON-compatible [Map] representing the API ad payload response,
     *         or null if a communication issue occurs.
     */
    suspend fun fetchDisplayAds(): Map<String, Any>? = withContext(Dispatchers.IO) {
        logger.info("Fetching Display Ads via Osmos SDK (pageType: ${ApiConstants.PAGE_TYPE}, adUnit: ${ApiConstants.AD_UNIT})...")
        try {
            // Self-healing initialization: ensure SDK is running before attempting to fetch ads
            if (!initializer.isInitialized) {
                initializer.init()
            }
            
            // Get active OsmosSDK instance
            val sdk = initializer.sdk
            val fetcher = sdk.adFetcherSDK()
            
            // Standard SDK error callback to capture network/parsing exceptions
            val errorCallback = object : ErrorCallback {
                override fun onError(errorCode: String, message: String, exception: Throwable?) {
                    logger.error("Osmos Fetch Ads Callback Error ($errorCode): $message", exception)
                }
            }
            
            // Invoke the SDK method passing parameters from ApiConstants
            val response = fetcher.fetchDisplayAdsWithAu(
                ApiConstants.CLI_UBID,        // client ubiquitous browser ID ("Any")
                ApiConstants.PAGE_TYPE,       // simulator page identifier ("demo_page")
                1,                            // fetch exactly one ad product
                listOf(ApiConstants.AD_UNIT), // target ad units list
                null,                         // optional targeting parameters map list
                errorCallback                 // error callback listener
            )
            logger.info("Raw SDK response: $response")
            response
        } catch (e: Exception) {
            logger.error("Error fetching ads from SDK", e)
            throw e
        }
    }
}

