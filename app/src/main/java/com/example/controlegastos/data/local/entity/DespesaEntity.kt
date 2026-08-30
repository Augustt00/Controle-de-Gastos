package com.example.controlegastos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "tb_despesas",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoria_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = GrupoParcelamentoEntity::class,
            parentColumns = ["id"],
            childColumns = ["grupo_parcelamento_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CartaoEntity::class,
            parentColumns = ["id"],
            childColumns = ["cartao_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ContaSaldoEntity::class,
            parentColumns = ["id"],
            childColumns = ["conta_saldo_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["categoria_id"]),
        Index(value = ["grupo_parcelamento_id"]),
        Index(value = ["data_vencimento"]),
        Index(value = ["data_compra"]),
        Index(value = ["categoria_id", "data_compra"]),
        Index(value = ["cartao_id"]),
        Index(value = ["conta_saldo_id"])
    ]
)
data class DespesaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "valor")
    val valor: Long,

    @ColumnInfo(name = "descricao")
    val descricao: String,

    @ColumnInfo(name = "data_compra")
    val dataCompra: LocalDate,

    @ColumnInfo(name = "data_vencimento")
    val dataVencimento: LocalDate,

    @ColumnInfo(name = "data_pagamento")
    val dataPagamento: LocalDate? = null,

    @ColumnInfo(name = "status_pago")
    val statusPago: Boolean = false,

    @ColumnInfo(name = "categoria_id")
    val categoriaId: Int,

    @ColumnInfo(name = "grupo_parcelamento_id")
    val grupoParcelamentoId: Int? = null,

    @ColumnInfo(name = "cartao_id")
    val cartaoId: Int? = null,

    @ColumnInfo(name = "conta_saldo_id")
    val contaSaldoId: Int? = null,

    @ColumnInfo(name = "tipo_lancamento")
    val tipoLancamento: String = "UNICA",

    @ColumnInfo(name = "origem_pagamento")
    val origemPagamento: String? = null
)