package com.example.professionallego.ui.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.professionallego.R
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(private var items: ArrayList<HistoryData>, private val context: Context, private val onClick:(item: HistoryData) -> Unit):
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    private val sdf: SimpleDateFormat = SimpleDateFormat("yyy/mm/dd hh:mm", Locale.getDefault())
    class ViewHolder(private val View: View): RecyclerView.ViewHolder(View) {
        val InputView: TextView = View.findViewById(R.id.history_item_input)
        val CreatedAt: TextView = View.findViewById(R.id.history_item_createdAt)
        val CubeImage: ImageView = View.findViewById(R.id.history_item_set_cube)
        val ItemSet: TextView = View.findViewById(R.id.history_item_set_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val createdAtStr = context.getString(R.string.history_item_createdAt)

        holder.InputView.text = item.input.toString()
        holder.CreatedAt.text = buildString {
        append(createdAtStr)
        append(":")
        append(sdf.format(item.timestamp))
    }
        holder.ItemSet.text = item.legoBoxId.toString()
        //holder.CubeImage.setImageResource(R.drawable.ic_cube)
        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(newItems: ArrayList<HistoryData>) {
        val diffResult = DiffUtil.calculateDiff(ItemDiffCallback(items, newItems))
        this.items = newItems
        diffResult.dispatchUpdatesTo(this)
    }
    class ItemDiffCallback(private val oldItems: List<HistoryData>, private val newItems: List<HistoryData>) : DiffUtil.Callback() {
        override fun getOldListSize() = oldItems.size
        override fun getNewListSize() = newItems.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldItems[oldItemPosition].id == newItems[newItemPosition].id
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldItems[oldItemPosition] == newItems[newItemPosition]
    }

}