package com.nikhilkhairnar.testquizapp.ui.db


import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class QuestionConverter {

    @TypeConverter
    fun fromQuestionList(value: List<Question>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Question>>() {}.type
        return gson.toJson(value, type)
    }

    // Converts a JSON string into a List<Question>
    @TypeConverter
    fun toQuestionList(value: String): List<Question> {
        val gson = Gson()
        val type = object : TypeToken<List<Question>>() {}.type
        return gson.fromJson(value, type)
    }
}


