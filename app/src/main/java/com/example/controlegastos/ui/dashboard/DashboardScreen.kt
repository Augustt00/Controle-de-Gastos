@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.controlegastos.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.controlegastos.domain.model.DespesaDetalhada
import com.example.controlegastos.domain.model.GastoPorCategoria
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DashboardScreen(
    onGerenciarCategorias: () -> Unit,
    onAdicionarDespesa: () -> Unit,
    onVerTodasTransacoes: () -> Unit,
    onVerProjecoes: () -> Unit,
    onVerPendencias: () -> Unit,
    onAbrirConfiguracoes: () -> Unit,
    onAbrirCartoes: () -> Unit,
    nomeUsuario: String = "Você",
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )

    Box(modifier = Modifier.fillMaxSize()) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 400.dp,
            sheetDragHandle = { BottomSheetDefaults.DragHandle() },
            sheetContainerColor = MaterialTheme.colorScheme.surface,
            sheetContent = {
                PainelTransacoes(
                    transacoes = uiState.transacoesDoMes,
                    mesSelecionado = uiState.mesSelecionado.formatarMesAno(),
                    numerosVisiveis = uiState.numerosVisiveis,
                    onVerTodas = onVerTodasTransacoes
                )
            },
            topBar = {
                CabecalhoInicio(
                    nomeUsuario = nomeUsuario,
                    numerosVisiveis = uiState.numerosVisiveis,
                    onAlternarVisibilidade = viewModel::alternarVisibilidadeValores,
                    onAbrirConfiguracoes = onAbrirConfiguracoes
                )
            }
        ) { innerPadding ->
            if (uiState.carregando) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                ConteudoDashboard(
                    modifier = Modifier.padding(innerPadding),
                    uiState = uiState
                )
            }
        }

        BarraNavegacaoInferior(
            modifier = Modifier.align(Alignment.BottomCenter),
            onRegistros = onVerTodasTransacoes,
            onAdicionarDespesa = onAdicionarDespesa,
            onFaturas = onVerPendencias,
            onCartoes = onAbrirCartoes
        )
    }
}

@Composable
private fun CabecalhoInicio(
    nomeUsuario: String,
    numerosVisiveis: Boolean,
    onAlternarVisibilidade: () -> Unit,
    onAbrirConfiguracoes: () -> Unit
) {
    val corCabecalho = Color(0xFF5F8D84)
    val formatoCabecalho = RoundedCornerShape(
        bottomStart = 32.dp,
        bottomEnd = 32.dp
    )
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = formatoCabecalho,
        color = corCabecalho,
        shadowElevation = 3.dp
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
                titleContentColor = Color.White,
                actionIconContentColor = Color.White
            ),
            title = {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Início",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.86f)
                    )
                    Text(
                        text = "Olá, $nomeUsuario",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            actions = {
                IconButton(onClick = onAlternarVisibilidade) {
                    Icon(
                        imageVector = if (numerosVisiveis) {
                            Icons.Default.Visibility
                        } else {
                            Icons.Default.VisibilityOff
                        },
                        contentDescription = if (numerosVisiveis) {
                            "Ocultar valores"
                        } else {
                            "Exibir valores"
                        }
                    )
                }
                IconButton(onClick = onAbrirConfiguracoes) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configurações"
                    )
                }
            }
        )
    }
}

@Composable
private fun ConteudoDashboard(
    modifier: Modifier,
    uiState: DashboardUiState
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 18.dp,
            bottom = 400.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Estrutura de despesas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            ResumoPrincipal(
                total = uiState.resumoMensal.totalGasto,
                numerosVisiveis = uiState.numerosVisiveis
            )
        }

        if (uiState.gastosPorCategoria.isEmpty()) {
            item {
                EstadoVazio(
                    titulo = "Nenhum gasto neste mês",
                    descricao = "Registre uma despesa para visualizar sua estrutura de gastos."
                )
            }
        } else {
            item {
                GraficoRosca(
                    gastosPorCategoria = uiState.gastosPorCategoria,
                    numerosVisiveis = uiState.numerosVisiveis
                )
            }

            item {
                LegendaCategorias(
                    gastosPorCategoria = uiState.gastosPorCategoria
                )
            }
        }
    }
}

