package com.example.pharmacyarg.model.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShiftX(
    @SerializedName("id")
    val id: Int,
    @SerializedName("date_from")
    val date_from: String,
    @SerializedName("date_to")
    val date_to: String,
    @SerializedName("pharmacy")
    val pharmacy: PharmacyX
) : Parcelable