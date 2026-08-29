package com.example.controlegastos.domain.model

import java.time.YearMonth

data class FaturaMensal(
    val mesAno: YearMonth,
    val totalCentavos: Long
)