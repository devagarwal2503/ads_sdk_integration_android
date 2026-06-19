package com.devagarwal.ads_sdk_integration_android.features.ads.data.datasource

import com.devagarwal.ads_sdk_integration_android.features.ads.data.dto.AdsResponseModel

/**
 * Interface defining the API to access remote ad resources.
 * Isolates data fetching implementations (e.g. Retrofit, SDK client, etc.) from repositories.
 */
interface AdsRemoteDataSource {
    
    // Asynchronously retrieves active display advertisements from the remote source.
    suspend fun fetchDisplayAds(): AdsResponseModel
}
