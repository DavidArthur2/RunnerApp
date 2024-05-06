package com.festipay.runnerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.Inventory
import com.festipay.runnerapp.utilities.DateFormatter


class InventoryAdapter(private val itemList: ArrayList<Inventory>) :
    RecyclerView.Adapter<InventoryAdapter.ItemViewHolder>() {

    private var mListener: OnItemClickListener? = null

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val darabszam: TextView = itemView.findViewById(R.id.firstValue)
        val sn: CheckBox = itemView.findViewById(R.id.secondCheckbox)
        val targyNev: TextView = itemView.findViewById(R.id.itemTitle)
        val utolsoMegjegyzes: TextView = itemView.findViewById(R.id.lastCommentText)
        val utolsoMegjegyzesDatum: TextView = itemView.findViewById(R.id.lastCommentDate)
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

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.targyNev.text = currentItem.targyNev
        holder.sn.isChecked = currentItem.sn
        holder.darabszam.text = currentItem.darabszam.toString()
        holder.utolsoMegjegyzes.text = currentItem.utolsoMegjegyzes?.megjegyzes
        holder.utolsoMegjegyzesDatum.text =
            DateFormatter.LocalDateTimeToString(currentItem.utolsoMegjegyzes?.megjegyzesIdo)

    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, inventoryItem: Inventory)
    }
}