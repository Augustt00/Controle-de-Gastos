package com.example.controlegastos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.controlegastos.data.local.entity.CategoriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserir(categoria: CategoriaEntity): Long

    @Update
    suspend fun atualizar(categoria: CategoriaEntity): Int

    @Delete
    suspend fun excluir(categoria: CategoriaEntity): Int

    @Query("SELECT * FROM tb_categorias ORDER BY nome COLLATE NOCASE ASC")
    fun observarTodas(): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM tb_categorias WHERE ativa = 1 ORDER BY nome COLLATE NOCASE ASC")
    fun observarAtivas(): Flow<List<CategoriaEntity>>

    @Query("UPDATE tb_categorias SET ativa = :ativa WHERE id = :categoriaId")
    suspend fun atualizarAtivacao(categoriaId: Int, ativa: Boolean): Int

    @Query("SELECT * FROM tb_categorias WHERE id = :categoriaId LIMIT 1")
    suspend fun buscarPorId(categoriaId: Int): CategoriaEntity?

    @Query("SELECT * FROM tb_categorias ORDER BY id ASC")
    suspend fun buscarTodasParaBackup(): List<CategoriaEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserirTodasParaBackup(categorias: List<CategoriaEntity>)

    @Query("DELETE FROM tb_categorias")
    suspend fun limparTodas()
}