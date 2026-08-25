package com.example.controlegastos.domain.usecase

import com.example.controlegastos.domain.model.DespesaDetalhada
import com.example.controlegastos.domain.repository.DespesaRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

class ObservarPendenciasUseCase @Inject constructor(
    private val repository: DespesaRepository
) {

    operator fun invoke(): Flow<List<DespesaDetalhada>> {
        val hoje = LocalDate.now(ZoneOffset.UTC)

        val dataInicio = hoje
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()

        val dataFim = hoje
            .plusDays(7)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()

        return repository.observarPendenciasDetalhadas(
            dataInicioEpoch = dataInicio,
            dataFimEpoch = dataFim
        )
    }
}