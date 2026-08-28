package com.example.controlegastos.domain.repository

import com.example.controlegastos.domain.model.Cartao
import kotlinx.coroutines.flow.Flow

interface CartaoRepository {
    fun observarTodos(): Flow<List<Cartao>>
    fun observarAtivos(): Flow<List<Cartao>>
    suspend fun salvarOuAtualizarPorMarca(cartao: Cartao): Int
    suspend fun atualizarAtivacao(cartaoId: Int, ativo: Boolean): Boolean
    suspend fun atualizarConfiguracao(
        cartaoId: Int,
        ativo: Boolean,
        diaFechamento: Int,
        diaVencimento: Int
    ): Boolean
}