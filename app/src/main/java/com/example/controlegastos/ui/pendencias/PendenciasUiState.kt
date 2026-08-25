package com.example.controlegastos.ui.pendencias

import com.example.controlegastos.domain.model.DespesaDetalhada

data class PendenciasUiState(
    val isLoading: Boolean = true,
    val pendencias: List<DespesaDetalhada> = emptyList(),
    val mensagemErro: String? = null
)