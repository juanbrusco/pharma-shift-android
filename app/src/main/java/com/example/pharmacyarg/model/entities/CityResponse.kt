package com.example.pharmacyarg.model.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CityResponse(
    @SerializedName("cities")
    val cities: ArrayList<CityX>
) : Parcelable