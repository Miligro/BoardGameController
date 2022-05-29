package com.example.bgc

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.LocalDateTime

class MyDBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int)
    : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION){

    companion object{
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "BGCDB.db"
        val TABLE_USER = "user"
        val COLUMN_ID = "_id"
        val COLUMN_USERNAME = "username"
        val COLUMN_GAMESNUM = "gamesnum"
        val COLUMN_ADDONSNUM = "addonsnum"
        val COLUMN_LASTSYNC = "lastsync"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_USERNAME_TABLE = ("CREATE TABLE " + TABLE_USER + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_USERNAME + " TEXT," + COLUMN_GAMESNUM + " INTEGER," + COLUMN_ADDONSNUM + " INTEGER," +
                COLUMN_LASTSYNC + " TEXT)")
        db.execSQL(CREATE_USERNAME_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER)
        onCreate(db)
    }

    fun deleteAll(){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_USER")
    }

    fun addUser(user: User){
        val values = ContentValues()
        values.put(COLUMN_USERNAME, user.username)
        values.put(COLUMN_GAMESNUM, user.numberOfGames)
        values.put(COLUMN_ADDONSNUM, user.numberOfAddOns)
        values.put(COLUMN_LASTSYNC, user.lastSync)
        val db = this.writableDatabase
        db.insert(TABLE_USER, null, values)
        db.close()
    }

    fun syncUser(user: User){
        val values = ContentValues()
        values.put(COLUMN_USERNAME, user.username)
        values.put(COLUMN_GAMESNUM, user.numberOfGames)
        values.put(COLUMN_ADDONSNUM, user.numberOfAddOns)
        values.put(COLUMN_LASTSYNC, LocalDateTime.now().toString())
        val db = this.writableDatabase
        db.update(TABLE_USER, values, "_id = 1", null);
    }

    fun findUser():User? {
        val query = "SELECT * FROM $TABLE_USER WHERE $COLUMN_ID = 1"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var user: User? = null

        if (cursor.moveToFirst()){
            val id = Integer.parseInt(cursor.getString(0))
            val username = cursor.getString(1)
            val num_of_games = Integer.parseInt(cursor.getString(2))
            val num_of_add_ons = Integer.parseInt(cursor.getString(3))
            val last_sync = cursor.getString(4)
            user = User(username, num_of_games, num_of_add_ons, last_sync)
            cursor.close()
        }
        db.close()
        return user
    }

    fun deleteUser():Boolean{
        var result = false
        val query = "SELECT * FROM $TABLE_USER WHERE $COLUMN_ID = 1"

        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            db.delete(TABLE_USER, COLUMN_ID+ " = ?", arrayOf(id.toString()))
            cursor.close()
            result=true
        }
        db.close()
        return result
    }
}