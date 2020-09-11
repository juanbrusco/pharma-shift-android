package com.example.pharmacyarg.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacyarg.R
import com.example.pharmacyarg.model.entities.PharmacyX
import com.example.pharmacyarg.model.entities.ShiftX
import mehdi.sakout.fancybuttons.FancyButton

/**
 * Created by juanbrusco on 07/09/2020.
 */

class MultipleShiftsAdapter(
    private val shifts: ArrayList<ShiftX>,
    private val activity: Activity,
    private val context: Context,
    private val packageManager: PackageManager
) :
    RecyclerView.Adapter<MultipleShiftsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v =
            LayoutInflater.from(p0.context).inflate(R.layout.adapter_multiple_shifts, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return shifts.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.name.text = shifts[p1].pharmacy.name
        p0.phone.text = shifts[p1].pharmacy.phone
        p0.address.text = shifts[p1].pharmacy.address
        p0.to.text = shifts[p1].date_to

        val utils = Utils(activity)
        p0.buttonaddressMultiple.setOnClickListener {
            requestGeoPermission(shifts[p1].pharmacy)
        }
        p0.buttonCallMultiple.setOnClickListener {
            requestCallPermission(shifts[p1].pharmacy.phone)
        }
        p0.buttonShareMultiple.setOnClickListener {
            utils.sharePharmacyData(shifts[p1].pharmacy, context)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.pharmacy_name_multiple)
        val phone: TextView = itemView.findViewById(R.id.pharmacy_phone_multiple)
        val address: TextView = itemView.findViewById(R.id.pharmacy_address_multiple)
        val to: TextView = itemView.findViewById(R.id.pharmacy_date_to_multiple)

        val buttonaddressMultiple: FancyButton =
            itemView.findViewById(R.id.button_address_multiple)
        val buttonCallMultiple: FancyButton = itemView.findViewById(R.id.button_call_multiple)
        val buttonShareMultiple: FancyButton = itemView.findViewById(R.id.button_share_multiple)
    }

    private fun requestGeoPermission(pharmacyObj: PharmacyX) {
        val permissionsRequestCode = 123
        var parsedGeo =
            "geo:0,0?q=" + Uri.encode(pharmacyObj.address + "," + pharmacyObj.city.name + "," + pharmacyObj.city.province_state)
        if (pharmacyObj.lat!!.isNotEmpty() || pharmacyObj.long!!.isNotEmpty()) {
            parsedGeo = "geo:0,0?q=" + pharmacyObj.lat + "," + pharmacyObj.long + "?z=21"
        }
        val managePermissions = ManagePermissions()
        // If version is lower than M, permissions are accepted on app installation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!managePermissions.checkFINELOCATION(activity)) {
                managePermissions.requestFINELOCATION(activity, permissionsRequestCode)
            } else if (!managePermissions.checkCOARSELOCATION(activity)) {
                managePermissions.requestCOARSELOCATION(
                    activity,
                    permissionsRequestCode
                )
            } else {
                // open geolocation activity
                val gmmIntentUri = Uri.parse(parsedGeo)
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                mapIntent.resolveActivity(packageManager)?.let {
                    context.startActivity(mapIntent)
                }
            }
        } else {
            // open geolocation activity
            val gmmIntentUri = Uri.parse(parsedGeo)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            mapIntent.resolveActivity(packageManager)?.let {
                context.startActivity(mapIntent)
            }
        }
    }

    private fun requestCallPermission(phone: String?) {
        val permissionsRequestCodeCall = 42
        val managePermissions = ManagePermissions()
        // If version is lower than M, permissions are accepted on app installation
        var granted = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (!managePermissions.checkCALL(activity)) {
                granted = false
                managePermissions.requestCALL(activity, permissionsRequestCodeCall)
            } else {
                granted = true
            }

        if (granted) {
            try {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone))
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
