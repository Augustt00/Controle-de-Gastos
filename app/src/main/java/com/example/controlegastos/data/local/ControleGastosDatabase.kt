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
    version = 5,
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
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE tb_categorias " +
                            "ADD COLUMN icone_chave TEXT NOT NULL DEFAULT 'outros'"
                )
                database.execSQL(
                    "ALTER TABLE tb_categorias " +
                            "ADD COLUMN ativa INTEGER NOT NULL DEFAULT 1"
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS tb_cartoes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nome TEXT NOT NULL,
                        marca_chave TEXT NOT NULL,
                        cor_hex TEXT NOT NULL,
                        ativo INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_tb_cartoes_marca_chave " +
                            "ON tb_cartoes (marca_chave)"
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS tb_contas_saldo (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nome TEXT NOT NULL,
                        instituicao_chave TEXT NOT NULL,
                        tipo TEXT NOT NULL,
                        saldo_centavos INTEGER NOT NULL,
                        cor_hex TEXT NOT NULL,
                        ativo INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE tb_despesas_nova (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        valor INTEGER NOT NULL,
                        descricao TEXT NOT NULL,
                        data_vencimento INTEGER NOT NULL,
                        data_pagamento INTEGER,
                        status_pago INTEGER NOT NULL,
                        categoria_id INTEGER NOT NULL,
                        grupo_parcelamento_id INTEGER,
                        cartao_id INTEGER,
                        FOREIGN KEY(categoria_id)
                            REFERENCES tb_categorias(id)
                            ON UPDATE NO ACTION
                            ON DELETE RESTRICT,
                        FOREIGN KEY(grupo_parcelamento_id)
                            REFERENCES tb_grupo_parcelamento(id)
                            ON UPDATE NO ACTION
                            ON DELETE CASCADE,
                        FOREIGN KEY(cartao_id)
                            REFERENCES tb_cartoes(id)
                            ON UPDATE NO ACTION
                            ON DELETE SET NULL
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    INSERT INTO tb_despesas_nova (
                        id,
                        valor,
                        descricao,
                        data_vencimento,
                        data_pagamento,
                        status_pago,
                        categoria_id,
                        grupo_parcelamento_id,
                        cartao_id
                    )
                    SELECT
                        id,
                        valor,
                        descricao,
                        data_vencimento,
                        data_pagamento,
                        status_pago,
                        categoria_id,
                        grupo_parcelamento_id,
                        NULL
                    FROM tb_despesas
                    """.trimIndent()
                )

                database.execSQL("DROP TABLE tb_despesas")
                database.execSQL("ALTER TABLE tb_despesas_nova RENAME TO tb_despesas")

                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_tb_despesas_categoria_id " +
                            "ON tb_despesas (categoria_id)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_tb_despesas_grupo_parcelamento_id " +
                            "ON tb_despesas (grupo_parcelamento_id)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_tb_despesas_data_vencimento " +
                            "ON tb_despesas (data_vencimento)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS " +
                            "index_tb_despesas_categoria_id_data_vencimento " +
                            "ON tb_despesas (categoria_id, data_vencimento)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_tb_despesas_cartao_id " +
                            "ON tb_despesas (cartao_id)"
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS tb_despesas_nova (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        valor INTEGER NOT NULL,
                        descricao TEXT NOT NULL,
                        data_compra INTEGER NOT NULL,
                        data_vencimento INTEGER NOT NULL,
                        data_pagamento INTEGER,
                        status_pago INTEGER NOT NULL,
                        categoria_id INTEGER NOT NULL,
                        grupo_parcelamento_id INTEGER,
                        cartao_id INTEGER,
                        FOREIGN KEY(categoria_id) REFERENCES tb_categorias(id)
                            ON UPDATE NO ACTION ON DELETE RESTRICT,
                        FOREIGN KEY(grupo_parcelamento_id) REFERENCES tb_grupo_parcelamento(id)
                            ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(cartao_id) REFERENCES tb_cartoes(id)
                            ON UPDATE NO ACTION ON DELETE SET NULL
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    INSERT INTO tb_despesas_nova (
                        id, valor, descricao, data_compra, data_vencimento,
                        data_pagamento, status_pago, categoria_id,
                        grupo_parcelamento_id, cartao_id
                    )
                    SELECT
                        id, valor, descricao, data_vencimento, data_vencimento,
                        data_pagamento, status_pago, categoria_id,
                        grupo_parcelamento_id, cartao_id
                    FROM tb_despesas
                    """.trimIndent()
                )
                database.execSQL("DROP TABLE tb_despesas")
                database.execSQL("ALTER TABLE tb_despesas_nova RENAME TO tb_despesas")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_tb_despesas_categoria_id ON tb_despesas(categoria_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_tb_despesas_grupo_parcelamento_id ON tb_despesas(grupo_parcelamento_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_tb_despesas_data_vencimento ON tb_despesas(data_vencimento)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_tb_despesas_data_compra ON tb_despesas(data_compra)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_tb_despesas_categoria_id_data_compra ON tb_despesas(categoria_id, data_compra)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_tb_despesas_cartao_id ON tb_despesas(cartao_id)")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE tb_despesas ADD COLUMN conta_saldo_id INTEGER DEFAULT NULL"
                )
                database.execSQL(
                    "ALTER TABLE tb_despesas ADD COLUMN tipo_lancamento TEXT NOT NULL DEFAULT 'UNICA'"
                )
                database.execSQL(
                    "ALTER TABLE tb_despesas ADD COLUMN origem_pagamento TEXT DEFAULT NULL"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_tb_despesas_conta_saldo_id " +
                            "ON tb_despesas(conta_saldo_id)"
                )
            }
        }
    }
}