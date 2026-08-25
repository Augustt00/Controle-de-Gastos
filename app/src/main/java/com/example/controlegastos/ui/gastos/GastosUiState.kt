package com.example.controlegastos.ui.gastos

import com.example.controlegastos.domain.model.DespesaDetalhada
import com.example.controlegastos.domain.model.GastoMensal
import java.time.YearMonth

data class GastosUiState(
    val carregando: Boolean = true,
    val mesSelecionado: YearMonth = YearMonth.now(),
    val gastosMensais: List<GastoMensal> = emptyList(),
    val totalMesSelecionado: Long = 0L,
    val despesasDoMes: List<DespesaDetalhada> = emptyList()
)