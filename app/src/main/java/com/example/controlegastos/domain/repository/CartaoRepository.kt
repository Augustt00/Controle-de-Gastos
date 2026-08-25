package com.example.controlegastos.domain.repository

import com.example.controlegastos.domain.model.Cartao
import kotlinx.coroutines.flow.Flow

interface CartaoRepository {
    fun observarTodos(): Flow<List<Cartao>>
    suspend fun salvarOuAtualizarPorMarca(cartao: Cartao): Int
    suspend fun atualizarAtivacao(cartaoId: Int, ativo: Boolean): Boolean
}