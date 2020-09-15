package com.jab.pharmacyarg.model.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ExtrasResponse(
    @SerializedName("extras")
    val extras: ArrayList<ExtrasX>
) : Parcelable