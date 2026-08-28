@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class
)

package com.example.controlegastos.ui.despesa

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.controlegastos.domain.model.TipoLancamento
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun InserirDespesaScreen(
    onVoltar: () -> Unit,
    viewModel: InserirDespesaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()

        viewModel.uiState.collectLatest { estado ->
            estado.mensagemErro?.let { mensagem ->
                snackbarHostState.showSnackbar(mensagem)
            }

            if (estado.despesaSalvaComSucesso) {
                snackbarHostState.showSnackbar("Despesa salva com sucesso.")
                viewModel.consumirSucesso()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Nova despesa")
                },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        FormularioDespesa(
            uiState = uiState,
            focusRequester = focusRequester,
            onValorAlterado = viewModel::atualizarValor,
            onDescricaoAlterada = viewModel::atualizarDescricao,
            onCategoriaSelecionada = viewModel::selecionarCategoria,
            onCartaoSelecionado = viewModel::selecionarCartao,
            onDataSelecionada = viewModel::atualizarDataCompra,
            onTipoLancamentoAlterado = viewModel::alterarTipoLancamento,
            onQuantidadeParcelasAlterada = viewModel::atualizarQuantidadeParcelas,
            onSalvar = viewModel::salvarDespesa,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun FormularioDespesa(
    uiState: InserirDespesaUiState,
    focusRequester: FocusRequester,
    onValorAlterado: (String) -> Unit,
    onDescricaoAlterada: (String) -> Unit,
    onCategoriaSelecionada: (Int) -> Unit,
    onCartaoSelecionado: (Int?) -> Unit,
    onDataSelecionada: (LocalDate) -> Unit,
    onTipoLancamentoAlterado: (TipoLancamento) -> Unit,
    onQuantidadeParcelasAlterada: (String) -> Unit,
    onSalvar: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Dados da despesa",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {
            val valorFormatado = uiState.valorTexto.formatarMoedaComCursor()
            OutlinedTextField(
                value = TextFieldValue(
                    text = valorFormatado,
                    selection = TextRange(valorFormatado.length)
                ),
                onValueChange = { novoValor ->
                    onValorAlterado(
                        novoValor.text.filter(Char::isDigit)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = {
                    Text(text = "Valor")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Start
                ),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = uiState.descricao,
                onValueChange = onDescricaoAlterada,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = "Descrição")
                },
                placeholder = {
                    Text(text = "Ex.: Almoço")
                },
                singleLine = true
            )
        }

        item {
            SeletorCategoria(
                uiState = uiState,
                onCategoriaSelecionada = onCategoriaSelecionada
            )
        }

        item {
            SeletorCartao(
                uiState = uiState,
                onCartaoSelecionado = onCartaoSelecionado
            )
        }

        item {
            SeletorDataCompra(
                dataSelecionada = uiState.dataCompra,
                onDataSelecionada = onDataSelecionada
            )
        }

        item {
            HorizontalDivider()
        }

        item {
            TipoLancamentoSelector(
                tipoSelecionado = uiState.tipoLancamento,
                onTipoSelecionado = onTipoLancamentoAlterado
            )
        }

        if (uiState.tipoLancamento == TipoLancamento.PARCELADA) {
            item {
                OutlinedTextField(
                    value = uiState.quantidadeParcelas.toString(),
                    onValueChange = onQuantidadeParcelasAlterada,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Quantidade de parcelas") },
                    suffix = { Text("x") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }

        item {
            Button(
                onClick = onSalvar,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.salvando &&
                        !uiState.carregandoCategorias
            ) {
                if (uiState.salvando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (uiState.tipoLancamento == TipoLancamento.PARCELADA) {
                            "Salvar compra parcelada"
                        } else {
                            "Salvar despesa"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SeletorCategoria(
    uiState: InserirDespesaUiState,
    onCategoriaSelecionada: (Int) -> Unit
) {
    var aberto by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = aberto,
        onExpandedChange = {
            aberto = !aberto
        }
    ) {
        OutlinedTextField(
            value = uiState.categoriaSelecionada?.nome.orEmpty(),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            label = {
                Text(text = "Categoria")
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = aberto
                )
            },
            supportingText = {
                if (uiState.carregandoCategorias) {
                    Text(text = "Carregando categorias...")
                } else if (uiState.categorias.isEmpty()) {
                    Text(text = "Cadastre uma categoria antes de lançar despesas.")
                }
            }
        )

        ExposedDropdownMenu(
            expanded = aberto,
            onDismissRequest = {
                aberto = false
            }
        ) {
            uiState.categorias.forEach { categoria ->
                DropdownMenuItem(
                    text = {
                        Text(text = categoria.nome)
                    },
                    onClick = {
                        onCategoriaSelecionada(categoria.id)
                        aberto = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SeletorCartao(
    uiState: InserirDespesaUiState,
    onCartaoSelecionado: (Int?) -> Unit
) {
    var aberto by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = aberto,
        onExpandedChange = { aberto = !aberto }
    ) {
        OutlinedTextField(
            value = uiState.cartaoSelecionado?.nome ?: "Sem cartão",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            label = { Text("Cartão opcional") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = aberto)
            },
            supportingText = {
                uiState.cartaoSelecionado?.let { cartao ->
                    Text(
                        "Fecha dia ${cartao.diaFechamento} • " +
                                "vence dia ${cartao.diaVencimento}"
                    )
                }
            }
        )

        ExposedDropdownMenu(
            expanded = aberto,
            onDismissRequest = { aberto = false }
        ) {
            DropdownMenuItem(
                text = { Text("Sem cartão") },
                onClick = {
                    onCartaoSelecionado(null)
                    aberto = false
                }
            )
            uiState.cartoes.forEach { cartao ->
                DropdownMenuItem(
                    text = {
                        Text(
                            "${cartao.nome} • vence dia ${cartao.diaVencimento}"
                        )
                    },
                    onClick = {
                        onCartaoSelecionado(cartao.id)
                        aberto = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TipoLancamentoSelector(
    tipoSelecionado: TipoLancamento,
    onTipoSelecionado: (TipoLancamento) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Tipo de lançamento",
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TipoLancamento.entries.forEach { tipo ->
                FilterChip(
                    selected = tipo == tipoSelecionado,
                    onClick = { onTipoSelecionado(tipo) },
                    label = {
                        Text(
                            when (tipo) {
                                TipoLancamento.UNICA -> "Única"
                                TipoLancamento.PARCELADA -> "Parcelada"
                                TipoLancamento.FIXA -> "Fixa"
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SeletorDataCompra(
    dataSelecionada: LocalDate,
    onDataSelecionada: (LocalDate) -> Unit
) {
    var mostrarCalendario by remember { mutableStateOf(false) }

    val dataFormatada = remember(dataSelecionada) {
        dataSelecionada.format(
            DateTimeFormatter.ofPattern(
                "dd/MM/yyyy",
                Locale("pt", "BR")
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                mostrarCalendario = true
            }
    ) {
        OutlinedTextField(
            value = dataFormatada,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Data da compra")
            },
            readOnly = true,
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledContainerColor = Color.Transparent
            ),
            singleLine = true
        )
    }

    if (mostrarCalendario) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dataSelecionada
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = {
                mostrarCalendario = false
            },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val novaData = Instant
                                .ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            onDataSelecionada(novaData)
                        }
                        mostrarCalendario = false
                    }
                ) {
                    Text(text = "Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarCalendario = false
                    }
                ) {
                    Text(text = "Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun String.formatarMoedaComCursor(): String {
    if (isBlank()) return "R$"
    val valorCentavos = toLongOrNull() ?: 0L
    return "R$ %d,%02d".format(
        valorCentavos / 100,
        valorCentavos % 100
    )
}