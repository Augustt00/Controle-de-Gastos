package com.example.controlegastos.data.local.repository

import androidx.room.withTransaction
import com.example.controlegastos.data.local.ControleGastosDatabase
import com.example.controlegastos.data.local.entity.DespesaEntity
import com.example.controlegastos.data.local.entity.GrupoParcelamentoEntity
import com.example.controlegastos.data.local.projection.DespesaComCategoria
import com.example.controlegastos.data.local.projection.ProjecaoMesTuple
import com.example.controlegastos.domain.model.Despesa
import com.example.controlegastos.domain.model.DespesaDetalhada
import com.example.controlegastos.domain.model.FaturaMensal
import com.example.controlegastos.domain.model.GastoPorCategoria
import com.example.controlegastos.domain.model.GrupoParcelamento
import com.example.controlegastos.domain.model.OrigemPagamento
import com.example.controlegastos.domain.model.ProjecaoMensal
import com.example.controlegastos.domain.model.TipoLancamento
import com.example.controlegastos.domain.repository.DespesaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset
import javax.inject.Inject

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
            .getDespesasProximasAoVencimento(
                dataInicio = inicioEpoch,
                dataFim = fimEpoch
            )
            .map { despesas ->
                despesas.map { despesa ->
                    despesa.toDomain()
                }
            }
    }

    override fun observarPorCategoria(
        categoriaId: Int
    ): Flow<List<Despesa>> {
        return despesaDao
            .observarPorCategoria(categoriaId)
            .map { despesas ->
                despesas.map { despesa ->
                    despesa.toDomain()
                }
            }
    }

    /*
     * Esta função continua baseada em dataCompra.
     *
     * A tela Transações recebe as compras feitas no mês e o ViewModel
     * decide, por cartão, a qual fatura elas pertencem considerando
     * o dia de fechamento.
     */
    override fun observarDespesasDetalhadasPorMes(
        mes: Int,
        ano: Int
    ): Flow<List<DespesaDetalhada>> {
        return despesaDao
            .getDespesasPorMesAno(
                mes = mes,
                ano = ano
            )
            .map { despesas ->
                despesas.map { despesa ->
                    despesa.toDomainDetalhada()
                }
            }
    }

    override fun observarDetalhadasEntre(
        inicioEpoch: Long,
        fimEpoch: Long
    ): Flow<List<DespesaDetalhada>> {
        val inicio = Instant
            .ofEpochMilli(inicioEpoch)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        val fim = Instant
            .ofEpochMilli(fimEpoch)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        return despesaDao
            .observarDetalhadasEntre(
                inicio = inicio,
                fim = fim
            )
            .map { despesas ->
                despesas.map { despesa ->
                    despesa.toDomainDetalhada()
                }
            }
    }

    override fun observarTotalGastoPorMes(
        mes: Int,
        ano: Int
    ): Flow<Long> {
        return despesaDao.getTotalGastoPorMes(
            mes = mes,
            ano = ano
        )
    }

    override fun observarTotalPagoPorMes(
        mes: Int,
        ano: Int
    ): Flow<Long> {
        return despesaDao.getTotalPagoPorMes(
            mes = mes,
            ano = ano
        )
    }

    override fun observarTotalPendentePorMes(
        mes: Int,
        ano: Int
    ): Flow<Long> {
        return despesaDao.getTotalPendentePorMes(
            mes = mes,
            ano = ano
        )
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

    override fun observarGastosAgrupadosPorMes(): Flow<List<ProjecaoMensal>> {
        return despesaDao
            .getGastosAgrupadosPorMes()
            .map { projecoes ->
                projecoes.map { projecao ->
                    ProjecaoMensal(
                        ano = projecao.ano,
                        mes = projecao.mes,
                        totalPendente = projecao.totalCentavos
                    )
                }
            }
    }

    override fun observarGastosPorCategoriaNoMes(
        mes: Int,
        ano: Int
    ): Flow<List<GastoPorCategoria>> {
        return despesaDao
            .getSomaGastosPorCategoriaNoMes(
                mes = mes,
                ano = ano
            )
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
                        },
                        iconeChave = categoria.categoriaIconeChave // novo
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
                despesas.map { despesa ->
                    despesa.toDomainDetalhada()
                }
            }
    }

    override fun observarFaturasAbertasPorMes(): Flow<List<FaturaMensal>> {
        return despesaDao
            .observarFaturasAbertasPorMes()
            .map { faturas ->
                faturas.map { fatura ->
                    FaturaMensal(
                        mesAno = YearMonth.of(
                            fatura.ano,
                            fatura.mes
                        ),
                        totalCentavos = fatura.totalCentavos
                    )
                }
            }
    }

    override fun observarDespesasDetalhadasDaFatura(
        mes: Int,
        ano: Int
    ): Flow<List<DespesaDetalhada>> {
        return despesaDao
            .observarDespesasDetalhadasDaFatura(
                mes = mes,
                ano = ano
            )
            .map { despesas ->
                despesas.map { despesa ->
                    despesa.toDomainDetalhada()
                }
            }
    }

    override suspend fun salvar(
        despesa: Despesa
    ): Int {
        return despesaDao
            .insertDespesa(despesa.toEntity())
            .toInt()
    }

    override suspend fun atualizar(
        despesa: Despesa
    ): Boolean {
        return despesaDao
            .atualizarDespesa(despesa.toEntity()) > 0
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
                despesa.toEntity().copy(
                    grupoParcelamentoId = grupoId
                )
            }

            despesaDao.insertDespesas(parcelasComGrupo)

            grupoId
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

    /*
     * Pagamento baseado no ciclo da fatura, e não em dataVencimento.
     *
     * Exemplo:
     * - cartão fecha dia 29;
     * - fatura indicada como Agosto/2026;
     * - período de compras: 30/07/2026 até 29/08/2026;
     * - limite exclusivo: 30/08/2026.
     */
    override suspend fun pagarFatura(
        cartaoId: Int,
        mes: Int,
        ano: Int,
        contaId: Int
    ): Boolean {
        return database.withTransaction {
            val cartao = database
                .cartaoDao()
                .observarTodos()
                .first()
                .firstOrNull { it.id == cartaoId }
                ?: return@withTransaction false

            val inicio = calcularInicioCiclo(
                ano = ano,
                mes = mes,
                diaFechamento = cartao.diaFechamento
            )

            val fim = calcularFimExclusivoCiclo(
                ano = ano,
                mes = mes,
                diaFechamento = cartao.diaFechamento
            )

            val totalFatura = despesaDao.totalFaturaAberta(
                cartaoId = cartaoId,
                inicio = inicio,
                fim = fim
            )

            if (totalFatura <= 0L) {
                return@withTransaction false
            }

            val conta = database
                .contaSaldoDao()
                .observarTodas()
                .first()
                .firstOrNull { it.id == contaId }
                ?: return@withTransaction false

            require(conta.saldoCentavos >= totalFatura) {
                "Saldo insuficiente para pagar esta fatura."
            }

            val contaAtualizada = database
                .contaSaldoDao()
                .atualizarSaldo(
                    contaId = contaId,
                    novoSaldo = conta.saldoCentavos - totalFatura
                )

            if (contaAtualizada == 0) {
                return@withTransaction false
            }

            val despesasPagas = despesaDao.pagarFatura(
                cartaoId = cartaoId,
                inicio = inicio,
                fim = fim,
                dataPagamento = LocalDate.now()
            )

            despesasPagas > 0
        }
    }

    override suspend fun excluirPorId(
        despesaId: Int
    ): Boolean {
        return despesaDao.excluirPorId(despesaId) > 0
    }

    override suspend fun excluirDespesaEParcelasFuturas(
        despesaId: Int
    ): Boolean {
        val grupoId = despesaDao.buscarGrupoIdPorDespesa(despesaId)

        val dataInicial = despesaDao
            .buscarDataVencimentoPorId(despesaId)

        return if (grupoId == null || dataInicial == null) {
            despesaDao.excluirPorId(despesaId) > 0
        } else {
            despesaDao.excluirParcelasDoGrupoAPartirDe(
                grupoId = grupoId,
                dataInicial = dataInicial
            ) > 0
        }
    }

    private fun calcularInicioCiclo(
        ano: Int,
        mes: Int,
        diaFechamento: Int
    ): LocalDate {
        val mesAnterior = YearMonth
            .of(ano, mes)
            .minusMonths(1)

        val fechamentoMesAnterior = mesAnterior.atDay(
            diaFechamento.coerceAtMost(
                mesAnterior.lengthOfMonth()
            )
        )

        return fechamentoMesAnterior.plusDays(1)
    }

    private fun calcularFimExclusivoCiclo(
        ano: Int,
        mes: Int,
        diaFechamento: Int
    ): LocalDate {
        val mesDaFatura = YearMonth.of(ano, mes)

        val fechamentoMesDaFatura = mesDaFatura.atDay(
            diaFechamento.coerceAtMost(
                mesDaFatura.lengthOfMonth()
            )
        )

        return fechamentoMesDaFatura.plusDays(1)
    }

    private fun DespesaEntity.toDomain(): Despesa {
        return Despesa(
            id = id,
            valor = valor,
            descricao = descricao,
            dataCompra = dataCompra
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli(),
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
            grupoParcelamentoId = grupoParcelamentoId,
            cartaoId = cartaoId,
            contaSaldoId = contaSaldoId,
            tipoLancamento = runCatching {
                TipoLancamento.valueOf(tipoLancamento)
            }.getOrDefault(TipoLancamento.UNICA),
            origemPagamento = origemPagamento?.let {
                runCatching {
                    OrigemPagamento.valueOf(it)
                }.getOrNull()
            }
        )
    }

    private fun Despesa.toEntity(): DespesaEntity {
        return DespesaEntity(
            id = id,
            valor = valor,
            descricao = descricao,
            dataCompra = Instant
                .ofEpochMilli(dataCompra)
                .atZone(ZoneOffset.UTC)
                .toLocalDate(),
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
            grupoParcelamentoId = grupoParcelamentoId,
            cartaoId = cartaoId,
            contaSaldoId = contaSaldoId,
            tipoLancamento = tipoLancamento.name,
            origemPagamento = origemPagamento?.name
        )
    }

    private fun DespesaComCategoria.toDomainDetalhada(): DespesaDetalhada {
        return DespesaDetalhada(
            id = despesa.id,
            valor = despesa.valor,
            descricao = despesa.descricao,
            dataCompra = despesa.dataCompra
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli(),
            dataVencimento = despesa.dataVencimento
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli(),
            statusPago = despesa.statusPago,
            categoriaId = categoria.id,
            categoriaNome = categoria.nome,
            categoriaCorHex = categoria.corHex,
            categoriaIconeChave = categoria.iconeChave, // <-- NOVO: propaga o icone salvo na categoria
            cartaoId = despesa.cartaoId,
            contaSaldoId = despesa.contaSaldoId,
            tipoLancamento = runCatching {
                TipoLancamento.valueOf(despesa.tipoLancamento)
            }.getOrDefault(TipoLancamento.UNICA),
            origemPagamento = despesa.origemPagamento?.let {
                runCatching {
                    OrigemPagamento.valueOf(it)
                }.getOrNull()
            }
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