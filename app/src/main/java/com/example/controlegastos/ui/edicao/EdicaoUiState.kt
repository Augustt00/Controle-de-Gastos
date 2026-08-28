package com.example.controlegastos.ui.edicao

import com.example.controlegastos.domain.model.Cartao
import com.example.controlegastos.domain.model.Categoria
import com.example.controlegastos.domain.model.ContaSaldo
import com.example.controlegastos.domain.model.TipoContaSaldo


data class EdicaoUiState(
    val carregando: Boolean = true,
    val salvando: Boolean = false,
    val categorias: List<Categoria> = emptyList(),
    val cartoes: List<Cartao> = emptyList(),
    val contas: List<ContaSaldo> = emptyList(),

    val novaCategoriaNome: String = "",
    val novaCategoriaTetoTexto: String = "",
    val novoIconeCategoria: String = "outros",
    val novaCategoriaCorHex: String = "#5F8D84",

    val instituicaoSelecionada: InstituicaoPredefinida = instituicoesPredefinidas.first(),
    val tipoContaSelecionado: TipoContaSaldo = TipoContaSaldo.CONTA,
    val saldoInicialTexto: String = "",
    val cartaoEmEdicao: Cartao? = null,
    val diaFechamentoTexto: String = "29",
    val diaVencimentoTexto: String = "5",
    val tipoSaldoSelecionado: TipoContaSaldo = TipoContaSaldo.CONTA,

    val mensagem: String? = null
)