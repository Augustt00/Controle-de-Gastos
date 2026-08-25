@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class
)

package com.example.controlegastos.ui.edicao

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.controlegastos.domain.model.Categoria
import com.example.controlegastos.domain.model.ContaSaldo
import com.example.controlegastos.domain.model.TipoContaSaldo

private val CorEdicao = Color(0xFF5F8D84)
private val CorTextoEdicao = Color(0xFF123C3A)

@Composable
fun EdicaoScreen(
    onVoltar: () -> Unit,
    viewModel: EdicaoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var secaoSelecionada by remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState.mensagem) {
        uiState.mensagem?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumirMensagem()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Edição",
                        color = CorTextoEdicao,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = CorTextoEdicao
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        if (uiState.carregando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CorEdicao)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    NavegacaoSecoesEdicao(
                        secaoSelecionada = secaoSelecionada,
                        onSelecionarSecao = { secaoSelecionada = it }
                    )
                }

                when (secaoSelecionada) {
                    0 -> {
                        item {
                            CabecalhoSecao(
                                titulo = "Categorias",
                                descricao = "Escolha quais categorias aparecem ao lançar uma despesa."
                            )
                        }

                        item {
                            CategoriasSugeridas(
                                categoriasExistentes = uiState.categorias,
                                onSelecionar = viewModel::selecionarCategoriaSugerida
                            )
                        }

                        item {
                            FormularioCategoriaEdicao(
                                uiState = uiState,
                                onNomeAlterado = viewModel::atualizarNomeCategoria,
                                onTetoAlterado = viewModel::atualizarTetoCategoria,
                                onSalvar = viewModel::salvarCategoria
                            )
                        }

                        items(uiState.categorias, key = { it.id }) { categoria ->
                            LinhaCategoria(
                                categoria = categoria,
                                onAtivacaoAlterada = { ativa ->
                                    viewModel.alterarAtivacaoCategoria(categoria, ativa)
                                }
                            )
                        }
                    }

                    1 -> {
                        item {
                            CabecalhoSecao(
                                titulo = "Cartões",
                                descricao = "Ative os cartões que você deseja usar futuramente nos lançamentos."
                            )
                        }

                        items(instituicoesPredefinidas, key = { it.chave }) { instituicao ->
                            val cartao = uiState.cartoes.firstOrNull {
                                it.marcaChave == instituicao.chave
                            }
                            LinhaCartaoPredefinido(
                                instituicao = instituicao,
                                ativo = cartao?.ativo == true,
                                onAtivacaoAlterada = { ativo ->
                                    viewModel.alterarAtivacaoCartao(instituicao, ativo)
                                }
                            )
                        }
                    }

                    2 -> {
                        item {
                            CabecalhoSecao(
                                titulo = "Saldo e carteira",
                                descricao = "Cadastre uma conta, dinheiro em carteira ou saldo reservado."
                            )
                        }

                        item {
                            FormularioContaSaldo(
                                uiState = uiState,
                                onInstituicaoSelecionada = viewModel::selecionarInstituicao,
                                onTipoSelecionado = viewModel::selecionarTipoConta,
                                onSaldoAlterado = viewModel::atualizarSaldoInicial,
                                onSalvar = viewModel::salvarContaSaldo
                            )
                        }

                        items(uiState.contas, key = { it.id }) { conta ->
                            LinhaContaSaldo(
                                conta = conta,
                                onAtivacaoAlterada = { ativa ->
                                    viewModel.alterarAtivacaoConta(conta, ativa)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CabecalhoSecao(titulo: String, descricao: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            titulo,
            color = CorTextoEdicao,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            descricao,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun CategoriasSugeridas(
    categoriasExistentes: List<Categoria>,
    onSelecionar: (CategoriaSugerida) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categoriasSugeridas.forEach { categoria ->
            val existe = categoriasExistentes.any {
                it.nome.equals(categoria.nome, ignoreCase = true)
            }
            FilterChip(
                selected = existe,
                onClick = { if (!existe) onSelecionar(categoria) },
                enabled = !existe,
                label = { Text(categoria.nome) },
                leadingIcon = {
                    Icon(
                        imageVector = iconeCategoria(categoria.iconeChave),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun FormularioCategoriaEdicao(
    uiState: EdicaoUiState,
    onNomeAlterado: (String) -> Unit,
    onTetoAlterado: (String) -> Unit,
    onSalvar: () -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = CorEdicao.copy(alpha = 0.10f))) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Adicionar categoria",
                color = CorTextoEdicao,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = uiState.novaCategoriaNome,
                onValueChange = onNomeAlterado,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nome") },
                singleLine = true
            )
            OutlinedTextField(
                value = uiState.novaCategoriaTetoTexto.formatarCentavosSemPrefixo(),
                onValueChange = onTetoAlterado,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Teto mensal opcional") },
                prefix = { Text("R$ ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Button(onClick = onSalvar, modifier = Modifier.fillMaxWidth()) {
                Text("Salvar categoria")
            }
        }
    }
}

@Composable
private fun LinhaCategoria(categoria: Categoria, onAtivacaoAlterada: (Boolean) -> Unit) {
    LinhaConfiguracao(
        badge = {
            IconeCategoria(categoria.iconeChave, categoria.corHex.toColor())
        },
        titulo = categoria.nome,
        subtitulo = categoria.tetoMensal?.let { "Teto: ${it.formatarMoeda()}" }
            ?: "Sem teto mensal",
        ativo = categoria.ativa,
        onAtivacaoAlterada = onAtivacaoAlterada
    )
}

@Composable
private fun LinhaCartaoPredefinido(
    instituicao: InstituicaoPredefinida,
    ativo: Boolean,
    onAtivacaoAlterada: (Boolean) -> Unit
) {
    LinhaConfiguracao(
        badge = { BadgeInstituicao(instituicao) },
        titulo = instituicao.nome,
        subtitulo = if (ativo) "Cartão ativo" else "Cartão desativado",
        ativo = ativo,
        onAtivacaoAlterada = onAtivacaoAlterada
    )
}

@Composable
private fun LinhaContaSaldo(conta: ContaSaldo, onAtivacaoAlterada: (Boolean) -> Unit) {
    val tituloTipo = when (conta.tipo) {
        TipoContaSaldo.CONTA -> "Conta"
        TipoContaSaldo.CARTEIRA -> "Carteira"
        TipoContaSaldo.SALDO_RESERVADO -> "Saldo reservado"
    }
    LinhaConfiguracao(
        badge = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(conta.corHex.toColor()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    conta.nome.take(2).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        titulo = conta.nome,
        subtitulo = "$tituloTipo • ${conta.saldoCentavos.formatarMoeda()}",
        ativo = conta.ativo,
        onAtivacaoAlterada = onAtivacaoAlterada
    )
}

@Composable
private fun LinhaConfiguracao(
    badge: @Composable () -> Unit,
    titulo: String,
    subtitulo: String,
    ativo: Boolean,
    onAtivacaoAlterada: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            badge()
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    titulo,
                    color = CorTextoEdicao,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    subtitulo,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(checked = ativo, onCheckedChange = onAtivacaoAlterada)
        }
    }
}

@Composable
private fun FormularioContaSaldo(
    uiState: EdicaoUiState,
    onInstituicaoSelecionada: (InstituicaoPredefinida) -> Unit,
    onTipoSelecionado: (TipoContaSaldo) -> Unit,
    onSaldoAlterado: (String) -> Unit,
    onSalvar: () -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = CorEdicao.copy(alpha = 0.10f))) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Adicionar saldo",
                color = CorTextoEdicao,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("Instituição", style = MaterialTheme.typography.labelLarge)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                instituicoesPredefinidas.forEach { instituicao ->
                    FilterChip(
                        selected = instituicao.chave == uiState.instituicaoSelecionada.chave,
                        onClick = { onInstituicaoSelecionada(instituicao) },
                        label = { Text(instituicao.sigla) }
                    )
                }
            }
            Text("Tipo", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoContaSaldo.entries.forEach { tipo ->
                    FilterChip(
                        selected = tipo == uiState.tipoContaSelecionado,
                        onClick = { onTipoSelecionado(tipo) },
                        label = {
                            Text(
                                when (tipo) {
                                    TipoContaSaldo.CONTA -> "Conta"
                                    TipoContaSaldo.CARTEIRA -> "Carteira"
                                    TipoContaSaldo.SALDO_RESERVADO -> "Reservado"
                                }
                            )
                        }
                    )
                }
            }
            OutlinedTextField(
                value = uiState.saldoInicialTexto.formatarCentavosSemPrefixo(),
                onValueChange = onSaldoAlterado,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Saldo inicial") },
                prefix = { Text("R$ ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Button(onClick = onSalvar, modifier = Modifier.fillMaxWidth()) {
                Text("Salvar saldo")
            }
        }
    }
}

@Composable
private fun NavegacaoSecoesEdicao(
    secaoSelecionada: Int,
    onSelecionarSecao: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            modifier = Modifier.weight(1f),
            selected = secaoSelecionada == 0,
            onClick = { onSelecionarSecao(0) },
            label = { Text("Categorias") }
        )
        FilterChip(
            modifier = Modifier.weight(1f),
            selected = secaoSelecionada == 1,
            onClick = { onSelecionarSecao(1) },
            label = { Text("Cartões") }
        )
        FilterChip(
            modifier = Modifier.weight(1f),
            selected = secaoSelecionada == 2,
            onClick = { onSelecionarSecao(2) },
            label = { Text("Saldo") }
        )
    }
}

@Composable
private fun BadgeInstituicao(instituicao: InstituicaoPredefinida) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(instituicao.cor),
        contentAlignment = Alignment.Center
    ) {
        Text(instituicao.sigla, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun IconeCategoria(chave: String, cor: Color) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(cor.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(iconeCategoria(chave), contentDescription = null, tint = cor)
    }
}

private fun iconeCategoria(chave: String): ImageVector = when (chave) {
    "alimentacao" -> Icons.Default.Fastfood
    "fastfood" -> Icons.Default.Fastfood
    "loja_online" -> Icons.Default.ShoppingBag
    "streaming" -> Icons.Default.Videocam
    "academia" -> Icons.Default.FitnessCenter
    "transporte" -> Icons.Default.DirectionsCar
    "moradia" -> Icons.Default.Home
    "saude" -> Icons.Default.Favorite
    "educacao" -> Icons.Default.School
    "lazer" -> Icons.Default.Celebration
    "assinaturas" -> Icons.Default.Subscriptions
    "pets" -> Icons.Default.Pets
    "presentes" -> Icons.Default.CardGiftcard
    "viagem" -> Icons.Default.Flight
    "contas" -> Icons.Default.ReceiptLong
    else -> Icons.Default.Category
}

private fun String.toColor(): Color = try {
    Color(android.graphics.Color.parseColor(this))
} catch (_: IllegalArgumentException) {
    CorEdicao
}

private fun String.formatarCentavosSemPrefixo(): String {
    val valor = filter(Char::isDigit).toLongOrNull() ?: return ""
    return "%d,%02d".format(valor / 100, valor % 100)
}

private fun Long.formatarMoeda(): String = "R$ %d,%02d".format(this / 100, this % 100)