package com.example.todonotesapp.utils

import androidx.compose.ui.graphics.Color
import java.util.*

object util {

    fun getRandomString() : String{
        val alphabet: List<Char> = ('a'..'z') + ('A'..'Z')

        val randomString: String = List(32) { alphabet.random() }.joinToString("")
        return randomString
    }

    fun decodeColor(color : String) : Color {
        return Color(android.graphics.Color.parseColor(color))
    }

    fun encodeColor(color : Color) : String {
        return color.toString()
    }

    fun dateFromTimeStamp(timestamp: Long) : Date {
        return Date(timestamp)
    }
}