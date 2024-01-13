package com.example.professionallego.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.professionallego.ui.LegoBoxBox.LegoBoxData
import com.example.professionallego.ui.calculator.CalculatorOutputData
import com.example.professionallego.ui.history.HistoryData
import com.example.professionallego.ui.legoBox.LegoItemData

class sqlliteDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    val legoBoxBoxTableName = "legoboxbox"
    val legoBoxTableName = "legobox"
    val historyTableName = "history"
    val calculatorOutputTableName = "outputData"

    val writeDB = this.writableDatabase
    val readDB = this.readableDatabase

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Database.db"
        @Volatile
        private var INSTANCE: sqlliteDBHelper? = null

        fun getInstance(context: Context): sqlliteDBHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = sqlliteDBHelper(context)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE_BBox = "CREATE TABLE $legoBoxBoxTableName (id INTEGER PRIMARY KEY, name TEXT, created_at INTEGER)"
        db.execSQL(CREATE_TABLE_BBox)
        val CREATE_TABLE_B = "CREATE TABLE $legoBoxTableName (ida INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, size INTEGER,  id INTEGER, parent_id INTEGER, FOREIGN KEY(id) REFERENCES $legoBoxBoxTableName(id))"
        db.execSQL(CREATE_TABLE_B)
        val CREATE_TABLE_H = "CREATE TABLE $historyTableName (id INTEGER PRIMARY KEY, input INTEGER, timestamp INTEGER, lego_box_box_id INTEGER)"
        db.execSQL(CREATE_TABLE_H)
        val CREATE_TABLE_O = "CREATE TABLE $calculatorOutputTableName (ida INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, size INTEGER,id INTEGER, parent_id INTEGER, FOREIGN KEY(parent_id) REFERENCES $historyTableName(id))"
        db.execSQL(CREATE_TABLE_O)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS example_table")
        onCreate(db)
    }


    fun deleteLegoBoxData(id: Int){
        val db = this.writeDB
        db.beginTransaction()
        try {
            db.delete("legoboxbox", "id = ?", arrayOf(id.toString()))
            db.delete("legobox", "parent_id = ?", arrayOf(id.toString()))
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun addBoxData(id: Int, legoItemData: LegoItemData){
        val db = this.writeDB
        db.beginTransaction()
        try {
            val childValues = ContentValues().apply {
                put("name", legoItemData.name)
                put("size", legoItemData.size)
                put("id", legoItemData.id)
                put("parent_id", id)
            }
            db.insertWithOnConflict(
                "legobox",
                null,
                childValues,
                SQLiteDatabase.CONFLICT_REPLACE
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun updateBoxName(id: Int, name: String){
        val db = this.writeDB
        db.beginTransaction()
        try {
            val childValues = ContentValues().apply {
                put("name", name)
            }
            db.update(
                "legoboxbox",
                childValues,
                "id = ?",
                arrayOf(id.toString())
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun saveLegoBoxData(legoBoxData: LegoBoxData){
        val db = this.writeDB
        db.beginTransaction()
        try {
            val parentValues = ContentValues().apply {
                put("name", legoBoxData.name)
                put("created_at", legoBoxData.createdAt)
                put("id", legoBoxData.id)
            }
            db.insertWithOnConflict(
                "legoboxbox",
                null,
                parentValues,
                SQLiteDatabase.CONFLICT_REPLACE
            )
            for (child in legoBoxData.LegoBox!!) {
                val childValues = ContentValues().apply {
                    put("name", child.name)
                    put("size", child.size)
                    put("id", child.id)
                    put("parent_id", legoBoxData.id)
                }
                if (child.DBid != null) {
                    db.update(
                        "legobox",
                        childValues,
                        "ida = ?",
                        arrayOf(child.DBid.toString())
                    )
                } else {
                    db.insertWithOnConflict(
                        "legobox",
                        null,
                        childValues,
                        SQLiteDatabase.CONFLICT_REPLACE
                    )
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun deleteHistoryData(id: Int){
        val db = this.writeDB
        db.beginTransaction()
        try {
            db.delete("history", "id = ?", arrayOf(id.toString()))
            db.delete("outputData", "parent_id = ?", arrayOf(id.toString()))
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
    fun saveHistoryData(hisoryData: HistoryData){
        val db = this.writeDB
        db.beginTransaction()
        try {
            val parentValues = ContentValues().apply{
                put("input", hisoryData.input)
                put("timestamp", hisoryData.timestamp)
                put("id", hisoryData.id)
                put("lego_box_box_id", hisoryData.legoBoxId)
            }
            db.insertWithOnConflict("history", null, parentValues, SQLiteDatabase.CONFLICT_REPLACE)
            for(child in hisoryData.output){
                val childValues = ContentValues().apply{
                    put("name", child.name)
                    put("size", child.size)
                    put("id", child.id)
                    put("parent_id", hisoryData.id)
                }
                if(child.DBid != null){
                    db.update(
                        "outputData",
                        childValues,
                        "ida = ?",
                        arrayOf(child.DBid.toString())
                    )
                } else {
                    db.insertWithOnConflict(
                        "outputData",
                        null,
                        childValues,
                        SQLiteDatabase.CONFLICT_REPLACE
                    )
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
    //Here and in all other functions, the @SuppressLint("Range") is needed because it is not possible for the cursor to not find the data in the row as all data is added in one go
    //And the transaction either fails -> the Row is not present which is checked for or succeeds -> all data will be in the row
    @SuppressLint("Range")
    fun getLegoBoxData(Boxid: Int): LegoBoxData? {
        val db = this.readDB
        val cursor = db.rawQuery("SELECT * FROM legoboxbox WHERE id = $Boxid", null)
        val legoBox: LegoBoxData?
        if(cursor.moveToFirst()){
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val createdAt = cursor.getLong(cursor.getColumnIndex("created_at"))
            val box = getLegoData(id)
            legoBox = LegoBoxData(name, createdAt, id)
            legoBox.LegoBox = box
        } else {
            legoBox = null
        }
        cursor.close()
        return legoBox
    }
    @SuppressLint("Range")
    fun getAllLegoBoxData():Map<Int, LegoBoxData>{
        val legbbdata: MutableMap<Int, LegoBoxData> = mutableMapOf()
        val db = this.readDB
        val cursor = db.rawQuery("SELECT * FROM legoboxbox", null)
        if(cursor.moveToFirst()){
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val createdAt = cursor.getLong(cursor.getColumnIndex("created_at"))
                val legoBox = getLegoData(id)
                val legb = LegoBoxData(name, createdAt, id)
                legb.LegoBox = legoBox
                legbbdata[id] = legb
            } while (cursor.moveToNext())
        }
        cursor.close()
        return legbbdata
    }
    @SuppressLint("Range")
    private fun getLegoData(Legoid: Int): ArrayList<LegoItemData>{
        val db = this.readDB
        val cursor = db.rawQuery("SELECT * FROM legobox WHERE parent_id = $Legoid", null)
        val legoBox: ArrayList<LegoItemData> = ArrayList()
        if(cursor.moveToFirst()){
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val size = cursor.getInt(cursor.getColumnIndex("size"))
                val ida = cursor.getInt(cursor.getColumnIndex("ida"))
                legoBox.add(LegoItemData(id, name, size, ida))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return legoBox
    }
    @SuppressLint("Range")
    fun getHistoryData(Historyid: Int): HistoryData? {
        val db = this.readDB
        val cursor = db.rawQuery("SELECT * FROM history WHERE id = $Historyid", null)
        val history: HistoryData?
        if(cursor.moveToFirst()){
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val input = cursor.getInt(cursor.getColumnIndex("input"))
            val timestamp = cursor.getLong(cursor.getColumnIndex("timestamp"))
            val legoBoxId = cursor.getInt(cursor.getColumnIndex("lego_box_box_id"))
            val output = getOutputData(id)
            history = HistoryData(id, timestamp, legoBoxId, input, output)
        } else {
            history = null
        }
        cursor.close()
        return history
    }
    @SuppressLint("Range")
    fun getAllHistoryData(): ArrayList<HistoryData>{
        val db = this.readDB
        val cursor = db.rawQuery("SELECT * FROM history", null)
        val history: ArrayList<HistoryData> = ArrayList()
        if(cursor.moveToFirst()){
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val input = cursor.getInt(cursor.getColumnIndex("input"))
                val timestamp = cursor.getLong(cursor.getColumnIndex("timestamp"))
                val legoBoxId = cursor.getInt(cursor.getColumnIndex("lego_box_box_id"))
                val output = getOutputData(id)
                history.add(HistoryData(id, timestamp, legoBoxId, input, output))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return history
    }
    @SuppressLint("Range")
    private fun getOutputData(DBid:Int): ArrayList<CalculatorOutputData>{
        val db = this.readDB
        val cursor = db.rawQuery("SELECT * FROM outputData WHERE parent_id = $DBid", null)
        val output: ArrayList<CalculatorOutputData> = ArrayList()
        if(cursor.moveToFirst()){
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val size = cursor.getInt(cursor.getColumnIndex("size"))
                val ida = cursor.getInt(cursor.getColumnIndex("ida"))
                output.add(CalculatorOutputData(id, name, size,1, ida))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return output
    }

}