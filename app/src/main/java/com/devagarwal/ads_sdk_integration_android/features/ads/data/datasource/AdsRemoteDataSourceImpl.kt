package com.devagarwal.ads_sdk_integration_android.features.ads.data.datasource

import com.devagarwal.ads_sdk_integration_android.features.ads.data.dto.AdsResponseModel
import com.devagarwal.ads_sdk_integration_android.sdk.OsmosAdService
import javax.inject.Inject

/**
 * Implementation of AdsRemoteDataSource that acts as a bridge between our core data layer
 * and the OsmosAdService SDK wrapper.
 *
 * This isolation ensures that if the SDK changes its API or format, we only need to modify
 * this data source class rather than leaking SDK implementation details throughout the codebase.
 */
class AdsRemoteDataSourceImpl @Inject constructor(
    private val osmosAdService: OsmosAdService
) : AdsRemoteDataSource {

    /**
     * Fetches ads from the SDK.
     * We map the raw Map<String, Any?> returned by the SDK into our structured AdsResponseModel.
     * Throws an exception if the SDK response is null or missing, which is handled upstream.
     */
    override suspend fun fetchDisplayAds(): AdsResponseModel {
        val response = osmosAdService.fetchDisplayAds() ?: throw Exception("Received null ad response from SDK")
        return AdsResponseModel.fromMap(response)
    }
}
