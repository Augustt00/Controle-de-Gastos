package com.example.controlegastos.domain.repository

import com.example.controlegastos.domain.model.Categoria
import kotlinx.coroutines.flow.Flow

interface CategoriaRepository {
    fun observarTodas(): Flow<List<Categoria>>
    fun observarAtivas(): Flow<List<Categoria>>
    suspend fun salvar(categoria: Categoria): Int
    suspend fun atualizarAtivacao(categoriaId: Int, ativa: Boolean): Boolean
    suspend fun excluir(categoriaId: Int): Boolean
}