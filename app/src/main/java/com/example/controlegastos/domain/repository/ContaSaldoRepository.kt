package com.example.controlegastos.domain.repository

import com.example.controlegastos.domain.model.ContaSaldo
import kotlinx.coroutines.flow.Flow

interface ContaSaldoRepository {
    fun observarTodas(): Flow<List<ContaSaldo>>
    suspend fun salvar(conta: ContaSaldo): Int
    suspend fun atualizarAtivacao(contaId: Int, ativo: Boolean): Boolean
}