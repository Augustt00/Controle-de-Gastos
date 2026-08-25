package com.example.controlegastos.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlegastos.domain.repository.DespesaRepository
import com.example.controlegastos.domain.usecase.GetGastosPorCategoriaUseCase
import com.example.controlegastos.domain.usecase.GetResumoMensalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getResumoMensalUseCase: GetResumoMensalUseCase,
    private val getGastosPorCategoriaUseCase: GetGastosPorCategoriaUseCase,
    private val despesaRepository: DespesaRepository
) : ViewModel() {

    private val mesSelecionado = MutableStateFlow(YearMonth.now())
    private val numerosVisiveis = MutableStateFlow(true)

    val uiState: StateFlow<DashboardUiState> = combine(
        mesSelecionado,
        numerosVisiveis
    ) { mesAno, valoresVisiveis ->
        mesAno to valoresVisiveis
    }
        .flatMapLatest { (mesAno, valoresVisiveis) ->
            combine(
                getResumoMensalUseCase(
                    mes = mesAno.monthValue,
                    ano = mesAno.year
                ),
                getGastosPorCategoriaUseCase(
                    mes = mesAno.monthValue,
                    ano = mesAno.year
                ),
                despesaRepository.observarDespesasDetalhadasPorMes(
                    mes = mesAno.monthValue,
                    ano = mesAno.year
                )
            ) { resumo, gastosPorCategoria, despesas ->
                DashboardUiState(
                    mesSelecionado = mesAno,
                    resumoMensal = resumo,
                    gastosPorCategoria = gastosPorCategoria,
                    transacoesDoMes = despesas.sortedByDescending { it.dataVencimento },
                    numerosVisiveis = valoresVisiveis,
                    carregando = false
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState()
        )

    fun irParaMesAnterior() {
        mesSelecionado.value = mesSelecionado.value.minusMonths(1)
    }

    fun irParaProximoMes() {
        mesSelecionado.value = mesSelecionado.value.plusMonths(1)
    }

    fun alternarVisibilidadeValores() {
        numerosVisiveis.value = !numerosVisiveis.value
    }
}