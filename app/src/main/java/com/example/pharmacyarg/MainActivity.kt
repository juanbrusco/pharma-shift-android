package com.example.pharmacyarg

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.Dimension.SP
import androidx.appcompat.app.AlertDialog
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
import java.util.*


class MainActivity : AppCompatActivity() {

    private var cityObj: CityX = CityX(0, "", "", "")
    private var citiesList = arrayListOf(cityObj)

    private var pharmacyObj: PharmacyX = PharmacyX(0, "", "", "", "", "", "", cityObj)
//    private var profileObj: ProfileX = ProfileX(cityObj)
//    private var userObj: UserX = UserX("", "", "", "", profileObj)

//    private var shiftObj: ShiftX = ShiftX(0, "", "", pharmacyObj, cityObj, userObj)
//    private var shiftsList = arrayListOf(shiftObj)
//    private val shiftResponseObj: ShiftResponse = ShiftResponse(shift = shiftsList)

    private val APP_CITY = "Salto"
    private var CITY = ""
    private var CITY_ID = 0
    private var HOUR_LIMIT = 8
    private var MINUTES_LIMIT = 30

    private lateinit var utils: Utils
    private var today: Map<String, String> =
        mutableMapOf("day" to "", "month" to "", "year" to "", "hour" to "", "minutes" to "")
    private var tomorrow: Map<String, String> =
        mutableMapOf("day" to "", "month" to "", "year" to "", "hour" to "", "minutes" to "")
    private var yesterday: Map<String, String> =
        mutableMapOf("day" to "", "month" to "", "year" to "", "hour" to "", "minutes" to "")
    private val permissionsRequestCode = 123
    private val permissionsRequestCodeCall = 42
    private lateinit var managePermissions: ManagePermissions


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        utils = Utils(this@MainActivity)
        today = utils.getDay(0)
        tomorrow = utils.getDay(1)
        yesterday = utils.getDay(-1)
        getCities()
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
                    displayAlterError(this@MainActivity.getString(R.string.response_error))
                    Log.i("getCities error", response.message())
                }
            }

            override fun onFailure(call: Call<CityResponse>, t: Throwable) {
                displayAlterError(this@MainActivity.getString(R.string.request_error))
                Log.i("getCities error", "onFailure")
            }
        })
    }

    private fun getTodayData() {
        val params = today
//        if ((today["hour"] ?: error("")).toInt() < HOUR_LIMIT) {
//            params = yesterday
//        } else if ((today["hour"] ?: error("")).toInt() == HOUR_LIMIT && (today["minutes"] ?: error(
//                ""
//            )).toInt() < MINUTES_LIMIT
//        ) {
//            params = yesterday
//        }
        val request = RetrofitClient.buildService(ApiService::class.java)
        val call = request.getShift(
            params["day"].toString(),
            params["month"].toString(),
            params["year"].toString(),
            params["hour"].toString(),
            params["minutes"].toString(),
            CITY_ID
        )
        call.enqueue(object : Callback<ShiftResponse> {
            override fun onResponse(call: Call<ShiftResponse>, response: Response<ShiftResponse>) {
                if (response.isSuccessful) {
                    val shifts: ArrayList<ShiftX> = response.body()?.shift!!
                    if (shifts.isNotEmpty()) {
                        // Multiple shifts day
                        if (shifts.size > 1) {
                            openMultipleViewWithMsg(shifts)
                        } else {
                            setContentView(R.layout.activity_main)
                            pharmacy_city.text = CITY

                            // TODO: use "?" to check if value comes from service
                            pharmacyObj = shifts[0].pharmacy

                            val parsedDate =
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
                        setContentView(R.layout.activity_main)
                        pharmacy_city.text = CITY
                        pharmacy_name.text = this@MainActivity.getString(R.string.no_data_found)
                        pharmacy_name.setTextSize(SP, 20F)
                        pharmacy_date_to.text = "-"
                        pharmacy_address.text = "-"
                        pharmacy_phone.text = "-"
                        pharmacy_name_tomorrow.text = ""
                        button_call.visibility = GONE
                        button_address.visibility = GONE
                        button_share.visibility = GONE
                        cardView_tomorrow.visibility = GONE
                        button_refresh.visibility = GONE
                        button_information.visibility = GONE
                    }

                } else {
                    displayAlterError(this@MainActivity.getString(R.string.response_error))
                    Log.i("getTodayData error", response.message())
                }
            }

            override fun onFailure(call: Call<ShiftResponse>, t: Throwable) {
                displayAlterError(this@MainActivity.getString(R.string.request_error))
                Log.i("getTodayData error", "onFailure")
            }
        })
    }

    private fun getTomorrowData() {
        val request = RetrofitClient.buildService(ApiService::class.java)
        val call = request.getShift(
            tomorrow["day"].toString(),
            tomorrow["month"].toString(),
            tomorrow["year"].toString(),
            tomorrow["hour"].toString(),
            tomorrow["minutes"].toString(),
            CITY_ID
        )
        call.enqueue(object : Callback<ShiftResponse> {
            override fun onResponse(call: Call<ShiftResponse>, response: Response<ShiftResponse>) {
                if (response.isSuccessful) {
                    val shifts: ArrayList<ShiftX> = response.body()?.shift!!
                    if (shifts.isNotEmpty()) {
                        pharmacy_name_tomorrow.text = shifts[0].pharmacy.name
                        cardView_tomorrow.visibility = VISIBLE
                    } else {
                        pharmacy_name_tomorrow.text = ""
                        cardView_tomorrow.visibility = GONE
                    }
                } else {
                    displayAlterError(this@MainActivity.getString(R.string.response_error))
                    Log.i("getTomorrowData error", response.message())
                }
            }

            override fun onFailure(call: Call<ShiftResponse>, t: Throwable) {
                displayAlterError(this@MainActivity.getString(R.string.request_error))
                Log.i("getTomorrowData error", "onFailure")
            }
        })
    }

    private fun openMultipleViewWithMsg(shifts: ArrayList<ShiftX>) {
        getExtraMsg(shifts, true)
    }

    private fun prepareSimpleScreenButtons() {
        button_call?.setOnClickListener {
            requestCallPermission()
        }

        button_address?.setOnClickListener {
            requestGeoPermission()
        }

        button_share?.setOnClickListener {
            utils.sharePharmacyData(pharmacyObj, this)
        }

        button_refresh?.setOnClickListener {
            refreshActivity()
        }

        button_information?.setOnClickListener {
            utils.displayInformationPopup()
        }
    }


    private fun getExtraMsg(
        shifts: ArrayList<ShiftX> = arrayListOf(),
        openMultipleVIew: Boolean = false
    ) {
        val request = RetrofitClient.buildService(ApiService::class.java)
        val call = request.getExtras(
            today["day"].toString(),
            today["month"].toString(),
            today["year"].toString(),
            CITY_ID
        )
        call.enqueue(object : Callback<ExtrasResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ExtrasResponse>,
                response: Response<ExtrasResponse>
            ) {
                if (response.isSuccessful) {
                    val extras: ArrayList<ExtrasX> = response.body()?.extras!!
                    if (openMultipleVIew) {
                        val intent = Intent(this@MainActivity, MainActivityMulti::class.java)
                        intent.putParcelableArrayListExtra("shifts", shifts)
                        intent.putParcelableArrayListExtra("extras", extras)
                        intent.putExtra("city", CITY)
                        startActivity(intent)
                        finish()
                    } else {
                        if (extras.isNotEmpty()) {
                            cardView_msg.visibility = VISIBLE
                            var msg = ""
                            for (ex in extras) {
                                msg = msg + " " + ex.msg + "\n"
                            }
                            important_msg.text = msg.dropLast(1)
                        } else {
                            cardView_msg.visibility = GONE
                            important_msg.text = ""
                        }
                    }
                } else {
                    displayAlterError(this@MainActivity.getString(R.string.response_error))
                    Log.i("getTomorrowData error", response.message())
                }
            }

            override fun onFailure(call: Call<ExtrasResponse>, t: Throwable) {
                displayAlterError(this@MainActivity.getString(R.string.request_error))
                Log.i("getTomorrowData error", "onFailure")
            }
        })
    }

    private fun requestCallPermission() {
        managePermissions = ManagePermissions()
        // If version is lower than M, permissions are accepted on app installation
        var granted = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (!managePermissions.checkCALL(this@MainActivity)) {
                granted = false
                managePermissions.requestCALL(this@MainActivity, permissionsRequestCodeCall)
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
        var parsedGeo =
            "geo:0,0?q=" + Uri.encode(pharmacyObj.address + "," + pharmacyObj.city.name + "," + pharmacyObj.city.province_state)
        if (pharmacyObj.lat!!.isNotEmpty() || pharmacyObj.long!!.isNotEmpty()) {
            parsedGeo = "geo:0,0?q=" + pharmacyObj.lat + "," + pharmacyObj.long + "?z=21"
        }
        managePermissions = ManagePermissions()
        // If version is lower than M, permissions are accepted on app installation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!managePermissions.checkFINELOCATION(this@MainActivity)) {
                managePermissions.requestFINELOCATION(this@MainActivity, permissionsRequestCode)
            } else if (!managePermissions.checkCOARSELOCATION(this@MainActivity)) {
                managePermissions.requestCOARSELOCATION(
                    this@MainActivity,
                    permissionsRequestCode
                )
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

    private fun displayAlterError(error: String) {
        val alertDialog =
            AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom)).create()
        alertDialog.setTitle(this@MainActivity.getString(R.string.error_title))
        alertDialog.setMessage(error)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, this@MainActivity.getString(R.string.retry)
        ) { _, _ -> refreshActivity() }

        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE, this@MainActivity.getString(R.string.cancel)
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    private fun refreshActivity() {
        val i = Intent(this@MainActivity, MainActivity::class.java)
        finish()
        overridePendingTransition(0, 0)
        startActivity(i)
        overridePendingTransition(0, 0)
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