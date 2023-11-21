package com.example.professionallego.ui.calculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.professionallego.R

class CalculatorOutputAdapter(var items: ArrayList<CalculatorOutputData>) : RecyclerView.Adapter<CalculatorOutputAdapter.ViewHolder>() {

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val BoxTitle: TextView = view.findViewById(R.id.calculator_output_item_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calculator_output_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val name = item.name
        val size = item.size.toString()
        holder.BoxTitle.text = buildString {
            append(name)
            append("(")
            append(size)
            append("mm)")
    }
    }

    override fun getItemCount(): Int = items.size

}