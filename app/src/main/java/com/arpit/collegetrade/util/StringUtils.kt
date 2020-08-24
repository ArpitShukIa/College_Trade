package com.arpit.collegetrade.util

import androidx.core.text.HtmlCompat
import io.tempo.Tempo
import java.text.SimpleDateFormat
import java.util.*

fun getUserName(name: String): String {
    val words = name.split(" ")
    return words[0] + " " + words[1]
}

fun getErrorMessage(s: String, n: Int): String? {
    val l = s.trim()
        .replace("\n", "")
        .replace("\\s+".toRegex(), " ")
        .length

    return when {
        l < 10 -> "Minimum 10 characters are required"
        l > n -> "Maximum $n characters are allowed"
        else -> null
    }
}

fun getNotificationText(s1: String, s2: String, bold: Boolean) =
    if (bold)
        HtmlCompat.fromHtml("<b>$s1:</b> $s2", HtmlCompat.FROM_HTML_MODE_LEGACY)
    else "$s1: $s2"

fun getLastSeen(time: String?): String {
    return when (time) {
        null -> ""
        "online" -> "online"
        else -> getTime(time, 1)
    }
}

fun getTime(time: String, code: Int): String {
    val currentTime = Tempo.now() ?: System.currentTimeMillis()
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance()
    cal1.timeInMillis = time.toLong()
    cal2.timeInMillis = currentTime

    val d1 = cal1.get(Calendar.DAY_OF_YEAR)
    val y1 = cal1.get(Calendar.YEAR)
    val d2 = cal2.get(Calendar.DAY_OF_YEAR)
    val y2 = cal2.get(Calendar.YEAR)

    val ampm = arrayOf("AM", "PM")[cal1.get(Calendar.AM_PM)]
    val t = SimpleDateFormat("h:mm", Locale.US).format(cal1.time)
    val date = SimpleDateFormat("d/M/yy", Locale.US).format(cal1.time)

    return if (code == 1) {
        if (y1 == y2 && d1 == d2)
            "last seen today at $t $ampm"
        else if (y1 == y2 && d1 + 1 == d2)
            "last seen yesterday at $t $ampm"
        else
            "last seen on $date"
    } else {
        if (y1 == y2 && d1 == d2)
            "$t $ampm"
        else if (y1 == y2 && d1 + 1 == d2)
            "yesterday"
        else
            date
    }
}