package com.example.controlegastos.data.repository

import com.example.controlegastos.data.local.dao.CategoriaDao
import com.example.controlegastos.data.local.entity.CategoriaEntity
import com.example.controlegastos.domain.model.Categoria
import com.example.controlegastos.domain.repository.CategoriaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoriaRepositoryImpl @Inject constructor(
    private val categoriaDao: CategoriaDao
) : CategoriaRepository {

    override fun observarTodas(): Flow<List<Categoria>> {
        return categoriaDao.observarTodas().map { categorias ->
            categorias.map { categoria ->
                categoria.toDomain()
            }
        }
    }

    override fun observarAtivas(): Flow<List<Categoria>> {
        return categoriaDao.observarAtivas().map { categorias ->
            categorias.map { categoria ->
                categoria.toDomain()
            }
        }
    }

    override suspend fun salvar(categoria: Categoria): Int {
        return if (categoria.id == 0) {
            categoriaDao.inserir(categoria.toEntity()).toInt()
        } else {
            categoriaDao.atualizar(categoria.toEntity())
            categoria.id
        }
    }

    override suspend fun atualizarAtivacao(categoriaId: Int, ativa: Boolean): Boolean {
        return categoriaDao.atualizarAtivacao(categoriaId, ativa) > 0
    }

    override suspend fun excluir(categoriaId: Int): Boolean {
        val categoria = categoriaDao.buscarPorId(categoriaId) ?: return false
        return categoriaDao.excluir(categoria) > 0
    }

    private fun CategoriaEntity.toDomain() = Categoria(
        id = id,
        nome = nome,
        corHex = corHex,
        tetoMensal = tetoMensal,
        iconeChave = iconeChave,
        ativa = ativa
    )

    private fun Categoria.toEntity() = CategoriaEntity(
        id = id,
        nome = nome,
        corHex = corHex,
        tetoMensal = tetoMensal,
        iconeChave = iconeChave,
        ativa = ativa
    )
}