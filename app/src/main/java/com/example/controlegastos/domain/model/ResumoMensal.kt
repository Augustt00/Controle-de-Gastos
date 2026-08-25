package com.example.controlegastos.domain.model

data class ResumoMensal(
    val totalGasto: Long,
    val totalPago: Long,
    val totalPendente: Long
)