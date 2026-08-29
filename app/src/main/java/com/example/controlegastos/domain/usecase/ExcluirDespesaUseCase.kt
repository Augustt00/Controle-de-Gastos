package com.example.controlegastos.domain.usecase

import com.example.controlegastos.domain.repository.DespesaRepository
import javax.inject.Inject

class ExcluirDespesaUseCase @Inject constructor(
    private val despesaRepository: DespesaRepository
) {
    suspend operator fun invoke(despesaId: Int): Boolean {
        require(despesaId > 0) { "Despesa inválida." }
        return despesaRepository.excluirDespesaEParcelasFuturas(despesaId)
    }
}