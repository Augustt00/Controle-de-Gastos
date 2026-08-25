package com.example.controlegastos.domain.usecase

import android.content.ContentResolver
import android.net.Uri
import com.example.controlegastos.domain.repository.BackupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ExportDatabaseUseCase @Inject constructor(
    private val backupRepository: BackupRepository
) {

    suspend operator fun invoke(
        contentResolver: ContentResolver,
        uri: Uri
    ) = withContext(Dispatchers.IO) {
        val backup = backupRepository.gerarBackup()

        val json = Json {
            prettyPrint = true
            encodeDefaults = true
        }.encodeToString(backup)

        contentResolver
            .openOutputStream(uri)
            ?.bufferedWriter()
            ?.use { writer ->
                writer.write(json)
            }
            ?: error("Não foi possível abrir o arquivo para gravação.")
    }
}