package com.festipay.runnerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.CompanyDemolition
import com.festipay.runnerapp.utilities.DateFormatter

class CompanyDemolitionAdapter(private val itemList: ArrayList<CompanyDemolition>) :
    RecyclerView.Adapter<CompanyDemolitionAdapter.ItemViewHolder>() {

    private var mListener: OnItemClickListener? = null

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val telephelyNev: TextView = itemView.findViewById(R.id.companyTitle)
        val eszkozszam: TextView = itemView.findViewById(R.id.firstValue)
        val folyamatban: CheckBox = itemView.findViewById(R.id.secondCheckbox)
        val csomagolt: CheckBox = itemView.findViewById(R.id.thirddCheckbox)
        val autoban: CheckBox = itemView.findViewById(R.id.fourthCheckbox)
        val bazisLeszereles: CheckBox = itemView.findViewById(R.id.fifthCheckbox)
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
            .inflate(R.layout.demolition_companysite_listitem, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.telephelyNev.text = currentItem.telephelyNev
        holder.folyamatban.isChecked = currentItem.folyamatban
        holder.eszkozszam.text = currentItem.eszkozszam.toString()
        holder.csomagolt.isChecked = currentItem.csomagolt
        holder.autoban.isChecked = currentItem.autoban
        holder.bazisLeszereles.isChecked = currentItem.bazisLeszereles
        holder.utolsoMegjegyzes.text = currentItem.utolsoMegjegyzes?.megjegyzes
        holder.utolsoMegjegyzesDatum.text =
            DateFormatter.LocalDateTimeToString(currentItem.utolsoMegjegyzes?.megjegyzesIdo)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, companyDemolition: CompanyDemolition)
    }
}