package com.example.controlegastos.domain.repository

import com.example.controlegastos.domain.model.Despesa
import com.example.controlegastos.domain.model.GrupoParcelamento
import kotlinx.coroutines.flow.Flow
import com.example.controlegastos.domain.model.GastoPorCategoria
import com.example.controlegastos.domain.model.DespesaDetalhada
import com.example.controlegastos.domain.model.ProjecaoMensal


interface DespesaRepository {

    fun observarPorPeriodo(
        inicioEpoch: Long,
        fimEpoch: Long
    ): Flow<List<Despesa>>

    fun observarPorCategoria(
        categoriaId: Int
    ): Flow<List<Despesa>>

    fun observarDespesasDetalhadasPorMes(
        mes: Int,
        ano: Int
    ): Flow<List<DespesaDetalhada>>

    fun observarTotalGastoPorMes(
        mes: Int,
        ano: Int
    ): Flow<Long>

    fun observarGastosPorCategoriaNoMes(
        mes: Int,
        ano: Int
    ): Flow<List<GastoPorCategoria>>

    fun observarTotalPagoPorMes(
        mes: Int,
        ano: Int
    ): Flow<Long>


    fun observarTotalPendentePorMes(
        mes: Int,
        ano: Int
    ): Flow<Long>

    fun observarProjecaoFutura(
        inicioEpoch: Long
    ): Flow<List<ProjecaoMensal>>

    suspend fun salvar(despesa: Despesa): Int

    suspend fun atualizar(despesa: Despesa): Boolean

    suspend fun criarDespesaParcelada(
        grupo: GrupoParcelamento,
        despesas: List<Despesa>
    ): Int

    fun observarPendenciasDetalhadas(
        dataInicioEpoch: Long,
        dataFimEpoch: Long
    ): Flow<List<DespesaDetalhada>>

    suspend fun marcarComoPaga(
        despesaId: Int,
        dataPagamentoEpoch: Long
    ): Boolean
}