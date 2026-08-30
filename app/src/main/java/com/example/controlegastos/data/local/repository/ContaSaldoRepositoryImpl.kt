package com.example.controlegastos.data.repository

import com.example.controlegastos.data.local.dao.ContaSaldoDao
import com.example.controlegastos.data.local.entity.ContaSaldoEntity
import com.example.controlegastos.domain.model.ContaSaldo
import com.example.controlegastos.domain.model.TipoContaSaldo
import com.example.controlegastos.domain.repository.ContaSaldoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ContaSaldoRepositoryImpl @Inject constructor(
    private val contaSaldoDao: ContaSaldoDao
) : ContaSaldoRepository {

    override fun observarTodas(): Flow<List<ContaSaldo>> {
        return contaSaldoDao.observarTodas().map { contas ->
            contas.map { conta ->
                conta.toDomain()
            }
        }
    }

    override suspend fun salvar(conta: ContaSaldo): Int {
        val entity = conta.toEntity()
        return if (conta.id == 0) contaSaldoDao.inserir(entity).toInt()
        else {
            contaSaldoDao.atualizar(entity)
            conta.id
        }
    }

    override suspend fun atualizarAtivacao(contaId: Int, ativo: Boolean): Boolean {
        return contaSaldoDao.atualizarAtivacao(contaId, ativo) > 0
    }

    override suspend fun atualizarSaldo(
        contaId: Int,
        novoSaldoCentavos: Long
    ): Boolean {
        require(novoSaldoCentavos >= 0L) {
            "O saldo não pode ficar negativo."
        }
        return contaSaldoDao.atualizarSaldo(contaId, novoSaldoCentavos) > 0
    }

    private fun ContaSaldoEntity.toDomain() = ContaSaldo(
        id = id,
        nome = nome,
        instituicaoChave = instituicaoChave,
        tipo = TipoContaSaldo.valueOf(tipo),
        saldoCentavos = saldoCentavos,
        corHex = corHex,
        ativo = ativo
    )

    private fun ContaSaldo.toEntity() = ContaSaldoEntity(
        id = id,
        nome = nome,
        instituicaoChave = instituicaoChave,
        tipo = tipo.name,
        saldoCentavos = saldoCentavos,
        corHex = corHex,
        ativo = ativo
    )
}