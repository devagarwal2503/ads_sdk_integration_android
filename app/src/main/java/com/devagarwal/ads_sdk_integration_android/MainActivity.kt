package com.devagarwal.ads_sdk_integration_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.devagarwal.ads_sdk_integration_android.core.logger.AppLogger
import com.devagarwal.ads_sdk_integration_android.features.ads.presentation.screen.HomeScreen
import com.devagarwal.ads_sdk_integration_android.features.ads.presentation.viewmodel.AdViewModel
import com.devagarwal.ads_sdk_integration_android.ui.theme.Ads_sdk_integration_androidTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * MainActivity acts as the single entry point for our Jetpack Compose application.
 *
 * We annotate it with @AndroidEntryPoint to enable Hilt dependency injection.
 * This automatically injects required dependencies (like our custom logger) into the activity.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // AppLogger is injected here so we can pass it down to view components that display logs.
    @Inject
    lateinit var logger: AppLogger

    // Retrieve the ViewModel scoped to this activity. Hilt handles the ViewModel creation/lifecycle.
    private val viewModel: AdViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enables edge-to-edge content rendering, allowing our layouts to draw under system status/navigation bars.
        enableEdgeToEdge()
        
        // Define our root Jetpack Compose composition
        setContent {
            Ads_sdk_integration_androidTheme {
                HomeScreen(
                    viewModel = viewModel,
                    logger = logger,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}