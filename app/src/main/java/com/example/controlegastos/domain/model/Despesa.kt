package com.example.controlegastos.domain.model

data class Despesa(
    val id: Int,
    val valor: Long,
    val descricao: String,
    val dataCompra: Long,
    val dataVencimento: Long,
    val dataPagamento: Long?,
    val statusPago: Boolean,
    val categoriaId: Int,
    val grupoParcelamentoId: Int?,
    val cartaoId: Int? = null,
    val contaSaldoId: Int? = null,
    val tipoLancamento: TipoLancamento = TipoLancamento.UNICA,
    val origemPagamento: OrigemPagamento? = null
)