package com.example.controlegastos.ui.dashboard

import com.example.controlegastos.domain.model.DespesaDetalhada
import com.example.controlegastos.domain.model.GastoPorCategoria
import com.example.controlegastos.domain.model.ResumoMensal
import java.time.YearMonth

data class DashboardUiState(
    val mesSelecionado: YearMonth = YearMonth.now(),
    val resumoMensal: ResumoMensal = ResumoMensal(
        totalGasto = 0L,
        totalPago = 0L,
        totalPendente = 0L
    ),
    val gastosPorCategoria: List<GastoPorCategoria> = emptyList(),
    val transacoesDoMes: List<DespesaDetalhada> = emptyList(),
    val numerosVisiveis: Boolean = true,
    val carregando: Boolean = true
)