package com.example.professionallego.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.example.professionallego.ui.LegoBoxBox.LegoBoxData
import com.example.professionallego.ui.calculator.CalculatorOutputData
import com.example.professionallego.ui.history.HistoryData
import com.example.professionallego.ui.legoBox.LegoItemData
import kotlinx.coroutines.flow.map

class AppSharedViewModel(application: Application): AndroidViewModel(application) {

    private val repo = Repo(application)

    val LegoBoxBox: MutableLiveData<Map<Int, LegoBoxData>> by lazy {
        MutableLiveData<Map<Int,LegoBoxData>>()
    }
    val History: MutableLiveData<ArrayList<HistoryData>> by lazy {
        MutableLiveData<ArrayList<HistoryData>>()
    }
    var currentLegoBoxBoxId: Int = 0

    var calculationBoxBoxId: Int? = null

    fun loadLegoBoxes(){
        val data = repo.getAllLegoBoxData()
        LegoBoxBox.value = data
    }

    fun loadHistories(){
        val data = repo.getAllHistoryData()
        History.value = data
    }

    fun addLegoBoxBox(): Int{
        val data = LegoBoxBox.value ?: mapOf()
        Log.d("addLegoBoxBox", "Fetched Data: ${data.toString()}")
        val id = data.size
        val createdAt = System.currentTimeMillis()
        val name = "Box $id"
        val legoBoxData = LegoBoxData(name, createdAt, id)
        legoBoxData.LegoBox = ArrayList()
        val updatedData = data.toMutableMap().apply { put(id, legoBoxData) }
        LegoBoxBox.value = updatedData
        repo.saveLegoBoxData(legoBoxData)
        Log.d("addLegoBoxBox", data.toString())
        Log.d("addLegoBoxBox", LegoBoxBox.value.toString())
        return id
    }
    fun addBox(Boxid: Int, size: Int): Boolean{
        val data = LegoBoxBox.value ?: return false
        val legoBoxBoxData = data[Boxid] ?: return false
        val id = legoBoxBoxData.LegoBox?.size ?: 0
        val name = "Box $id"
        val legoItemData = LegoItemData(size, name, id)
        legoBoxBoxData.LegoBox?.add(legoItemData)
        LegoBoxBox.value = data
        repo.addBoxData(Boxid, legoItemData)
        return true
    }

    fun setLegoBoxName(id: Int, name:String){
        val data = LegoBoxBox.value ?: return
        val legoBoxBoxData = data[id] ?: return
        legoBoxBoxData.name = name
        LegoBoxBox.value = data
        repo.updateBoxName(id, name)
    }

    fun getLegoBoxLiveData(id: Int): LiveData<ArrayList<LegoItemData>> {
        return LegoBoxBox.asFlow().map { map ->
            map[id]?.LegoBox ?: arrayListOf()
        }.asLiveData()
    }

    fun getLegoBoxBoxLiveName(id: Int): LiveData<String> {
        return LegoBoxBox.asFlow().map { map ->
            map[id]?.name ?: ""
        }.asLiveData()
    }

    fun getLegoBoxDataforCalc(id: Int): ArrayList<LegoItemData>?{
        val data = LegoBoxBox.value ?: return null
        val legoBoxBoxData = data[id] ?: return null
        return legoBoxBoxData.LegoBox
    }

    fun getLegoBoxBoyDataAsArray(): ArrayList<LegoBoxData>{
        val data = LegoBoxBox.value ?: mapOf()
        return ArrayList(data.values)
    }

    fun getHistoryData(id: Int): HistoryData?{
        val data = History.value ?: return null
        return data[id]
    }

    fun addHistoryData(items: ArrayList<CalculatorOutputData>, Input: Int, BoxId: Int){
        val data = History.value ?: arrayListOf()
        val historyData = HistoryData(data.size, System.currentTimeMillis(), BoxId, Input, items)
        data.add(historyData)
        History.value = data
        repo.saveHistoryData(historyData)
    }
    fun saveHistoryData(){
        val data = History.value ?: return
        for (historyData in data){
            repo.saveHistoryData(historyData)
        }
    }

    fun saveLegoBoxData(){
        val data = LegoBoxBox.value ?: return
        for (legoBoxBoxData in data.values){
            repo.saveLegoBoxData(legoBoxBoxData)
        }
    }

}