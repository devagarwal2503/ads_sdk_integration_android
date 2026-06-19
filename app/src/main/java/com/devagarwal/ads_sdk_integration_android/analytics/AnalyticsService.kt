package com.devagarwal.ads_sdk_integration_android.analytics

import com.devagarwal.ads_sdk_integration_android.core.logger.AppLogger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AnalyticsService serves as our analytics hub, publishing events like
 * ad loads, impression triggers, clicks, and load failures to the dashboard/AppLogger.
 */
@Singleton
class AnalyticsService @Inject constructor(
    private val logger: AppLogger
) {
    // Attributes ad-loaded milestones.
    fun logAdLoaded() {
        logger.info("Analytics Event: Ad Loaded Successfully")
    }

    // Attributes impression-fired milestones (when verified 50%+ visible).
    fun logImpression() {
        logger.info("Analytics Event: Impression Fired (50%+ Visibility)")
    }

    // Attributes ad click milestones.
    fun logClick() {
        logger.info("Analytics Event: Ad Click Fired")
    }

    // Attributes failure/error milestones.
    fun logFailure(message: String) {
        logger.error("Analytics Event: Ad Error/Failure - $message")
    }
}
