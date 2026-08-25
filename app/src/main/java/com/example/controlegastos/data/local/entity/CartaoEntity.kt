package com.example.controlegastos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tb_cartoes",
    indices = [Index(value = ["marca_chave"], unique = true)]
)
data class CartaoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "nome")
    val nome: String,

    @ColumnInfo(name = "marca_chave")
    val marcaChave: String,

    @ColumnInfo(name = "cor_hex")
    val corHex: String,

    @ColumnInfo(name = "ativo")
    val ativo: Boolean = true
)