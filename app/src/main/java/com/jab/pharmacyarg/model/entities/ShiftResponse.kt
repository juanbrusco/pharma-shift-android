package com.jab.pharmacyarg.model.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShiftResponse(
    @SerializedName("shift")
    val shift: ArrayList<ShiftX>
) : Parcelable