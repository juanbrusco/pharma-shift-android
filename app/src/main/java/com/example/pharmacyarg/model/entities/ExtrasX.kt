package com.example.pharmacyarg.model.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ExtrasX(
    @SerializedName("id")
    val id: Int,
    @SerializedName("msg")
    val msg: String,
    @SerializedName("city_extra")
    val city_extra: CityX,
    @SerializedName("date_to_display")
    val date_to_display: String?,
    @SerializedName("added_by")
    val added_by: UserX?
) : Parcelable