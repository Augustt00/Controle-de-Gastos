package com.example.controlegastos.ui.timeline

import com.example.controlegastos.domain.model.ProjecaoMensal
import java.time.YearMonth

data class TimelineUiState(
    val mesInicial: YearMonth = YearMonth.now(),
    val projecoes: List<ProjecaoMensal> = emptyList(),
    val carregando: Boolean = true
)