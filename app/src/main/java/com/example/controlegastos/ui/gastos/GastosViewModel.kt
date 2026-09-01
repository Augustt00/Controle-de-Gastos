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
import com.example.controlegastos.domain.repository.CartaoRepository

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

    private val faturasComIntervalo = despesaRepository
        .observarFaturasAbertasPorMes()
        .map { faturas -> faturas.preencherMesesIntermediarios() }

    private val cartoesAtivosFlow = cartaoRepository.observarAtivos()

    // Deriva um mês "válido" com base nas faturas e na seleção do usuário.
    // Isso garante consistência: as consultas abaixo usarão o mesmo mês que a UI exibirá.
    private val mesValidoFlow = combine(faturasComIntervalo, mesSelecionado) { faturas, selecionado ->
        when {
            faturas.any { it.mesAno == selecionado } -> selecionado
            faturas.any { it.mesAno == YearMonth.now() } -> YearMonth.now()
            faturas.isNotEmpty() -> faturas.first().mesAno
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

    val uiState: StateFlow<GastosUiState> = combine(
        faturasComIntervalo,
        mesValidoFlow,
        despesasDaFatura,
        gastosPorCategoriaFlow,
        cartoesAtivosFlow
    ) { faturas, mesValido, despesas, gastosPorCategoria, cartoesAtivos ->
        GastosUiState(
            carregando = false,
            mesSelecionado = mesValido,
            gastosMensais = faturas.map { fatura ->
                GastoMensal(
                    mesAno = fatura.mesAno,
                    totalCentavos = fatura.totalCentavos
                )
            },
            totalMesSelecionado = faturas.firstOrNull { it.mesAno == mesValido }?.totalCentavos ?: 0L,
            despesasDoMes = despesas.sortedByDescending { it.dataCompra },
            gastosPorCategoria = gastosPorCategoria,
            cartoes = cartoesAtivos
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