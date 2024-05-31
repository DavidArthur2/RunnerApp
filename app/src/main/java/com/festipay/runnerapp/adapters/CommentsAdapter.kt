package com.festipay.runnerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.Comment
import com.festipay.runnerapp.utilities.DateFormatter

class CommentsAdapter(private val itemList: ArrayList<Comment>) :
    RecyclerView.Adapter<CommentsAdapter.ItemViewHolder>() {

    private var mListener: OnItemDeleteListener? = null

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.commentText)
        val timestamp: TextView = itemView.findViewById(R.id.commentDate)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

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
            .inflate(R.layout.fragment_comments_listitem, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.message.text = currentItem.megjegyzes
        holder.timestamp.text = DateFormatter.localDateTimeToString(currentItem.megjegyzesIdo)
    }

    fun setOnItemClickListener(listener: OnItemDeleteListener) {
        mListener = listener
    }

    interface OnItemDeleteListener {
        fun onItemDelete(position: Int, comment: Comment)
    }
}