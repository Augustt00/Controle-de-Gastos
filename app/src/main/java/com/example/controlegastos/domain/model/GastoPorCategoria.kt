package com.example.controlegastos.domain.model

data class GastoPorCategoria(
    val categoriaId: Int,
    val nomeCategoria: String,
    val corHex: String,
    val tetoMensal: Long?,
    val totalGasto: Long,
    val percentualDoTotal: Float
)