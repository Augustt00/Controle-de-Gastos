package com.example.controlegastos.domain.model

data class NovaDespesaParcelada(
    val descricao: String,
    val valorTotalCentavos: Long,
    val quantidadeParcelas: Int,
    val dataCompra: Long,
    val dataPrimeiroVencimento: Long,
    val categoriaId: Int,
    val cartaoId: Int? = null,
    val tipoLancamento: TipoLancamento = TipoLancamento.PARCELADA
)