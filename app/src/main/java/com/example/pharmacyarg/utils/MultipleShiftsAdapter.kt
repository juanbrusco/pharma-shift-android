package com.example.pharmacyarg.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacyarg.R
import com.example.pharmacyarg.model.entities.ShiftX

/**
 * Created by juanbrusco on 07/09/2020.
 */

class MultipleShiftsAdapter(val shifts: ArrayList<ShiftX>) :
    RecyclerView.Adapter<MultipleShiftsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v =
            LayoutInflater.from(p0?.context).inflate(R.layout.adapter_multiple_shifts, p0, false)
        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return shifts.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.name?.text = shifts[p1].pharmacy.name
        p0.phone?.text = shifts[p1].pharmacy.phone
        p0.address?.text = shifts[p1].pharmacy.address
        p0.to?.text = shifts[p1].date_to
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.pharmacy_name_multiple)
        val phone = itemView.findViewById<TextView>(R.id.pharmacy_phone_multiple)
        val address = itemView.findViewById<TextView>(R.id.pharmacy_address_multiple)
        val to = itemView.findViewById<TextView>(R.id.pharmacy_date_to_multiple)
    }
}
