package com.example.controlegastos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_contas_saldo")
data class ContaSaldoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "nome")
    val nome: String,

    @ColumnInfo(name = "instituicao_chave")
    val instituicaoChave: String,

    @ColumnInfo(name = "tipo")
    val tipo: String,

    @ColumnInfo(name = "saldo_centavos")
    val saldoCentavos: Long,

    @ColumnInfo(name = "cor_hex")
    val corHex: String,

    @ColumnInfo(name = "ativo")
    val ativo: Boolean = true
)