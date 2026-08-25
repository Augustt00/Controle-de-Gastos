package com.example.controlegastos.domain.usecase

import com.example.controlegastos.domain.model.Categoria
import com.example.controlegastos.domain.repository.CategoriaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BuscarCategoriasUseCase @Inject constructor(
    private val categoriaRepository: CategoriaRepository
) {
    operator fun invoke(somenteAtivas: Boolean = false): Flow<List<Categoria>> {
        return if (somenteAtivas) {
            categoriaRepository.observarAtivas()
        } else {
            categoriaRepository.observarTodas()
        }
    }
}