package com.example.controlegastos.data.local.projection

import androidx.room.ColumnInfo

data class FaturaMensalTuple(
    @ColumnInfo(name = "ano")
    val ano: Int,

    @ColumnInfo(name = "mes")
    val mes: Int,

    @ColumnInfo(name = "total_centavos")
    val totalCentavos: Long
)