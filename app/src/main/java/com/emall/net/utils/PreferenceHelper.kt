package com.emall.net.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object PreferenceHelper {

    private lateinit var prefs: SharedPreferences
    fun init(context: Context) {
        prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun readString(key: String): String? {
        return prefs.getString(key, "")
    }

    fun readInt(key: String): Int? {
        return prefs.getInt(key, 0)
    }

    fun readFloat(key: String): Float? {
        return prefs.getFloat(key, 1.0f)
    }

    fun readBoolean(key: String): Boolean? {
        return prefs.getBoolean(key, false)
    }

//    fun getArrayList(key: String): ArrayList<String> {
////        val gson = Gson()
////        val json = prefs.getString(key, null)
////        val type: Type = object : TypeToken<ArrayList<String>>() {}.type
////        return gson.fromJson(json, type)
//        val gson = Gson()
//        val json: String? = prefs.getString(key, null)
////        val type: Type = object : TypeToken<ArrayList<String>>() {}.type
//        val text: ArrayList<String> = gson.fromJson(json, ArrayList<String>::class.java) //
//
//        return text
//    }

    fun writeString(key: String, value: String) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putString(key, value)
            commit()
        }
    }

    fun writeBoolean(key: String, value: Boolean) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putBoolean(key, value)
            commit()
        }
    }

    fun writeInt(key: String, value: Int) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putInt(key, value)
            commit()
        }
    }

    fun clear() {
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    fun writeFloat(key: String, value: Float) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putFloat(key, value)
            commit()
        }
    }

    //    fun setArrayList(key: String, list: ArrayList<String>) {
//        val prefsEditor: SharedPreferences.Editor = prefs.edit()
////        val gson = Gson()
////        val json = gson.toJson(list)
////        prefsEditor.putString(key, json)
////        prefsEditor.apply()
//        val gson = Gson()
//        val textList: ArrayList<String> = ArrayList(list)
//        val jsonText = gson.toJson(textList)
//        prefsEditor.putString(key, jsonText)
//        prefsEditor.apply()
//    }
    fun saveArrayList(key: String, list: ArrayList<String>) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        val gson = Gson()
        val json: String = gson.toJson(list)
        prefsEditor.putString(key, json)
        prefsEditor.apply()
    }

    fun getArrayList(key: String): ArrayList<String> {
//        val gson = Gson()
//        val json: String = prefs.getString(key, emptyList<String>())!!
//        val type: Type = object : TypeToken<ArrayList<String>>() {}.getType()
//        return gson.fromJson(json, type)
        val emptyList = Gson().toJson(ArrayList<String>())
        return Gson().fromJson(
            prefs.getString(key, emptyList),
            object : TypeToken<ArrayList<String>>() {
            }.type)
    }
}