package com.example.controlegastos.ui.despesa

import com.example.controlegastos.domain.model.Categoria
import java.time.LocalDate

data class InserirDespesaUiState(
    val valorTexto: String = "",
    val descricao: String = "",
    val categorias: List<Categoria> = emptyList(),
    val categoriaSelecionada: Categoria? = null,
    val dataVencimento: LocalDate = LocalDate.now(),
    val parcelado: Boolean = false,
    val quantidadeParcelas: Int = 2,
    val carregandoCategorias: Boolean = true,
    val salvando: Boolean = false,
    val mensagemErro: String? = null,
    val despesaSalvaComSucesso: Boolean = false
)