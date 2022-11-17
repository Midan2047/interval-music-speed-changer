package com.ddodang.intervalmusicspeedchanger.common.extensions

inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> {
    val result = getOrNull() ?: return Result.failure(exceptionOrNull()!!)
    return transform(result)
}

inline fun <T> Result<T>.errorMap(transform: (Throwable) -> Throwable): Result<T> {
    val exception = exceptionOrNull() ?: return Result.success(getOrNull()!!)
    return Result.failure(transform(exception))
}