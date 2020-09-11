package com.example.pharmacyarg

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacyarg.model.entities.ExtrasX
import com.example.pharmacyarg.model.entities.ShiftX
import com.example.pharmacyarg.utils.MultipleShiftsAdapter
import com.example.pharmacyarg.utils.Utils
import kotlinx.android.synthetic.main.activity_main_multi.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivityMulti : AppCompatActivity() {

    private lateinit var shiftsList: ArrayList<ShiftX>
    private lateinit var extrasList: ArrayList<ExtrasX>
    private var city = ""
    private var today: Map<String, String> =
        mutableMapOf("day" to "", "month" to "", "year" to "", "hour" to "", "minutes" to "")
    private lateinit var utils: Utils
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        shiftsList = intent.getParcelableArrayListExtra("shifts")
        extrasList = intent.getParcelableArrayListExtra("extras")
        city = intent.getStringExtra("city")
        utils = Utils(this@MainActivityMulti)
        today = utils.getDay(0)

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

        button_information_multiple?.setOnClickListener {
            utils.displayInformationPopup()
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
        if (extrasList.isNotEmpty()) {
            cardView_msg_multiple.visibility = View.VISIBLE
            var msg = ""
            for (ex in extrasList) {
                msg = msg + " " + ex.msg + "\n"
            }
            important_msg_multiple.text = msg.dropLast(1)
        } else {
            cardView_msg_multiple.visibility = View.GONE
            important_msg_multiple.text = ""
        }
    }
}