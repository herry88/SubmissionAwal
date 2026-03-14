package com.belajar.submissionawal.data.retrofit

import com.belajar.submissionawal.data.response.DetailResponse
import com.belajar.submissionawal.data.response.EventResponse
import retrofit2.http.*

interface ApiService {
    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int
    ): EventResponse

    @GET("events")
    suspend fun searchEvents(
        @Query("active") active: Int = -1,
        @Query("q") query: String
    ): EventResponse

    @GET("events/{id}")
    suspend fun getDetailEvent(
        @Path("id") id: String
    ): DetailResponse

    @GET("events")
    suspend fun getNearestEvent(
        @Query("active") active: Int = -1,
        @Query("limit") limit: Int = 1
    ): EventResponse
}
