package com.example.controlegastos.data.local.projection

import androidx.room.ColumnInfo

data class CategoriaSomaTuple(
    @ColumnInfo(name = "categoria_id")
    val categoriaId: Int,

    @ColumnInfo(name = "categoria_nome")
    val categoriaNome: String,

    @ColumnInfo(name = "categoria_cor_hex")
    val categoriaCorHex: String,

    @ColumnInfo(name = "teto_mensal")
    val tetoMensal: Long?,

    @ColumnInfo(name = "total_centavos")
    val totalCentavos: Long
)