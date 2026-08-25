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

    suspend operator fun invoke(
        novaDespesa: NovaDespesaParcelada
    ): Int {
        val descricaoNormalizada = novaDespesa.descricao.trim()

        require(descricaoNormalizada.isNotBlank()) {
            "A descrição da despesa é obrigatória."
        }

        require(novaDespesa.valorTotalCentavos > 0L) {
            "O valor total precisa ser maior que zero."
        }

        require(novaDespesa.quantidadeParcelas > 0) {
            "A quantidade de parcelas precisa ser maior que zero."
        }

        require(novaDespesa.categoriaId > 0) {
            "Selecione uma categoria válida."
        }

        val dataBase = Instant
            .ofEpochMilli(novaDespesa.dataPrimeiroVencimento)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        val valorBaseParcela =
            novaDespesa.valorTotalCentavos / novaDespesa.quantidadeParcelas

        val restoCentavos =
            novaDespesa.valorTotalCentavos % novaDespesa.quantidadeParcelas

        val grupo = GrupoParcelamento(
            id = 0,
            qtdParcelas = novaDespesa.quantidadeParcelas,
            valorTotal = novaDespesa.valorTotalCentavos,
            descricaoBase = descricaoNormalizada
        )

        val despesas = (0 until novaDespesa.quantidadeParcelas).map { indice ->
            val numeroParcela = indice + 1

            val valorParcela = valorBaseParcela +
                    if (indice.toLong() < restoCentavos) 1L else 0L

            val vencimentoDaParcela = dataBase
                .plusMonths(indice.toLong())
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()

            Despesa(
                id = 0,
                valor = valorParcela,
                descricao = if (novaDespesa.quantidadeParcelas == 1) {
                    descricaoNormalizada
                } else {
                    "$descricaoNormalizada ($numeroParcela/${novaDespesa.quantidadeParcelas})"
                },
                dataVencimento = vencimentoDaParcela,
                dataPagamento = null,
                statusPago = false,
                categoriaId = novaDespesa.categoriaId,
                grupoParcelamentoId = null
            )
        }

        return despesaRepository.criarDespesaParcelada(
            grupo = grupo,
            despesas = despesas
        )
    }
}