package com.devagarwal.ads_sdk_integration_android.core.constants

/**
 * Constants passed into the Osmos SDK config parameters to query display ads.
 */
object ApiConstants {
    // Unique user session ID template (defaults to Any for the assessment simulator)
    const val CLI_UBID = "Any"
    
    // Page context classifier passed to target relevant ads
    const val PAGE_TYPE = "demo_page"
    
    // Identifier mapping to our banner ad unit zone
    const val AD_UNIT = "banner_ads"
}
