package com.festipay.runnerapp.RViews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.utilities.DateFormatter.LocalDateTimeToString

class CompanyInstallAdapter(private val companyInstallList: ArrayList<CompanyInstall>) :
    RecyclerView.Adapter<CompanyInstallAdapter.CompanyInstallViewHolder>() {

    private var mListener: OnItemClickListener? = null

    inner class CompanyInstallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val telephelyNev: TextView = itemView.findViewById(R.id.companyTitle)
        val kiadva: CheckBox = itemView.findViewById(R.id.firstCheckbox)
        val nemKirakhato: CheckBox = itemView.findViewById(R.id.secondCheckbox)
        val kirakva: CheckBox = itemView.findViewById(R.id.thirdCheckbox)
        val eloszto: CheckBox = itemView.findViewById(R.id.fourthCheckbox)
        val aram: CheckBox = itemView.findViewById(R.id.fifthCheckbox)
        val szoftver: CheckBox = itemView.findViewById(R.id.sixthCheckbox)
        val param: CheckBox = itemView.findViewById(R.id.seventhCheckbox)
        val teszt: CheckBox = itemView.findViewById(R.id.eightCheckbox)
        val utolsoMegjegyzes: TextView = itemView.findViewById(R.id.lastCommentText)
        val utolsoMegjegyzesDatum: TextView = itemView.findViewById(R.id.lastCommentDate)


        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    mListener?.onItemClick(position, companyInstallList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyInstallViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.install_companysite_listitem, parent, false)
        return CompanyInstallViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return companyInstallList.size
    }

    override fun onBindViewHolder(holder: CompanyInstallViewHolder, position: Int) {
        val currentItem = companyInstallList[position]
        holder.telephelyNev.text = currentItem.telephelyNev
        holder.kiadva.isChecked = currentItem.kiadva
        holder.nemKirakhato.isChecked = currentItem.nemKirakhato
        holder.kirakva.isChecked = currentItem.kirakva
        holder.eloszto.isChecked = currentItem.eloszto
        holder.aram.isChecked = currentItem.aram
        holder.szoftver.isChecked = currentItem.szoftver
        holder.param.isChecked = currentItem.param
        holder.teszt.isChecked = currentItem.teszt
        holder.utolsoMegjegyzes.text = currentItem.utolsoMegjegyzes?.megjegyzes
        holder.utolsoMegjegyzesDatum.text = LocalDateTimeToString(currentItem.utolsoMegjegyzes?.megjegyzesIdo)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, companyInstall: CompanyInstall)
    }
}