package com.example.pharmacyarg.model.api

import com.example.pharmacyarg.model.entities.ShiftResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by juanbrusco on 02/09/2020.
 */
interface ApiService {
    @GET("get_shift?")
    fun getShiftByDay(
        @Query("day") day: String,
        @Query("month") month: String,
        @Query("year") year: String
    ): Call<ShiftResponse>

    @GET("get_shift")
    fun getShift(): Call<ShiftResponse>
}