package com.festipay.runnerapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.Inventory
import com.festipay.runnerapp.utilities.DateFormatter


class InventoryAdapter(private var itemList: ArrayList<Inventory>) :
    RecyclerView.Adapter<InventoryAdapter.ItemViewHolder>(), IAdapter {

    private var mListener: OnItemClickListener? = null

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val darabszam: TextView = itemView.findViewById(R.id.firstValue)
        val targyNev: TextView = itemView.findViewById(R.id.itemTitle)
        val utolsoMegjegyzes: TextView = itemView.findViewById(R.id.lastCommentText)
        val utolsoMegjegyzesDatum: TextView = itemView.findViewById(R.id.lastCommentDate)
        val lastModifiedDate: TextView = itemView.findViewById(R.id.lastModifiedDate)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    mListener?.onItemClick(position, itemList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.inventory_items_listitem, parent, false)
        return ItemViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: List<Inventory>) {
        itemList = filterlist as ArrayList<Inventory>
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.targyNev.text = currentItem.itemName
        holder.darabszam.text = currentItem.quantity.toString()
        holder.utolsoMegjegyzes.text = currentItem.lastComment?.megjegyzes
        holder.utolsoMegjegyzesDatum.text =
            DateFormatter.localDateTimeToString(currentItem.lastComment?.megjegyzesIdo)
        holder.lastModifiedDate.text = DateFormatter.localDateTimeToString(currentItem.lastModified)

    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, inventoryItem: Inventory)
    }
}