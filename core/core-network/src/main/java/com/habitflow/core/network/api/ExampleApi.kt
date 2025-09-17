package com.habitflow.core.network.api

import retrofit2.http.GET

interface ExampleApi {
    @GET("health")
    suspend fun health(): String
}

