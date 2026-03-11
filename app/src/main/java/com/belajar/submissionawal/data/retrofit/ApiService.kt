package com.belajar.submissionawal.data.retrofit

import com.belajar.submissionawal.data.response.DetailResponse
import com.belajar.submissionawal.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("events")
    fun getEvents(
        @Query("active") active: Int
    ): Call<EventResponse>

    @GET("events")
    fun searchEvents(
        @Query("active") active: Int = -1,
        @Query("q") query: String
    ): Call<EventResponse>

    @GET("events/{id}")
    fun getDetailEvent(
        @Path("id") id: String
    ): Call<DetailResponse>
}
