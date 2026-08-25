package com.example.controlegastos.ui.pendencias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlegastos.domain.usecase.MarcarDespesaComoPagaUseCase
import com.example.controlegastos.domain.usecase.ObservarPendenciasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendenciasViewModel @Inject constructor(
    private val observarPendencias: ObservarPendenciasUseCase,
    private val marcarComoPaga: MarcarDespesaComoPagaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PendenciasUiState())
    val uiState: StateFlow<PendenciasUiState> = _uiState.asStateFlow()

    init {
        observarPendencias()
            .catch { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mensagemErro = throwable.message
                            ?: "Não foi possível carregar as pendências."
                    )
                }
            }
            .let { fluxo ->
                viewModelScope.launch {
                    fluxo.collect { despesas ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                pendencias = despesas,
                                mensagemErro = null
                            )
                        }
                    }
                }
            }
    }

    fun confirmarPagamento(despesaId: Int) {
        viewModelScope.launch {
            runCatching {
                marcarComoPaga(despesaId)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        mensagemErro = throwable.message
                            ?: "Não foi possível confirmar o pagamento."
                    )
                }
            }
        }
    }

    fun limparErro() {
        _uiState.update {
            it.copy(mensagemErro = null)
        }
    }
}