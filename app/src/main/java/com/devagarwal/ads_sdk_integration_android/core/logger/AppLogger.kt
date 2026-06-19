package com.devagarwal.ads_sdk_integration_android.core.logger

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AppLogger acts as a centralized logging coordinator.
 *
 * It bridges classic Android system logs (Logcat) with a reactive coroutines [SharedFlow].
 * This allows screens (like ConsoleLogsView) to subscribe to system events reactively and
 * display logs on the device screen in real time.
 */
@Singleton
class AppLogger @Inject constructor() {
    private val TAG = "OsmosDemo"

    // Backing field holding the log history. We specify 'replay = 100' so that when
    // a user switches tabs to the logs view, they immediately see the last 100 logged events.
    private val _logStream = MutableSharedFlow<String>(replay = 100, extraBufferCapacity = 100)
    val logStream: SharedFlow<String> = _logStream.asSharedFlow()

    /**
     * Formats and emits a log message into the hot stream.
     */
    private fun logAndStream(level: String, message: String) {
        val formattedLog = "[$level] $message"
        _logStream.tryEmit(formattedLog)
    }

    // Prints an info-level log.
    fun info(message: String) {
        Log.i(TAG, message)
        logAndStream("INFO", message)
    }

    // Prints a debug-level log.
    fun debug(message: String) {
        Log.d(TAG, message)
        logAndStream("DEBUG", message)
    }

    // Prints a warning-level log.
    fun warning(message: String) {
        Log.w(TAG, message)
        logAndStream("WARNING", message)
    }

    // Prints an error-level log, appending standard exception messages if they are supplied.
    fun error(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
        val errorMessage = if (throwable != null) "$message: ${throwable.message}" else message
        logAndStream("ERROR", errorMessage)
    }
}
