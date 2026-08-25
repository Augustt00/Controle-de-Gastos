package com.example.controlegastos.ui.despesa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlegastos.domain.model.NovaDespesaParcelada
import com.example.controlegastos.domain.usecase.BuscarCategoriasUseCase
import com.example.controlegastos.domain.usecase.CriarDespesaParceladaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class InserirDespesaViewModel @Inject constructor(
    buscarCategoriasUseCase: BuscarCategoriasUseCase,
    private val criarDespesaParceladaUseCase: CriarDespesaParceladaUseCase
) : ViewModel() {

    private val formulario = MutableStateFlow(InserirDespesaUiState())

    val uiState: StateFlow<InserirDespesaUiState> = combine(
        formulario,
        buscarCategoriasUseCase(somenteAtivas = true)
    ) { estadoFormulario, categorias ->
        estadoFormulario.copy(
            categorias = categorias,
            carregandoCategorias = false,
            categoriaSelecionada = estadoFormulario.categoriaSelecionada
                ?.let { selecionada ->
                    categorias.find { it.id == selecionada.id }
                }
                ?: estadoFormulario.categoriaSelecionada
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

    fun atualizarDataVencimento(data: LocalDate) {
        formulario.value = formulario.value.copy(
            dataVencimento = data,
            mensagemErro = null
        )
    }

    fun alterarParcelamento(parcelado: Boolean) {
        formulario.value = formulario.value.copy(
            parcelado = parcelado,
            quantidadeParcelas = if (parcelado) {
                formulario.value.quantidadeParcelas.coerceAtLeast(2)
            } else {
                1
            }
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
                        quantidadeParcelas = if (estadoAtual.parcelado) {
                            estadoAtual.quantidadeParcelas
                        } else {
                            1
                        },
                        dataPrimeiroVencimento = estadoAtual.dataVencimento
                            .atStartOfDay(ZoneOffset.UTC)
                            .toInstant()
                            .toEpochMilli(),
                        categoriaId = categoria.id
                    )
                )

                formulario.value = InserirDespesaUiState(
                    dataVencimento = estadoAtual.dataVencimento,
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