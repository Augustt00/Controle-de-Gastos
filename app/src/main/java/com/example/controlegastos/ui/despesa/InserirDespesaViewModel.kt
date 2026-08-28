package com.example.controlegastos.ui.despesa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlegastos.domain.model.NovaDespesaParcelada
import com.example.controlegastos.domain.model.TipoLancamento
import com.example.controlegastos.domain.repository.CartaoRepository
import com.example.controlegastos.domain.usecase.BuscarCategoriasUseCase
import com.example.controlegastos.domain.usecase.CalcularVencimentoCartaoUseCase
import com.example.controlegastos.domain.usecase.CriarDespesaParceladaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class InserirDespesaViewModel @Inject constructor(
    buscarCategoriasUseCase: BuscarCategoriasUseCase,
    private val criarDespesaParceladaUseCase: CriarDespesaParceladaUseCase,
    private val cartaoRepository: CartaoRepository,
    private val calcularVencimentoCartao: CalcularVencimentoCartaoUseCase
) : ViewModel() {

    private val formulario = MutableStateFlow(InserirDespesaUiState())

    val uiState: StateFlow<InserirDespesaUiState> = combine(
        formulario,
        buscarCategoriasUseCase(somenteAtivas = true),
        cartaoRepository.observarAtivos()
    ) { estadoFormulario, categorias, cartoes ->
        estadoFormulario.copy(
            categorias = categorias,
            cartoes = cartoes,
            carregandoCategorias = false,
            carregandoCartoes = false,
            categoriaSelecionada = estadoFormulario.categoriaSelecionada
                ?.let { selecionada -> categorias.find { it.id == selecionada.id } },
            cartaoSelecionado = estadoFormulario.cartaoSelecionado
                ?.let { selecionado -> cartoes.find { it.id == selecionado.id } }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InserirDespesaUiState()
    )

    fun atualizarValor(texto: String) {
        formulario.value = formulario.value.copy(
            valorTexto = texto.filter(Char::isDigit),
            mensagemErro = null,
            despesaSalvaComSucesso = false
        )
    }

    fun atualizarDescricao(descricao: String) {
        formulario.value = formulario.value.copy(
            descricao = descricao,
            mensagemErro = null,
            despesaSalvaComSucesso = false
        )
    }

    fun selecionarCategoria(categoriaId: Int) {
        val categoria = uiState.value.categorias.find {
            it.id == categoriaId
        }

        formulario.value = formulario.value.copy(
            categoriaSelecionada = categoria,
            mensagemErro = null
        )
    }

    fun selecionarCartao(cartaoId: Int?) {
        val cartao = uiState.value.cartoes.firstOrNull { it.id == cartaoId }
        formulario.value = formulario.value.copy(
            cartaoSelecionado = cartao,
            mensagemErro = null
        )
    }

    fun alterarTipoLancamento(tipo: TipoLancamento) {
        formulario.value = formulario.value.copy(
            tipoLancamento = tipo,
            quantidadeParcelas = if (tipo == TipoLancamento.PARCELADA) {
                formulario.value.quantidadeParcelas.coerceAtLeast(2)
            } else {
                1
            },
            mensagemErro = null
        )
    }

    fun atualizarDataCompra(data: LocalDate) {
        formulario.value = formulario.value.copy(
            dataCompra = data,
            mensagemErro = null
        )
    }

    fun atualizarQuantidadeParcelas(valor: String) {
        val quantidade = valor
            .filter(Char::isDigit)
            .toIntOrNull()
            ?.coerceIn(2, 120)
            ?: 2

        formulario.value = formulario.value.copy(
            quantidadeParcelas = quantidade,
            mensagemErro = null
        )
    }

    fun salvarDespesa() {
        val estadoAtual = formulario.value
        val categoria = estadoAtual.categoriaSelecionada
        if (categoria == null) {
            formulario.value = estadoAtual.copy(
                mensagemErro = "Selecione uma categoria."
            )
            return
        }

        val valorCentavos = estadoAtual.valorTexto.toLongOrNull()
        if (valorCentavos == null || valorCentavos <= 0L) {
            formulario.value = estadoAtual.copy(
                mensagemErro = "Informe um valor válido."
            )
            return
        }

        val cartao = estadoAtual.cartaoSelecionado
        val dataPrimeiroVencimento = if (cartao != null) {
            calcularVencimentoCartao(
                dataCompra = estadoAtual.dataCompra,
                diaFechamento = cartao.diaFechamento,
                diaVencimento = cartao.diaVencimento
            )
        } else {
            estadoAtual.dataCompra
        }

        viewModelScope.launch {
            formulario.value = estadoAtual.copy(
                salvando = true,
                mensagemErro = null,
                despesaSalvaComSucesso = false
            )
            try {
                criarDespesaParceladaUseCase(
                    NovaDespesaParcelada(
                        descricao = estadoAtual.descricao,
                        valorTotalCentavos = valorCentavos,
                        quantidadeParcelas = if (estadoAtual.tipoLancamento == TipoLancamento.PARCELADA) {
                            estadoAtual.quantidadeParcelas
                        } else 1,
                        dataCompra = estadoAtual.dataCompra
                            .atStartOfDay(ZoneOffset.UTC)
                            .toInstant()
                            .toEpochMilli(),
                        dataPrimeiroVencimento = dataPrimeiroVencimento
                            .atStartOfDay(ZoneOffset.UTC)
                            .toInstant()
                            .toEpochMilli(),
                        categoriaId = categoria.id,
                        cartaoId = cartao?.id
                    )
                )
                formulario.value = InserirDespesaUiState(
                    dataCompra = estadoAtual.dataCompra,
                    despesaSalvaComSucesso = true
                )
            } catch (erro: IllegalArgumentException) {
                formulario.value = estadoAtual.copy(
                    salvando = false,
                    mensagemErro = erro.message ?: "Não foi possível salvar a despesa."
                )
            } catch (erro: Exception) {
                formulario.value = estadoAtual.copy(
                    salvando = false,
                    mensagemErro = "Ocorreu um erro ao salvar a despesa."
                )
            }
        }
    }

    fun consumirSucesso() {
        formulario.value = formulario.value.copy(
            despesaSalvaComSucesso = false
        )
    }
}