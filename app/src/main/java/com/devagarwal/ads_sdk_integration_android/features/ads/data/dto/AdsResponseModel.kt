package com.devagarwal.ads_sdk_integration_android.features.ads.data.dto

import org.json.JSONArray
import org.json.JSONObject

/**
 * Top-level model representing the complete ad response packet returned by the Osmos SDK.
 */
data class AdsResponseModel(
    val bannerAds: List<AdModel>
) {
    companion object {
        
        /**
         * Recursively converts native JSON data types (like [JSONObject] and [JSONArray]) 
         * returned by the SDK into standard JVM collection types ([Map] and [List]).
         * 
         * This utility is crucial because the native SDK response can sometimes wrap maps 
         * and lists inside nested JSON objects, which cannot be natively pattern-matched 
         * in Kotlin without type deserialization.
         */
        fun toKotlinType(value: Any?): Any? {
            return when (value) {
                null, JSONObject.NULL -> null
                is JSONObject -> {
                    val map = mutableMapOf<String, Any?>()
                    val keys = value.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        map[key] = toKotlinType(value.get(key))
                    }
                    map
                }
                is JSONArray -> {
                    val list = mutableListOf<Any?>()
                    for (i in 0 until value.length()) {
                        list.add(toKotlinType(value.get(i)))
                    }
                    list
                }
                is Map<*, *> -> {
                    val map = mutableMapOf<String, Any?>()
                    for ((k, v) in value) {
                        map[k.toString()] = toKotlinType(v)
                    }
                    map
                }
                is List<*> -> {
                    value.map { toKotlinType(it) }
                }
                else -> value
            }
        }

        /**
         * Factory constructor to parse and map raw SDK payloads into [AdsResponseModel].
         *
         * Designed to accommodate different backend envelope formats:
         * 1. Direct structures: Map contains "ads" directly at the root.
         * 2. Wrapped responses: Map contains a root "response" node containing a "data" node
         *    either as a nested Map or as a stringified JSON payload.
         */
        fun fromMap(map: Map<String, Any?>): AdsResponseModel {
            // Recursively convert nested structures to standard Kotlin maps/lists
            val convertedMap = toKotlinType(map) as? Map<String, Any?> ?: map
            var dataMap = convertedMap
            
            // Check if the payload is wrapped under a 'response' node
            val rawResponse = convertedMap["response"]
            if (rawResponse is Map<*, *>) {
                val rawData = rawResponse["data"]
                if (rawData is Map<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    dataMap = rawData as Map<String, Any?>
                } else if (rawData is String) {
                    try {
                        // De-serialize raw stringified JSON inside the "data" wrapper
                        val jsonObject = JSONObject(rawData)
                        dataMap = toKotlinType(jsonObject) as? Map<String, Any?> ?: dataMap
                    } catch (e: Exception) {
                        // Ignore parsing issues and fallback to default map
                    }
                }
            }

            val bannerAdsList = mutableListOf<AdModel>()
            // Extract the list nested under ads -> banner_ads
            val adsMap = dataMap["ads"]
            if (adsMap is Map<*, *>) {
                val bannerAdsData = adsMap["banner_ads"]
                if (bannerAdsData is List<*>) {
                    for (item in bannerAdsData) {
                        if (item is Map<*, *>) {
                            @Suppress("UNCHECKED_CAST")
                            bannerAdsList.add(AdModel.fromMap(item as Map<String, Any?>))
                        }
                    }
                }
            }

            return AdsResponseModel(bannerAds = bannerAdsList)
        }
    }

    /**
     * Helper to serialize the model back into a Map (e.g. for mock tests or payloads).
     */
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "ads" to mapOf(
                "banner_ads" to bannerAds.map { it.toMap() }
            )
        )
    }
}

