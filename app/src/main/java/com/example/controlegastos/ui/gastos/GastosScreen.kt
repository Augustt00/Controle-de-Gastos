@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)

package com.example.controlegastos.ui.gastos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.controlegastos.domain.model.DespesaDetalhada
import com.example.controlegastos.domain.model.GastoMensal
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Category
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Sort
import androidx.compose.ui.text.style.TextOverflow
import com.example.controlegastos.domain.model.Cartao
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween

private val CorGastos = Color(0xFF5F8D84)
private val CorGastosClara = Color(0xFF9DBCB5)
private val CorTextoGastos = Color(0xFF123C3A)

@Composable
fun GastosScreen(
    onVoltar: () -> Unit,
    onAbrirEdicao: () -> Unit,
    viewModel: GastosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var despesaParaExcluir by remember { mutableStateOf<DespesaDetalhada?>(null) }

    if (despesaParaExcluir != null) {
        val despesa = despesaParaExcluir!!

        AlertDialog(
            onDismissRequest = { despesaParaExcluir = null },
            title = {
                Text("Excluir despesa?")
            },
            text = {
                Text(
                    "A despesa \"${despesa.descricao}\" será removida definitivamente."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.excluirDespesa(despesa.id)
                        despesaParaExcluir = null
                    }
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { despesaParaExcluir = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Trocado de BottomSheetScaffold para Scaffold comum
    androidx.compose.material3.Scaffold(
        topBar = {
            TopBarGastos(
                onVoltar = onVoltar,
                onAbrirEdicao = onAbrirEdicao
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
                CircularProgressIndicator(
                    color = CorGastos
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFEEF2EF))
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 16.dp,
                    bottom = 32.dp // Ajustado o padding inferior já que não há mais o painel
                ),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .border(
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable {
                                    viewModel.selecionarMes(
                                        uiState.mesSelecionado.minusMonths(1)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowLeft,
                                contentDescription = "Mês anterior",
                                tint = CorTextoGastos,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = uiState.mesSelecionado.formatarMesCompleto(),
                            color = CorTextoGastos,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .border(
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable {
                                    viewModel.selecionarMes(
                                        uiState.mesSelecionado.plusMonths(1)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "Próximo mês",
                                tint = CorTextoGastos,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "HISTÓRICO",
                                    color = Color(0xFF6F7C76),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Toque para selecionar",
                                    color = Color(0xFFB5C0BA),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            GraficoBarrasMensal(
                                gastosMensais = uiState.gastosMensais,
                                mesSelecionado = uiState.mesSelecionado,
                                onSelecionarMes = viewModel::selecionarMes
                            )
                        }
                    }
                }

                item {
                    ResumoMesSelecionado(
                        mesSelecionado = uiState.mesSelecionado,
                        totalCentavos = uiState.totalMesSelecionado,
                        gastosMensais = uiState.gastosMensais,
                        quantidadeLancamentos = uiState.despesasDoMes.size
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        // Retiramos o padding geral desta Column para a linha poder encostar nas bordas
                        Column {
                            // 1. Cabeçalho com o seu próprio padding
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "POR CATEGORIA",
                                    color = Color(0xFF6F7C76), // Deixei no tom de cinza padrão
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }

                            // 2. Traço cinza longo (HorizontalDivider)
                            HorizontalDivider(
                                color = Color(0xFFE1E7E3), // Cinza bem suave
                                thickness = 1.dp
                            )

                            // 3. Conteúdo da lista de categorias (com o padding aplicado novamente)
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Spacer(modifier = Modifier.height(4.dp))

                                // lista dinâmica de categorias (com porcentagens que somam 100% e barra contínua)
                                val categorias = uiState.gastosPorCategoria
                                val totalCentavos = categorias.sumOf { it.totalGasto }

                                if (categorias.isNotEmpty()) {
                                    // calcula porcentagens reais (float)
                                    val rawPercents = categorias.map { gasto ->
                                        if (totalCentavos > 0L) {
                                            gasto.totalGasto.toFloat() / totalCentavos.toFloat() * 100f
                                        } else {
                                            0f
                                        }
                                    }

                                    // converte para inteiros por floor e distribui o resto para somar 100
                                    val floorInts = rawPercents.map { kotlin.math.floor(it).toInt() }.toMutableList()
                                    var diff = 100 - floorInts.sum()
                                    if (diff > 0) {
                                        // calcula as "frações" para decidir onde dar +1
                                        val remainders = rawPercents.mapIndexed { idx, v -> idx to (v - kotlin.math.floor(v)) }
                                            .sortedByDescending { it.second }
                                        var i = 0
                                        while (diff > 0 && i < remainders.size) {
                                            floorInts[remainders[i].first] = floorInts[remainders[i].first] + 1
                                            diff--
                                            i++
                                        }
                                    }

                                    // renderiza cada categoria usando índices
                                    categorias.forEachIndexed { index, gasto ->
                                        val percentualReal = rawPercents.getOrNull(index) ?: 0f
                                        val percentualAjustado = floorInts.getOrNull(index) ?: 0

                                        Column {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // ícone de categoria (usa iconeChave quando disponível)
                                                IconeCategoriaPill(
                                                    iconeChave = gasto.iconeChave,
                                                    corHex = gasto.corHex
                                                )

                                                Spacer(modifier = Modifier.width(12.dp))

                                                // nome categoria
                                                Text(
                                                    text = gasto.nomeCategoria,
                                                    modifier = Modifier.weight(1f),
                                                    color = CorTextoGastos,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )

                                                Spacer(modifier = Modifier.width(8.dp))

                                                // valor e percentual ajustado (exibe inteiro que soma 100)
                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text(
                                                        text = gasto.totalGasto.formatarMoeda(),
                                                        color = CorTextoGastos,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    // porcentagem em cinza (onSurfaceVariant)
                                                    Text(
                                                        text = "${percentualAjustado}%",
                                                        color = Color(0xFF78909C),
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }
                                            }

                                            // barra contínua proporcional ao percentual real (animada)
                                            val fraction = (percentualReal / 100f).coerceIn(0f, 1f)
                                            val animatedFraction by animateFloatAsState(
                                                targetValue = fraction,
                                                animationSpec = tween(durationMillis = 600)
                                            )

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(8.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant) // track
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth(animatedFraction)
                                                        .height(8.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(gasto.corHex.toComposeColor()) // filled portion
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                } else {
                                    // sem categorias: manter um espaço (ou mensagem)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }

                item {
                    // Estado local de ordenação
                    var sortExpanded by remember { mutableStateOf(false) }
                    var sortMode by remember { mutableStateOf(SortMode.DATA) }

                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LANÇAMENTOS",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFF6F7C76), // <-- Cor alterada para cinza
                                fontWeight = FontWeight.Bold
                            )


                            // botão de ordenação estilizado: pill branco com ícone + texto (Data / Valor)
                            Box {
                                // botão visível (pill branco)
                                androidx.compose.material3.Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFFE6EFEA)),
                                    modifier = Modifier
                                        .height(36.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { sortExpanded = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Sort,
                                            contentDescription = "Ordenar",
                                            tint = Color(0xFF78909C),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (sortMode == SortMode.DATA) "Data" else "Valor",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF78909C)
                                        )
                                    }
                                }


                                // Usando MaterialTheme para forçar o arredondamento perfeito do menu nativo
                                MaterialTheme(
                                    shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(12.dp))
                                ) {
                                    DropdownMenu(
                                        expanded = sortExpanded,
                                        onDismissRequest = { sortExpanded = false },
                                        modifier = Modifier
                                            .width(125.dp) // 1. Largura da caixinha reduzida
                                            .background(Color.White)
                                            .border(BorderStroke(1.dp, Color(0xFFE1E7E3)), RoundedCornerShape(12.dp))
                                    ) {
                                        // item: Mais recentes (DATA)
                                        val selectedData = sortMode == SortMode.DATA
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(if (selectedData) CorGastos.copy(alpha = 0.12f) else Color.Transparent)
                                                .clickable {
                                                    sortMode = SortMode.DATA
                                                    sortExpanded = false
                                                }
                                                .padding(vertical = 10.dp, horizontal = 12.dp) // 2. Espaçamento (padding) reduzido
                                        ) {
                                            Text(
                                                text = "Mais recentes",
                                                color = if (selectedData) CorGastos else Color(0xFF78909C),
                                                fontWeight = if (selectedData) FontWeight.SemiBold else FontWeight.Medium,
                                                style = MaterialTheme.typography.labelMedium // 3. Fonte menor
                                            )
                                        }

                                        // item: Maior valor (VALOR)
                                        val selectedValor = sortMode == SortMode.VALOR
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(if (selectedValor) CorGastos.copy(alpha = 0.12f) else Color.Transparent)
                                                .clickable {
                                                    sortMode = SortMode.VALOR
                                                    sortExpanded = false
                                                }
                                                .padding(vertical = 10.dp, horizontal = 12.dp) // 2. Espaçamento (padding) reduzido
                                        ) {
                                            Text(
                                                text = "Maior valor",
                                                color = if (selectedValor) CorGastos else Color(0xFF78909C),
                                                fontWeight = if (selectedValor) FontWeight.SemiBold else FontWeight.Medium,
                                                style = MaterialTheme.typography.labelMedium // 3. Fonte menor
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Preparar lista ordenada conforme modo
                        val despesasOrdenadas: List<DespesaDetalhada> = when (sortMode) {
                            SortMode.DATA -> uiState.despesasDoMes.sortedByDescending { it.dataCompra } // mais recentes primeiro
                            SortMode.VALOR -> uiState.despesasDoMes.sortedByDescending { it.valor } // maior valor primeiro
                        }

                        // Agrupar por LocalDate (UTC) para mostrar cabeçalhos "28 de julho", etc.
                        fun dateOf(d: DespesaDetalhada) = Instant.ofEpochMilli(d.dataCompra).atZone(ZoneOffset.UTC).toLocalDate()

                        val despesasPorDia: Map<java.time.LocalDate, List<DespesaDetalhada>> =
                            despesasOrdenadas.groupBy { desp -> dateOf(desp) }

// Dias em ordem:
// - se ordenar por DATA: dias ordenados por data desc (comportamento antigo)
// - se ordenar por VALOR: usamos a ordem dos dias conforme aparecem em despesasOrdenadas (garante dia do maior valor primeiro)
                        val diasOrdenados: List<java.time.LocalDate> = if (sortMode == SortMode.DATA) {
                            despesasPorDia.keys.sortedDescending()
                        } else {
                            // mantém a ordem de aparição na lista ordenada por valor e remove duplicatas mantendo ordem
                            despesasOrdenadas.map { dateOf(it) }.distinct()
                        }

                        // render por dia
                        diasOrdenados.forEach { dia ->
                            val listaDoDia = despesasOrdenadas.filter { dateOf(it) == dia }
                            // cabeçalho da data
                            Text(
                                text = dia.format(DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("pt", "BR"))),
                                color = Color(0xFF6F7C76), // <-- Cor alterada para cinza
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )

                            // cada lançamento do dia
                            listaDoDia.forEach { desp ->
                                // componente do card do lançamento
                                LancamentoItem(
                                    despesa = desp,
                                    cartoes = uiState.cartoes,
                                    onLongPress = { despesaParaExcluir = desp },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }

}

private enum class SortMode {
    DATA,
    VALOR
}

@Composable
private fun LancamentoItem(
    despesa: DespesaDetalhada,
    cartoes: List<Cartao>,
    onLongPress: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .combinedClickable(
                onClick = {},
                onLongClick = { onLongPress?.invoke() },
                role = Role.Button
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ícone da categoria — tenta usar drawable/iconeChave presente na categoria cadastrado
            // OBS: DespesaDetalhada atualmente NÃO contém iconeChave; se você mapeou a projeção para
            // incluir icone da categoria (recomendo), substitua `categoriaIconeChave` pelo campo correto.
            // Aqui uso o nome da categoria para derivar chave (fallback).
            IconeCategoriaPill(
                iconeChave = despesa.categoriaIconeChave,
                corHex = despesa.categoriaCorHex
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = despesa.descricao,
                    style = MaterialTheme.typography.titleSmall,
                    color = CorTextoGastos,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = despesa.categoriaNome,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    val cartao = cartoes.firstOrNull { it.id == despesa.cartaoId }
                    if (cartao != null) {
                        val context = LocalContext.current
                        val marcaChaveLower = cartao.marcaChave.orEmpty().lowercase(Locale("pt", "BR"))
                        val resIdCard = remember(marcaChaveLower) {
                            if (marcaChaveLower.isBlank()) 0 else context.resources.getIdentifier(marcaChaveLower, "drawable", context.packageName)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 6.dp)) {
                            if (resIdCard != 0) {
                                // Drawable do cartão existe -> mostra imagem e o nome ao lado
                                Image(
                                    painter = painterResource(id = resIdCard),
                                    contentDescription = cartao.nome,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = cartao.nome,
                                    color = Color(0xFF123C3A),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            } else {
                                // Fallback: sigla dentro de pill (mantive seu layout anterior)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFECEFF0))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cartao.marcaChave.takeIf { it.isNotBlank() }?.let {
                                            it.uppercase().take(2)
                                        } ?: cartao.nome.firstOrNull()?.toString() ?: "",
                                        color = Color(0xFF123C3A),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

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
private fun IconeCategoriaPill(
    iconeChave: String?,
    corHex: String
) {
    val chave = iconeChave ?: ""
    val context = LocalContext.current
    val resId = remember(chave) {
        if (chave.isBlank()) 0 else context.resources.getIdentifier(chave, "drawable", context.packageName)
    }

    val cor = try {
        Color(android.graphics.Color.parseColor(corHex))
    } catch (_: Exception) {
        CorGastos
    }

    if (resId != 0) {
        Icon(
            painter = painterResource(id = resId),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(cor.copy(alpha = 0.12f))
                .padding(6.dp)
        )
    } else if (chave.isNotBlank() && chave.any { it.code > 255 }) {
        // emoji
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(cor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = chave, fontSize = 18.sp)
        }
    } else {
        // fallback para ícone vetorial mapeado a partir da chave (ou name derivado)
        val chaveDerivada = if (chave.isNotBlank()) chave else chaveDaCategoriaAPartirDoNome("")
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(cor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconeCategoria(chaveDerivada),
                contentDescription = null,
                tint = cor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// extensão local para emoji (adapte do EdicaoScreen)
private fun String?.ehEmoji(): Boolean {
    return this?.any { caractere -> caractere.code > 255 } ?: false
}

private fun chaveDaCategoriaAPartirDoNome(nome: String): String {
    val m = nome.lowercase(Locale("pt", "BR"))
        .replace("ç", "c")
        .replace("ã", "a")
        .replace("õ", "o")
        .replace("á", "a")
        .replace("é", "e")
        .replace("í", "i")
        .replace("ó", "o")
        .replace("ú", "u")
        .replace(Regex("[^a-z0-9]"), "_")
        .replace(Regex("_+"), "_")
        .trim('_')

    // Map de nomes mais comuns para chaves exatas (fallback)
    val mapFallback = mapOf(
        "viagem" to "viagem",
        "alimentacao" to "alimentacao",
        "alimentação" to "alimentacao",
        "fast_food" to "fastfood",
        "streaming" to "streaming",
        "academia" to "academia",
        "transporte" to "transporte",
        "contas_da_casa" to "contas",
        "contas" to "contas",
        "saude" to "saude",
        "lazer" to "lazer",
        "assinaturas" to "assinaturas",
        "pets" to "pets",
        "presentes" to "presentes",
        "moradia" to "moradia"
    )

    return mapFallback[m] ?: m
}

private fun iconeCategoria(chave: String): ImageVector = when (chave) {
    "alimentacao", "alimentação", "fastfood" -> Icons.Default.Fastfood
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
    "contas", "contas_da_casa" -> Icons.Default.ReceiptLong
    else -> Icons.Default.Category
}

@Composable
private fun GraficoBarrasMensal(
    gastosMensais: List<GastoMensal>,
    mesSelecionado: java.time.YearMonth,
    onSelecionarMes: (java.time.YearMonth) -> Unit
) {
    val estadoLista = rememberLazyListState()

    val valoresComGasto = gastosMensais
        .map { it.totalCentavos }
        .filter { it > 0L }

    val mediaGastos = if (valoresComGasto.isNotEmpty()) {
        valoresComGasto.average().toFloat()
    } else {
        1f
    }

    val maiorGasto = gastosMensais
        .maxOfOrNull { it.totalCentavos }
        ?.coerceAtLeast(1L)
        ?.toFloat()
        ?: 1f

    val tetoVisual = maxOf(
        mediaGastos * 2f,
        maiorGasto
    )

    LazyRow(
        state = estadoLista,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        items(
            items = gastosMensais,
            key = { it.mesAno.toString() }
        ) { gasto ->
            val selecionado = gasto.mesAno == mesSelecionado
            val valor = gasto.totalCentavos.toFloat()

            val proporcao = if (valor > 0f) {
                (valor / tetoVisual).coerceIn(0.08f, 1f)
            } else {
                0.06f
            }

            val alturaBase = 30f + proporcao * 125f
            val alturaBarra = (
                    alturaBase + if (selecionado) 10f else 0f
                    ).dp

            val formatoBarra = MaterialTheme.shapes.medium

            Column(
                modifier = Modifier
                    .width(62.dp)
                    .clickable {
                        onSelecionarMes(gasto.mesAno)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                // Texto superior (Valor ou indicação de toque) -> Cinza bem claro (ou CorGastos se selecionado)
                Text(
                    text = gasto.totalCentavos.formatarMoeda(),
                    color = if (selecionado) {
                        CorGastos
                    } else {
                        Color(0xFFB0BEC5) // Cinza bem claro para os não selecionados
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (selecionado) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    },
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

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

                // Texto inferior (Mês/Ano) -> Cor cinza padrão (ou CorTextoGastos se preferir destacar mais o selecionado)
                Text(
                    text = gasto.mesAno.formatarRotuloGrafico(),
                    color = if (selecionado) CorTextoGastos else Color(0xFF78909C), // Cinza padrão
                    fontWeight = if (selecionado) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    },
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
    totalCentavos: Long,
    gastosMensais: List<GastoMensal>,
    quantidadeLancamentos: Int
) {
    val diasMesAtual = mesSelecionado.lengthOfMonth()
    val mediaAtual = if (diasMesAtual > 0) {
        totalCentavos / 100.0 / diasMesAtual
    } else {
        0.0
    }

    val mesAnterior = mesSelecionado.minusMonths(1)
    val totalAnteriorCentavos = gastosMensais
        .firstOrNull { it.mesAno == mesAnterior }
        ?.totalCentavos
        ?: 0L

    val diasMesAnterior = mesAnterior.lengthOfMonth()
    val mediaAnterior = if (diasMesAnterior > 0) {
        totalAnteriorCentavos / 100.0 / diasMesAnterior
    } else {
        0.0
    }

    val diffPercent: Double? = if (mediaAnterior == 0.0) {
        null
    } else {
        (mediaAtual - mediaAnterior) / mediaAnterior * 100.0
    }

    val corFundoTotal = Color(0xFF1B5B3A)
    val corTextoCinza = Color(0xFFB8C4BE)
    val corTextoPrincipal = Color(0xFF123C3A)
    val corPositiva = Color(0xFF18BB8D)
    val corNegativa = Color(0xFFD32F2F)
    val corBorda = Color(0xFFE0E8E3)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .height(112.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = false
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = corFundoTotal
            ),
            border = BorderStroke(1.dp, corBorda),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 16.dp
                )
            ) {
                Text(
                    text = "TOTAL GASTO",
                    color = corTextoCinza,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = totalCentavos.formatarMoeda(),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$quantidadeLancamentos lançamentos",
                    color = corTextoCinza,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .height(112.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = false
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, corBorda),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 16.dp
                )
            ) {
                Text(
                    text = "MÉDIA DIÁRIA",
                    color = corTextoPrincipal,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = mediaAtual.formatarMoeda(),
                    color = corTextoPrincipal,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (diffPercent == null) {
                    Text(
                        text = "— vs. mês ant.",
                        color = corPositiva,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    val gastouMenos = mediaAtual < mediaAnterior
                    val corVariacao = if (gastouMenos) corPositiva else corNegativa
                    val absPercent = kotlin.math.abs(diffPercent)
                        .let { kotlin.math.round(it).toInt() }
                    val seta = if (gastouMenos) "▼" else "▲"

                    Text(
                        text = "$seta $absPercent%  vs. mês ant.",
                        color = corVariacao,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}



@Composable
fun TopBarGastos(
    onVoltar: () -> Unit,
    onAbrirEdicao: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEEF2EF)) // Cor de fundo solicitada
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Botão de Voltar
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                tonalElevation = 0.dp,
                border = BorderStroke(1.dp, Color(0xFFE6EFEA)),
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onVoltar() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color(0xFF2F6F62),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Título e Subtítulo (com weight(1f) para ocupar o espaço do meio)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Gastos",
                    color = Color(0xFF123C3A),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Gerencie seus gastos",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Botão de Configurações (Engrenagem) na direita
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                tonalElevation = 0.dp,
                border = BorderStroke(1.dp, Color(0xFFE6EFEA)),
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onAbrirEdicao() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configurações / Edição",
                        tint = Color(0xFF2F6F62),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun Long.formatarMoeda(): String {
    val valor = this / 100.0
    val nf = java.text.NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return nf.format(valor)
}

private fun Double.formatarMoeda(): String {
    val nf = java.text.NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return nf.format(this)
}

private fun Long.formatarDiaMes(): String {
    return Instant
        .ofEpochMilli(this)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
        .format(
            DateTimeFormatter.ofPattern(
                "dd MMM",
                Locale("pt", "BR")
            )
        )
        .uppercase(Locale("pt", "BR"))
}

private fun java.time.YearMonth.formatarRotuloGrafico(): String {
    return format(
        DateTimeFormatter.ofPattern(
            "MMM yy",
            Locale("pt", "BR")
        )
    ).uppercase(Locale("pt", "BR"))
}

private fun java.time.YearMonth.formatarMesCompleto(): String {
    return format(
        DateTimeFormatter.ofPattern(
            "MMMM 'de' yyyy",
            Locale("pt", "BR")
        )
    ).replaceFirstChar {
        if (it.isLowerCase()) {
            it.titlecase(Locale("pt", "BR"))
        } else {
            it.toString()
        }
    }
}

private fun String.toComposeColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (_: IllegalArgumentException) {
        CorGastos
    }
}