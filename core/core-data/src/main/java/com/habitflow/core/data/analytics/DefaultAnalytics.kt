package com.habitflow.core.data.analytics

import com.habitflow.domain.analytics.Analytics
import timber.log.Timber
import javax.inject.Inject

class DefaultAnalytics @Inject constructor() : Analytics {
    override fun track(event: String, params: Map<String, Any?>) {
        if (params.isEmpty()) Timber.i("analytics: %s", event)
        else Timber.i("analytics: %s %s", event, params)
    }
}

