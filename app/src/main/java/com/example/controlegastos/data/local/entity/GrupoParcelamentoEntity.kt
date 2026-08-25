package com.example.controlegastos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_grupo_parcelamento")
data class GrupoParcelamentoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "qtd_parcelas")
    val qtdParcelas: Int,

    @ColumnInfo(name = "valor_total")
    val valorTotal: Long,

    @ColumnInfo(name = "descricao_base")
    val descricaoBase: String
)