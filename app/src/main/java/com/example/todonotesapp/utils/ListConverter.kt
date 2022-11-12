package com.example.todonotesapp.utils

import androidx.room.TypeConverter
import com.example.todonotesapp.model.remote.NoteCheckpoints
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ListConverters {
    var gson: Gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<NoteCheckpoints> {
        if (data == null) {
            return emptyList()
        }
        val listType: Type? = object : TypeToken<List<NoteCheckpoints?>?>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<NoteCheckpoints?>?): String {
        return gson.toJson(someObjects)
    }

}

class ColorConverters{

}