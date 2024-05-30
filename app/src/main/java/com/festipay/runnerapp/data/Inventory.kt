package com.festipay.runnerapp.data

import java.time.LocalDateTime

data class Inventory(
    var itemName: String,
    var quantity: Int,
    var docID: String = "",
    var lastComment: Comment? = null,
    var lastModified: LocalDateTime? = null
) : IData
