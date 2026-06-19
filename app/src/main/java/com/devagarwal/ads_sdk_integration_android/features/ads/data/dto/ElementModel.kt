package com.devagarwal.ads_sdk_integration_android.features.ads.data.dto

/**
 * ElementModel holds the specific content values of the advertisement,
 * such as the dynamic image URL (value) and destination target landing page.
 */
data class ElementModel(
    val value: String,
    val destinationUrl: String
) {
    companion object {
        /**
         * Parses the nested element properties from the raw response map.
         */
        fun fromMap(map: Map<String, Any?>): ElementModel {
            return ElementModel(
                value = map["value"]?.toString() ?: "",
                destinationUrl = map["destination_url"]?.toString() ?: ""
            )
        }
    }

    /**
     * Serializes element details back to Map representation.
     */
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "value" to value,
            "destination_url" to destinationUrl
        )
    }
}
