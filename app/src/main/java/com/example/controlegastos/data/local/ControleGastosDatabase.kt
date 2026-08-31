package com.example.controlegastos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.controlegastos.data.local.converter.DatabaseConverters
import com.example.controlegastos.data.local.dao.CartaoDao
import com.example.controlegastos.data.local.dao.CategoriaDao
import com.example.controlegastos.data.local.dao.ContaSaldoDao
import com.example.controlegastos.data.local.dao.DespesaDao
import com.example.controlegastos.data.local.dao.GrupoParcelamentoDao
import com.example.controlegastos.data.local.entity.CartaoEntity
import com.example.controlegastos.data.local.entity.CategoriaEntity
import com.example.controlegastos.data.local.entity.ContaSaldoEntity
import com.example.controlegastos.data.local.entity.DespesaEntity
import com.example.controlegastos.data.local.entity.GrupoParcelamentoEntity

@Database(
    entities = [
        CategoriaEntity::class,
        GrupoParcelamentoEntity::class,
        DespesaEntity::class,
        CartaoEntity::class,
        ContaSaldoEntity::class
    ],
    version = 6, // 1. ALTERADO DE 5 PARA 6
    exportSchema = true
)
@TypeConverters(DatabaseConverters::class)
abstract class ControleGastosDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao
    abstract fun grupoParcelamentoDao(): GrupoParcelamentoDao
    abstract fun despesaDao(): DespesaDao
    abstract fun cartaoDao(): CartaoDao
    abstract fun contaSaldoDao(): ContaSaldoDao

    companion object {
        const val DATABASE_NAME = "controle_gastos.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            // ... seu código original da migration 1_2 (mantido igualzinho)
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tb_categorias ADD COLUMN icone_chave TEXT NOT NULL DEFAULT 'outros'")
                database.execSQL("ALTER TABLE tb_categorias ADD COLUMN ativa INTEGER NOT NULL DEFAULT 1")
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS tb_cartoes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nome TEXT NOT NULL,
                        marca_chave TEXT NOT NULL,
                        cor_hex TEXT NOT NULL,
                        ativo INTEGER NOT NULL
                    )
                """.trimIndent())
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_tb_cartoes_marca_chave ON tb_cartoes (marca_chave)")
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS tb_contas_saldo (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nome TEXT NOT NULL,
                        instituicao_chave TEXT NOT NULL,
                        tipo TEXT NOT NULL,
                        saldo_centavos INTEGER NOT NULL,
                        cor_hex TEXT NOT NULL,
                        ativo INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            // ... seu código original da migration 2_3
            override fun migrate(database: SupportSQLiteDatabase) {
                // ... mantido igualzinho
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            // ... seu código original da migration 3_4
            override fun migrate(database: SupportSQLiteDatabase) {
                // ... mantido igualzinho
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tb_despesas ADD COLUMN conta_saldo_id INTEGER DEFAULT NULL")
                database.execSQL("ALTER TABLE tb_despesas ADD COLUMN tipo_lancamento TEXT NOT NULL DEFAULT 'UNICA'")
                database.execSQL("ALTER TABLE tb_despesas ADD COLUMN origem_pagamento TEXT DEFAULT NULL")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_tb_despesas_conta_saldo_id ON tb_despesas(conta_saldo_id)")
            }
        }

        // 2. NOVA MIGRATION ADICIONADA AQUI (5 PARA 6)
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE tb_cartoes ADD COLUMN limite_centavos INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }
}