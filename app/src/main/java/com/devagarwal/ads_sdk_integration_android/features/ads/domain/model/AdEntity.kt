package com.devagarwal.ads_sdk_integration_android.features.ads.domain.model

/**
 * Domain entity representing an advertisement.
 *
 * This is a pure Kotlin data representation of an ad, completely isolated from
 * network DTOs, database models, or direct SDK-specific types. If the API changes,
 * we map those changes into this entity in the data layer, leaving our domain
 * logic and UI entirely unaffected.
 */
data class AdEntity(
    val imageUrl: String,
    val destinationUrl: String,
    val impressionTrackingUrl: String?,
    val clickTrackingUrl: String?,
    val width: Double?,
    val height: Double?,
    val uclid: String
)
