package com.devagarwal.ads_sdk_integration_android.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Base Application class for the project.
 *
 * Decorated with @HiltAndroidApp to trigger Hilt's code-generation, which establishes
 * the dependency injection container at the application-level lifecycle.
 */
@HiltAndroidApp
class MyApplication : Application()
