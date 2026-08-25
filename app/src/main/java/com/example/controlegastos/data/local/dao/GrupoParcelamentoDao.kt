package com.example.controlegastos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.controlegastos.data.local.entity.GrupoParcelamentoEntity

@Dao
interface GrupoParcelamentoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserir(grupo: GrupoParcelamentoEntity): Long

    @Delete
    suspend fun excluir(grupo: GrupoParcelamentoEntity): Int

    @Query("SELECT * FROM tb_grupo_parcelamento WHERE id = :grupoId LIMIT 1")
    suspend fun buscarPorId(grupoId: Int): GrupoParcelamentoEntity?

    @Query("SELECT * FROM tb_grupo_parcelamento ORDER BY id ASC")
    suspend fun buscarTodosParaBackup(): List<GrupoParcelamentoEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserirTodosParaBackup(
        grupos: List<GrupoParcelamentoEntity>
    )

    @Query("DELETE FROM tb_grupo_parcelamento")
    suspend fun limparTodos()
}