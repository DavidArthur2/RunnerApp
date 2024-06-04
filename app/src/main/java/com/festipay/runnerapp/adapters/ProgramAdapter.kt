package com.festipay.runnerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.Program

class ProgramAdapter(private val itemList: ArrayList<Program>) :
    RecyclerView.Adapter<ProgramAdapter.ItemViewHolder>() {

    private var mListener: OnItemClickListener? = null

    inner class ItemViewHolder(itemView: View) : ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.programTitle)

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
            .inflate(R.layout.program_selector_listitem, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.title.text = currentItem.title
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, program: Program)
    }
}