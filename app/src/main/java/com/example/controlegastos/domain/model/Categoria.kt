package com.example.controlegastos.domain.model

data class Categoria(
    val id: Int,
    val nome: String,
    val corHex: String,
    val tetoMensal: Long?,
    val iconeChave: String = "outros",
    val ativa: Boolean = true
)