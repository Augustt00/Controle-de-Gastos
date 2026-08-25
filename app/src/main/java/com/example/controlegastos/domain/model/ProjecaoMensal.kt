package com.example.controlegastos.domain.model

data class ProjecaoMensal(
    val ano: Int,
    val mes: Int,
    val totalPendente: Long
)