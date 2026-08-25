package com.example.controlegastos.domain.usecase

import com.example.controlegastos.domain.model.ProjecaoMensal
import com.example.controlegastos.domain.repository.DespesaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProjecaoFuturaUseCase @Inject constructor(
    private val despesaRepository: DespesaRepository
) {

    operator fun invoke(
        inicioEpoch: Long
    ): Flow<List<ProjecaoMensal>> {
        return despesaRepository.observarProjecaoFutura(
            inicioEpoch = inicioEpoch
        )
    }
}