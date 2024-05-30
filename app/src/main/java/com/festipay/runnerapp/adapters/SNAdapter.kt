package com.festipay.runnerapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.SN

class SNAdapter(private var itemList: ArrayList<SN>) :
    RecyclerView.Adapter<SNAdapter.ItemViewHolder>(), IAdapter {

    private var mListener: OnItemDeleteListener? = null

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sn: TextView = itemView.findViewById(R.id.snValue)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.snDeleteButton)

        init {
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    mListener?.onItemDelete(position, itemList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_s_n_listitem, parent, false)
        return ItemViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: List<SN>) {
        itemList = filterlist as ArrayList<SN>
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.sn.text = currentItem.sn
    }

    fun setOnItemClickListener(listener: OnItemDeleteListener) {
        mListener = listener
    }

    interface OnItemDeleteListener {
        fun onItemDelete(position: Int, snItem: SN)
    }
}