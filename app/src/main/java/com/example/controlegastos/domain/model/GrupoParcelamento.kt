// domain/model/GrupoParcelamento.kt
package com.example.controlegastos.domain.model

data class GrupoParcelamento(
    val id: Int,
    val qtdParcelas: Int,
    val valorTotal: Long,
    val descricaoBase: String
)