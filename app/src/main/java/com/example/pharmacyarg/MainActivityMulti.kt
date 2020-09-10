package com.example.pharmacyarg

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacyarg.model.entities.PharmacyX
import com.example.pharmacyarg.model.entities.ShiftResponse
import com.example.pharmacyarg.model.entities.ShiftX
import com.example.pharmacyarg.utils.ManagePermissions
import com.example.pharmacyarg.utils.MultipleShiftsAdapter
import com.example.pharmacyarg.utils.Utils
import kotlinx.android.synthetic.main.activity_main_multi.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main_multi.*
import kotlinx.android.synthetic.main.content_main_multi.view.*

class MainActivityMulti : AppCompatActivity() {

    private lateinit var shiftsList: ArrayList<ShiftX>
    private var city = ""

    private val permissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions

    private lateinit var utils: Utils

    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        shiftsList = intent.getParcelableArrayListExtra<ShiftX>("shifts")
        city = intent.getStringExtra("city")
        utils = Utils(this@MainActivityMulti)

        for (s in shiftsList) {
            s.date_to = utils.parseDate(s.date_to.toString(), this@MainActivityMulti)
        }

        setContentView(R.layout.activity_main_multi)

        linearLayoutManager = LinearLayoutManager(this)

        populateView()

        button_refresh_multiple?.setOnClickListener {
            val i = Intent(this@MainActivityMulti, MainActivity::class.java)
            finish()
            overridePendingTransition(0, 0)
            startActivity(i)
            overridePendingTransition(0, 0)
        }

        button_information?.setOnClickListener {
        }

    }

    private fun populateView() {
        pharmacy_city_multiple.text = city

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = linearLayoutManager

        val rvAdapter = MultipleShiftsAdapter(shiftsList)
        recyclerView.adapter = rvAdapter
        recyclerView.isFocusable = false

        getExtraMsg()
    }

    private fun getExtraMsg() {
        cardView_msg_multiple.visibility = View.GONE
    }
}