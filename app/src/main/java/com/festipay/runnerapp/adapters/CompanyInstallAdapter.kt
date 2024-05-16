package com.festipay.runnerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.utilities.DateFormatter.LocalDateTimeToString

class CompanyInstallAdapter(private val itemList: ArrayList<CompanyInstall>) :
    RecyclerView.Adapter<CompanyInstallAdapter.ItemViewHolder>() {

    private var mListener: OnItemClickListener? = null

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val companyName: TextView = itemView.findViewById(R.id.companyTitle)
        val firstItem: TextView = itemView.findViewById(R.id.firstItem)
        val secondItem: TextView = itemView.findViewById(R.id.secondItem)
        val thirdItem: CheckBox = itemView.findViewById(R.id.thirdItem)
        val fourthItem: TextView = itemView.findViewById(R.id.fourthItem)
        val fifthItem: CheckBox = itemView.findViewById(R.id.fifthItem)
        val sixthItem: CheckBox = itemView.findViewById(R.id.sixthItem)
        val seventhItem: CheckBox = itemView.findViewById(R.id.seventhItem)
        val eightItem: CheckBox = itemView.findViewById(R.id.eightItem)
        val ninethItem: CheckBox = itemView.findViewById(R.id.ninethItem)
        val tenthItem: CheckBox = itemView.findViewById(R.id.tenthItem)
        val eleventhItem: CheckBox = itemView.findViewById(R.id.eleventhItem)
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
            .inflate(R.layout.install_companysite_listitem, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.companyName.text = currentItem.companyName
        holder.firstItem.text = currentItem.firstItem.name
        holder.secondItem.text = currentItem.secondItem.name
        holder.thirdItem.isChecked = currentItem.thirdItem
        holder.fourthItem.text = currentItem.fourthItem.name
        holder.fifthItem.isChecked = currentItem.fifthItem
        holder.sixthItem.isChecked = currentItem.sixthItem
        holder.seventhItem.isChecked = currentItem.seventhItem
        holder.eightItem.isChecked = currentItem.eightItem
        holder.ninethItem.isChecked = currentItem.ninethItem
        holder.tenthItem.isChecked = currentItem.tenthItem
        holder.eleventhItem.isChecked = currentItem.eleventhItem
        holder.utolsoMegjegyzes.text = currentItem.lastComment?.megjegyzes
        holder.utolsoMegjegyzesDatum.text = LocalDateTimeToString(currentItem.lastComment?.megjegyzesIdo)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, companyInstall: CompanyInstall)
    }
}