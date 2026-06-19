package com.devagarwal.ads_sdk_integration_android.features.ads.domain.usecase

import com.devagarwal.ads_sdk_integration_android.core.error.Failure
import com.devagarwal.ads_sdk_integration_android.core.utils.Result
import com.devagarwal.ads_sdk_integration_android.features.ads.domain.repository.AdsRepository
import javax.inject.Inject

/**
 * Parameters class representing required details to attribute and track a user click.
 * We bundle these in a data class to adhere to the Clean Architecture design pattern of
 * passing a single parameter object to use cases.
 */
data class TrackClickParams(
    val uclid: String,
    val clickTrackingUrl: String?
)

/**
 * TrackClickUseCase encapsulates the business logic of verifying and reporting
 * a click event when a user taps a loaded banner ad.
 */
class TrackClickUseCase @Inject constructor(
    private val repository: AdsRepository
) {
    /**
     * Executes the use case.
     * We overload the invoke operator so the use case can be called directly as a function
     * (e.g. `trackClickUseCase(params)`).
     */
    suspend operator fun invoke(params: TrackClickParams): Result<Unit, Failure> {
        return repository.trackClick(
            uclid = params.uclid,
            clickTrackingUrl = params.clickTrackingUrl
        )
    }
}
