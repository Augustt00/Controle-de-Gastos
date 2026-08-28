package com.example.controlegastos.ui.despesa

import com.example.controlegastos.domain.model.Cartao
import com.example.controlegastos.domain.model.Categoria
import com.example.controlegastos.domain.model.TipoLancamento
import java.time.LocalDate

data class InserirDespesaUiState(
    val valorTexto: String = "",
    val descricao: String = "",
    val categorias: List<Categoria> = emptyList(),
    val categoriaSelecionada: Categoria? = null,
    val cartoes: List<Cartao> = emptyList(),
    val cartaoSelecionado: Cartao? = null,
    val dataCompra: LocalDate = LocalDate.now(),
    val tipoLancamento: TipoLancamento = TipoLancamento.UNICA,
    val quantidadeParcelas: Int = 2,
    val carregandoCategorias: Boolean = true,
    val carregandoCartoes: Boolean = true,
    val salvando: Boolean = false,
    val mensagemErro: String? = null,
    val despesaSalvaComSucesso: Boolean = false
)