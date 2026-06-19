package com.devagarwal.ads_sdk_integration_android.core.error

/**
 * Failure is a sealed class hierarchy representing typed application errors.
 *
 * Modeling errors this way allows the compiler to enforce exhaustive checking when
 * resolving failures in ViewModel or UI layers, preventing generic crash handlers.
 */
sealed class Failure(val message: String) {
    
    // Triggered when internet connections, timeouts, or network routes fail.
    class NetworkFailure(message: String) : Failure(message)
    
    // Triggered when backend API responses return error codes or bad payloads.
    class ServerFailure(message: String) : Failure(message)
    
    // Triggered if the Osmos SDK initializer hasn't completed setup prior to making ad calls.
    class SdkNotInitializedFailure(message: String) : Failure(message)
    
    // Fallback error subclass for unhandled runtime exceptions.
    class UnexpectedFailure(message: String) : Failure(message)
}
