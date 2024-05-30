package com.festipay.runnerapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.CompanyDemolition
import com.festipay.runnerapp.utilities.DateFormatter
import com.festipay.runnerapp.utilities.Filter

class CompanyDemolitionAdapter(private var itemList: ArrayList<CompanyDemolition>) :
    RecyclerView.Adapter<CompanyDemolitionAdapter.ItemViewHolder>(), IAdapter {

    private var mListener: OnItemClickListener? = null

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val constLayout: ConstraintLayout = itemView.findViewById(R.id.constLayout)
        val companyName: TextView = itemView.findViewById(R.id.companyTitle)
        val firstItem: TextView = itemView.findViewById(R.id.firstItem)
        val secondItem: TextView = itemView.findViewById(R.id.secondItem)
        val thirdItem: CheckBox = itemView.findViewById(R.id.thirdItem)
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
            .inflate(R.layout.demolition_companysite_listitem, parent, false)
        return ItemViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: List<CompanyDemolition>) {
        itemList = filterlist as ArrayList<CompanyDemolition>
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]

        if (Filter.selectedDemolitionItems.any { it -> it }) holder.constLayout.background =
            ContextCompat.getDrawable(holder.itemView.context, R.drawable.orange_rectangle)
        else holder.constLayout.background =
            ContextCompat.getDrawable(holder.itemView.context, R.drawable.rectangle)

        holder.companyName.text = currentItem.companyName
        holder.firstItem.text = currentItem.firstItem.toString()
        holder.secondItem.text = currentItem.secondItem.toString()
        holder.thirdItem.isChecked = currentItem.thirdItem
        holder.utolsoMegjegyzes.text = currentItem.lastComment?.megjegyzes
        holder.utolsoMegjegyzesDatum.text =
            DateFormatter.LocalDateTimeToString(currentItem.lastComment?.megjegyzesIdo)
        holder.lastModifiedDate.text = DateFormatter.LocalDateTimeToString(currentItem.lastModified)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, companyDemolition: CompanyDemolition)
    }
}