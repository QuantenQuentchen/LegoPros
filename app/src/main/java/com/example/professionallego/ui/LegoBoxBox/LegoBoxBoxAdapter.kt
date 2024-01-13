package com.example.professionallego.ui.LegoBoxBox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.professionallego.R
import java.text.SimpleDateFormat
import java.util.Locale

class LegoBoxBoxAdapter(var items: ArrayList<LegoBoxData>, private val onItemClick:(Item: LegoBoxData) -> Unit, private val onAddClick:(() ->Unit)?):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val sdf = SimpleDateFormat("yyy/mm/dd hh:mm", Locale.getDefault())
    private val Addable = onAddClick != null
    companion object {
        const val VIEW_TYPE_REGULAR = 0
        const val VIEW_TYPE_ADDABLE = 1
    }

    class ItemViewHolder(val view: View): RecyclerView.ViewHolder(view)
    class AddViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun getItemViewType(position: Int): Int {
        return if(position == items.size && Addable) VIEW_TYPE_ADDABLE else VIEW_TYPE_REGULAR
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        return when (viewType){
            VIEW_TYPE_REGULAR -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.lego_box_box_item, parent, false)
                ItemViewHolder(view)
            }
            VIEW_TYPE_ADDABLE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.lego_box_box_add_item, parent, false)
                AddViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ItemViewHolder){
            val item = items[position]
            holder.view.findViewById<TextView>(R.id.lego_box_box_item_title).text = item.name
            holder.view.findViewById<TextView>(R.id.lego_box_box_item_CreatedAt).text = sdf.format(item.createdAt)
            holder.itemView.setOnClickListener {
               onItemClick(item)
            }
            //DataBindings
        }
        if(holder is AddViewHolder && Addable){
            holder.itemView.setOnClickListener {
                onAddClick?.invoke()
            }
        }
    }

    fun updateItems(newItems: ArrayList<LegoBoxData>) {
        val diffResult = DiffUtil.calculateDiff(ItemDiffCallback(items, newItems))
        this.items = newItems
        diffResult.dispatchUpdatesTo(this)
    }
    class ItemDiffCallback(private val oldItems: List<LegoBoxData>, private val newItems: List<LegoBoxData>) : DiffUtil.Callback() {
        override fun getOldListSize() = oldItems.size
        override fun getNewListSize() = newItems.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldItems[oldItemPosition].id == newItems[newItemPosition].id
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldItems[oldItemPosition] == newItems[newItemPosition]
    }


    override fun getItemCount(): Int {
        return if(Addable) items.size + 1 else items.size
    }


}