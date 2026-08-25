package com.example.controlegastos.domain.usecase

import com.example.controlegastos.domain.model.Categoria
import com.example.controlegastos.domain.repository.CategoriaRepository
import javax.inject.Inject

class SalvarCategoriaUseCase @Inject constructor(
    private val categoriaRepository: CategoriaRepository
) {

    suspend operator fun invoke(categoria: Categoria): Int {
        val nomeNormalizado = categoria.nome.trim()

        require(nomeNormalizado.isNotBlank()) {
            "O nome da categoria é obrigatório."
        }

        require(categoria.tetoMensal == null || categoria.tetoMensal >= 0L) {
            "O teto mensal não pode ser negativo."
        }

        return categoriaRepository.salvar(
            categoria.copy(nome = nomeNormalizado)
        )
    }
}