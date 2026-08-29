package com.example.controlegastos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.controlegastos.data.local.entity.DespesaEntity
import com.example.controlegastos.data.local.projection.CategoriaSomaTuple
import com.example.controlegastos.data.local.projection.DespesaComCategoria
import com.example.controlegastos.data.local.projection.FaturaMensalTuple
import com.example.controlegastos.data.local.projection.ProjecaoMesTuple
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DespesaDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDespesa(despesa: DespesaEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDespesas(despesas: List<DespesaEntity>): List<Long>

    @Update
    suspend fun atualizarDespesa(despesa: DespesaEntity): Int

    @Delete
    suspend fun excluirDespesa(despesa: DespesaEntity): Int

    @Query(
        """
        SELECT
            d.id AS despesa_id,
            d.valor AS despesa_valor,
            d.descricao AS despesa_descricao,
            d.data_compra AS despesa_data_compra,
            d.data_vencimento AS despesa_data_vencimento,
            d.data_pagamento AS despesa_data_pagamento,
            d.status_pago AS despesa_status_pago,
            d.categoria_id AS despesa_categoria_id,
            d.grupo_parcelamento_id AS despesa_grupo_parcelamento_id,
            d.cartao_id AS despesa_cartao_id,

            c.id AS categoria_id,
            c.nome AS categoria_nome,
            c.cor_hex AS categoria_cor_hex,
            c.teto_mensal AS categoria_teto_mensal,
            c.icone_chave AS categoria_icone_chave,
            c.ativa AS categoria_ativa

        FROM tb_despesas d
        INNER JOIN tb_categorias c ON c.id = d.categoria_id
        WHERE d.data_compra >= (
            CAST(
                strftime(
                    '%s',
                    printf('%04d-%02d-01', :ano, :mes)
                ) AS INTEGER
            ) * 1000
        )
        AND d.data_compra < (
            CAST(
                strftime(
                    '%s',
                    printf('%04d-%02d-01', :ano, :mes),
                    '+1 month'
                ) AS INTEGER
            ) * 1000
        )
        ORDER BY d.data_compra ASC
        """
    )
    fun getDespesasPorMesAno(
        mes: Int,
        ano: Int
    ): Flow<List<DespesaComCategoria>>

    @Query(
        """
        SELECT
    c.id AS categoria_id,
    c.nome AS categoria_nome,
    c.cor_hex AS categoria_cor_hex,
    c.teto_mensal AS teto_mensal,
    COALESCE(SUM(d.valor), 0) AS total_centavos

        FROM tb_despesas d
        INNER JOIN tb_categorias c ON c.id = d.categoria_id
        WHERE d.data_compra >= (
            CAST(
                strftime(
                    '%s',
                    printf('%04d-%02d-01', :ano, :mes)
                ) AS INTEGER
            ) * 1000
        )
        AND d.data_compra < (
            CAST(
                strftime(
                    '%s',
                    printf('%04d-%02d-01', :ano, :mes),
                    '+1 month'
                ) AS INTEGER
            ) * 1000
        )
        GROUP BY c.id, c.nome, c.cor_hex
        ORDER BY total_centavos DESC
        """
    )
    fun getSomaGastosPorCategoriaNoMes(
        mes: Int,
        ano: Int
    ): Flow<List<CategoriaSomaTuple>>

    @Query(
        """
        SELECT COALESCE(SUM(valor), 0)
        FROM tb_despesas
        WHERE data_compra >= (
            CAST(
                strftime(
                    '%s',
                    printf('%04d-%02d-01', :ano, :mes)
                ) AS INTEGER
            ) * 1000
        )
        AND data_compra < (
            CAST(
                strftime(
                    '%s',
                    printf('%04d-%02d-01', :ano, :mes),
                    '+1 month'
                ) AS INTEGER
            ) * 1000
        )
        """
    )
    fun getTotalGastoPorMes(
        mes: Int,
        ano: Int
    ): Flow<Long>

    @Query(
        """
        SELECT COALESCE(SUM(valor), 0)
        FROM tb_despesas
        WHERE status_pago = 1
        AND data_compra >= (
            CAST(
                strftime(
                    '%s',
                    printf('%04d-%02d-01', :ano, :mes)
                ) AS INTEGER
            ) * 1000
        )
        AND data_compra < (
            CAST(
                strftime(
                    '%s',
                    printf('%04d-%02d-01', :ano, :mes),
                    '+1 month'
                ) AS INTEGER
            ) * 1000
        )
        """
    )
    fun getTotalPagoPorMes(
        mes: Int,
        ano: Int
    ): Flow<Long>

    @Query(
        """
        SELECT COALESCE(SUM(valor), 0)
        FROM tb_despesas
        WHERE status_pago = 0
        AND data_compra >= (
            CAST(
                strftime(
                    '%s',
                    printf('%04d-%02d-01', :ano, :mes)
                ) AS INTEGER
            ) * 1000
        )
        AND data_compra < (
            CAST(
                strftime(
                    '%s',
                    printf('%04d-%02d-01', :ano, :mes),
                    '+1 month'
                ) AS INTEGER
            ) * 1000
        )
        """
    )
    fun getTotalPendentePorMes(
        mes: Int,
        ano: Int
    ): Flow<Long>

    @Query(
        """
        SELECT * FROM tb_despesas
        WHERE status_pago = 0
        AND data_vencimento >= :dataInicio
        AND data_vencimento <= :dataFim
        ORDER BY data_vencimento ASC
        """
    )
    fun getDespesasProximasAoVencimento(
        dataInicio: Long,
        dataFim: Long
    ): Flow<List<DespesaEntity>>

    @Query(
        """
        SELECT
            CAST(strftime('%Y', data_vencimento / 1000, 'unixepoch') AS INTEGER) AS ano,
            CAST(strftime('%m', data_vencimento / 1000, 'unixepoch') AS INTEGER) AS mes,
            COALESCE(SUM(valor), 0) AS total_centavos

        FROM tb_despesas
        WHERE status_pago = 0
        AND data_vencimento >= :mesAnoInicio
        GROUP BY ano, mes
        ORDER BY ano ASC, mes ASC
        """
    )
    fun getProjecaoFuturaAgrupadaPorMes(
        mesAnoInicio: Long
    ): Flow<List<ProjecaoMesTuple>>

    @Query(
        """
    SELECT * FROM tb_despesas
    WHERE categoria_id = :categoriaId
    ORDER BY data_vencimento ASC
    """
    )
    fun observarPorCategoria(
        categoriaId: Int
    ): Flow<List<DespesaEntity>>

    @Query(
        """
        SELECT COUNT(*) FROM tb_despesas
        WHERE grupo_parcelamento_id = :grupoParcelamentoId
        """
    )
    suspend fun contarPorGrupo(
        grupoParcelamentoId: Int
    ): Int

    @Query(
        """
    SELECT
        d.id AS despesa_id,
        d.valor AS despesa_valor,
        d.descricao AS despesa_descricao,
        d.data_compra AS despesa_data_compra,
        d.data_vencimento AS despesa_data_vencimento,
        d.data_pagamento AS despesa_data_pagamento,
        d.status_pago AS despesa_status_pago,
        d.categoria_id AS despesa_categoria_id,
        d.grupo_parcelamento_id AS despesa_grupo_parcelamento_id,
        d.cartao_id AS despesa_cartao_id,

        c.id AS categoria_id,
        c.nome AS categoria_nome,
        c.cor_hex AS categoria_cor_hex,
        c.teto_mensal AS categoria_teto_mensal,
        c.icone_chave AS categoria_icone_chave,
        c.ativa AS categoria_ativa

    FROM tb_despesas d
    INNER JOIN tb_categorias c ON c.id = d.categoria_id
    WHERE d.status_pago = 0
    AND d.data_vencimento >= :dataInicio
    AND d.data_vencimento <= :dataFim
    ORDER BY d.data_vencimento ASC
    """
    )
    fun observarPendenciasDetalhadas(
        dataInicio: Long,
        dataFim: Long
    ): Flow<List<DespesaComCategoria>>

    @Query(
        """
    SELECT
        d.id AS despesa_id,
        d.valor AS despesa_valor,
        d.descricao AS despesa_descricao,
        d.data_compra AS despesa_data_compra,
        d.data_vencimento AS despesa_data_vencimento,
        d.data_pagamento AS despesa_data_pagamento,
        d.status_pago AS despesa_status_pago,
        d.categoria_id AS despesa_categoria_id,
        d.grupo_parcelamento_id AS despesa_grupo_parcelamento_id,
        d.cartao_id AS despesa_cartao_id,

        c.id AS categoria_id,
        c.nome AS categoria_nome,
        c.cor_hex AS categoria_cor_hex,
        c.teto_mensal AS categoria_teto_mensal,
        c.icone_chave AS categoria_icone_chave,
        c.ativa AS categoria_ativa

    FROM tb_despesas d
    INNER JOIN tb_categorias c ON c.id = d.categoria_id
    WHERE d.data_compra >= :inicio
      AND d.data_compra < :fim
    ORDER BY d.data_compra ASC
    """
    )
    fun observarDetalhadasEntre(
        inicio: LocalDate,
        fim: LocalDate
    ): Flow<List<DespesaComCategoria>>

    @Query(
        """
    SELECT
        CAST(strftime('%Y', data_vencimento / 1000, 'unixepoch') AS INTEGER) AS ano,
        CAST(strftime('%m', data_vencimento / 1000, 'unixepoch') AS INTEGER) AS mes,
        COALESCE(SUM(valor), 0) AS total_centavos
    FROM tb_despesas
    WHERE status_pago = 0
    GROUP BY ano, mes
    ORDER BY ano ASC, mes ASC
    """
    )
    fun observarFaturasAbertasPorMes(): Flow<List<FaturaMensalTuple>>

    @Query(
        """
    SELECT
        d.id AS despesa_id,
        d.valor AS despesa_valor,
        d.descricao AS despesa_descricao,
        d.data_compra AS despesa_data_compra,
        d.data_vencimento AS despesa_data_vencimento,
        d.data_pagamento AS despesa_data_pagamento,
        d.status_pago AS despesa_status_pago,
        d.categoria_id AS despesa_categoria_id,
        d.grupo_parcelamento_id AS despesa_grupo_parcelamento_id,
        d.cartao_id AS despesa_cartao_id,
        c.id AS categoria_id,
        c.nome AS categoria_nome,
        c.cor_hex AS categoria_cor_hex,
        c.teto_mensal AS categoria_teto_mensal,
        c.icone_chave AS categoria_icone_chave,
        c.ativa AS categoria_ativa
    FROM tb_despesas d
    INNER JOIN tb_categorias c ON c.id = d.categoria_id
    WHERE d.status_pago = 0
      AND d.data_vencimento >= (
          CAST(strftime('%s', printf('%04d-%02d-01', :ano, :mes)) AS INTEGER) * 1000
      )
      AND d.data_vencimento < (
          CAST(strftime('%s', printf('%04d-%02d-01', :ano, :mes), '+1 month') AS INTEGER) * 1000
      )
    ORDER BY d.data_compra ASC
    """
    )
    fun observarDespesasDetalhadasDaFatura(
        mes: Int,
        ano: Int
    ): Flow<List<DespesaComCategoria>>

    @Query(
        """
    UPDATE tb_despesas
    SET
        status_pago = 1,
        data_pagamento = :dataPagamento
    WHERE id = :despesaId
    """
    )
    suspend fun marcarComoPaga(
        despesaId: Int,
        dataPagamento: LocalDate
    ): Int

    @Query("SELECT * FROM tb_despesas ORDER BY id ASC")
    suspend fun buscarTodasParaBackup(): List<DespesaEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserirTodasParaBackup(
        despesas: List<DespesaEntity>
    )

    @Query("DELETE FROM tb_despesas")
    suspend fun limparTodas()

    @Query("DELETE FROM tb_despesas WHERE id = :despesaId")
    suspend fun excluirPorId(despesaId: Int): Int

    @Query(
        """
        DELETE FROM tb_despesas
        WHERE grupo_parcelamento_id = :grupoId
          AND data_vencimento >= :dataInicial
        """
    )
    suspend fun excluirParcelasDoGrupoAPartirDe(
        grupoId: Int,
        dataInicial: LocalDate
    ): Int

    @Query("SELECT grupo_parcelamento_id FROM tb_despesas WHERE id = :despesaId LIMIT 1")
    suspend fun buscarGrupoIdPorDespesa(despesaId: Int): Int?

    @Query("SELECT data_vencimento FROM tb_despesas WHERE id = :despesaId LIMIT 1")
    suspend fun buscarDataVencimentoPorId(despesaId: Int): LocalDate?
}