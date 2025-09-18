package com.habitflow.domain.analytics

interface Analytics {
    fun track(event: String, params: Map<String, Any?> = emptyMap())
}

