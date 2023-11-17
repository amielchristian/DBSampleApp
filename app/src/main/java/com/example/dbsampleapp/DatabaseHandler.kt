package com.example.dbsampleapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler (context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object    {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "EmployeeDatabase"
        private val TABLE_CONTACTS = "EmployeeTable"
        private val KEY_ID = "id"
        private val KEY_NAME = "name"
        private val KEY_EMAIL = "email"
    }

    override fun onCreate(db: SQLiteDatabase?)   {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE" + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_NAME + " TEXT, "
                + KEY_EMAIL + "TEXT)")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int)   {
        db!!.execSQL("DROP TABLE IF EXISTS "+ TABLE_CONTACTS)
        onCreate(db)
    }

    fun addEmployee(emp: EmpModelClass):Long {
        val db = this.writableDatabase
        val contentValues = ContentValues() //collects data
        contentValues.put(KEY_ID, emp.userId)
        contentValues.put(KEY_EMAIL, emp.userEmail)

        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun viewEmployee():List<EmpModelClass>{
        val empList:ArrayList<EmpModelClass> = ArrayList<EmpModelClass>()
        val selectQuery = "SELECT * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        var cursor:Cursor? = null //pointer

        try {
            cursor = db.rawQuery(selectQuery, null) //if table is present and if may data na inside
        }
        catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var userId: Int
        var userName: String
        var userEmail: String

        if(cursor.moveToFirst()){
            do{//assign values from first row
                userId = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                userName = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                userEmail = cursor.getString(cursor.getColumnIndex(KEY_EMAIL))
                val emp = EmpModelClass(userId = userId, userName = userName, userEmail = userEmail)
                empList.add(emp)
            } while (cursor.moveToNext())
        }
        return empList
    }

    fun updateEmployee(emp: EmpModelClass):Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, emp.userId)
        contentValues.put(KEY_EMAIL, emp.userEmail)
        contentValues.put(KEY_EMAIL, emp.userEmail)

        var success = db.update(TABLE_CONTACTS, contentValues, "id="+emp.userId, null)
        db.close()
        return success
    }

    fun deleteEmployee(emp: EmpModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, emp.userId)
        val success = db.delete(TABLE_CONTACTS, "id="+emp.userId, null)
        db.close()
        return success
    }
}