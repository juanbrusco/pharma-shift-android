package com.jab.pharmacyarg.model.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PharmacyX(
    @SerializedName("id")
    val id: Int,
    @SerializedName("address")
    val address: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("lat")
    val lat: String?,
    @SerializedName("long")
    val long: String?,
    @SerializedName("alt")
    val alt: String?,
    @SerializedName("city")
    val city: CityX
) : Parcelable