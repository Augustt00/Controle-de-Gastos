package com.example.controlegastos.ui.settings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlegastos.domain.usecase.ExportDatabaseUseCase
import com.example.controlegastos.domain.usecase.ImportDatabaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val exportDatabase: ExportDatabaseUseCase,
    private val importDatabase: ImportDatabaseUseCase
) : AndroidViewModel(application) {

    private val preferences = application.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            ultimoBackupEpochMillis = preferences.getLong(
                CHAVE_ULTIMO_BACKUP,
                0L
            ).takeIf { it > 0L }
        )
    )

    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun exportar(uri: Uri) {
        executarOperacao {
            exportDatabase(
                contentResolver = getApplication<Application>().contentResolver,
                uri = uri
            )

            val agora = System.currentTimeMillis()

            preferences
                .edit()
                .putLong(CHAVE_ULTIMO_BACKUP, agora)
                .apply()

            _uiState.update {
                it.copy(
                    ultimoBackupEpochMillis = agora,
                    mensagemSucesso = "Backup gerado com sucesso."
                )
            }
        }
    }

    fun restaurar(uri: Uri) {
        executarOperacao {
            importDatabase(
                contentResolver = getApplication<Application>().contentResolver,
                uri = uri
            )

            _uiState.update {
                it.copy(
                    mensagemSucesso = "Backup restaurado com sucesso."
                )
            }
        }
    }

    fun limparMensagens() {
        _uiState.update {
            it.copy(
                mensagemSucesso = null,
                mensagemErro = null
            )
        }
    }

    private fun executarOperacao(
        operacao: suspend () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    mensagemSucesso = null,
                    mensagemErro = null
                )
            }

            runCatching {
                operacao()
            }.onFailure { erro ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mensagemErro = erro.message
                            ?: "Não foi possível concluir a operação."
                    )
                }
            }.onSuccess {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "backup_preferences"
        const val CHAVE_ULTIMO_BACKUP = "ultimo_backup_epoch_millis"
    }
}