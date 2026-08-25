package com.example.controlegastos.domain.usecase

import com.example.controlegastos.domain.repository.DespesaRepository
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

class MarcarDespesaComoPagaUseCase @Inject constructor(
    private val repository: DespesaRepository
) {

    suspend operator fun invoke(despesaId: Int): Boolean {
        val hojeEpoch = LocalDate
            .now(ZoneOffset.UTC)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()

        return repository.marcarComoPaga(
            despesaId = despesaId,
            dataPagamentoEpoch = hojeEpoch
        )
    }
}