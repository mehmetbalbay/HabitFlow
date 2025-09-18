package com.habitflow.reminder

import com.habitflow.domain.analytics.Analytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeAnalytics @Inject constructor() : Analytics {
    override fun track(event: String, params: Map<String, Any?>) { /* no-op */ }
}

