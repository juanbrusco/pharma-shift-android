package com.example.pharmacyarg.model.entities

import android.os.Parcel
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
    val date_to: String?,
    @SerializedName("pharmacy")
    val pharmacy: PharmacyX
) : Parcelable, Serializable

