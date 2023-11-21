package com.example.professionallego.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.professionallego.ui.LegoBoxBox.LegoBoxBoxData
import com.example.professionallego.ui.calculator.CalculatorOutputData
import com.example.professionallego.ui.history.HistoryData
import com.example.professionallego.ui.legoBox.LegoItemData

class AppSharedViewModel: ViewModel() {
    val LegoBoxBox: MutableLiveData<Map<Int, LegoBoxBoxData>> by lazy {
        MutableLiveData<Map<Int,LegoBoxBoxData>>()
    }
    val History: MutableLiveData<ArrayList<HistoryData>> by lazy {
        MutableLiveData<ArrayList<HistoryData>>()
    }
    var currentLegoBoxBoxId: Int = 0

    var calculationBoxBoxId: Int? = null

    fun addLegoBoxBox(): Int{
        val data = LegoBoxBox.value ?: mapOf()
        val id = data.size
        val createdAt = System.currentTimeMillis()
        val name = "Box $id"
        val legoBoxBoxData = LegoBoxBoxData(name, createdAt, id)
        legoBoxBoxData.LegoBox = ArrayList()
        LegoBoxBox.value = data + Pair(id, legoBoxBoxData)
        return id
    }
    fun addLegoBox(Boxid: Int, size: Int): Boolean{
        val data = LegoBoxBox.value ?: return false
        val legoBoxBoxData = data[Boxid] ?: return false
        val id = legoBoxBoxData.LegoBox?.size ?: 0
        val name = "Box $id"
        legoBoxBoxData.LegoBox?.add(LegoItemData(size, name, id))
        LegoBoxBox.value = data
        return true
    }

    fun setLegoBoxBoxName(id: Int, name:String){
        val data = LegoBoxBox.value ?: return
        val legoBoxBoxData = data[id] ?: return
        legoBoxBoxData.name = name
        LegoBoxBox.value = data
    }

    fun getLegoBoxLiveData(id: Int): LiveData<ArrayList<LegoItemData>>? {
        val data = LegoBoxBox.value ?: return null
        val legoBoxBoxData = data[id] ?: return null
        return Transformations.map(LegoBoxBox) { map ->
            map[id]?.LegoBox
        }
    }
    fun getLegoBoxBoxLiveName(id: Int): LiveData<String>{
        val data = LegoBoxBox.value ?: return MutableLiveData<String>()
        return Transformations.map(LegoBoxBox) { map ->
            map[id]?.name
        }
    }

    fun getLegoBoxDataforCalc(id: Int): ArrayList<LegoItemData>?{
        val data = LegoBoxBox.value ?: return null
        val legoBoxBoxData = data[id] ?: return null
        return legoBoxBoxData.LegoBox
    }

    fun getLegoBoxBoyDataAsArray(): ArrayList<LegoBoxBoxData>{
        val data = LegoBoxBox.value ?: mapOf()
        return ArrayList(data.values)
    }

    fun getHistoryData(id: Int): HistoryData?{
        val data = History.value ?: return null
        return data[id]
    }

}