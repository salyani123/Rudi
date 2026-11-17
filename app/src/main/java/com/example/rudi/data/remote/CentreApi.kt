package com.example.rudi.data.remote

import retrofit2.http.GET

interface CentreApi {
    @GET("api/centres.json")
    suspend fun centres(): List<CentreDto>
}
