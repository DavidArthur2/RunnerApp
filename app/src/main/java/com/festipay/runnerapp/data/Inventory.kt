package com.festipay.runnerapp.data

data class Inventory(var itemName: String, var quantity: Int, var docID: String = "", var lastComment: Comment? = null)
