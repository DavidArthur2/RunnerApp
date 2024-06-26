package com.festipay.runnerapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.utilities.DateFormatter.localDateTimeToString
import com.festipay.runnerapp.utilities.Filter


class CompanyInstallAdapter(private var itemList: ArrayList<CompanyInstall>) :
    RecyclerView.Adapter<CompanyInstallAdapter.ItemViewHolder>(), IAdapter {

    private var mListener: OnItemClickListener? = null

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val constLayout: ConstraintLayout = itemView.findViewById(R.id.constLayout)
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
        val lastModifiedDate: TextView = itemView.findViewById(R.id.lastModifiedDate)
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

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: List<CompanyInstall>) {
        itemList = filterlist as ArrayList<CompanyInstall>
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]

        if (Filter.selectedInstallItems.any { it -> it }) holder.constLayout.background =
            ContextCompat.getDrawable(holder.itemView.context, R.drawable.orange_rectangle)
        else holder.constLayout.background =
            ContextCompat.getDrawable(holder.itemView.context, R.drawable.rectangle)

        holder.companyName.text = currentItem.companyName
        holder.firstItem.text = currentItem.firstItem.toString()
        holder.secondItem.text = currentItem.secondItem.toString()
        holder.thirdItem.isChecked = currentItem.thirdItem
        holder.fourthItem.isChecked = currentItem.fourthItem
        holder.fifthItem.isChecked = currentItem.fifthItem
        holder.sixthItem.isChecked = currentItem.sixthItem
        holder.seventhItem.isChecked = currentItem.seventhItem
        holder.eightItem.isChecked = currentItem.eightItem
        holder.ninethItem.isChecked = currentItem.ninethItem
        holder.utolsoMegjegyzes.text = currentItem.lastComment?.megjegyzes
        holder.utolsoMegjegyzesDatum.text =
            localDateTimeToString(currentItem.lastComment?.megjegyzesIdo)
        holder.lastModifiedDate.text = localDateTimeToString(currentItem.lastModified)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, companyInstall: CompanyInstall)
        fun onPinGoClick(position: Int, companyInstall: CompanyInstall)
    }
}