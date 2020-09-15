package com.example.pharmacyarg.model.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class ShiftX(
    @SerializedName("id")
    val id: Int,
    @SerializedName("date_from")
    val date_from: String?,
    @SerializedName("date_to")
    var date_to: String?,
    @SerializedName("pharmacy")
    val pharmacy: PharmacyX,
    @SerializedName("city_shift")
    val city_shift: CityX,
    @SerializedName("added_by")
    val added_by: UserX
) : Parcelable, Serializable

