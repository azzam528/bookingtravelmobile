package com.travelku.app.api

import retrofit2.Response
import retrofit2.http.GET

data class HomeResponse(
    val message: String
)

interface ApiService {

    @GET("/")
    suspend fun getHome(): Response<HomeResponse>
}