@Composable
private fun ResumoPrincipal(
    total: Long,
    numerosVisiveis: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = "ESTE MÊS",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = total.formatarMoeda(numerosVisiveis),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GraficoRosca(
    gastosPorCategoria: List<GastoPorCategoria>,
    numerosVisiveis: Boolean
) {
    val total = gastosPorCategoria.sumOf { it.totalGasto }

    Box(
        modifier = Modifier.fillMaxWidth().height(250.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(218.dp)) {
            var inicio = -90f
            gastosPorCategoria.forEach { gasto ->
                val angulo = if (total > 0L) gasto.totalGasto.toFloat() / total.toFloat() * 360f else 0f
                drawArc(
                    color = gasto.corHex.toComposeColor(),
                    startAngle = inicio,
                    sweepAngle = angulo,
                    useCenter = false,
                    topLeft = Offset(24.dp.toPx(), 24.dp.toPx()),
                    size = Size(size.width - 48.dp.toPx(), size.height - 48.dp.toPx()),
                    style = Stroke(width = 34.dp.toPx(), cap = StrokeCap.Butt)
                )
                inicio += angulo
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Este mês", style = MaterialTheme.typography.labelLarge)
            Text(
                text = total.formatarMoeda(numerosVisiveis),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LegendaCategorias(
    gastosPorCategoria: List<GastoPorCategoria>
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(gastosPorCategoria) { gasto ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(gasto.corHex.toComposeColor())
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = gasto.nomeCategoria,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun PainelTransacoes(
    transacoes: List<DespesaDetalhada>,
    mesSelecionado: String,
    numerosVisiveis: Boolean,
    onVerTodas: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 176.dp, max = 680.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Últimas transações", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Somente $mesSelecionado", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            }
            Text(
                text = "Ver todas",
                modifier = Modifier.clickable(onClick = onVerTodas),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
        HorizontalDivider()

        if (transacoes.isEmpty()) {
            EstadoVazio(
                titulo = "Nenhuma transação",
                descricao = "As despesas deste mês aparecerão aqui."
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transacoes, key = { it.id }) { despesa ->
                    TransacaoItem(despesa, numerosVisiveis)
                }
            }
        }
    }
}

@Composable
private fun TransacaoItem(despesa: DespesaDetalhada, numerosVisiveis: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(despesa.categoriaCorHex.toComposeColor())
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = despesa.descricao, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${despesa.categoriaNome} • ${despesa.dataVencimento.formatarData()}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = despesa.valor.formatarMoeda(numerosVisiveis),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (despesa.statusPago) "Pago" else "Pendente",
                    color = if (despesa.statusPago) Color(0xFF2E7D32) else Color(0xFFD84315),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun BarraNavegacaoInferior(
    modifier: Modifier,
    onRegistros: () -> Unit,
    onAdicionarDespesa: () -> Unit,
    onFaturas: () -> Unit,
    onCartoes: () -> Unit
) {
    val corBarra = Color(0xFF5F8D84)
    val coresItem = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.White,
        selectedTextColor = Color.White,
        indicatorColor = Color.White.copy(alpha = 0.18f),
        unselectedIconColor = Color.White.copy(alpha = 0.84f),
        unselectedTextColor = Color.White.copy(alpha = 0.84f)
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(corBarra)
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(94.dp)
                .padding(top = 10.dp),
            containerColor = corBarra,
            contentColor = Color.White,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                modifier = Modifier.padding(top = 5.dp),
                selected = true,
                onClick = {},
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Início"
                    )
                },
                label = { Text("Início") },
                colors = coresItem
            )
            NavigationBarItem(
                modifier = Modifier.padding(top = 5.dp),
                selected = false,
                onClick = onRegistros,
                icon = {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = "Registros"
                    )
                },
                label = { Text("Registros") },
                colors = coresItem
            )
            Spacer(modifier = Modifier.weight(1f))
            NavigationBarItem(
                modifier = Modifier.padding(top = 5.dp),
                selected = false,
                onClick = onFaturas,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Faturas"
                    )
                },
                label = { Text("Gastos") },
                colors = coresItem
            )
            NavigationBarItem(
                modifier = Modifier.padding(top = 5.dp),
                selected = false,
                onClick = onCartoes,
                icon = {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = "Edição"
                    )
                },
                label = { Text("Edição") },
                colors = coresItem
            )
        }

        val formatoBotaoAdicionar = RoundedCornerShape(18.dp)
        FloatingActionButton(
            onClick = onAdicionarDespesa,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
                .size(62.dp),
            containerColor = Color.Transparent,
            contentColor = Color.White,
            shape = formatoBotaoAdicionar,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 9.dp,
                pressedElevation = 12.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF82ADA5),
                                Color(0xFF5F8D84),
                                Color(0xFF416E66)
                            )
                        ),
                        shape = formatoBotaoAdicionar
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar despesa",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun EstadoVazio(titulo: String, descricao: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(descricao, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun Long.formatarMoeda(numerosVisiveis: Boolean): String {
    if (!numerosVisiveis) return "R$ •••••"
    return "R$ %d,%02d".format(this / 100, this % 100)
}

private fun Long.formatarData(): String = Instant.ofEpochMilli(this)
    .atZone(ZoneOffset.UTC)
    .toLocalDate()
    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("pt", "BR")))

private fun java.time.YearMonth.formatarMesAno(): String = format(
    DateTimeFormatter.ofPattern("MMMM yyyy", Locale("pt", "BR"))
).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("pt", "BR")) else it.toString() }

private fun String.toComposeColor(): Color = try {
    Color(android.graphics.Color.parseColor(this))
} catch (_: IllegalArgumentException) {
    Color.Gray
}