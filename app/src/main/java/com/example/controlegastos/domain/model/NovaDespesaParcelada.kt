package com.example.controlegastos.domain.model

data class NovaDespesaParcelada(
    val descricao: String,
    val valorTotalCentavos: Long,
    val quantidadeParcelas: Int,
    val dataPrimeiroVencimento: Long,
    val categoriaId: Int
)