package com.example.professionallego.ui.legoBox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.professionallego.R

class LegoBoxAdapter(private var items: List<LegoItemData> = listOf()) : RecyclerView.Adapter<LegoBoxAdapter.ViewHolder>() {

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val headline: TextView = view.findViewById(R.id.legoBoxItemHeaderTextView)
        val content: TextView = view.findViewById(R.id.legoBoxItemContentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lego_box_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.headline.text = buildString {
        append(item.name)
        append(":")
    }
        holder.content.text = buildString {
        append(item.size.toString())
        append("mm")
    }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<LegoItemData>) {
        val diffResult = DiffUtil.calculateDiff(ItemDiffCallback(items, newItems))
        this.items = newItems
        diffResult.dispatchUpdatesTo(this)
    }
    class ItemDiffCallback(private val oldItems: List<LegoItemData>, private val newItems: List<LegoItemData>) : DiffUtil.Callback() {
        override fun getOldListSize() = oldItems.size
        override fun getNewListSize() = newItems.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldItems[oldItemPosition].id == newItems[newItemPosition].id
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldItems[oldItemPosition] == newItems[newItemPosition]
    }

}