package com.example.controlegastos.domain.usecase

import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class CalcularVencimentoCartaoUseCase @Inject constructor() {

    operator fun invoke(
        dataCompra: LocalDate,
        diaFechamento: Int,
        diaVencimento: Int
    ): LocalDate {
        require(diaFechamento in 1..31) {
            "O dia de fechamento deve estar entre 1 e 31."
        }

        require(diaVencimento in 1..31) {
            "O dia de vencimento deve estar entre 1 e 31."
        }

        val mesDaFatura = if (dataCompra.dayOfMonth <= diaFechamento) {
            YearMonth.from(dataCompra).plusMonths(1)
        } else {
            YearMonth.from(dataCompra).plusMonths(2)
        }

        val diaValido = diaVencimento.coerceAtMost(
            mesDaFatura.lengthOfMonth()
        )

        return mesDaFatura.atDay(diaValido)
    }
}