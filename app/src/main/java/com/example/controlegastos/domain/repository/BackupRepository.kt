package com.example.controlegastos.domain.repository

import com.example.controlegastos.data.backup.dto.BackupDatabaseDTO

interface BackupRepository {

    suspend fun gerarBackup(): BackupDatabaseDTO

    suspend fun restaurarBackup(backup: BackupDatabaseDTO)
}