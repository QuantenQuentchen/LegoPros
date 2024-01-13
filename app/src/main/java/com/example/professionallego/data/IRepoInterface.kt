package com.example.professionallego.data

import com.example.professionallego.ui.LegoBoxBox.LegoBoxData
import com.example.professionallego.ui.history.HistoryData
import com.example.professionallego.ui.legoBox.LegoItemData

interface IRepoInterface{

    fun saveLegoBoxData(legoBox: LegoBoxData)

    fun addBoxData(id: Int, legoItemData: LegoItemData)

    fun updateBoxName(id: Int, name: String)

    fun getAllLegoBoxData(): Map<Int, LegoBoxData>
    fun deleteLegoBoxData(id: Int)
    fun updateLegoBoxData(legoBox: LegoBoxData)
    fun saveHistoryData(history: HistoryData)
    fun getHistoryData(id: Int): HistoryData?
    fun getAllHistoryData(): ArrayList<HistoryData>
    fun deleteHistoryData(id: Int)
}