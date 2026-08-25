package com.example.controlegastos.domain.usecase

import com.example.controlegastos.domain.model.ResumoMensal
import com.example.controlegastos.domain.repository.DespesaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetResumoMensalUseCase @Inject constructor(
    private val despesaRepository: DespesaRepository
) {

    operator fun invoke(
        mes: Int,
        ano: Int
    ): Flow<ResumoMensal> {
        return combine(
            despesaRepository.observarTotalGastoPorMes(mes, ano),
            despesaRepository.observarTotalPagoPorMes(mes, ano),
            despesaRepository.observarTotalPendentePorMes(mes, ano)
        ) { totalGasto, totalPago, totalPendente ->
            ResumoMensal(
                totalGasto = totalGasto,
                totalPago = totalPago,
                totalPendente = totalPendente
            )
        }
    }
}