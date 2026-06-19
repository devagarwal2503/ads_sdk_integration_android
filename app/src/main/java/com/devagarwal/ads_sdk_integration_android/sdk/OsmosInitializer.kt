package com.devagarwal.ads_sdk_integration_android.sdk

import com.ai.osmos.core.OsmosSDK
import com.devagarwal.ads_sdk_integration_android.core.constants.AppConstants
import com.devagarwal.ads_sdk_integration_android.core.logger.AppLogger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service responsible for bootstrapping and managing the lifespan of the native Osmos SDK.
 * It manages configuration hostnames and client ID credentials.
 */
@Singleton
class OsmosInitializer @Inject constructor(
    private val logger: AppLogger
) {
    // Flag indicating whether the SDK was successfully initialized
    private var _isInitialized = false
    val isInitialized: Boolean
        get() = _isInitialized

    // The initialized instance of the SDK
    private var _sdk: OsmosSDK? = null

    val sdk: OsmosSDK
        get() {
            return _sdk ?: throw IllegalStateException("Osmos SDK is not initialized. Call init() first.")
        }

    /**
     * Initializes the Osmos SDK using constants defined in [AppConstants].
     * 
     * Includes error recovery logic to capture singleton collision issues (commonly thrown
     * if hot restarts occur where the JVM remains active but Dart/UI contexts are re-created).
     */
    suspend fun init() {
        if (_isInitialized) return
        try {
            logger.info("Initializing Osmos SDK with Client ID: ${AppConstants.CLIENT_ID}")

            // Build configuration options via the native builder
            val builder = OsmosSDK.clientId(AppConstants.CLIENT_ID)
                .displayAdsHost(AppConstants.DISPLAY_ADS_HOST)
                .productAdsHost(AppConstants.PRODUCT_ADS_HOST)
                .debug(true)

            try {
                // Attempt standard global singleton registration
                builder.buildGlobalInstance()
                _sdk = OsmosSDK.globalInstance()
            } catch (e: Exception) {
                val errorStr = e.toString()
                // Catch native singleton conflicts (e.g. if already built in current process)
                if (errorStr.contains("ERROR_ALREADY_INITIALIZED") || errorStr.contains("already been built")) {
                    logger.info("Osmos SDK already initialized on native side. Falling back to build().")
                    // Retrieve local instance instead to prevent app startup crashes
                    _sdk = builder.build()
                } else {
                    throw e
                }
            }

            _isInitialized = true
            logger.info("Osmos SDK initialized successfully.")
        } catch (e: Exception) {
            logger.error("Failed to initialize Osmos SDK", e)
            _isInitialized = false
            throw e
        }
    }
}

