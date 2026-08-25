@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.controlegastos.ui.gastos

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.controlegastos.domain.model.DespesaDetalhada
import com.example.controlegastos.domain.model.GastoMensal
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val CorGastos = Color(0xFF5F8D84)
private val CorGastosClara = Color(0xFF9DBCB5)
private val CorTextoGastos = Color(0xFF123C3A)

@Composable
fun GastosScreen(
    onVoltar: () -> Unit,
    viewModel: GastosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 400.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(
                color = CorTextoGastos.copy(alpha = 0.8f)
            )
        },
        sheetContent = {
            PainelGastosDetalhados(
                mesSelecionado = uiState.mesSelecionado,
                despesas = uiState.despesasDoMes
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Gastos",
                        color = CorTextoGastos,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = CorTextoGastos
                        )
                    }
                }
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
                CircularProgressIndicator(color = CorGastos)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 16.dp,
                    bottom = 370.dp
                ),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    Text(
                        text = uiState.mesSelecionado.formatarMesCompleto(),
                        color = CorTextoGastos,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    GraficoBarrasMensal(
                        gastosMensais = uiState.gastosMensais,
                        mesSelecionado = uiState.mesSelecionado,
                        onSelecionarMes = viewModel::selecionarMes
                    )
                }

                item {
                    ResumoMesSelecionado(
                        mesSelecionado = uiState.mesSelecionado,
                        totalCentavos = uiState.totalMesSelecionado
                    )
                }
            }
        }
    }
}

@Composable
private fun GraficoBarrasMensal(
    gastosMensais: List<GastoMensal>,
    mesSelecionado: java.time.YearMonth,
    onSelecionarMes: (java.time.YearMonth) -> Unit
) {
    val maiorTotal = gastosMensais
        .maxOfOrNull { it.totalCentavos }
        ?.coerceAtLeast(1L)
        ?: 1L

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        gastosMensais.forEach { gasto ->
            val selecionado = gasto.mesAno == mesSelecionado
            val proporcao = gasto.totalCentavos.toFloat() / maiorTotal.toFloat()
            val alturaBase = 42f + (proporcao * 105f)
            val alturaBarra = (alturaBase + if (selecionado) 10f else 0f).dp
            val formatoBarra = MaterialTheme.shapes.medium

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelecionarMes(gasto.mesAno) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(alturaBarra)
                        .then(
                            if (selecionado) {
                                Modifier
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = formatoBarra,
                                        clip = false
                                    )
                                    .clip(formatoBarra)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF8FBBB2),
                                                Color(0xFF5F8D84),
                                                Color(0xFF3F6C64)
                                            )
                                        ),
                                        shape = formatoBarra
                                    )
                            } else {
                                Modifier
                                    .clip(formatoBarra)
                                    .background(
                                        color = CorGastosClara,
                                        shape = formatoBarra
                                    )
                            }
                        )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = gasto.mesAno.formatarRotuloGrafico(),
                    color = CorTextoGastos,
                    fontWeight = if (selecionado) FontWeight.Bold else FontWeight.Medium,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun ResumoMesSelecionado(
    mesSelecionado: java.time.YearMonth,
    totalCentavos: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CorGastos.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Total de gastos",
                color = CorTextoGastos,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = totalCentavos.formatarMoeda(),
                color = CorGastos,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PainelGastosDetalhados(
    mesSelecionado: java.time.YearMonth,
    despesas: List<DespesaDetalhada>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 168.dp, max = 680.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                top = 4.dp,
                bottom = 12.dp
            )
        ) {
            Text(
                text = "Gastos detalhados",
                color = CorTextoGastos,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = mesSelecionado.formatarMesCompleto(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium
            )
        }
        HorizontalDivider()

        if (despesas.isEmpty()) {
            EstadoVazioGastos(mesSelecionado = mesSelecionado)
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 12.dp,
                    bottom = 28.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = despesas,
                    key = { despesa -> despesa.id }
                ) { despesa ->
                    ItemGastoDetalhado(despesa = despesa)
                }
            }
        }
    }
}

@Composable
private fun ItemGastoDetalhado(despesa: DespesaDetalhada) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = despesa.dataVencimento.formatarDiaMes(),
                modifier = Modifier.width(52.dp),
                color = CorTextoGastos,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(despesa.categoriaCorHex.toComposeColor())
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = despesa.descricao,
                    color = CorTextoGastos,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = despesa.categoriaNome,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = despesa.valor.formatarMoeda(),
                color = CorTextoGastos,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EstadoVazioGastos(
    mesSelecionado: java.time.YearMonth
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Nenhum gasto em ${mesSelecionado.formatarMesCompleto()}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Toque em outra barra para consultar um mês diferente.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun Long.formatarMoeda(): String {
    return "R$ %d,%02d".format(this / 100, this % 100)
}

private fun Long.formatarDiaMes(): String {
    return Instant
        .ofEpochMilli(this)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("dd MMM", Locale("pt", "BR")))
        .uppercase(Locale("pt", "BR"))
}

private fun java.time.YearMonth.formatarRotuloGrafico(): String {
    return format(
        DateTimeFormatter.ofPattern("MMM yy", Locale("pt", "BR"))
    ).uppercase(Locale("pt", "BR"))
}

private fun java.time.YearMonth.formatarMesCompleto(): String {
    return format(
        DateTimeFormatter.ofPattern("MMMM 'de' yyyy", Locale("pt", "BR"))
    ).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale("pt", "BR")) else it.toString()
    }
}

private fun String.toComposeColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (_: IllegalArgumentException) {
        CorGastos
    }
}