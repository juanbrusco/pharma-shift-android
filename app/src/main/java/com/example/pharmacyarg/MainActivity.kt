package com.example.pharmacyarg

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pharmacyarg.model.api.ApiService
import com.example.pharmacyarg.model.api.RetrofitClient
import com.example.pharmacyarg.model.entities.PharmacyX
import com.example.pharmacyarg.model.entities.ShiftResponse
import com.example.pharmacyarg.model.entities.ShiftX
import com.example.pharmacyarg.utils.ManagePermissions
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

//        setSupportActionBar(findViewById(R.id.toolbar))
//        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }

        setCity()
        getTodayData()
    }

    private fun prepareSimpleScreenButtons() {
        button_call?.setOnClickListener {
        }

        button_address?.setOnClickListener {
            requestGeoPermission()
        }

        button_share?.setOnClickListener {
        }

        button_refresh?.setOnClickListener {
            val i = Intent(this@MainActivity, MainActivity::class.java)
            finish()
            overridePendingTransition(0, 0)
            startActivity(i)
            overridePendingTransition(0, 0)
        }
    }

    private fun setCity() {
        city = "Salto, Buenos Aires, Argentina"
    }

    private fun getExtraMsg() {
        cardView_msg.visibility = GONE
    }

    private fun openMultipleView(shifts: ArrayList<ShiftX>) {
        val intent = Intent(this@MainActivity, MainActivityMulti::class.java)
        intent.putParcelableArrayListExtra("shifts", shifts)
        intent.putExtra("city", city)
        startActivity(intent)
        finish()
    }

    private fun getTodayData() {
        val request = RetrofitClient.buildService(ApiService::class.java)
        val call = request.getShift()
        call.enqueue(object : Callback<ShiftResponse> {
            override fun onResponse(call: Call<ShiftResponse>, response: Response<ShiftResponse>) {
                if (response.isSuccessful) {
                    val shifts: ArrayList<ShiftX> = response.body()?.shift!!
                    if (shifts.isNotEmpty()) {
                        // Multiple shifts day
                        if (shifts.size > 1) {
                            openMultipleView(shifts)
                        } else {
                            setContentView(R.layout.activity_main)
                            pharmacy_city.text = city

                            // TODO: use "?" to check if value comes from service
                            pharmacyObj = shifts[0].pharmacy
                            pharmacy_name.text = shifts[0].pharmacy.name
                            pharmacy_date_to.text = shifts[0].date_from
                            pharmacy_address.text = shifts[0].pharmacy.address
                            pharmacy_phone.text = shifts[0].pharmacy.phone

                            prepareSimpleScreenButtons()
                            getTomorrowData()
                            getExtraMsg()
                        }
                    } else {
                        pharmacy_name.text = "-"
                        pharmacy_date_to.text = "-"
                        pharmacy_address.text = "-"
                        pharmacy_phone.text = "-"
                        button_call.visibility = GONE
                        button_address.visibility = GONE
                        button_share.visibility = GONE
                    }

                } else {
                    displayMainError(this@MainActivity.getString(R.string.response_error))
                }
            }

            override fun onFailure(call: Call<ShiftResponse>, t: Throwable) {
                displayMainError(this@MainActivity.getString(R.string.request_error))
            }
        })
    }

    private fun getTomorrowData() {
        var nextDay: Map<String, String> = getNextDay()
        val request = RetrofitClient.buildService(ApiService::class.java)
        val call = request.getShiftByDay(
            nextDay["day"].toString(),
            nextDay["month"].toString(),
            nextDay["year"].toString()
        )
        call.enqueue(object : Callback<ShiftResponse> {
            override fun onResponse(call: Call<ShiftResponse>, response: Response<ShiftResponse>) {
                if (response.isSuccessful) {
                    val shifts: ArrayList<ShiftX> = response.body()?.shift!!
                    if (shifts.isNotEmpty()) {
                        pharmacy_name_tomorrow.text = shifts[0].pharmacy.name
                    } else {
                        pharmacy_name_tomorrow.text = ""
                    }

                } else {
                    displayMainError(this@MainActivity.getString(R.string.response_error))
                }
            }

            override fun onFailure(call: Call<ShiftResponse>, t: Throwable) {
                displayMainError(this@MainActivity.getString(R.string.request_error))
            }
        })
    }

    private fun displayMainError(error: String) {
        Toast.makeText(
            this@MainActivity,
            error,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun requestGeoPermission() {
        // Initialize a list of required permissions to request runtime
        val list = listOf<String>(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        // Initialize a new instance of ManagePermissions class
        managePermissions = ManagePermissions(this, list, permissionsRequestCode)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            managePermissions.checkPermissions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            permissionsRequestCode -> {
                val isPermissionsGranted = managePermissions
                    .processPermissionsResult(requestCode, permissions, grantResults)
                if (isPermissionsGranted) {
                    Toast.makeText(this, "GRANTED", Toast.LENGTH_SHORT).show()
                    //TODO: open geolocation activity
                } else {
                    Toast.makeText(this, "DENIED", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun getCurrentDay() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val zonedDateTime: ZonedDateTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
            day = zonedDateTime.dayOfMonth.toString()
            month = zonedDateTime.monthValue.toString()
            year = zonedDateTime.year.toString()
            hour = zonedDateTime.hour.toString()
            minutes = zonedDateTime.minute.toString()
        } else {
            val calendar = GregorianCalendar()
            calendar.timeZone = TimeZone.getDefault()
            day = calendar[Calendar.DATE].toString()
            month = (calendar[Calendar.MONTH] + 1).toString()
            year = calendar[Calendar.YEAR].toString()
            hour = calendar[Calendar.HOUR].toString()
            minutes = calendar[Calendar.MINUTE].toString()
        }
    }

    private fun getNextDay(): Map<String, String> {
        var dateInfo = mutableMapOf("day" to "", "month" to "", "year" to "")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val zonedDateTime: ZonedDateTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
            val nextZonedDateTime: ZonedDateTime = zonedDateTime.plusDays(1)
            dateInfo["day"] = nextZonedDateTime.dayOfMonth.toString()
            dateInfo["month"] = nextZonedDateTime.monthValue.toString()
            dateInfo["year"] = nextZonedDateTime.year.toString()
        } else {
            var calendar = GregorianCalendar()
            calendar.timeZone = TimeZone.getDefault()
            calendar.add(Calendar.DATE, 1)
            dateInfo["day"] = calendar[Calendar.DATE].toString()
            dateInfo["month"] = (calendar[Calendar.MONTH] + 1).toString()
            dateInfo["year"] = calendar[Calendar.YEAR].toString()
        }
        return dateInfo;
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}