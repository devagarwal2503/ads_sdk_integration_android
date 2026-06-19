package com.devagarwal.ads_sdk_integration_android.features.ads.data.dto

import android.net.Uri

/**
 * AdModel represents the structural representation of the advertisement data returned by the backend API/SDK.
 *
 * It contains robust deserialization helper methods to normalize varying response shapes into a
 * clean and uniform object structure.
 */
data class AdModel(
    val elements: ElementModel?,
    val impressionTrackingUrl: String?,
    val clickTrackingUrl: String?,
    val width: Double?,
    val height: Double?,
    val uclid: String?
) {
    companion object {
        /**
         * Safely normalizes and parses a raw SDK response map into our typed [AdModel].
         * Handles field mismatches, type coercions, list-to-object fallbacks, and dimension extraction.
         */
        fun fromMap(map: Map<String, Any?>): AdModel {
            val rawElements = map["elements"]
            
            // The elements property could arrive as a single map or a list of maps.
            // We check both formats to guarantee compatibility.
            val parsedElements = when (rawElements) {
                is Map<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    ElementModel.fromMap(rawElements as Map<String, Any?>)
                }
                is List<*> -> {
                    if (rawElements.isNotEmpty() && rawElements.first() is Map<*, *>) {
                        @Suppress("UNCHECKED_CAST")
                        ElementModel.fromMap(rawElements.first() as Map<String, Any?>)
                    } else null
                }
                else -> null
            }

            // Extract dimensions: width and height can arrive as numeric values or string numbers.
            val rawWidth = map["width"]
            var widthValue = when (rawWidth) {
                is Number -> rawWidth.toDouble()
                is String -> rawWidth.toDoubleOrNull()
                else -> null
            }

            val rawHeight = map["height"]
            var heightValue = when (rawHeight) {
                is Number -> rawHeight.toDouble()
                is String -> rawHeight.toDoubleOrNull()
                else -> null
            }

            // Fallback: If root-level dimensions are missing, check if they are nested inside elements.
            if (widthValue == null && rawElements is Map<*, *>) {
                val elWidth = rawElements["width"]
                widthValue = when (elWidth) {
                    is Number -> elWidth.toDouble()
                    is String -> elWidth.toDoubleOrNull()
                    else -> null
                }
            }
            if (heightValue == null && rawElements is Map<*, *>) {
                val elHeight = rawElements["height"]
                heightValue = when (elHeight) {
                    is Number -> elHeight.toDouble()
                    is String -> elHeight.toDoubleOrNull()
                    else -> null
                }
            }

            // Parse impression tracking URL. Can be a single String or a List.
            val rawImpression = map["impression_tracking_url"]
            val parsedImpressionUrl = when (rawImpression) {
                is String -> rawImpression
                is List<*> -> if (rawImpression.isNotEmpty()) rawImpression.first()?.toString() else null
                else -> null
            }

            // Parse click tracking URL. Can be a single String or a List.
            val rawClick = map["click_tracking_url"]
            val parsedClickUrl = when (rawClick) {
                is String -> rawClick
                is List<*> -> if (rawClick.isNotEmpty()) rawClick.first()?.toString() else null
                else -> null
            }

            // Fallback: Extract the UCLID (Unique Click Identifier) from the map or parse it out of URL query parameters.
            var parsedUclid = map["uclid"]?.toString()
            if (parsedUclid.isNullOrEmpty()) {
                parsedUclid = extractUclid(parsedClickUrl) ?: extractUclid(parsedImpressionUrl)
            }

            return AdModel(
                elements = parsedElements,
                impressionTrackingUrl = parsedImpressionUrl,
                clickTrackingUrl = parsedClickUrl,
                width = widthValue,
                height = heightValue,
                uclid = parsedUclid
            )
        }

        /**
         * Helper that extracts the 'uclid' query parameter from a tracking URL if it is present.
         */
        private fun extractUclid(url: String?): String? {
            if (url.isNullOrEmpty()) return null
            return try {
                val uri = Uri.parse(url)
                uri.getQueryParameter("uclid")
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Serializes our model back into a Map structure to pass cleanly back into SDK/analytics APIs.
     */
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "elements" to elements?.toMap(),
            "impression_tracking_url" to impressionTrackingUrl,
            "click_tracking_url" to clickTrackingUrl,
            "width" to width,
            "height" to height,
            "uclid" to uclid
        )
    }
}
