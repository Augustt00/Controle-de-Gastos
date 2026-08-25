@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)

package com.example.controlegastos.ui.timeline

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.controlegastos.domain.model.ProjecaoMensal
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TimelineScreen(
    onVoltar: () -> Unit,
    viewModel: TimelineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Projeções futuras")
                },
                navigationIcon = {
                    TextButton(onClick = onVoltar) {
                        Text(text = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.carregando -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.projecoes.isEmpty() -> {
                EstadoVazioTimeline(
                    modifier = Modifier.padding(innerPadding)
                )
            }

            else -> {
                TimelineConteudo(
                    projecoes = uiState.projecoes,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun TimelineConteudo(
    projecoes: List<ProjecaoMensal>,
    modifier: Modifier = Modifier
) {
    val projecoesPorAno = projecoes.groupBy { it.ano }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        projecoesPorAno.forEach { (ano, projecoesDoAno) ->
            stickyHeader {
                Text(
                    text = ano.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(
                items = projecoesDoAno,
                key = { projecao ->
                    "${projecao.ano}-${projecao.mes}"
                }
            ) { projecao ->
                ProjecaoMesItem(
                    projecao = projecao
                )
            }
        }
    }
}

@Composable
private fun ProjecaoMesItem(
    projecao: ProjecaoMensal
) {
    val mesAno = YearMonth.of(
        projecao.ano,
        projecao.mes
    )

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = mesAno.formatarMesAno(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Total comprometido",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = projecao.totalPendente.formatarMoeda(),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EstadoVazioTimeline(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Nenhum compromisso futuro",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "As parcelas pendentes dos próximos meses aparecerão aqui.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun YearMonth.formatarMesAno(): String {
    return format(
        DateTimeFormatter.ofPattern(
            "MMMM yyyy",
            Locale("pt", "BR")
        )
    ).replaceFirstChar { letra ->
        if (letra.isLowerCase()) {
            letra.titlecase(Locale("pt", "BR"))
        } else {
            letra.toString()
        }
    }
}

private fun Long.formatarMoeda(): String {
    return "R$ %d,%02d".format(
        this / 100,
        this % 100
    )
}