package com.festipay.runnerapp.fragments

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface IFragment<T> {
    var recyclerView: RecyclerView
    var itemList: ArrayList<T>
    fun loadList(view: View)
    fun loadComments(view: View)
    fun setupView(view: View)

    fun onViewLoaded()
}