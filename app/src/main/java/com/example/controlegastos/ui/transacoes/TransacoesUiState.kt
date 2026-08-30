package com.example.controlegastos.ui.transacoes

import com.example.controlegastos.domain.model.ContaSaldo
import com.example.controlegastos.domain.model.DespesaDetalhada
import com.example.controlegastos.domain.model.FaturaCartao
import java.time.YearMonth

data class TransacoesUiState(
    val carregando: Boolean = true,
    val mesSelecionado: YearMonth = YearMonth.now(),
    val valoresVisiveis: Boolean = true,
    val abaSelecionada: AbaFaturas = AbaFaturas.ABERTAS,
    val cartoesExpandidos: Set<Int> = emptySet(),
    val saldoInicialTotal: Long = 0L,
    val despesasAvulsasTotal: Long = 0L,
    val saldoAtualTotal: Long = 0L,
    val contas: List<ContaSaldo> = emptyList(),
    val faturasAbertas: List<FaturaCartao> = emptyList(),
    val faturasFechadas: List<FaturaCartao> = emptyList(),
    val despesasFixas: List<DespesaDetalhada> = emptyList(),
    val mensagemSucesso: String? = null,
    val mensagemErro: String? = null,
    val processandoPagamento: Boolean = false
)

enum class AbaFaturas {
    ABERTAS,
    FECHADAS
}