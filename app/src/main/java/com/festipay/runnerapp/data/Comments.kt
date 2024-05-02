package com.festipay.runnerapp.data

import java.time.LocalDateTime

data class Comments(var megjegyzesek: List<Comment>)

data class Comment(var megjegyzes: String, var megjegyzesIdo: LocalDateTime)
