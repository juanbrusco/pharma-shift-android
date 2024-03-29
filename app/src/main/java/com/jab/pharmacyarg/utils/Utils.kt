package com.jab.pharmacyarg.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.jab.pharmacyarg.R
import com.jab.pharmacyarg.model.entities.PharmacyX
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*


/**
 * Created by juanbrusco on 07/09/2020.
 */

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class Utils(private val activity: Activity) {

    private val daysArray = arrayOf(
        activity.getString(R.string.sunday),
        activity.getString(R.string.monday),
        activity.getString(R.string.tuesday),
        activity.getString(R.string.wednesday),
        activity.getString(R.string.thursday),
        activity.getString(R.string.friday),
        activity.getString(R.string.saturday)
    )

    @SuppressLint("SimpleDateFormat")
    fun parseDate(date: String, context: Context): String {
        val parsedDate = mutableMapOf("day" to "", "date" to "", "time" to "")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                val zonedDateTime: LocalDateTime =
                    LocalDateTime.parse(date)
                parsedDate["day"] = daysArray[zonedDateTime.dayOfWeek.value]
                parsedDate["date"] =
                    zonedDateTime.dayOfMonth.toString() + "/" + zonedDateTime.monthValue.toString()
                val h = zonedDateTime.hour.toString()
                var m = zonedDateTime.minute.toString()
                m = if (m == "0") "00" else m
                parsedDate["time"] =
                    h + ":" + m + "hs"
            } catch (e: Exception) {
                Log.e("parseDate mDate error", e.toString())
                Log.i("parseDate mDate error", e.toString())
            }
        } else {
            try {
                val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                val dateVal: Date = df.parse(date)
                val cal = GregorianCalendar()
                cal.time = dateVal
                parsedDate["day"] = daysArray[cal[Calendar.DAY_OF_WEEK] - 1]
                parsedDate["date"] =
                    cal[Calendar.DATE].toString() + "/" + (cal[Calendar.MONTH] + 1).toString()
                val h = cal[Calendar.HOUR_OF_DAY].toString()
                var m = cal[Calendar.MINUTE].toString()
                m = if (m == "0") "00" else m
                parsedDate["time"] =
                    h + ":" + m + "hs"
            } catch (e: Exception) {
                Log.e("parseDate mDate error", e.toString())
                Log.i("parseDate mDate error", e.toString())
            }
        }

        return context.getString(R.string.date_to) + parsedDate["day"] + " " + parsedDate["date"] + context.getString(
            R.string.time_to
        ) + parsedDate["time"]
    }

    fun getDay(dayIndicator: Int): Map<String, String> {
        // 0: current day
        // 1: +1 day
        // -1: -1 day
        val dateInfo =
            mutableMapOf("day" to "", "month" to "", "year" to "", "hour" to "", "minutes" to "")

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                var zonedDateTime: ZonedDateTime =
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                if (dayIndicator == -1) {
                    zonedDateTime = zonedDateTime.minusDays(1)
                } else if (dayIndicator == 1) {
                    zonedDateTime = zonedDateTime.plusDays(1)
                }
                dateInfo["day"] = zonedDateTime.dayOfMonth.toString()
                dateInfo["month"] = zonedDateTime.monthValue.toString()
                dateInfo["year"] = zonedDateTime.year.toString()
                dateInfo["hour"] = zonedDateTime.hour.toString()
                dateInfo["minutes"] = zonedDateTime.minute.toString()
            } catch (e: Exception) {
                Log.e("getDay error", e.toString())
                Log.i("getDay error", e.toString())
                showToast(activity.getString(R.string.date_error))
            }
        } else {
            try {
                val calendar = GregorianCalendar()
                calendar.timeZone = TimeZone.getDefault()
                if (dayIndicator == -1) {
                    calendar.add(Calendar.DATE, -1)
                } else if (dayIndicator == 1) {
                    calendar.add(Calendar.DATE, 1)
                }
                dateInfo["day"] = calendar[Calendar.DATE].toString()
                dateInfo["month"] = (calendar[Calendar.MONTH] + 1).toString()
                dateInfo["year"] = calendar[Calendar.YEAR].toString()
                dateInfo["hour"] = calendar[Calendar.HOUR_OF_DAY].toString()
                dateInfo["minutes"] = calendar[Calendar.MINUTE].toString()
            } catch (e: Exception) {
                Log.e("getDay error", e.toString())
                Log.i("getDay error", e.toString())
                showToast(activity.getString(R.string.date_error))
            }
        }
        Log.i("getDay", "($dayIndicator)getDay->$dateInfo")
        return dateInfo
    }

    fun displayInformationPopup() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(activity, R.style.AlertDialogCustom))
        with(builder)
        {
            setTitle(activity.getString(R.string.info_description))
            setMessage(activity.getString(R.string.app_description))
            setPositiveButton(activity.getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

    fun sharePharmacyData(pharmacyObj: PharmacyX, context: Context) {
        val intent = Intent()

        intent.action = Intent.ACTION_SEND
        intent.putExtra(
            Intent.EXTRA_TEXT,
            activity.getString(R.string.share_msg) + pharmacyObj.name + " / " + pharmacyObj.address + " / " + pharmacyObj.phone
        )
        intent.type = "text/plain"
        context.startActivity(
            Intent.createChooser(
                intent,
                activity.getString(R.string.share_title)
            )
        )
    }

    fun showToast(msg: String) {
        Toast.makeText(
            activity,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun calculateShiftTIme(hour_limit: Int, minutes_limit: Int): Map<String, String> {
        var today: Map<String, String> =
            mutableMapOf("day" to "", "month" to "", "year" to "", "hour" to "", "minutes" to "")
        var yesterday: Map<String, String> =
            mutableMapOf("day" to "", "month" to "", "year" to "", "hour" to "", "minutes" to "")
        today = this.getDay(0)
        yesterday = this.getDay(-1)
        var params = today
        if ((today["hour"] ?: error("")).toInt() < hour_limit) {
            params = yesterday
        } else if ((today["hour"] ?: error("")).toInt() == hour_limit && (today["minutes"] ?: error(
                ""
            )).toInt() < minutes_limit
        ) {
            params = yesterday
        }
        return params
    }
}
