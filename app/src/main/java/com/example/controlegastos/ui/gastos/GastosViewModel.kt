package com.example.controlegastos.ui.gastos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlegastos.domain.model.FaturaMensal
import com.example.controlegastos.domain.model.GastoMensal
import com.example.controlegastos.domain.model.GastoPorCategoria
import com.example.controlegastos.domain.repository.DespesaRepository
import com.example.controlegastos.domain.usecase.ExcluirDespesaUseCase
import com.example.controlegastos.domain.usecase.GetGastosPorCategoriaUseCase
import com.example.controlegastos.domain.repository.CartaoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

private const val TAG = "GastosViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GastosViewModel @Inject constructor(
    private val despesaRepository: DespesaRepository,
    private val excluirDespesaUseCase: ExcluirDespesaUseCase,
    private val getGastosPorCategoriaUseCase: GetGastosPorCategoriaUseCase,
    private val cartaoRepository: CartaoRepository
) : ViewModel() {

    private val mesSelecionado = MutableStateFlow(YearMonth.now())

    // Mantido para telas de fatura (não usado mais na UI de gastos)
    private val faturasComIntervalo = despesaRepository
        .observarFaturasAbertasPorMes()
        .map { faturas -> faturas.preencherMesesIntermediarios() }

    // NOVO: observa gastos agrupados por mês com base em data_compra
    private val gastosAgrupadosPorMes = despesaRepository
        .observarGastosAgrupadosPorMes()
        .map { projecoes ->
            projecoes.map { p ->
                GastoMensal(
                    mesAno = YearMonth.of(p.ano, p.mes),
                    totalCentavos = p.totalPendente
                )
            }
        }

    private val cartoesAtivosFlow = cartaoRepository.observarAtivos()

    // Deriva um mês "válido" com base nos gastos agrupados (data_compra) e na seleção do usuário
    private val mesValidoFlow = combine(gastosAgrupadosPorMes, mesSelecionado) { gastosMensais, selecionado ->
        when {
            gastosMensais.any { it.mesAno == selecionado } -> selecionado
            gastosMensais.any { it.mesAno == YearMonth.now() } -> YearMonth.now()
            gastosMensais.isNotEmpty() -> gastosMensais.first().mesAno
            else -> selecionado
        }
    }

    // Consultas baseadas no mesValidoFlow (não no mesSelecionado direto)
    private val despesasDaFatura = mesValidoFlow.flatMapLatest { mesAno ->
        despesaRepository.observarDespesasDetalhadasDaFatura(
            mes = mesAno.monthValue,
            ano = mesAno.year
        )
    }

    private val gastosPorCategoriaFlow = mesValidoFlow.flatMapLatest { mesAno ->
        getGastosPorCategoriaUseCase(mes = mesAno.monthValue, ano = mesAno.year)
    }

    // NOVO: total do mês selecionado calculado por data_compra
    private val totalDoMesFlow = mesValidoFlow.flatMapLatest { mesAno ->
        despesaRepository.observarTotalGastoPorMes(
            mes = mesAno.monthValue,
            ano = mesAno.year
        )
    }

    val uiStateBase: Flow<GastosUiState> = combine(
        gastosAgrupadosPorMes,
        mesValidoFlow,
        despesasDaFatura,
        gastosPorCategoriaFlow,
        cartoesAtivosFlow
    ) { gastosMensais, mesValido, despesas, gastosPorCategoria, cartoesAtivos ->
        GastosUiState(
            carregando = false,
            mesSelecionado = mesValido,
            gastosMensais = gastosMensais,
            totalMesSelecionado = 0L,
            despesasDoMes = despesas.sortedByDescending { it.dataCompra },
            gastosPorCategoria = gastosPorCategoria,
            cartoes = cartoesAtivos
        )
    }

    val uiState: StateFlow<GastosUiState> = combine(
        uiStateBase,
        totalDoMesFlow
    ) { estado, totalDoMes ->
        estado.copy(
            totalMesSelecionado = totalDoMes
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GastosUiState()
    )

    fun selecionarMes(mesAno: YearMonth) {
        mesSelecionado.value = mesAno
    }

    fun excluirDespesa(despesaId: Int) {
        viewModelScope.launch {
            excluirDespesaUseCase(despesaId)
        }
    }
}

private fun List<FaturaMensal>.preencherMesesIntermediarios(): List<FaturaMensal> {
    if (isEmpty()) return emptyList()

    val totaisPorMes = associateBy { it.mesAno }
    val primeiroMesComFatura = minOf { it.mesAno }
    val ultimoMesComFatura = maxOf { it.mesAno }
    val primeiroMes = primeiroMesComFatura.minusMonths(1)

    return generateSequence(primeiroMes) { mesAtual ->
        mesAtual.takeIf { it < ultimoMesComFatura }?.plusMonths(1)
    }.map { mesAno ->
        totaisPorMes[mesAno] ?: FaturaMensal(
            mesAno = mesAno,
            totalCentavos = 0L
        )
    }.toList()
}