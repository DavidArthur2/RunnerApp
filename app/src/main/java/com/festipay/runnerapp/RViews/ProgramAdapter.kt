package com.festipay.runnerapp.RViews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.festipay.runnerapp.R

class ProgramAdapter(private val programItemList: ArrayList<ProgramItem>) :
    RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder>() {

    private var mListener: OnItemClickListener? = null

    inner class ProgramViewHolder(itemView: View) : ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.programTitle)
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    mListener?.onItemClick(position, programItemList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.program_selector_listitem, parent, false)
        return ProgramViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return programItemList.size
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        val currentItem = programItemList[position]
        holder.title.text = currentItem.title
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, programItem: ProgramItem)
    }
}