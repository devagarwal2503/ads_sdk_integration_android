package com.devagarwal.ads_sdk_integration_android.features.ads.domain.usecase

import com.devagarwal.ads_sdk_integration_android.core.error.Failure
import com.devagarwal.ads_sdk_integration_android.core.utils.Result
import com.devagarwal.ads_sdk_integration_android.features.ads.domain.model.AdEntity
import com.devagarwal.ads_sdk_integration_android.features.ads.domain.repository.AdsRepository
import javax.inject.Inject

/**
 * FetchBannerAdUseCase encapsulates the domain-level workflow to retrieve a banner advertisement.
 *
 * Use cases represent single reusable pieces of business logic that coordinates repositories,
 * making it extremely easy to unit-test specific workflows without loading UI/ViewModel scopes.
 */
class FetchBannerAdUseCase @Inject constructor(
    private val repository: AdsRepository
) {
    /**
     * Executes the use case.
     * We overload the invoke operator so the use case can be called directly as a function
     * (e.g. `fetchBannerAdUseCase()`).
     */
    suspend operator fun invoke(): Result<AdEntity, Failure> {
        return repository.fetchBannerAd()
    }
}
