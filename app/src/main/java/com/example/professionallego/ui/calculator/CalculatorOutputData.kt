package com.example.professionallego.ui.calculator

data class CalculatorOutputData(
    val id: Int,
    val name: String,
    val size: Int,
    val amount: Int = 1,
    val DBid: Int? = null
)
