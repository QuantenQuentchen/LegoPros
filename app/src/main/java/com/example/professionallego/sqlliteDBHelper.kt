package com.example.professionallego

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class sqlliteDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    val legoBoxBoxTableName = "legoboxbox"
    val legoBoxTableName = "legobox"
    val historyTableName = "history"
    val calculatorOutputTableName = "outputData"

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Database.db"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE_BBox = "CREATE TABLE $legoBoxBoxTableName (id INTEGER PRIMARY KEY, name STRING, created_at INTEGER)"
        db.execSQL(CREATE_TABLE_BBox)
        val CREATE_TABLE_B = "CREATE TABLE $legoBoxTableName (ida INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER, name STRING, size INTEGER,  id INTEGER, FOREIGN KEY(id) REFERENCES $legoBoxBoxTableName(id))"
        db.execSQL(CREATE_TABLE_B)
        val CREATE_TABLE_H = "CREATE TABLE $historyTableName (id INTEGER PRIMARY KEY, input INTEGER, timestamp INTEGER, lego_box_box_id INTEGER)"
        db.execSQL(CREATE_TABLE_H)
        val CREATE_TABLE_O = "CREATE TABLE $calculatorOutputTableName (ida INTEGER PRIMARY KEY AUTOINCREMENT, name STRING, size INTEGER,id INTEGER, parent_id INTEGER, FOREIGN KEY(parent_id) REFERENCES $historyTableName(id))"
        db.execSQL(CREATE_TABLE_O)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS example_table")
        onCreate(db)
    }
}