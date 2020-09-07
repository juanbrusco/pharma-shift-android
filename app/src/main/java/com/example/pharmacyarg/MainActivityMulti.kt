package com.example.pharmacyarg

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacyarg.model.entities.PharmacyX
import com.example.pharmacyarg.model.entities.ShiftResponse
import com.example.pharmacyarg.model.entities.ShiftX
import com.example.pharmacyarg.utils.ManagePermissions
import com.example.pharmacyarg.utils.MultipleShiftsAdapter
import kotlinx.android.synthetic.main.activity_main_multi.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main_multi.*
import kotlinx.android.synthetic.main.content_main_multi.view.*

class MainActivityMulti : AppCompatActivity() {

    private var pharmacyObj: PharmacyX = PharmacyX(0, "", "", "", "", "", "", "")
    private var shiftObj: ShiftX = ShiftX(0, "", "", pharmacyObj)
    private var shiftsList = arrayListOf(shiftObj)
    private val shiftResponseObj: ShiftResponse = ShiftResponse(shift = shiftsList)
    private var day = ""
    private var month = ""
    private var year = ""
    private var hour = ""
    private var minutes = ""
    private var city = ""

    private val permissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions

    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_multi)

        shiftsList = intent.getParcelableArrayListExtra<ShiftX>("shifts")
        city = intent.getStringExtra("city")

        linearLayoutManager = LinearLayoutManager(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = linearLayoutManager

        val rvAdapter = MultipleShiftsAdapter(shiftsList)
        recyclerView.adapter = rvAdapter;
        getExtraMsg()
        pharmacy_city_multiple.text = city

        recyclerView.setFocusable(false);

    }

    private fun getExtraMsg() {
        cardView_msg_multiple.visibility = View.GONE
    }
}