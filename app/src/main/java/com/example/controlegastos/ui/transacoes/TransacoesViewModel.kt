package com.example.controlegastos.ui.transacoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlegastos.domain.model.Cartao
import com.example.controlegastos.domain.model.ContaSaldo
import com.example.controlegastos.domain.model.DespesaDetalhada
import com.example.controlegastos.domain.model.FaturaCartao
import com.example.controlegastos.domain.model.TipoLancamento
import com.example.controlegastos.domain.repository.CartaoRepository
import com.example.controlegastos.domain.repository.ContaSaldoRepository
import com.example.controlegastos.domain.repository.DespesaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class TransacoesViewModel @Inject constructor(
    private val despesaRepository: DespesaRepository,
    private val cartaoRepository: CartaoRepository,
    private val contaSaldoRepository: ContaSaldoRepository
) : ViewModel() {

    private val mesSelecionado = MutableStateFlow(YearMonth.now())

    private val valoresVisiveis = MutableStateFlow(true)

    private val abaSelecionada = MutableStateFlow(AbaFaturas.ABERTAS)

    private val cartoesExpandidos = MutableStateFlow<Set<Int>>(emptySet())

    /*
     * Carrega as despesas do mês selecionado pela data da compra.
     *
     * Este fluxo é usado para:
     * - despesas avulsas;
     * - despesas fixas;
     * - cálculo do saldo mensal.
     */
    private val despesasDoMesCompra: Flow<List<DespesaDetalhada>> =
        mesSelecionado.flatMapLatest { mes ->
            despesaRepository.observarDespesasDetalhadasPorMes(
                mes = mes.monthValue,
                ano = mes.year
            )
        }

    /*
     * Carrega um intervalo maior para formar a fatura completa.
     *
     * Para a fatura de Agosto:
     * - início da busca: 01/07;
     * - fim da busca: 01/09.
     *
     * Depois cada cartão aplica seu próprio dia de fechamento:
     * fechamento 29 => ciclo de 30/07 até 29/08.
     */
    private val despesasParaFaturas: Flow<List<DespesaDetalhada>> =
        mesSelecionado.flatMapLatest { mes ->
            val inicioEpoch = mes
                .minusMonths(1)
                .atDay(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()

            val fimEpoch = mes
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()

            despesaRepository.observarDetalhadasEntre(
                inicioEpoch = inicioEpoch,
                fimEpoch = fimEpoch
            )
        }

    private val contas: Flow<List<ContaSaldo>> =
        contaSaldoRepository.observarTodas()

    private val cartoes: Flow<List<Cartao>> =
        cartaoRepository.observarTodos()

    /*
     * Combina os quatro estados visuais primeiro.
     * Assim, não ultrapassamos o limite de cinco Flow na assinatura
     * tipada do combine da versão atual de Kotlin/Coroutines.
     */
    private val filtrosTela = combine(
        mesSelecionado,
        valoresVisiveis,
        abaSelecionada,
        cartoesExpandidos
    ) { mes, visiveis, aba, expandidos ->
        FiltrosTelaTransacoes(
            mes = mes,
            valoresVisiveis = visiveis,
            abaSelecionada = aba,
            cartoesExpandidos = expandidos
        )
    }

    /*
     * Combina os quatro fluxos de dados primeiro.
     */
    private val dadosTela = combine(
        despesasDoMesCompra,
        despesasParaFaturas,
        contas,
        cartoes
    ) { despesasMesCompra, despesasFaturas, contasAtuais, cartoesAtuais ->
        DadosTransacoes(
            despesasMesCompra = despesasMesCompra,
            despesasFaturas = despesasFaturas,
            contas = contasAtuais,
            cartoes = cartoesAtuais
        )
    }

    /*
     * Combinação final com somente dois Flow.
     */
    val uiState: StateFlow<TransacoesUiState> = combine(
        filtrosTela,
        dadosTela
    ) { filtros, dados ->

        val contasAtivas = dados.contas.filter { conta ->
            conta.ativo
        }

        val despesasAvulsas = dados.despesasMesCompra.filter { despesa ->
            despesa.cartaoId == null &&
                    despesa.tipoLancamento != TipoLancamento.FIXA
        }

        val despesasFixas = dados.despesasMesCompra.filter { despesa ->
            despesa.tipoLancamento == TipoLancamento.FIXA
        }

        val saldoInicialTotal = contasAtivas.sumOf { conta ->
            conta.saldoCentavos
        }

        /*
         * Despesas de cartão não entram aqui.
         * Elas reduzem o saldo somente no pagamento da fatura.
         */
        val despesasAvulsasTotal = despesasAvulsas
            .filter { despesa ->
                despesa.contaSaldoId != null &&
                        despesa.statusPago
            }
            .sumOf { despesa ->
                despesa.valor
            }

        val faturas = dados.cartoes
            .filter { cartao ->
                cartao.ativo
            }
            .map { cartao ->

                val inicioCiclo = inicioCicloFatura(
                    mesFatura = filtros.mes,
                    diaFechamento = cartao.diaFechamento
                )

                val fimExclusivoCiclo = fimExclusivoCicloFatura(
                    mesFatura = filtros.mes,
                    diaFechamento = cartao.diaFechamento
                )

                val despesasDoCartao = dados.despesasFaturas
                    .filter { despesa ->
                        despesa.cartaoId == cartao.id &&
                                despesa.dataCompra >= inicioCiclo &&
                                despesa.dataCompra < fimExclusivoCiclo
                    }
                    .sortedBy { despesa ->
                        despesa.dataCompra
                    }

                FaturaCartao(
                    cartao = cartao,
                    mesAno = filtros.mes,
                    totalCentavos = despesasDoCartao.sumOf { it.valor },
                    despesas = despesasDoCartao,
                    paga = despesasDoCartao.isNotEmpty() &&
                            despesasDoCartao.all { despesa ->
                                despesa.statusPago
                            }
                )
            }

        val faturasAbertas = faturas.filter { fatura ->
            fatura.despesas.isNotEmpty() &&
                    !fatura.paga
        }

        val faturasFechadas = faturas.filter { fatura ->
            fatura.despesas.isNotEmpty() &&
                    fatura.paga
        }

        TransacoesUiState(
            carregando = false,
            mesSelecionado = filtros.mes,
            valoresVisiveis = filtros.valoresVisiveis,
            abaSelecionada = filtros.abaSelecionada,
            cartoesExpandidos = filtros.cartoesExpandidos,
            saldoInicialTotal = saldoInicialTotal,
            despesasAvulsasTotal = despesasAvulsasTotal,
            saldoAtualTotal = saldoInicialTotal - despesasAvulsasTotal,
            contas = contasAtivas,
            faturasAbertas = faturasAbertas,
            faturasFechadas = faturasFechadas,
            despesasFixas = despesasFixas
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TransacoesUiState()
    )

    fun mesAnterior() {
        mesSelecionado.value = mesSelecionado.value.minusMonths(1)
    }

    fun proximoMes() {
        mesSelecionado.value = mesSelecionado.value.plusMonths(1)
    }

    fun alternarValores() {
        valoresVisiveis.value = !valoresVisiveis.value
    }

    fun selecionarAbaFaturas(aba: AbaFaturas) {
        abaSelecionada.value = aba
    }

    fun alternarCartao(cartaoId: Int) {
        cartoesExpandidos.value = if (
            cartaoId in cartoesExpandidos.value
        ) {
            cartoesExpandidos.value - cartaoId
        } else {
            cartoesExpandidos.value + cartaoId
        }
    }

    fun pagarFatura(
        cartaoId: Int,
        contaId: Int,
        aoConcluir: (String?) -> Unit
    ) {
        val mes = mesSelecionado.value

        viewModelScope.launch {
            try {
                val pago = despesaRepository.pagarFatura(
                    cartaoId = cartaoId,
                    mes = mes.monthValue,
                    ano = mes.year,
                    contaId = contaId
                )

                if (pago) {
                    aoConcluir(null)
                } else {
                    aoConcluir(
                        "Não foi possível pagar esta fatura."
                    )
                }
            } catch (erro: IllegalArgumentException) {
                aoConcluir(
                    erro.message
                        ?: "Não foi possível pagar esta fatura."
                )
            } catch (erro: Exception) {
                aoConcluir(
                    "Ocorreu um erro ao pagar a fatura."
                )
            }
        }
    }

    /*
     * Exemplo:
     *
     * Fatura Agosto/2026, cartão fecha no dia 29:
     * início = 30/07/2026 às 00:00 UTC
     */
    private fun inicioCicloFatura(
        mesFatura: YearMonth,
        diaFechamento: Int
    ): Long {
        val mesAnterior = mesFatura.minusMonths(1)

        val fechamentoMesAnterior = mesAnterior.atDay(
            diaFechamento.coerceAtMost(
                mesAnterior.lengthOfMonth()
            )
        )

        return fechamentoMesAnterior
            .plusDays(1)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }

    /*
     * Exemplo:
     *
     * Fatura Agosto/2026, cartão fecha no dia 29:
     * fim exclusivo = 30/08/2026 às 00:00 UTC
     */
    private fun fimExclusivoCicloFatura(
        mesFatura: YearMonth,
        diaFechamento: Int
    ): Long {
        val fechamentoMesAtual = mesFatura.atDay(
            diaFechamento.coerceAtMost(
                mesFatura.lengthOfMonth()
            )
        )

        return fechamentoMesAtual
            .plusDays(1)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }
}

private data class FiltrosTelaTransacoes(
    val mes: YearMonth,
    val valoresVisiveis: Boolean,
    val abaSelecionada: AbaFaturas,
    val cartoesExpandidos: Set<Int>
)

private data class DadosTransacoes(
    val despesasMesCompra: List<DespesaDetalhada>,
    val despesasFaturas: List<DespesaDetalhada>,
    val contas: List<ContaSaldo>,
    val cartoes: List<Cartao>
)