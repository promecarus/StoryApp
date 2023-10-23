package com.promecarus.storyapp.utils

import android.content.Context
import android.location.Geocoder
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.appcompat.R.attr.colorPrimary
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.promecarus.storyapp.R
import com.promecarus.storyapp.R.string.time_handle
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object HelperUtils {
    private const val secondsInMinute = 60
    private const val secondsInHour = secondsInMinute * 60
    private const val secondsInDay = secondsInHour * 24
    private const val secondsInWeek = secondsInDay * 7
    private const val secondsInMonth = secondsInDay * 30
    private const val secondsInYear = secondsInDay * 365

    fun parseDate(dateTime: String, pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") = try {
        SimpleDateFormat(pattern, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.parse(dateTime)
    } catch (e: ParseException) {
        null
    }

    fun getTimeAgo(context: Context, dateTime: String) = when (val date = parseDate(dateTime)) {
        null -> "Invalid Date"
        else -> calculateTimeAgo(context, date)
    }

    private fun calculateTimeAgo(context: Context, date: Date): String {
        val timeDifferenceMillis = System.currentTimeMillis() - date.time
        val timeDifferenceSeconds = (timeDifferenceMillis / 1000).toInt()

        return when {
            timeDifferenceSeconds <= 0 -> context.getString(time_handle)

            timeDifferenceSeconds < secondsInMinute -> getQuantityString(
                context, timeDifferenceSeconds, R.plurals.time_seconds_ago, 1
            )

            timeDifferenceSeconds < secondsInHour -> getQuantityString(
                context, timeDifferenceSeconds, R.plurals.time_minutes_ago, secondsInMinute
            )

            timeDifferenceSeconds < secondsInDay -> getQuantityString(
                context, timeDifferenceSeconds, R.plurals.time_hours_ago, secondsInHour
            )

            timeDifferenceSeconds < secondsInWeek -> getQuantityString(
                context, timeDifferenceSeconds, R.plurals.time_days_ago, secondsInDay
            )

            timeDifferenceSeconds < secondsInMonth -> getQuantityString(
                context, timeDifferenceSeconds, R.plurals.time_weeks_ago, secondsInWeek
            )

            timeDifferenceSeconds < secondsInYear -> getQuantityString(
                context, timeDifferenceSeconds, R.plurals.time_months_ago, secondsInMonth
            )

            else -> getQuantityString(
                context, timeDifferenceSeconds, R.plurals.time_years_ago, secondsInYear
            )
        }
    }

    private fun getQuantityString(
        context: Context, secondsDifference: Int, resourceId: Int, divisor: Int,
    ): String {
        val quantity = secondsDifference / divisor
        return context.resources.getQuantityString(resourceId, quantity, quantity)
    }

    @Suppress("DEPRECATION")
    fun getCity(context: Context, lat: Double?, lon: Double?, complete: Boolean = false): String? {
        if (lat == null || lon == null) return null
        try {
            val list = Geocoder(context, Locale.getDefault()).getFromLocation(lat, lon, 1)?.get(0)
            return if (complete) {
                "${list?.adminArea?.let { "$it, " } ?: ""}${list?.locality?.let { "$it, " } ?: ""}${list?.postalCode?.let { "$it, " } ?: ""}${list?.countryName ?: ""}"
            } else when {
                list?.adminArea != null -> list.adminArea
                list?.locality != null -> list.locality
                list?.postalCode != null -> list.postalCode
                list?.countryName != null -> list.countryName
                else -> null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getThemeColor(context: Context, @AttrRes attr: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    fun loadWithCircularProgress(context: Context, imageUrl: String, imageView: ImageView) {
        val circularProgressDrawable = CircularProgressDrawable(context).apply {
            strokeWidth = 5f
            centerRadius = 30f
            setColorSchemeColors(getThemeColor(context, colorPrimary))
            start()
        }

        val requestOptions = RequestOptions().placeholder(circularProgressDrawable)
            .diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter()

        Glide.with(context).load(imageUrl).apply(requestOptions).transition(withCrossFade())
            .into(imageView)
    }
}
