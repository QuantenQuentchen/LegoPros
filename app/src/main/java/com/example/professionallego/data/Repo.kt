package com.example.professionallego.data

import android.content.Context
import com.example.professionallego.ui.LegoBoxBox.LegoBoxData
import com.example.professionallego.ui.history.HistoryData
import com.example.professionallego.ui.legoBox.LegoItemData

class Repo(context: Context): IRepoInterface {

    private val db = sqlliteDBHelper.getInstance(context)

    override fun saveLegoBoxData(legoBox: LegoBoxData) {
        db.saveLegoBoxData(legoBox)
    }

    override fun addBoxData(id: Int, legoItemData: LegoItemData) {
        db.addBoxData(id, legoItemData)
    }

    override fun updateBoxName(id: Int, name: String) {
        db.updateBoxName(id, name)
    }

    override fun getAllLegoBoxData(): Map<Int, LegoBoxData> {
        return db.getAllLegoBoxData()
    }
    override fun deleteLegoBoxData(id: Int) {
        db.deleteLegoBoxData(id)
    }
    override fun updateLegoBoxData(legoBox: LegoBoxData) {
        db.saveLegoBoxData(legoBox)
    }
    override fun saveHistoryData(history: HistoryData) {
        db.saveHistoryData(history)
    }
    override fun getHistoryData(id: Int): HistoryData? {
        return db.getHistoryData(id)
    }
    override fun getAllHistoryData(): ArrayList<HistoryData> {
        return db.getAllHistoryData()
    }
    override fun deleteHistoryData(id: Int) {
        db.deleteHistoryData(id)
    }
}