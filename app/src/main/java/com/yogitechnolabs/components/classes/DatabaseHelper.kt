package com.yogitechnolabs.components.classes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "UserDB", null, 2) {

    private val TABLE_NAME = "users_table"

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
        CREATE TABLE IF NOT EXISTS $TABLE_NAME (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            table_name TEXT,
            first_name TEXT,
            last_name TEXT,
            gender TEXT,
            dob TEXT,
            selections TEXT
        )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE users_table ADD COLUMN selections TEXT DEFAULT ''")
            } catch (e: Exception) {
                // already table not exist, so create table
                val createTable = """
            CREATE TABLE IF NOT EXISTS users_table (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                table_name TEXT,
                first_name TEXT,
                last_name TEXT,
                gender TEXT,
                dob TEXT,
                selections TEXT
            )
            """.trimIndent()
                db.execSQL(createTable)
            }
        }
    }

    fun insertUser(tableName: String, f: String, l: String, g: String, d: String, selections: String) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("table_name", tableName)
            put("first_name", f)
            put("last_name", l)
            put("gender", g)
            put("dob", d)
            put("selections", selections)
        }
        db.insert(TABLE_NAME, null, cv)  // ✅ use consistent table name
    }

    fun getAllUsers(tableName: String): List<Map<String, String>> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf("first_name","last_name","gender","dob","selections"),
            "table_name=?",
            arrayOf(tableName),
            null, null, null
        )
        val list = mutableListOf<Map<String,String>>()
        while(cursor.moveToNext()) {
            list.add(
                mapOf(
                    "first_name" to cursor.getString(0),
                    "last_name" to cursor.getString(1),
                    "gender" to cursor.getString(2),
                    "dob" to cursor.getString(3),
                    "selections" to cursor.getString(4)
                )
            )
        }
        cursor.close()
        return list
    }
}
