package com.example.controlegastos.domain.model

data class DespesaDetalhada(
    val id: Int,
    val valor: Long,
    val descricao: String,
    val dataCompra: Long,
    val dataVencimento: Long,
    val statusPago: Boolean,
    val categoriaId: Int,
    val categoriaNome: String,
    val categoriaCorHex: String,
    val cartaoId: Int? = null,
    val contaSaldoId: Int? = null,
    val tipoLancamento: TipoLancamento = TipoLancamento.UNICA,
    val origemPagamento: OrigemPagamento? = null,
    val categoriaIconeChave: String?,
)