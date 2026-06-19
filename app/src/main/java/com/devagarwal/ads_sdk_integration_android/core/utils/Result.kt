package com.devagarwal.ads_sdk_integration_android.core.utils

/**
 * Result is a functional/monadic wrapper representing either a successful outcome or a failure.
 * This is a classic pattern in Clean Architecture to model domain errors explicitly as data,
 * rather than throwing/catching arbitrary exceptions, which makes our flows predictable.
 *
 * @param S The type of the Success value.
 * @param F The type of the Failure value.
 */
sealed interface Result<out S, out F> {
    
    // Represents a successful result containing the output value.
    data class Success<out S>(val value: S) : Result<S, Nothing>
    
    // Represents a failed result containing the error details.
    data class FailureResult<out F>(val value: F) : Result<Nothing, F>

    // Handy check to see if the outcome is Success.
    val isSuccess: Boolean
        get() = this is Success

    // Handy check to see if the outcome is Failure.
    val isFailure: Boolean
        get() = this is FailureResult

    // Force-unwraps the success value. Throws an exception if it was actually a failure.
    fun getOrThrow(): S = when (this) {
        is Success -> value
        is FailureResult -> throw IllegalStateException("Not a success: $value")
    }

    // Force-unwraps the failure value. Throws an exception if it was actually a success.
    fun getFailureOrThrow(): F = when (this) {
        is Success -> throw IllegalStateException("Not a failure: $value")
        is FailureResult -> value
    }
}

/**
 * Inline fold helper function to cleanly process the result by matching on success and failure.
 * This is highly optimized using 'inline' so Kotlin compiles this directly into conditional checks.
 */
inline fun <S, F, R> Result<S, F>.fold(
    onSuccess: (S) -> R,
    onFailure: (F) -> R
): R = when (this) {
    is Result.Success -> onSuccess(value)
    is Result.FailureResult -> onFailure(value)
}
