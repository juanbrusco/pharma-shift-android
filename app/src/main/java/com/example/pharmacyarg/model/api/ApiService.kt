package com.example.pharmacyarg.model.api

import com.example.pharmacyarg.model.entities.CityResponse
import com.example.pharmacyarg.model.entities.ExtrasResponse
import com.example.pharmacyarg.model.entities.ShiftResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by juanbrusco on 02/09/2020.
 */
interface ApiService {
    @GET("get_shift")
    fun getShift(
        @Query("day") day: String,
        @Query("month") month: String,
        @Query("year") year: String,
        @Query("hour") hour: String,
        @Query("minutes") minutes: String,
        @Query("city_id") city: Int,
    ): Call<ShiftResponse>

    @GET("get_cities")
    fun getCities(): Call<CityResponse>

    @GET("get_extras")
    fun getExtras(
        @Query("day") day: String,
        @Query("month") month: String,
        @Query("year") year: String,
        @Query("city_id") city: Int,
    ): Call<ExtrasResponse>
}