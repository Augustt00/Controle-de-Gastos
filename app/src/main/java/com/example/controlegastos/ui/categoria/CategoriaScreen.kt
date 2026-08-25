package com.example.controlegastos.ui.categoria

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.layout.ExperimentalLayoutApi


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaScreen(
    onVoltar: () -> Unit,
    viewModel: CategoriaViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiState.collectLatest { estado ->
            estado.mensagemErro?.let { mensagem ->
                snackbarHostState.showSnackbar(mensagem)
            }

            if (estado.categoriaSalvaComSucesso) {
                snackbarHostState.showSnackbar("Categoria salva com sucesso.")
                viewModel.consumirSucesso()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Categorias")
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                FormularioCategoria(
                    uiState = uiState,
                    onNomeAlterado = viewModel::atualizarNome,
                    onTetoAlterado = viewModel::atualizarTetoMensal,
                    onCorSelecionada = viewModel::selecionarCor,
                    onSalvar = viewModel::salvarCategoria
                )
            }

            item {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Categorias cadastradas",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (uiState.carregando) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(
                    items = uiState.categorias,
                    key = { categoria -> categoria.id }
                ) { categoria ->
                    CategoriaItem(categoria = categoria)
                }
            }
        }
    }
}

@Composable
private fun FormularioCategoria(
    uiState: CategoriaUiState,
    onNomeAlterado: (String) -> Unit,
    onTetoAlterado: (String) -> Unit,
    onCorSelecionada: (String) -> Unit,
    onSalvar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Nova categoria",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = uiState.nome,
                onValueChange = onNomeAlterado,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = "Nome da categoria")
                },
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.tetoMensalTexto.formatarCentavos(),
                onValueChange = onTetoAlterado,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = "Teto mensal")
                },
                prefix = {
                    Text(text = "R$ ")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true
            )

            Text(
                text = "Cor de identificação",
                style = MaterialTheme.typography.labelLarge
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoriaUiState.CORES_PADRAO.forEach { corHex ->
                    SeletorDeCor(
                        corHex = corHex,
                        selecionada = corHex == uiState.corHexSelecionada,
                        onClick = {
                            onCorSelecionada(corHex)
                        }
                    )
                }
            }

            Button(
                onClick = onSalvar,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.salvando
            ) {
                if (uiState.salvando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = "Salvar categoria")
                }
            }
        }
    }
}

@Composable
private fun SeletorDeCor(
    corHex: String,
    selecionada: Boolean,
    onClick: () -> Unit
) {
    val cor = Color(android.graphics.Color.parseColor(corHex))

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(cor)
            .border(
                width = if (selecionada) 3.dp else 1.dp,
                color = if (selecionada) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.outline
                },
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    )
}

@Composable
private fun CategoriaItem(
    categoria: com.example.controlegastos.domain.model.Categoria
) {
    val cor = Color(android.graphics.Color.parseColor(categoria.corHex))

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(cor)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = categoria.nome,
                style = MaterialTheme.typography.titleSmall
            )

            categoria.tetoMensal?.let { teto ->
                Text(
                    text = "Teto: ${teto.formatarCentavos()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun String.formatarCentavos(): String {
    if (isBlank()) return ""

    val valor = toLongOrNull() ?: return ""

    return "%d,%02d".format(
        valor / 100,
        valor % 100
    )
}

private fun Long.formatarCentavos(): String {
    return "R$ %d,%02d".format(
        this / 100,
        this % 100
    )
}