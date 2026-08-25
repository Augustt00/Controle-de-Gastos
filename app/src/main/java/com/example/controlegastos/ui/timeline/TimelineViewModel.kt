package com.example.controlegastos.ui.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlegastos.domain.usecase.GetProjecaoFuturaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class TimelineViewModel @Inject constructor(
    getProjecaoFuturaUseCase: GetProjecaoFuturaUseCase
) : ViewModel() {

    private val mesInicial = YearMonth.now()

    private val inicioEpoch = mesInicial
        .atDay(1)
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

    val uiState: StateFlow<TimelineUiState> = getProjecaoFuturaUseCase(
        inicioEpoch = inicioEpoch
    )
        .map { projecoes ->
            val limite = mesInicial.plusMonths(5)

            val proximosSeisMeses = projecoes.filter { projecao ->
                val mesDaProjecao = YearMonth.of(
                    projecao.ano,
                    projecao.mes
                )

                !mesDaProjecao.isBefore(mesInicial) &&
                        !mesDaProjecao.isAfter(limite)
            }

            TimelineUiState(
                mesInicial = mesInicial,
                projecoes = proximosSeisMeses,
                carregando = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TimelineUiState()
        )
}