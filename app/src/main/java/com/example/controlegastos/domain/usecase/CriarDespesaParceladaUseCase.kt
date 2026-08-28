package com.example.controlegastos.domain.usecase

import com.example.controlegastos.domain.model.Despesa
import com.example.controlegastos.domain.model.GrupoParcelamento
import com.example.controlegastos.domain.model.NovaDespesaParcelada
import com.example.controlegastos.domain.repository.DespesaRepository
import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject

class CriarDespesaParceladaUseCase @Inject constructor(
    private val despesaRepository: DespesaRepository
) {
    suspend operator fun invoke(novaDespesa: NovaDespesaParcelada): Int {
        val descricao = novaDespesa.descricao.trim()
        require(descricao.isNotBlank()) { "A descrição da despesa é obrigatória." }
        require(novaDespesa.valorTotalCentavos > 0L) { "O valor total precisa ser maior que zero." }
        require(novaDespesa.quantidadeParcelas > 0) { "A quantidade de parcelas precisa ser maior que zero." }
        require(novaDespesa.categoriaId > 0) { "Selecione uma categoria válida." }

        val dataCompraBase = Instant.ofEpochMilli(novaDespesa.dataCompra)
            .atZone(ZoneOffset.UTC).toLocalDate()
        val vencimentoBase = Instant.ofEpochMilli(novaDespesa.dataPrimeiroVencimento)
            .atZone(ZoneOffset.UTC).toLocalDate()
        val valorBase = novaDespesa.valorTotalCentavos / novaDespesa.quantidadeParcelas
        val resto = novaDespesa.valorTotalCentavos % novaDespesa.quantidadeParcelas

        val grupo = GrupoParcelamento(
            id = 0,
            qtdParcelas = novaDespesa.quantidadeParcelas,
            valorTotal = novaDespesa.valorTotalCentavos,
            descricaoBase = descricao
        )

        val despesas = (0 until novaDespesa.quantidadeParcelas).map { indice ->
            val numero = indice + 1
            val valor = valorBase + if (indice.toLong() < resto) 1L else 0L
            val dataCompraParcela = dataCompraBase.plusMonths(indice.toLong())
            val vencimentoParcela = vencimentoBase.plusMonths(indice.toLong())

            Despesa(
                id = 0,
                valor = valor,
                descricao = if (novaDespesa.quantidadeParcelas == 1) descricao
                else "$descricao ($numero/${novaDespesa.quantidadeParcelas})",
                dataCompra = dataCompraParcela.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                dataVencimento = vencimentoParcela.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                dataPagamento = null,
                statusPago = false,
                categoriaId = novaDespesa.categoriaId,
                grupoParcelamentoId = null,
                cartaoId = novaDespesa.cartaoId
            )
        }
        return despesaRepository.criarDespesaParcelada(grupo, despesas)
    }
}