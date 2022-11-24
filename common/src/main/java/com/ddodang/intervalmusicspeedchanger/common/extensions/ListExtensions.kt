package com.ddodang.intervalmusicspeedchanger.common.extensions

fun <T> List<T>.getOrDefault(index: Int, default: T): T {
    return getOrNull(index) ?: default
}