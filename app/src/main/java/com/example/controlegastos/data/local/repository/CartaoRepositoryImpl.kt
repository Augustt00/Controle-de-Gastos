package com.example.controlegastos.data.repository

import com.example.controlegastos.data.local.dao.CartaoDao
import com.example.controlegastos.data.local.entity.CartaoEntity
import com.example.controlegastos.domain.model.Cartao
import com.example.controlegastos.domain.repository.CartaoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartaoRepositoryImpl @Inject constructor(
    private val cartaoDao: CartaoDao
) : CartaoRepository {

    override fun observarTodos(): Flow<List<Cartao>> {
        return cartaoDao.observarTodos().map { cartoes ->
            cartoes.map { cartao ->
                cartao.toDomain()
            }
        }
    }

    override suspend fun salvarOuAtualizarPorMarca(cartao: Cartao): Int {
        val existente = cartaoDao.buscarPorMarca(cartao.marcaChave)
        return if (existente == null) {
            cartaoDao.inserir(cartao.toEntity()).toInt()
        } else {
            cartaoDao.atualizar(cartao.toEntity(id = existente.id))
            existente.id
        }
    }

    override suspend fun atualizarAtivacao(cartaoId: Int, ativo: Boolean): Boolean {
        return cartaoDao.atualizarAtivacao(cartaoId, ativo) > 0
    }

    private fun CartaoEntity.toDomain() = Cartao(id, nome, marcaChave, corHex, ativo)

    private fun Cartao.toEntity(id: Int = this.id) = CartaoEntity(
        id = id,
        nome = nome,
        marcaChave = marcaChave,
        corHex = corHex,
        ativo = ativo
    )
}