package com.example.pharmacyarg

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pharmacyarg.model.api.ApiService
import com.example.pharmacyarg.model.api.RetrofitClient
import com.example.pharmacyarg.model.entities.*
import com.example.pharmacyarg.utils.ManagePermissions
import com.example.pharmacyarg.utils.Utils
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*


class MainActivity : AppCompatActivity() {

    private var cityObj: CityX = CityX(0, "", "", "")
    private var citiesList = arrayListOf(cityObj)

    private var pharmacyObj: PharmacyX = PharmacyX(0, "", "", "", "", "", "", cityObj)
    private var profileObj: ProfileX = ProfileX(cityObj)
    private var userObj: UserX = UserX("", "", "", "", profileObj)

    private var shiftObj: ShiftX = ShiftX(0, "", "", pharmacyObj, cityObj, userObj)
    private var shiftsList = arrayListOf(shiftObj)
    private val shiftResponseObj: ShiftResponse = ShiftResponse(shift = shiftsList)

    private var day = ""
    private var month = ""
    private var year = ""
    private var hour = ""
    private var minutes = ""

    private var APP_CITY = "Salto"
    private var CITY = ""
    private var CITY_ID = 0

    private lateinit var utils: Utils

    private val permissionsRequestCode = 123
    private val permissionsRequestCodeCall = 42
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

