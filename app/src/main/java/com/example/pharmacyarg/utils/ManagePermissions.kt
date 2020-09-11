package com.example.pharmacyarg.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Created by juanbrusco on 02/09/2020.
 */
class ManagePermissions {

    fun requestCALL(act: Activity?, code: Int) {
        ActivityCompat.requestPermissions(
            act!!,
            arrayOf(Manifest.permission.CALL_PHONE),
            code
        )
    }

    fun requestFINELOCATION(act: Activity?, code: Int) {
        ActivityCompat.requestPermissions(
            act!!,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            code
        )
    }

    fun requestCOARSELOCATION(act: Activity?, code: Int) {
        ActivityCompat.requestPermissions(
            act!!,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            code
        )
    }

    fun checkCALL(act: Activity?): Boolean {
        val result =
            ContextCompat.checkSelfPermission(act!!, Manifest.permission.CALL_PHONE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun checkFINELOCATION(act: Activity?): Boolean {
        val result =
            ContextCompat.checkSelfPermission(act!!, Manifest.permission.ACCESS_FINE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun checkCOARSELOCATION(act: Activity?): Boolean {
        val result =
            ContextCompat.checkSelfPermission(act!!, Manifest.permission.ACCESS_COARSE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

}