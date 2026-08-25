package com.example.controlegastos.domain.usecase

import android.content.ContentResolver
import android.net.Uri
import com.example.controlegastos.data.backup.dto.BackupDatabaseDTO
import com.example.controlegastos.domain.repository.BackupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ImportDatabaseUseCase @Inject constructor(
    private val backupRepository: BackupRepository
) {

    suspend operator fun invoke(
        contentResolver: ContentResolver,
        uri: Uri
    ) = withContext(Dispatchers.IO) {
        val conteudo = contentResolver
            .openInputStream(uri)
            ?.bufferedReader()
            ?.use { reader ->
                reader.readText()
            }
            ?: error("Não foi possível abrir o arquivo selecionado.")

        val backup = Json {
            ignoreUnknownKeys = false
        }.decodeFromString<BackupDatabaseDTO>(conteudo)

        backupRepository.restaurarBackup(backup)
    }
}