        getCities()
    }

    private fun prepareSimpleScreenButtons() {
        button_call?.setOnClickListener {
            requestCallPermission()
        }

        button_address?.setOnClickListener {
            requestGeoPermission()
        }

        button_share?.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT,
                this@MainActivity.getString(R.string.share_msg) + pharmacyObj.name + " / " + pharmacyObj.address + " / " + pharmacyObj.phone
            )
            intent.type = "text/plain"
            startActivity(
                Intent.createChooser(
                    intent,
                    this@MainActivity.getString(R.string.share_title)
                )
            )
        }

        button_refresh?.setOnClickListener {
            val i = Intent(this@MainActivity, MainActivity::class.java)
            finish()
            overridePendingTransition(0, 0)
            startActivity(i)
            overridePendingTransition(0, 0)
        }
    }

    private fun getCities() {
        val request = RetrofitClient.buildService(ApiService::class.java)
        val call = request.getCities()
        call.enqueue(object : Callback<CityResponse> {
            override fun onResponse(call: Call<CityResponse>, response: Response<CityResponse>) {
                if (response.isSuccessful) {
                    citiesList = response.body()?.cities!!
                    if (citiesList.size > 0) {
                        val filteredCity: CityX? = citiesList.find { it.name!!.contains(APP_CITY) }
                        if (filteredCity !== null) {
                            CITY =
                                filteredCity.name + ", " + filteredCity.province_state!! + ", " + filteredCity.country!!
                            CITY_ID = filteredCity.id
                        }
                        getTodayData()
                    }
                } else {
                    displayMainError(this@MainActivity.getString(R.string.response_error))
                }
            }

            override fun onFailure(call: Call<CityResponse>, t: Throwable) {
                displayMainError(this@MainActivity.getString(R.string.request_error))
            }
        })
    }

    private fun getExtraMsg() {
        // TODO: call api to get extra msg
//        cardView_msg.visibility = VISIBLE
    }

    private fun openMultipleView(shifts: ArrayList<ShiftX>) {
        val intent = Intent(this@MainActivity, MainActivityMulti::class.java)
        intent.putParcelableArrayListExtra("shifts", shifts)
        intent.putExtra("city", CITY)
        startActivity(intent)
        finish()
    }

    private fun getTodayData() {
        val request = RetrofitClient.buildService(ApiService::class.java)
        val call = request.getShift(CITY_ID)
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
                            pharmacy_city.text = CITY

                            // TODO: use "?" to check if value comes from service
                            pharmacyObj = shifts[0].pharmacy

                            utils = Utils(this@MainActivity)
                            var parsedDate =
                                utils.parseDate(shifts[0].date_to.toString(), this@MainActivity)

                            pharmacy_name.text = shifts[0].pharmacy.name
                            pharmacy_date_to.text = parsedDate
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
                        pharmacy_name_tomorrow.text = ""
                        cardView_tomorrow.visibility = GONE
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
            nextDay["year"].toString(),
            CITY_ID
        )
        call.enqueue(object : Callback<ShiftResponse> {
            override fun onResponse(call: Call<ShiftResponse>, response: Response<ShiftResponse>) {
                if (response.isSuccessful) {
                    val shifts: ArrayList<ShiftX> = response.body()?.shift!!
                    if (shifts.isNotEmpty()) {
                        // TODO: handle empty value (?)
                        pharmacy_name_tomorrow.text = shifts[0].pharmacy.name
                    } else {
                        pharmacy_name_tomorrow.text = ""
                        cardView_tomorrow.visibility = GONE
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

    private fun requestCallPermission() {
        val list = listOf<String>(
            Manifest.permission.CALL_PHONE
        )
        managePermissions = ManagePermissions(this)
        // If version is lower than M, permissions are accepted on app installation
        var granted = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (!managePermissions.Check_CALL(this@MainActivity)) {
                granted = false
                managePermissions.Request_CALL(this@MainActivity, permissionsRequestCodeCall);
            } else {
                granted = true
            }

        if (granted) {
            try {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + pharmacyObj.phone))
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun requestGeoPermission() {
        var parsedGeo = ""
        if (pharmacyObj.lat!!.isEmpty() || pharmacyObj.long!!.isEmpty()) {
            parsedGeo =
                "geo:0,0?q=" + Uri.encode(pharmacyObj.address + "," + pharmacyObj.city.name + "," + pharmacyObj.city.province_state)
        } else {
            parsedGeo = "geo:0,0?q=" + pharmacyObj.lat + "," + pharmacyObj.long + "?z=21"
        }
        managePermissions = ManagePermissions(this)
        // If version is lower than M, permissions are accepted on app installation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!managePermissions.Check_FINE_LOCATION(this@MainActivity)) {
                managePermissions.Request_FINE_LOCATION(this@MainActivity, permissionsRequestCode);
            } else if (!managePermissions.Check_COARSE_LOCATION(this@MainActivity)) {
                managePermissions.Request_COARSE_LOCATION(
                    this@MainActivity,
                    permissionsRequestCode
                );
            } else {
                // open geolocation activity
                val gmmIntentUri = Uri.parse(parsedGeo)
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                mapIntent.resolveActivity(packageManager)?.let {
                    startActivity(mapIntent)
                }
            }
        } else {
            // open geolocation activity
            val gmmIntentUri = Uri.parse(parsedGeo)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            mapIntent.resolveActivity(packageManager)?.let {
                startActivity(mapIntent)
            }
        }
    }

    private fun getCurrentDay() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                val zonedDateTime: ZonedDateTime =
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                day = zonedDateTime.dayOfMonth.toString()
                month = zonedDateTime.monthValue.toString()
                year = zonedDateTime.year.toString()
                hour = zonedDateTime.hour.toString()
                minutes = zonedDateTime.minute.toString()
            } catch (e: Exception) {
                Log.e("getCurrentDay error", e.toString())
                displayMainError(this@MainActivity.getString(R.string.date_error))
            }
        } else {
            try {
                val calendar = GregorianCalendar()
                calendar.timeZone = TimeZone.getDefault()
                day = calendar[Calendar.DATE].toString()
                month = (calendar[Calendar.MONTH] + 1).toString()
                year = calendar[Calendar.YEAR].toString()
                hour = calendar[Calendar.HOUR].toString()
                minutes = calendar[Calendar.MINUTE].toString()
            } catch (e: Exception) {
                Log.e("getCurrentDay error", e.toString())
                displayMainError(this@MainActivity.getString(R.string.date_error))
            }
        }
    }

    private fun getNextDay(): Map<String, String> {
        var dateInfo = mutableMapOf("day" to "", "month" to "", "year" to "")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                val zonedDateTime: ZonedDateTime =
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                val nextZonedDateTime: ZonedDateTime = zonedDateTime.plusDays(1)
                dateInfo["day"] = nextZonedDateTime.dayOfMonth.toString()
                dateInfo["month"] = nextZonedDateTime.monthValue.toString()
                dateInfo["year"] = nextZonedDateTime.year.toString()
            } catch (e: Exception) {
                Log.e("getNextDay error", e.toString())
                displayMainError(this@MainActivity.getString(R.string.date_error))
            }
        } else {
            try {
                var calendar = GregorianCalendar()
                calendar.timeZone = TimeZone.getDefault()
                calendar.add(Calendar.DATE, 1)
                dateInfo["day"] = calendar[Calendar.DATE].toString()
                dateInfo["month"] = (calendar[Calendar.MONTH] + 1).toString()
                dateInfo["year"] = calendar[Calendar.YEAR].toString()
            } catch (e: Exception) {
                Log.e("getNextDay error", e.toString())
                displayMainError(this@MainActivity.getString(R.string.date_error))
            }
        }
        return dateInfo
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