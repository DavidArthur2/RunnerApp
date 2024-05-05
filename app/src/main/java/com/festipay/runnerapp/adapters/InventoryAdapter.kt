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


class InventoryAdapter(private val inventoryItemList: ArrayList<Inventory>) :
    RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    private var mListener: OnItemClickListener? = null

    inner class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val darabszam: TextView = itemView.findViewById(R.id.firstValue)
        val sn: CheckBox = itemView.findViewById(R.id.secondCheckbox)
        val targyNev: TextView = itemView.findViewById(R.id.itemTitle)
        val utolsoMegjegyzes: TextView = itemView.findViewById(R.id.lastCommentText)
        val utolsoMegjegyzesDatum: TextView = itemView.findViewById(R.id.lastCommentDate)
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    mListener?.onItemClick(position, inventoryItemList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.inventory_items_listitem, parent, false)
        return InventoryViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return inventoryItemList.size
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val currentItem = inventoryItemList[position]
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