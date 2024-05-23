package com.festipay.runnerapp.utilities

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateFormatter {
    fun TimestampToLocalDateTime(input: Timestamp?): LocalDateTime? {
        if (input == null)return null
        val javaDate: java.util.Date = input.toDate()
        return LocalDateTime.ofInstant(javaDate.toInstant(), ZoneId.systemDefault())
    }

    fun LocalDateTimeToTimestamp(input: LocalDateTime): Timestamp {
        val instant = input.atZone(ZoneId.systemDefault()).toInstant()
        return Timestamp(instant.epochSecond, instant.nano)
    }

    fun LocalDateTimeToString(input: LocalDateTime?): String{
        val pattern = "yyyy.MM.dd - HH:mm"
        val formatter = DateTimeFormatter.ofPattern(pattern)
        if (input != null) {
            return input.format(formatter)
        }
        return ""
    }
}