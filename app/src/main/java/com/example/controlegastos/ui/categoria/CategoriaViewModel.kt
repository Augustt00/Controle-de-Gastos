package com.example.controlegastos.ui.categoria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlegastos.domain.model.Categoria
import com.example.controlegastos.domain.usecase.BuscarCategoriasUseCase
import com.example.controlegastos.domain.usecase.SalvarCategoriaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriaViewModel @Inject constructor(
    buscarCategoriasUseCase: BuscarCategoriasUseCase,
    private val salvarCategoriaUseCase: SalvarCategoriaUseCase
) : ViewModel() {

    private val formulario = MutableStateFlow(CategoriaUiState())

    val uiState: StateFlow<CategoriaUiState> = combine(
        formulario,
        buscarCategoriasUseCase()
    ) { estadoFormulario, categorias ->
        estadoFormulario.copy(
            categorias = categorias,
            carregando = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CategoriaUiState(carregando = true)
    )

    fun atualizarNome(nome: String) {
        formulario.value = formulario.value.copy(
            nome = nome,
            mensagemErro = null,
            categoriaSalvaComSucesso = false
        )
    }

    fun atualizarTetoMensal(texto: String) {
        val somenteDigitos = texto.filter(Char::isDigit)

        formulario.value = formulario.value.copy(
            tetoMensalTexto = somenteDigitos,
            mensagemErro = null,
            categoriaSalvaComSucesso = false
        )
    }

    fun selecionarCor(corHex: String) {
        formulario.value = formulario.value.copy(
            corHexSelecionada = corHex
        )
    }

    fun salvarCategoria() {
        val estadoAtual = formulario.value

        viewModelScope.launch {
            formulario.value = estadoAtual.copy(
                salvando = true,
                mensagemErro = null,
                categoriaSalvaComSucesso = false
            )

            try {
                salvarCategoriaUseCase(
                    Categoria(
                        id = 0,
                        nome = estadoAtual.nome,
                        corHex = estadoAtual.corHexSelecionada,
                        tetoMensal = estadoAtual.tetoMensalTexto
                            .takeIf { it.isNotBlank() }
                            ?.toLong()
                    )
                )

                formulario.value = CategoriaUiState(
                    corHexSelecionada = estadoAtual.corHexSelecionada,
                    categoriaSalvaComSucesso = true
                )
            } catch (erro: IllegalArgumentException) {
                formulario.value = estadoAtual.copy(
                    salvando = false,
                    mensagemErro = erro.message ?: "Não foi possível salvar a categoria."
                )
            } catch (erro: Exception) {
                formulario.value = estadoAtual.copy(
                    salvando = false,
                    mensagemErro = "Ocorreu um erro ao salvar a categoria."
                )
            }
        }
    }

    fun consumirSucesso() {
        formulario.value = formulario.value.copy(
            categoriaSalvaComSucesso = false
        )
    }
}