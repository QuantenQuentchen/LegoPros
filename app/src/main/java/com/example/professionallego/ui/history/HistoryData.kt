package com.example.professionallego.ui.history

import com.example.professionallego.ui.calculator.CalculatorOutputData

data class HistoryData(
    val id: Int,
    val timestamp: Long,
    val legoBoxId: Int,
    val input: Int,
    val output: ArrayList<CalculatorOutputData>
)