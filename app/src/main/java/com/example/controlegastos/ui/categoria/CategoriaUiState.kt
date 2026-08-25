package com.example.controlegastos.ui.categoria

import com.example.controlegastos.domain.model.Categoria

data class CategoriaUiState(
    val nome: String = "",
    val tetoMensalTexto: String = "",
    val corHexSelecionada: String = CORES_PADRAO.first(),
    val categorias: List<Categoria> = emptyList(),
    val carregando: Boolean = false,
    val salvando: Boolean = false,
    val mensagemErro: String? = null,
    val categoriaSalvaComSucesso: Boolean = false
) {
    companion object {
        val CORES_PADRAO = listOf(
            "#E53935",
            "#D81B60",
            "#8E24AA",
            "#5E35B1",
            "#3949AB",
            "#1E88E5",
            "#039BE5",
            "#00ACC1",
            "#00897B",
            "#43A047",
            "#7CB342",
            "#FDD835",
            "#FFB300",
            "#FB8C00",
            "#F4511E",
            "#6D4C41"
        )
    }
}