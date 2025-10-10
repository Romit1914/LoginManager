package com.yogitechnolabs.components.classes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "UserDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS users(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "table_name TEXT," +
                    "first_name TEXT," +
                    "last_name TEXT," +
                    "gender TEXT," +
                    "dob TEXT)"
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {

    }

    fun insertUser(tableName: String, f: String, l: String, g: String, d: String) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("table_name", tableName)
            put("first_name", f)
            put("last_name", l)
            put("gender", g)
            put("dob", d)
        }
        db.insert("users", null, cv)
    }

    fun getAllUsers(tableName: String): List<Map<String, String>> {
        val db = readableDatabase
        val cursor = db.query(
            "users", arrayOf("first_name","last_name","gender","dob"),
            "table_name=?", arrayOf(tableName), null, null, null
        )
        val list = mutableListOf<Map<String,String>>()
        while(cursor.moveToNext()) {
            list.add(
                mapOf(
                    "first_name" to cursor.getString(0),
                    "last_name" to cursor.getString(1),
                    "gender" to cursor.getString(2),
                    "dob" to cursor.getString(3)
                )
            )
        }
        cursor.close()
        return list
    }
}
