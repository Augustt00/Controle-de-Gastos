package com.example.controlegastos.domain.usecase

import com.example.controlegastos.domain.model.GastoPorCategoria
import com.example.controlegastos.domain.repository.DespesaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGastosPorCategoriaUseCase @Inject constructor(
    private val despesaRepository: DespesaRepository
) {

    operator fun invoke(
        mes: Int,
        ano: Int
    ): Flow<List<GastoPorCategoria>> {
        return despesaRepository.observarGastosPorCategoriaNoMes(
            mes = mes,
            ano = ano
        )
    }
}