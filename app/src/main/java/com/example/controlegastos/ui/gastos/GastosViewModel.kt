package com.example.controlegastos.ui.gastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlegastos.domain.model.GastoMensal
import com.example.controlegastos.domain.repository.DespesaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class GastosViewModel @Inject constructor(
    private val despesaRepository: DespesaRepository
) : ViewModel() {

    private val mesAtual = YearMonth.now()

    private val mesesDoGrafico = List(8) { indice ->
        mesAtual.minusMonths(5).plusMonths(indice.toLong())
    }

    private val mesSelecionado = MutableStateFlow(mesAtual)

    private val gastosMensais: Flow<List<GastoMensal>> = combine(
        mesesDoGrafico.map { mesAno ->
            despesaRepository
                .observarTotalGastoPorMes(
                    mes = mesAno.monthValue,
                    ano = mesAno.year
                )
                .map { total ->
                    GastoMensal(
                        mesAno = mesAno,
                        totalCentavos = total
                    )
                }
        }
    ) { totais ->
        totais.toList()
    }

    private val despesasDoMes = mesSelecionado.flatMapLatest { mesAno ->
        despesaRepository.observarDespesasDetalhadasPorMes(
            mes = mesAno.monthValue,
            ano = mesAno.year
        )
    }

    val uiState: StateFlow<GastosUiState> = combine(
        gastosMensais,
        mesSelecionado,
        despesasDoMes
    ) { totais, mesAno, despesas ->
        GastosUiState(
            carregando = false,
            mesSelecionado = mesAno,
            gastosMensais = totais,
            totalMesSelecionado = totais
                .firstOrNull { it.mesAno == mesAno }
                ?.totalCentavos
                ?: 0L,
            despesasDoMes = despesas.sortedByDescending { it.dataCompra }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GastosUiState()
    )

    fun selecionarMes(mesAno: YearMonth) {
        mesSelecionado.value = mesAno
    }
}