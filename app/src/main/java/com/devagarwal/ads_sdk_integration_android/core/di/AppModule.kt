package com.devagarwal.ads_sdk_integration_android.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

/**
 * Hilt module that provides global, application-wide singletons.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides a single, configured OkHttpClient instance to make network/direct tracker calls.
     * Includes a basic logging interceptor to log request lines in Android Studio Logcat.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }
}
