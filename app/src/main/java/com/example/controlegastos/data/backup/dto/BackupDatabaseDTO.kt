package com.example.controlegastos.data.backup.dto

import com.example.controlegastos.data.local.entity.CartaoEntity
import com.example.controlegastos.data.local.entity.CategoriaEntity
import com.example.controlegastos.data.local.entity.ContaSaldoEntity
import com.example.controlegastos.data.local.entity.DespesaEntity
import com.example.controlegastos.data.local.entity.GrupoParcelamentoEntity
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class BackupDatabaseDTO(
    val versao: Int = VERSAO_ATUAL,
    val criadoEmEpochMillis: Long,
    val categorias: List<CategoriaBackupDTO>,
    val gruposParcelamento: List<GrupoParcelamentoBackupDTO>,
    val despesas: List<DespesaBackupDTO>,
    val cartoes: List<CartaoBackupDTO> = emptyList(),
    val contasSaldo: List<ContaSaldoBackupDTO> = emptyList()
) {
    companion object {
        const val VERSAO_ATUAL = 2
    }
}

@Serializable
data class CategoriaBackupDTO(
    val id: Int,
    val nome: String,
    val corHex: String,
    val tetoMensal: Long?,
    val iconeChave: String = "outros",
    val ativa: Boolean = true
)

@Serializable
data class GrupoParcelamentoBackupDTO(
    val id: Int,
    val qtdParcelas: Int,
    val valorTotal: Long,
    val descricaoBase: String
)

@Serializable
data class DespesaBackupDTO(
    val id: Int,
    val valor: Long,
    val descricao: String,
    val dataVencimento: String,
    val dataPagamento: String?,
    val statusPago: Boolean,
    val categoriaId: Int,
    val grupoParcelamentoId: Int?
)

@Serializable
data class CartaoBackupDTO(
    val id: Int,
    val nome: String,
    val marcaChave: String,
    val corHex: String,
    val ativo: Boolean
)

@Serializable
data class ContaSaldoBackupDTO(
    val id: Int,
    val nome: String,
    val instituicaoChave: String,
    val tipo: String,
    val saldoCentavos: Long,
    val corHex: String,
    val ativo: Boolean
)

fun CategoriaEntity.toBackupDTO() = CategoriaBackupDTO(
    id = id,
    nome = nome,
    corHex = corHex,
    tetoMensal = tetoMensal,
    iconeChave = iconeChave,
    ativa = ativa
)

fun GrupoParcelamentoEntity.toBackupDTO(): GrupoParcelamentoBackupDTO {
    return GrupoParcelamentoBackupDTO(
        id = id,
        qtdParcelas = qtdParcelas,
        valorTotal = valorTotal,
        descricaoBase = descricaoBase
    )
}

fun DespesaEntity.toBackupDTO(): DespesaBackupDTO {
    return DespesaBackupDTO(
        id = id,
        valor = valor,
        descricao = descricao,
        dataVencimento = dataVencimento.toString(),
        dataPagamento = dataPagamento?.toString(),
        statusPago = statusPago,
        categoriaId = categoriaId,
        grupoParcelamentoId = grupoParcelamentoId
    )
}

fun CategoriaBackupDTO.toEntity() = CategoriaEntity(
    id = id,
    nome = nome,
    corHex = corHex,
    tetoMensal = tetoMensal,
    iconeChave = iconeChave,
    ativa = ativa
)

fun GrupoParcelamentoBackupDTO.toEntity(): GrupoParcelamentoEntity {
    return GrupoParcelamentoEntity(
        id = id,
        qtdParcelas = qtdParcelas,
        valorTotal = valorTotal,
        descricaoBase = descricaoBase
    )
}

fun DespesaBackupDTO.toEntity(): DespesaEntity {
    return DespesaEntity(
        id = id,
        valor = valor,
        descricao = descricao,
        dataVencimento = LocalDate.parse(dataVencimento),
        dataPagamento = dataPagamento?.let(LocalDate::parse),
        statusPago = statusPago,
        categoriaId = categoriaId,
        grupoParcelamentoId = grupoParcelamentoId
    )
}

fun CartaoEntity.toBackupDTO() = CartaoBackupDTO(
    id = id,
    nome = nome,
    marcaChave = marcaChave,
    corHex = corHex,
    ativo = ativo
)

fun CartaoBackupDTO.toEntity() = CartaoEntity(
    id = id,
    nome = nome,
    marcaChave = marcaChave,
    corHex = corHex,
    ativo = ativo
)

fun ContaSaldoEntity.toBackupDTO() = ContaSaldoBackupDTO(
    id = id,
    nome = nome,
    instituicaoChave = instituicaoChave,
    tipo = tipo,
    saldoCentavos = saldoCentavos,
    corHex = corHex,
    ativo = ativo
)

fun ContaSaldoBackupDTO.toEntity() = ContaSaldoEntity(
    id = id,
    nome = nome,
    instituicaoChave = instituicaoChave,
    tipo = tipo,
    saldoCentavos = saldoCentavos,
    corHex = corHex,
    ativo = ativo
)