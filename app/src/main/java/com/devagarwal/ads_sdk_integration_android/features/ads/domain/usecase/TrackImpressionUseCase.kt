package com.devagarwal.ads_sdk_integration_android.features.ads.domain.usecase

import com.devagarwal.ads_sdk_integration_android.core.error.Failure
import com.devagarwal.ads_sdk_integration_android.core.utils.Result
import com.devagarwal.ads_sdk_integration_android.features.ads.domain.repository.AdsRepository
import javax.inject.Inject

/**
 * Parameters class representing required details to attribute and track an impression.
 * We bundle these in a data class to adhere to the Clean Architecture design pattern of
 * passing a single parameter object to use cases.
 */
data class TrackImpressionParams(
    val uclid: String,
    val impressionTrackingUrl: String?
)

/**
 * TrackImpressionUseCase encapsulates the business logic of verifying and reporting
 * an impression event when a banner ad is confirmed visible.
 */
class TrackImpressionUseCase @Inject constructor(
    private val repository: AdsRepository
) {
    /**
     * Executes the use case.
     * We overload the invoke operator so the use case can be called directly as a function
     * (e.g. `trackImpressionUseCase(params)`).
     */
    suspend operator fun invoke(params: TrackImpressionParams): Result<Unit, Failure> {
        return repository.trackImpression(
            uclid = params.uclid,
            impressionTrackingUrl = params.impressionTrackingUrl
        )
    }
}
