// domain/model/Despesa.kt
package com.example.controlegastos.domain.model

data class Despesa(
    val id: Int,
    val valor: Long,
    val descricao: String,
    val dataVencimento: Long,
    val dataPagamento: Long?,
    val statusPago: Boolean,
    val categoriaId: Int,
    val grupoParcelamentoId: Int?
)