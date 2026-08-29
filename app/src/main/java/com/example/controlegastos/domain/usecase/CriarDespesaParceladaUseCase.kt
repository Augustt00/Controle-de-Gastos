package com.example.controlegastos.domain.usecase

import com.example.controlegastos.domain.model.Despesa
import com.example.controlegastos.domain.model.GrupoParcelamento
import com.example.controlegastos.domain.model.NovaDespesaParcelada
import com.example.controlegastos.domain.model.TipoLancamento
import com.example.controlegastos.domain.repository.DespesaRepository
import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject

class CriarDespesaParceladaUseCase @Inject constructor(
    private val despesaRepository: DespesaRepository
) {

    suspend operator fun invoke(
        novaDespesa: NovaDespesaParcelada
    ): Int {
        val descricao = novaDespesa.descricao.trim()

        require(descricao.isNotBlank()) {
            "A descrição da despesa é obrigatória."
        }

        require(novaDespesa.valorTotalCentavos > 0L) {
            "O valor deve ser maior que zero."
        }

        require(novaDespesa.quantidadeParcelas > 0) {
            "A quantidade deve ser maior que zero."
        }

        require(novaDespesa.categoriaId > 0) {
            "Selecione uma categoria válida."
        }

        val dataCompraBase = Instant
            .ofEpochMilli(novaDespesa.dataCompra)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        val dataVencimentoBase = Instant
            .ofEpochMilli(novaDespesa.dataPrimeiroVencimento)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        val quantidadeOcorrencias = when (novaDespesa.tipoLancamento) {
            TipoLancamento.UNICA -> 1
            TipoLancamento.PARCELADA -> novaDespesa.quantidadeParcelas
            TipoLancamento.FIXA -> 12
        }

        val valorPorOcorrencia = when (novaDespesa.tipoLancamento) {
            TipoLancamento.UNICA,
            TipoLancamento.FIXA -> novaDespesa.valorTotalCentavos

            TipoLancamento.PARCELADA -> {
                novaDespesa.valorTotalCentavos / novaDespesa.quantidadeParcelas
            }
        }

        val restoParcelamento = if (
            novaDespesa.tipoLancamento == TipoLancamento.PARCELADA
        ) {
            novaDespesa.valorTotalCentavos % novaDespesa.quantidadeParcelas
        } else {
            0L
        }

        val valorTotalGrupo = if (
            novaDespesa.tipoLancamento == TipoLancamento.FIXA
        ) {
            valorPorOcorrencia * quantidadeOcorrencias
        } else {
            novaDespesa.valorTotalCentavos
        }

        val grupo = GrupoParcelamento(
            id = 0,
            qtdParcelas = quantidadeOcorrencias,
            valorTotal = valorTotalGrupo,
            descricaoBase = descricao
        )

        val despesas = (0 until quantidadeOcorrencias).map { indice ->
            val numeroOcorrencia = indice + 1
            val valorOcorrencia = if (
                novaDespesa.tipoLancamento == TipoLancamento.PARCELADA
            ) {
                valorPorOcorrencia +
                        if (indice.toLong() < restoParcelamento) 1L else 0L
            } else {
                valorPorOcorrencia
            }

            val sufixo = when (novaDespesa.tipoLancamento) {
                TipoLancamento.UNICA -> ""
                TipoLancamento.PARCELADA -> " ($numeroOcorrencia/${novaDespesa.quantidadeParcelas})"
                TipoLancamento.FIXA -> ""
            }

            Despesa(
                id = 0,
                valor = valorOcorrencia,
                descricao = descricao + sufixo,
                dataCompra = dataCompraBase
                    .plusMonths(indice.toLong())
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant()
                    .toEpochMilli(),
                dataVencimento = dataVencimentoBase
                    .plusMonths(indice.toLong())
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant()
                    .toEpochMilli(),
                dataPagamento = null,
                statusPago = false,
                categoriaId = novaDespesa.categoriaId,
                grupoParcelamentoId = null,
                cartaoId = novaDespesa.cartaoId
            )
        }

        return despesaRepository.criarDespesaParcelada(
            grupo = grupo,
            despesas = despesas
        )
    }
}