package com.example.bgc

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.lang.Exception
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

        val TABLE_GAMEADDON = "gameaddon"
        val COLUMN_TITLE = "title"
        val COLUMN_IMAGE = "image"
        val COLUMN_RELEASEYEAR = "releaseyear"
        val COLUMN_RANKING = "ranking"
        val COLUMN_TYPE = "type"
        val COLUMN_TOREMOVE = "toremove"

        val TABLE_RANKINGHISTORY = "rankinghistory"
        val COLUMN_SYNCDATE = "syncdate"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_USERNAME_TABLE = ("CREATE TABLE " + TABLE_USER + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_USERNAME + " TEXT," + COLUMN_GAMESNUM + " INTEGER," + COLUMN_ADDONSNUM + " INTEGER," +
                COLUMN_LASTSYNC + " TEXT)")

        val CREATE_GAME_ADDON_TABLE = ("CREATE TABLE " + TABLE_GAMEADDON + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_TITLE + " TEXT," + COLUMN_IMAGE + " TEXT," + COLUMN_RELEASEYEAR + " INTEGER," +
                COLUMN_RANKING + " INTEGER," + COLUMN_TYPE + " TEXT," + COLUMN_TOREMOVE + " INTEGER)")

        val CREATE_RANKING_HISTORY_TABLE = ("CREATE TABLE " + TABLE_RANKINGHISTORY + "(" + COLUMN_ID + " INTEGER,"
                + COLUMN_SYNCDATE + " TEXT," + COLUMN_RANKING + " INTEGER," + "FOREIGN KEY (" + COLUMN_ID + ") REFERENCES " +
                TABLE_GAMEADDON + " ON DELETE CASCADE)");

        db.execSQL(CREATE_GAME_ADDON_TABLE)
        db.execSQL(CREATE_USERNAME_TABLE)
        db.execSQL(CREATE_RANKING_HISTORY_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GAMEADDON")
        onCreate(db)
    }

    fun deleteAllUsers(){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_USER")
    }

    fun setToRemove(remove: Int){
        val db = this.writableDatabase
        val query = "UPDATE $TABLE_GAMEADDON SET $COLUMN_TOREMOVE = $remove"
        db.execSQL(query)
    }

    fun removeOldGames(){
        val db = this.writableDatabase
        val query = "DELETE FROM $TABLE_GAMEADDON WHERE $COLUMN_TOREMOVE = 1"
        db.execSQL(query)
    }

    fun getRankingHistory(id: Long):ArrayList<GameRankingHistory>{
        val db = this.writableDatabase
        val query = "SELECT * FROM $TABLE_RANKINGHISTORY WHERE $COLUMN_ID = $id"
        val cursor = db.rawQuery(query, null)

        val rankingHistoryList: ArrayList<GameRankingHistory> = ArrayList()
        if (cursor.moveToFirst()){
            do{
                val syncDate = cursor.getString(1)
                val ranking = cursor.getInt(2)

                val gameRanking = GameRankingHistory(id = id, syncDate = syncDate,ranking = ranking)
                rankingHistoryList.add(gameRanking)
            } while(cursor.moveToNext())
        }
        return rankingHistoryList
    }

    fun addGame(game: GameAddOn){
        val gameId = game.id
        val db = this.writableDatabase
        val query = "SELECT * FROM $TABLE_GAMEADDON WHERE $COLUMN_ID = $gameId"
        val cursor = db.rawQuery(query, null)
        if (cursor.count==0){
            val values = ContentValues()
            values.put(COLUMN_ID, gameId)
            values.put(COLUMN_TITLE, game.title)
            values.put(COLUMN_IMAGE, game.img)
            values.put(COLUMN_RELEASEYEAR, game.releaseYear)
            values.put(COLUMN_RANKING, game.ranking)
            values.put(COLUMN_TYPE, game.type)
            values.put(COLUMN_TOREMOVE, 0)
            db.insert(TABLE_GAMEADDON, null, values)
        }else{
            val values = ContentValues()
            values.put(COLUMN_ID, gameId)
            values.put(COLUMN_TITLE, game.title)
            values.put(COLUMN_IMAGE, game.img)
            values.put(COLUMN_RELEASEYEAR, game.releaseYear)
            values.put(COLUMN_RANKING, game.ranking)
            values.put(COLUMN_TYPE, game.type)
            values.put(COLUMN_TOREMOVE, 0)
            db.update(TABLE_GAMEADDON, values, "_id = $gameId", null);
        }
        val values_history = ContentValues()
        values_history.put(COLUMN_ID, gameId)
        values_history.put(COLUMN_SYNCDATE, LocalDateTime.now().toString())
        values_history.put(COLUMN_RANKING, game.ranking)
        db.insert(TABLE_RANKINGHISTORY, null, values_history)

        cursor.close()
        db.close()
    }

    fun addAddOn(addOn: GameAddOn){
        val id = addOn.id
        val query = "SELECT * FROM $TABLE_GAMEADDON WHERE $COLUMN_ID = $id"
        val queryDeleteHistory = "DELETE FROM $TABLE_RANKINGHISTORY WHERE $COLUMN_ID = $id"
        val db = this.writableDatabase
        db.execSQL(queryDeleteHistory)
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()){
            val values = ContentValues()
            values.put(COLUMN_ID, addOn.id)
            values.put(COLUMN_TITLE, addOn.title)
            values.put(COLUMN_IMAGE, addOn.img)
            values.put(COLUMN_RELEASEYEAR, addOn.releaseYear)
            values.put(COLUMN_TYPE, "Expansion")
            values.put(COLUMN_TOREMOVE, 0)
            db.update(TABLE_GAMEADDON, values, "_id = $id", null);
        } else{
            val values = ContentValues()
            values.put(COLUMN_ID, addOn.id)
            values.put(COLUMN_TITLE, addOn.title)
            values.put(COLUMN_IMAGE, addOn.img)
            values.put(COLUMN_RELEASEYEAR, addOn.releaseYear)
            values.put(COLUMN_TYPE, "Expansion")
            values.put(COLUMN_TOREMOVE, 0)
            db.insert(TABLE_GAMEADDON, null, values)
        }

        cursor.close()
        db.close()
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

    fun deleteAllGamesAddOns(){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_GAMEADDON")
    }

    fun deleteAllHistory(){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_RANKINGHISTORY")
    }

    fun getGames():ArrayList<GameAddOn>{
        val gamesList: ArrayList<GameAddOn> = ArrayList()

        var query = "SELECT * FROM $TABLE_GAMEADDON WHERE $COLUMN_TYPE = \"Game\""
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try{
            cursor = db.rawQuery(query, null)
        } catch (e: SQLiteException){
            db.execSQL(query)
            return ArrayList()
        }
        var id: Long
        var title: String
        var image: String
        var releaseYear: Int
        var ranking: Int

        if (cursor.moveToFirst()){
            do{
                id = cursor.getInt(0).toLong()
                title = cursor.getString(1)
                image = cursor.getString(2)
                releaseYear = cursor.getInt(3)
                ranking = cursor.getInt(4)

                val game = GameAddOn(id = id, title = title, img = image, releaseYear = releaseYear, ranking = ranking)
                gamesList.add(game)
            } while(cursor.moveToNext())
        }
        return gamesList
    }

    fun getAddOns():ArrayList<GameAddOn>{
        val addOnsList: ArrayList<GameAddOn> = ArrayList()

        var query = "SELECT * FROM $TABLE_GAMEADDON WHERE $COLUMN_TYPE = \"Expansion\""

        val db = this.readableDatabase
        var cursor: Cursor? = null

        try{
            cursor = db.rawQuery(query, null)
        } catch (e: SQLiteException){
            db.execSQL(query)
            return ArrayList()
        }
        var id: Long
        var title: String
        var image: String
        var releaseYear: Int

        if (cursor.moveToFirst()){
            do{
                id = cursor.getInt(0).toLong()
                title = cursor.getString(1)
                image = cursor.getString(2)
                releaseYear = cursor.getInt(3)

                val addOn = GameAddOn(id = id, title = title, img = image, releaseYear = releaseYear)
                addOnsList.add(addOn)
            } while(cursor.moveToNext())
        }
        return addOnsList
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
            val username = cursor.getString(1)
            val num_of_games = Integer.parseInt(cursor.getString(2))
            val num_of_add_ons = Integer.parseInt(cursor.getString(3))
            val last_sync = cursor.getString(4)
            user = User(username, num_of_games, num_of_add_ons, last_sync)
        }
        cursor.close()
        db.close()
        return user
    }

    fun getNumGames(): Int {
        val query = "SELECT * FROM $TABLE_GAMEADDON WHERE $COLUMN_TYPE = \"Game\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        val count = cursor.count
        cursor.close()
        db.close()
        return count
    }

    fun getNumAddOns(): Int {
        val query = "SELECT * FROM $TABLE_GAMEADDON WHERE $COLUMN_TYPE = \"Expansion\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        val count = cursor.count
        cursor.close()
        db.close()
        return count
    }
}