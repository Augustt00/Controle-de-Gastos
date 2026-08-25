package com.example.controlegastos.domain.model

data class DespesaDetalhada(
    val id: Int,
    val valor: Long,
    val descricao: String,
    val dataVencimento: Long,
    val statusPago: Boolean,
    val categoriaId: Int,
    val categoriaNome: String,
    val categoriaCorHex: String
)