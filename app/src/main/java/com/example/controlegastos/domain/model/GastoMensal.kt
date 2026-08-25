package com.example.controlegastos.domain.model

import java.time.YearMonth

data class GastoMensal(
    val mesAno: YearMonth,
    val totalCentavos: Long
)