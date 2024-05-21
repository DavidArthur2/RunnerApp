package com.festipay.runnerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.utilities.DateFormatter.LocalDateTimeToString


class CompanyInstallAdapter(private var itemList: ArrayList<CompanyInstall>) :
    RecyclerView.Adapter<CompanyInstallAdapter.ItemViewHolder>(), IAdapter {

    private var mListener: OnItemClickListener? = null

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val companyName: TextView = itemView.findViewById(R.id.companyTitle)
        val firstItem: TextView = itemView.findViewById(R.id.firstItem)
        val secondItem: TextView = itemView.findViewById(R.id.secondItem)
        val thirdItem: CheckBox = itemView.findViewById(R.id.thirdItem)
        val fourthItem: CheckBox = itemView.findViewById(R.id.fourthItem)
        val fifthItem: CheckBox = itemView.findViewById(R.id.fifthItem)
        val sixthItem: CheckBox = itemView.findViewById(R.id.sixthItem)
        val seventhItem: CheckBox = itemView.findViewById(R.id.seventhItem)
        val eightItem: CheckBox = itemView.findViewById(R.id.eightItem)
        val ninethItem: CheckBox = itemView.findViewById(R.id.ninethItem)
        val utolsoMegjegyzes: TextView = itemView.findViewById(R.id.lastCommentText)
        val utolsoMegjegyzesDatum: TextView = itemView.findViewById(R.id.lastCommentDate)
        private val pinGo: ImageView = itemView.findViewById(R.id.pinGo)


        init {
            pinGo.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    mListener?.onPinGoClick(position, itemList[position])
                }
            }
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

    fun filterList(filterlist: List<CompanyInstall>) {
        itemList = filterlist as ArrayList<CompanyInstall>
        notifyDataSetChanged()
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
        holder.fourthItem.isChecked = currentItem.fourthItem
        holder.fifthItem.isChecked = currentItem.fifthItem
        holder.sixthItem.isChecked = currentItem.sixthItem
        holder.seventhItem.isChecked = currentItem.seventhItem
        holder.eightItem.isChecked = currentItem.eightItem
        holder.ninethItem.isChecked = currentItem.ninethItem
        holder.utolsoMegjegyzes.text = currentItem.lastComment?.megjegyzes
        holder.utolsoMegjegyzesDatum.text = LocalDateTimeToString(currentItem.lastComment?.megjegyzesIdo)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, companyInstall: CompanyInstall)
        fun onPinGoClick(position: Int, companyInstall: CompanyInstall)
    }
}