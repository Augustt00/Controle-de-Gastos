package com.example.controlegastos.ui.settings

data class SettingsUiState(
    val isLoading: Boolean = false,
    val mensagemSucesso: String? = null,
    val mensagemErro: String? = null,
    val ultimoBackupEpochMillis: Long? = null
)