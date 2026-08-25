package com.example.controlegastos.data.local.repository

import androidx.room.withTransaction
import com.example.controlegastos.data.local.ControleGastosDatabase
import com.example.controlegastos.data.local.entity.DespesaEntity
import com.example.controlegastos.data.local.entity.GrupoParcelamentoEntity
import com.example.controlegastos.domain.model.Despesa
import com.example.controlegastos.domain.model.GrupoParcelamento
import com.example.controlegastos.domain.repository.DespesaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject
import com.example.controlegastos.domain.model.GastoPorCategoria
import com.example.controlegastos.domain.model.DespesaDetalhada
import com.example.controlegastos.data.local.projection.DespesaComCategoria
import com.example.controlegastos.domain.model.ProjecaoMensal
import com.example.controlegastos.data.local.projection.ProjecaoMesTuple

class DespesaRepositoryImpl @Inject constructor(
    private val database: ControleGastosDatabase
) : DespesaRepository {

    private val despesaDao = database.despesaDao()
    private val grupoParcelamentoDao = database.grupoParcelamentoDao()

    override fun observarPorPeriodo(
        inicioEpoch: Long,
        fimEpoch: Long
    ): Flow<List<Despesa>> {
        return despesaDao
            .getDespesasProximasAoVencimento(inicioEpoch, fimEpoch)
            .map { despesas ->
                despesas.map { it.toDomain() }
            }
    }

    override fun observarPorCategoria(
        categoriaId: Int
    ): Flow<List<Despesa>> {
        return despesaDao
            .observarPorCategoria(categoriaId)
            .map { despesas ->
                despesas.map { it.toDomain() }
            }
    }

    override fun observarDespesasDetalhadasPorMes(
        mes: Int,
        ano: Int
    ): Flow<List<DespesaDetalhada>> {
        return despesaDao
            .getDespesasPorMesAno(mes, ano)
            .map { despesas ->
                despesas.map { despesaComCategoria ->
                    despesaComCategoria.toDomainDetalhada()
                }
            }
    }



    override fun observarTotalGastoPorMes(
        mes: Int,
        ano: Int
    ): Flow<Long> {
        return despesaDao.getTotalGastoPorMes(mes, ano)
    }

    override fun observarTotalPagoPorMes(
        mes: Int,
        ano: Int
    ): Flow<Long> {
        return despesaDao.getTotalPagoPorMes(mes, ano)
    }

    override fun observarTotalPendentePorMes(
        mes: Int,
        ano: Int
    ): Flow<Long> {
        return despesaDao.getTotalPendentePorMes(mes, ano)
    }

    override fun observarProjecaoFutura(
        inicioEpoch: Long
    ): Flow<List<ProjecaoMensal>> {
        return despesaDao
            .getProjecaoFuturaAgrupadaPorMes(inicioEpoch)
            .map { projecoes ->
                projecoes.map { projecao ->
                    projecao.toDomain()
                }
            }
    }

    override fun observarGastosPorCategoriaNoMes(
        mes: Int,
        ano: Int
    ): Flow<List<GastoPorCategoria>> {
        return despesaDao
            .getSomaGastosPorCategoriaNoMes(mes, ano)
            .map { categorias ->
                val totalGeral = categorias.sumOf { it.totalCentavos }

                categorias.map { categoria ->
                    GastoPorCategoria(
                        categoriaId = categoria.categoriaId,
                        nomeCategoria = categoria.categoriaNome,
                        corHex = categoria.categoriaCorHex,
                        tetoMensal = categoria.tetoMensal,
                        totalGasto = categoria.totalCentavos,
                        percentualDoTotal = if (totalGeral > 0L) {
                            categoria.totalCentavos
                                .toFloat()
                                .div(totalGeral.toFloat())
                                .times(100f)
                        } else {
                            0f
                        }
                    )
                }
            }
    }

    override fun observarPendenciasDetalhadas(
        dataInicioEpoch: Long,
        dataFimEpoch: Long
    ): Flow<List<DespesaDetalhada>> {
        return despesaDao
            .observarPendenciasDetalhadas(
                dataInicio = dataInicioEpoch,
                dataFim = dataFimEpoch
            )
            .map { despesas ->
                despesas.map { despesaComCategoria ->
                    despesaComCategoria.toDomainDetalhada()
                }
            }
    }

    override suspend fun marcarComoPaga(
        despesaId: Int,
        dataPagamentoEpoch: Long
    ): Boolean {
        val dataPagamento = Instant
            .ofEpochMilli(dataPagamentoEpoch)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        return despesaDao.marcarComoPaga(
            despesaId = despesaId,
            dataPagamento = dataPagamento
        ) > 0
    }

    override suspend fun salvar(despesa: Despesa): Int {
        return despesaDao.insertDespesa(
            despesa.toEntity()
        ).toInt()
    }

    override suspend fun atualizar(despesa: Despesa): Boolean {
        return despesaDao.atualizarDespesa(
            despesa.toEntity()
        ) > 0
    }

    override suspend fun criarDespesaParcelada(
        grupo: GrupoParcelamento,
        despesas: List<Despesa>
    ): Int {
        return database.withTransaction {
            val grupoId = grupoParcelamentoDao
                .inserir(grupo.toEntity())
                .toInt()

            val parcelasComGrupo = despesas.map { despesa ->
                despesa
                    .toEntity()
                    .copy(grupoParcelamentoId = grupoId)
            }

            despesaDao.insertDespesas(parcelasComGrupo)

            grupoId
        }
    }

    private fun DespesaEntity.toDomain(): Despesa {
        return Despesa(
            id = id,
            valor = valor,
            descricao = descricao,
            dataVencimento = dataVencimento
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli(),
            dataPagamento = dataPagamento
                ?.atStartOfDay(ZoneOffset.UTC)
                ?.toInstant()
                ?.toEpochMilli(),
            statusPago = statusPago,
            categoriaId = categoriaId,
            grupoParcelamentoId = grupoParcelamentoId
        )
    }

    private fun Despesa.toEntity(): DespesaEntity {
        return DespesaEntity(
            id = id,
            valor = valor,
            descricao = descricao,
            dataVencimento = Instant
                .ofEpochMilli(dataVencimento)
                .atZone(ZoneOffset.UTC)
                .toLocalDate(),
            dataPagamento = dataPagamento
                ?.let(Instant::ofEpochMilli)
                ?.atZone(ZoneOffset.UTC)
                ?.toLocalDate(),
            statusPago = statusPago,
            categoriaId = categoriaId,
            grupoParcelamentoId = grupoParcelamentoId
        )
    }

    private fun DespesaComCategoria.toDomainDetalhada(): DespesaDetalhada {
        return DespesaDetalhada(
            id = despesa.id,
            valor = despesa.valor,
            descricao = despesa.descricao,
            dataVencimento = despesa.dataVencimento
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli(),
            statusPago = despesa.statusPago,
            categoriaId = categoria.id,
            categoriaNome = categoria.nome,
            categoriaCorHex = categoria.corHex
        )
    }

    private fun GrupoParcelamento.toEntity(): GrupoParcelamentoEntity {
        return GrupoParcelamentoEntity(
            id = id,
            qtdParcelas = qtdParcelas,
            valorTotal = valorTotal,
            descricaoBase = descricaoBase
        )
    }

    private fun ProjecaoMesTuple.toDomain(): ProjecaoMensal {
        return ProjecaoMensal(
            ano = ano,
            mes = mes,
            totalPendente = totalCentavos
        )
    }
}