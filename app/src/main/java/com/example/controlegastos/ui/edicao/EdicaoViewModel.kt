package com.example.controlegastos.ui.edicao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlegastos.domain.model.Cartao
import com.example.controlegastos.domain.model.Categoria
import com.example.controlegastos.domain.model.ContaSaldo
import com.example.controlegastos.domain.model.TipoContaSaldo
import com.example.controlegastos.domain.repository.CartaoRepository
import com.example.controlegastos.domain.repository.CategoriaRepository
import com.example.controlegastos.domain.repository.ContaSaldoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EdicaoViewModel @Inject constructor(
    private val categoriaRepository: CategoriaRepository,
    private val cartaoRepository: CartaoRepository,
    private val contaSaldoRepository: ContaSaldoRepository
) : ViewModel() {

    private val formulario = MutableStateFlow(EdicaoUiState())

    val uiState: StateFlow<EdicaoUiState> = combine(
        formulario,
        categoriaRepository.observarTodas(),
        cartaoRepository.observarTodos(),
        contaSaldoRepository.observarTodas()
    ) { formularioAtual, categorias, cartoes, contas ->
        formularioAtual.copy(
            carregando = false,
            categorias = categorias,
            cartoes = cartoes,
            contas = contas
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EdicaoUiState()
    )

    fun selecionarCategoriaSugerida(categoria: CategoriaSugerida) {
        formulario.value = formulario.value.copy(
            novaCategoriaNome = categoria.nome,
            novoIconeCategoria = categoria.iconeChave,
            novaCategoriaCorHex = categoria.corHex,
            mensagem = null
        )
    }

    fun atualizarNomeCategoria(nome: String) {
        formulario.value = formulario.value.copy(
            novaCategoriaNome = nome,
            mensagem = null
        )
    }

    fun atualizarTetoCategoria(texto: String) {
        formulario.value = formulario.value.copy(
            novaCategoriaTetoTexto = texto.filter(Char::isDigit),
            mensagem = null
        )
    }

    fun salvarCategoria() {
        val estado = formulario.value
        val nome = estado.novaCategoriaNome.trim()

        if (nome.isBlank()) {
            formulario.value = estado.copy(mensagem = "Informe o nome da categoria.")
            return
        }

        if (estado.categorias.any { it.nome.equals(nome, ignoreCase = true) }) {
            formulario.value = estado.copy(mensagem = "Essa categoria já está cadastrada.")
            return
        }

        viewModelScope.launch {
            runCatching {
                categoriaRepository.salvar(
                    Categoria(
                        id = 0,
                        nome = nome,
                        corHex = estado.novaCategoriaCorHex,
                        tetoMensal = estado.novaCategoriaTetoTexto
                            .takeIf { it.isNotBlank() }
                            ?.toLong(),
                        iconeChave = estado.novoIconeCategoria,
                        ativa = true
                    )
                )
            }.onSuccess {
                formulario.value = EdicaoUiState(
                    instituicaoSelecionada = estado.instituicaoSelecionada,
                    tipoContaSelecionado = estado.tipoContaSelecionado,
                    mensagem = "Categoria adicionada."
                )
            }.onFailure {
                formulario.value = estado.copy(
                    mensagem = it.message ?: "Não foi possível salvar a categoria."
                )
            }
        }
    }

    fun alterarAtivacaoCategoria(categoria: Categoria, ativa: Boolean) {
        viewModelScope.launch {
            categoriaRepository.atualizarAtivacao(categoria.id, ativa)
        }
    }

    fun alterarAtivacaoCartao(instituicao: InstituicaoPredefinida, ativo: Boolean) {
        val cartaoExistente = uiState.value.cartoes.firstOrNull {
            it.marcaChave == instituicao.chave
        }

        viewModelScope.launch {
            if (cartaoExistente == null) {
                cartaoRepository.salvarOuAtualizarPorMarca(
                    Cartao(
                        nome = instituicao.nome,
                        marcaChave = instituicao.chave,
                        corHex = instituicao.cor.toHex(),
                        ativo = ativo
                    )
                )
            } else {
                cartaoRepository.atualizarAtivacao(cartaoExistente.id, ativo)
            }
        }
    }

    fun selecionarInstituicao(instituicao: InstituicaoPredefinida) {
        formulario.value = formulario.value.copy(instituicaoSelecionada = instituicao)
    }

    fun selecionarTipoConta(tipo: TipoContaSaldo) {
        formulario.value = formulario.value.copy(tipoContaSelecionado = tipo)
    }

    fun atualizarSaldoInicial(texto: String) {
        formulario.value = formulario.value.copy(
            saldoInicialTexto = texto.filter(Char::isDigit),
            mensagem = null
        )
    }

    fun salvarContaSaldo() {
        val estado = formulario.value
        val saldo = estado.saldoInicialTexto.toLongOrNull()

        if (saldo == null || saldo < 0L) {
            formulario.value = estado.copy(mensagem = "Informe um saldo inicial válido.")
            return
        }

        viewModelScope.launch {
            runCatching {
                val instituicao = estado.instituicaoSelecionada
                contaSaldoRepository.salvar(
                    ContaSaldo(
                        nome = when (estado.tipoContaSelecionado) {
                            TipoContaSaldo.CONTA -> instituicao.nome
                            TipoContaSaldo.CARTEIRA -> "Carteira"
                            TipoContaSaldo.SALDO_RESERVADO -> "Saldo reservado ${instituicao.nome}"
                        },
                        instituicaoChave = instituicao.chave,
                        tipo = estado.tipoContaSelecionado,
                        saldoCentavos = saldo,
                        corHex = instituicao.cor.toHex(),
                        ativo = true
                    )
                )
            }.onSuccess {
                formulario.value = EdicaoUiState(
                    instituicaoSelecionada = estado.instituicaoSelecionada,
                    tipoContaSelecionado = estado.tipoContaSelecionado,
                    mensagem = "Conta adicionada."
                )
            }.onFailure {
                formulario.value = estado.copy(
                    mensagem = it.message ?: "Não foi possível salvar a conta."
                )
            }
        }
    }

    fun alterarAtivacaoConta(conta: ContaSaldo, ativo: Boolean) {
        viewModelScope.launch {
            contaSaldoRepository.atualizarAtivacao(conta.id, ativo)
        }
    }

    fun consumirMensagem() {
        formulario.value = formulario.value.copy(mensagem = null)
    }
}

private fun androidx.compose.ui.graphics.Color.toHex(): String {
    return "#%02X%02X%02X".format(
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
}