package com.example.controlegastos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.controlegastos.data.local.entity.CartaoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartaoDao {

    @Query("SELECT * FROM tb_cartoes ORDER BY nome COLLATE NOCASE ASC")
    fun observarTodos(): Flow<List<CartaoEntity>>

    @Query("SELECT * FROM tb_cartoes WHERE ativo = 1 ORDER BY nome COLLATE NOCASE ASC")
    fun observarAtivos(): Flow<List<CartaoEntity>>

    @Query("SELECT * FROM tb_cartoes WHERE marca_chave = :marcaChave LIMIT 1")
    suspend fun buscarPorMarca(marcaChave: String): CartaoEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserir(cartao: CartaoEntity): Long

    @Update
    suspend fun atualizar(cartao: CartaoEntity): Int

    @Query(
        """
        UPDATE tb_cartoes
        SET ativo = :ativo,
            dia_fechamento = :diaFechamento,
            dia_vencimento = :diaVencimento
        WHERE id = :cartaoId
        """
    )
    suspend fun atualizarConfiguracao(
        cartaoId: Int,
        ativo: Boolean,
        diaFechamento: Int,
        diaVencimento: Int
    ): Int

    @Query("UPDATE tb_cartoes SET ativo = :ativo WHERE id = :cartaoId")
    suspend fun atualizarAtivacao(cartaoId: Int, ativo: Boolean): Int

    @Query("SELECT * FROM tb_cartoes ORDER BY id ASC")
    suspend fun buscarTodosParaBackup(): List<CartaoEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserirTodosParaBackup(cartoes: List<CartaoEntity>)

    @Query("DELETE FROM tb_cartoes")
    suspend fun limparTodos()
}