package com.example.controlegastos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_categorias")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "nome")
    val nome: String,

    @ColumnInfo(name = "cor_hex")
    val corHex: String,

    @ColumnInfo(name = "teto_mensal")
    val tetoMensal: Long? = null,

    @ColumnInfo(name = "icone_chave")
    val iconeChave: String = "outros",

    @ColumnInfo(name = "ativa")
    val ativa: Boolean = true
)