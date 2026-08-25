package com.example.controlegastos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.controlegastos.data.local.entity.ContaSaldoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContaSaldoDao {

    @Query("SELECT * FROM tb_contas_saldo ORDER BY nome COLLATE NOCASE ASC")
    fun observarTodas(): Flow<List<ContaSaldoEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserir(conta: ContaSaldoEntity): Long

    @Update
    suspend fun atualizar(conta: ContaSaldoEntity): Int

    @Query("UPDATE tb_contas_saldo SET ativo = :ativo WHERE id = :contaId")
    suspend fun atualizarAtivacao(contaId: Int, ativo: Boolean): Int

    @Query("SELECT * FROM tb_contas_saldo ORDER BY id ASC")
    suspend fun buscarTodasParaBackup(): List<ContaSaldoEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserirTodasParaBackup(contas: List<ContaSaldoEntity>)

    @Query("DELETE FROM tb_contas_saldo")
    suspend fun limparTodas()
}