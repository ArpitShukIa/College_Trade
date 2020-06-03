package com.example.collegetrade.util

fun getErrorMessage(s: String, n: Int): String? {
    val l = s.trim()
        .replace("\n","")
        .replace("\\s+".toRegex(), " ")
        .length

    return when {
        l < 10 -> "Minimum 10 characters are required"
        l > n -> "Maximum $n characters are allowed"
        else -> null
    }
}

