package com.example.pharmacyarg.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.util.Log.VERBOSE
import android.widget.Toast
import com.example.pharmacyarg.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*


/**
 * Created by juanbrusco on 07/09/2020.
 */

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

    fun parseDate(date: String, context: Context): String {
        var parsedDate = mutableMapOf("day" to "", "date" to "", "time" to "")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                val zonedDateTime: LocalDateTime =
                    LocalDateTime.parse(date)
                parsedDate["day"] = daysArray[zonedDateTime.dayOfWeek.value]
                parsedDate["date"] =
                    zonedDateTime.dayOfMonth.toString() + "/" + zonedDateTime.monthValue.toString()
                parsedDate["time"] =
                    zonedDateTime.hour.toString() + ":" + zonedDateTime.minute.toString() + "hs"
            } catch (e: Exception) {
                Log.e("mDate", e.toString())
            }
        } else {
            try {
                val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                val date: Date = df.parse(date)
                val cal = GregorianCalendar()
                cal.time = date
                parsedDate["day"] = daysArray[cal[Calendar.DAY_OF_WEEK] - 1]
                parsedDate["date"] =
                    cal[Calendar.DATE].toString() + "/" + (cal[Calendar.MONTH] + 1).toString()
                parsedDate["time"] =
                    cal[Calendar.HOUR].toString() + ":" + cal[Calendar.MINUTE].toString() + "hs"
            } catch (e: Exception) {
                Log.e("mDate", e.toString()) // this never gets called either
            }
        }

        return context.getString(R.string.date_to) + parsedDate["day"] + " " + parsedDate["date"] + context.getString(
            R.string.time_to
        ) + parsedDate["time"]
    }

    fun getCurrentDay(): Map<String, String> {
        var dateInfo =
            mutableMapOf("day" to "", "month" to "", "year" to "", "hour" to "", "minutes" to "")

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                val zonedDateTime: ZonedDateTime =
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                dateInfo["day"] = zonedDateTime.dayOfMonth.toString()
                dateInfo["month"] = zonedDateTime.monthValue.toString()
                dateInfo["year"] = zonedDateTime.year.toString()
                dateInfo["hour"] = zonedDateTime.hour.toString()
                dateInfo["minutes"] = zonedDateTime.minute.toString()
            } catch (e: Exception) {
                Log.e("getCurrentDay error", e.toString())
                showToast(activity.getString(R.string.date_error))
            }
        } else {
            try {
                val calendar = GregorianCalendar()
                calendar.timeZone = TimeZone.getDefault()
                dateInfo["day"] = calendar[Calendar.DATE].toString()
                dateInfo["month"] = (calendar[Calendar.MONTH] + 1).toString()
                dateInfo["year"] = calendar[Calendar.YEAR].toString()
                dateInfo["hour"] = calendar[Calendar.HOUR].toString()
                dateInfo["minutes"] = calendar[Calendar.MINUTE].toString()
            } catch (e: Exception) {
                Log.e("getCurrentDay error", e.toString())
                showToast(activity.getString(R.string.date_error))
            }
        }
        Log.i("getCurrentDay", "TODAY--------->" + dateInfo)
        return dateInfo;
    }

    fun getNextDay(): Map<String, String> {
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
                showToast(activity.getString(R.string.date_error))
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
                showToast(activity.getString(R.string.date_error))
            }
        }
        Log.i("getNextDay", "TOMORROW--------->" + dateInfo)
        return dateInfo
    }

    fun getPreviousDay(): Map<String, String> {
        var dateInfo = mutableMapOf("day" to "", "month" to "", "year" to "")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                val zonedDateTime: ZonedDateTime =
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                val nextZonedDateTime: ZonedDateTime = zonedDateTime.minusDays(1)
                dateInfo["day"] = nextZonedDateTime.dayOfMonth.toString()
                dateInfo["month"] = nextZonedDateTime.monthValue.toString()
                dateInfo["year"] = nextZonedDateTime.year.toString()
            } catch (e: Exception) {
                Log.e("getNextDay error", e.toString())
                showToast(activity.getString(R.string.date_error))
            }
        } else {
            try {
                var calendar = GregorianCalendar()
                calendar.timeZone = TimeZone.getDefault()
                calendar.add(Calendar.DATE, -1)
                dateInfo["day"] = calendar[Calendar.DATE].toString()
                dateInfo["month"] = (calendar[Calendar.MONTH] + 1).toString()
                dateInfo["year"] = calendar[Calendar.YEAR].toString()
            } catch (e: Exception) {
                Log.e("getNextDay error", e.toString())
                showToast(activity.getString(R.string.date_error))
            }
        }
        Log.i("getPreviousDay", "YESTERDAY--------->" + dateInfo)
        return dateInfo
    }

    private fun showToast(error: String) {
        Toast.makeText(
            activity,
            error,
            Toast.LENGTH_SHORT
        ).show()
    }
}
