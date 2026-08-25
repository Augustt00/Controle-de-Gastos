// domain/repository/GrupoParcelamentoRepository.kt
package com.example.controlegastos.domain.repository

import com.example.controlegastos.domain.model.GrupoParcelamento

interface GrupoParcelamentoRepository {
    suspend fun salvar(grupo: GrupoParcelamento): Int
    suspend fun excluir(grupoId: Int): Boolean
}