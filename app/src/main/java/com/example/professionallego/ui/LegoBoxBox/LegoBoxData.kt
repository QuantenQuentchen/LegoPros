package com.example.professionallego.ui.LegoBoxBox

import com.example.professionallego.ui.legoBox.LegoItemData

data class LegoBoxData(
    var name: String,
    val createdAt: Long,
    val id: Int
    ) {
    var LegoBox: ArrayList<LegoItemData>? = null

}
