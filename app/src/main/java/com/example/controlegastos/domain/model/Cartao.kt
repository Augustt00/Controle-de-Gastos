package com.example.controlegastos.domain.model

data class Cartao(
    val id: Int = 0,
    val nome: String,
    val marcaChave: String,
    val corHex: String,
    val ativo: Boolean = true,
    val diaFechamento: Int = 29,
    val diaVencimento: Int = 5,
    val limiteCentavos: Long = 0L // novo campo
)