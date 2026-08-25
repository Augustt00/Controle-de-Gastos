package com.example.controlegastos.data.backup

import androidx.room.withTransaction
import com.example.controlegastos.data.backup.dto.BackupDatabaseDTO
import com.example.controlegastos.data.backup.dto.toBackupDTO
import com.example.controlegastos.data.backup.dto.toEntity
import com.example.controlegastos.data.local.ControleGastosDatabase
import com.example.controlegastos.domain.repository.BackupRepository
import java.time.Instant
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    private val database: ControleGastosDatabase
) : BackupRepository {

    override suspend fun gerarBackup(): BackupDatabaseDTO {
        val categorias = database
            .categoriaDao()
            .buscarTodasParaBackup()
            .map { categoria ->
                categoria.toBackupDTO()
            }

        val gruposParcelamento = database
            .grupoParcelamentoDao()
            .buscarTodosParaBackup()
            .map { grupo ->
                grupo.toBackupDTO()
            }

        val despesas = database
            .despesaDao()
            .buscarTodasParaBackup()
            .map { despesa ->
                despesa.toBackupDTO()
            }

        val cartoes = database
            .cartaoDao()
            .buscarTodosParaBackup()
            .map { it.toBackupDTO() }

        val contasSaldo = database
            .contaSaldoDao()
            .buscarTodasParaBackup()
            .map { it.toBackupDTO() }

        return BackupDatabaseDTO(
            criadoEmEpochMillis = Instant.now().toEpochMilli(),
            categorias = categorias,
            gruposParcelamento = gruposParcelamento,
            despesas = despesas,
            cartoes = cartoes,
            contasSaldo = contasSaldo
        )
    }

    override suspend fun restaurarBackup(
        backup: BackupDatabaseDTO
    ) {
        validarBackup(backup)

        database.withTransaction {
            val categoriaDao = database.categoriaDao()
            val grupoDao = database.grupoParcelamentoDao()
            val despesaDao = database.despesaDao()
            val cartaoDao = database.cartaoDao()
            val contaSaldoDao = database.contaSaldoDao()

            despesaDao.limparTodas()
            grupoDao.limparTodos()
            cartaoDao.limparTodos()
            contaSaldoDao.limparTodas()
            categoriaDao.limparTodas()

            categoriaDao.inserirTodasParaBackup(
                backup.categorias.map { categoria ->
                    categoria.toEntity()
                }
            )

            grupoDao.inserirTodosParaBackup(
                backup.gruposParcelamento.map { grupo ->
                    grupo.toEntity()
                }
            )

            despesaDao.inserirTodasParaBackup(
                backup.despesas.map { despesa ->
                    despesa.toEntity()
                }
            )

            cartaoDao.inserirTodosParaBackup(
                backup.cartoes.map { it.toEntity() }
            )

            contaSaldoDao.inserirTodasParaBackup(
                backup.contasSaldo.map { it.toEntity() }
            )
        }
    }

    private fun validarBackup(backup: BackupDatabaseDTO) {
        require(
            backup.versao in 1..BackupDatabaseDTO.VERSAO_ATUAL
        ) {
            "Versão de backup incompatível."
        }

        require(
            backup.categorias.map { it.id }.distinct().size ==
                    backup.categorias.size
        ) {
            "O backup contém categorias duplicadas."
        }

        require(
            backup.gruposParcelamento.map { it.id }.distinct().size ==
                    backup.gruposParcelamento.size
        ) {
            "O backup contém grupos de parcelamento duplicados."
        }

        require(
            backup.despesas.map { it.id }.distinct().size ==
                    backup.despesas.size
        ) {
            "O backup contém despesas duplicadas."
        }

        val categoriaIds = backup.categorias
            .map { it.id }
            .toSet()

        val grupoIds = backup.gruposParcelamento
            .map { it.id }
            .toSet()

        backup.despesas.forEach { despesa ->
            require(despesa.categoriaId in categoriaIds) {
                "Uma despesa referencia uma categoria inexistente."
            }

            require(
                despesa.grupoParcelamentoId == null ||
                        despesa.grupoParcelamentoId in grupoIds
            ) {
                "Uma despesa referencia um grupo de parcelamento inexistente."
            }
        }
    }
}