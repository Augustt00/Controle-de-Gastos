package com.example.controlegastos.data.local.converter

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class DatabaseConverters {

    @TypeConverter
    fun localDateParaEpochMillis(data: LocalDate?): Long? {
        return data
            ?.atStartOfDay(ZoneOffset.UTC)
            ?.toInstant()
            ?.toEpochMilli()
    }

    @TypeConverter
    fun epochMillisParaLocalDate(valor: Long?): LocalDate? {
        return valor
            ?.let(Instant::ofEpochMilli)
            ?.atZone(ZoneOffset.UTC)
            ?.toLocalDate()
    }
}