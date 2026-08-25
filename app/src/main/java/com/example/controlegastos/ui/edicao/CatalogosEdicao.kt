package com.example.controlegastos.ui.edicao

import androidx.compose.ui.graphics.Color

data class CategoriaSugerida(
    val nome: String,
    val iconeChave: String,
    val corHex: String
)

data class InstituicaoPredefinida(
    val nome: String,
    val chave: String,
    val sigla: String,
    val cor: Color
)

val categoriasSugeridas = listOf(
    CategoriaSugerida("Alimentação", "alimentacao", "#43A047"),
    CategoriaSugerida("Fast food", "fastfood", "#F4511E"),
    CategoriaSugerida("Loja online", "loja_online", "#1E88E5"),
    CategoriaSugerida("Streaming", "streaming", "#8E24AA"),
    CategoriaSugerida("Academia", "academia", "#00897B"),
    CategoriaSugerida("Transporte", "transporte", "#3949AB"),
    CategoriaSugerida("Moradia", "moradia", "#6D4C41"),
    CategoriaSugerida("Saúde", "saude", "#E53935"),
    CategoriaSugerida("Educação", "educacao", "#039BE5"),
    CategoriaSugerida("Lazer", "lazer", "#FDD835"),
    CategoriaSugerida("Assinaturas", "assinaturas", "#5E35B1"),
    CategoriaSugerida("Pets", "pets", "#FB8C00"),
    CategoriaSugerida("Presentes", "presentes", "#D81B60"),
    CategoriaSugerida("Viagem", "viagem", "#00ACC1"),
    CategoriaSugerida("Contas da casa", "contas", "#7CB342"),
    CategoriaSugerida("Outros", "outros", "#5F8D84")
)

val instituicoesPredefinidas = listOf(
    InstituicaoPredefinida("Nubank", "nubank", "Nu", Color(0xFF820AD1)),
    InstituicaoPredefinida("C6 Bank", "c6", "C6", Color(0xFF1E1E1E)),
    InstituicaoPredefinida("Itaú", "itau", "IT", Color(0xFFEC7000)),
    InstituicaoPredefinida("Bradesco", "bradesco", "B", Color(0xFFCC092F)),
    InstituicaoPredefinida("PicPay", "picpay", "P", Color(0xFF21C25E)),
    InstituicaoPredefinida("Mercado Pago", "mercado_pago", "MP", Color(0xFF009EE3)),
    InstituicaoPredefinida("Caixa Tem", "caixa_tem", "CX", Color(0xFF005CA9)),
    InstituicaoPredefinida("Santander", "santander", "S", Color(0xFFEC0000))
)