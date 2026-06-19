package com.devagarwal.ads_sdk_integration_android.core.di

import com.devagarwal.ads_sdk_integration_android.features.ads.data.datasource.AdsRemoteDataSource
import com.devagarwal.ads_sdk_integration_android.features.ads.data.datasource.AdsRemoteDataSourceImpl
import com.devagarwal.ads_sdk_integration_android.features.ads.data.repository.AdsRepositoryImpl
import com.devagarwal.ads_sdk_integration_android.features.ads.domain.repository.AdsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module responsible for mapping our Repository and DataSource interfaces to their
 * concrete implementations.
 *
 * We define it as an abstract class because Hilt can optimize interface-to-implementation mapping
 * using `@Binds` without generating extra class boilerplate at compile time.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds the remote data source interface to its concrete implementation.
     */
    @Binds
    @Singleton
    abstract fun bindAdsRemoteDataSource(
        impl: AdsRemoteDataSourceImpl
    ): AdsRemoteDataSource

    /**
     * Binds the main repository interface to its concrete implementation.
     */
    @Binds
    @Singleton
    abstract fun bindAdsRepository(
        impl: AdsRepositoryImpl
    ): AdsRepository
}
