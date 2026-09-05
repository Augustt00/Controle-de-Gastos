@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.controlegastos.ui.transacoes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.controlegastos.domain.model.ContaSaldo
import com.example.controlegastos.domain.model.DespesaDetalhada
import com.example.controlegastos.domain.model.FaturaCartao
import com.example.controlegastos.domain.model.TipoContaSaldo
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.foundation.Canvas




private val CorFundoApp = Color(0xFFF3F7F2)
private val CorPrincipal = Color(0xFF5F8D84)
private val CorTexto = Color(0xFF123C3A)
private val CorFundoSaldo = Color(0xFFE1EBE7)
private val CorCard = Color(0xFFE6EFEA)

// Cores do Card Principal
private val CorCardSaldoDark = Color(0xFF0E3B36)
private val CorCardSaldoAccent = Color(0xFF154C45)
private val CorReceitaValor = Color(0xFF75E2A8)
private val CorDespesaValor = Color(0xFFFF9E80)
private val CorFundoIconeReceita = Color(0xFF1B4D3E)
private val CorFundoIconeDespesa = Color(0xFF503431)

@Composable
fun TransacoesScreen(
    onVoltar: () -> Unit,
    viewModel: TransacoesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var faturaParaPagar by remember { mutableStateOf<FaturaCartao?>(null) }
    var contaSelecionada by remember { mutableStateOf<ContaSaldo?>(null) }
    var processandoPagamento by remember { mutableStateOf(false) }

    val faturas = if (uiState.abaSelecionada == AbaFaturas.ABERTAS) {
        uiState.faturasAbertas
    } else {
        uiState.faturasFechadas
    }

    val totalFaturas = faturas.sumOf { it.totalCentavos }

    val contasDisponiveis = uiState.contas.filter {
        it.tipo != TipoContaSaldo.SALDO_RESERVADO
    }

    Scaffold(
        containerColor = CorFundoApp,
        topBar = {
            TopBarTransacoes(
                onVoltar = onVoltar,
                onToggleValores = viewModel::alternarValores,
                valoresVisiveis = uiState.valoresVisiveis
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "mes") {
                SeletorMes(
                    mes = uiState.mesSelecionado,
                    onAnterior = viewModel::mesAnterior,
                    onProximo = viewModel::proximoMes
                )
            }

            item(key = "saldo") {
                CardSaldoPrincipal(
                    saldoAtual = uiState.saldoAtualTotal,
                    saldoInicial = uiState.saldoInicialTotal,
                    despesas = uiState.despesasAvulsasTotal,
                    visivel = uiState.valoresVisiveis
                )
            }

            item(key = "titulo_contas") {
                TituloSecao(texto = "Contas")
            }

            items(
                items = uiState.contas,
                key = { "conta_${it.id}" }
            ) { conta ->
                CardConta(
                    conta = conta,
                    visivel = uiState.valoresVisiveis
                )
            }
            // calcular total e adicionar item de sumário
            val totalContas = uiState.contas.sumOf { it.saldoCentavos }

            item(key = "total_em_contas") {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFE9EFEA), // Cor de fundo verde bem clarinho
                    border = BorderStroke(1.dp, Color(0xFFD4E0D8)) // Borda sutil um pouco mais escura
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp), // Espaçamento interno
                        horizontalArrangement = Arrangement.SpaceBetween, // Joga um item pra cada lado
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total em contas",
                            color = Color(0xFF0F5A4A), // Verde escuro da imagem
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 15.sp // Ajuste fino do tamanho da fonte
                            ),
                            fontWeight = FontWeight.Bold
                        )

                        // Variável dinâmica colocada no lugar correto
                        Text(
                            text = totalContas.formatarMoeda(true),
                            color = Color(0xFF0F5A4A),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 15.sp
                            ),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item(key = "titulo_cartoes") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TituloSecao(texto = "Cartão de crédito")
                    Spacer(Modifier.weight(1f))
                    // Badge no canto direito com total de faturas abertas
                    val abertosCount = uiState.faturasAbertas.size
                    val totalAbertos = uiState.faturasAbertas.sumOf { it.totalCentavos }
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFFF4F2), // fundo levemente avermelhado
                        border = BorderStroke(1.dp, Color(0xFFFFD6CF)),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "$abertosCount fatura(s) em aberto", color = Color(0xFFB33A27), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            item(key = "abas_faturas") {
                AbasFaturas(
                    selecionada = uiState.abaSelecionada,
                    onSelecionar = viewModel::selecionarAbaFaturas,
                    abertosCount = uiState.faturasAbertas.size,
                    fechadosCount = uiState.faturasFechadas.size
                )
            }

            item(key = "total_faturas") {
                CardTotalFaturas(
                    total = totalFaturas,
                    quantidade = faturas.size,
                    visivel = uiState.valoresVisiveis,
                    aba = uiState.abaSelecionada
                )
            }

            if (faturas.isEmpty()) {
                item(key = "faturas_vazias") {
                    TextoVazio(
                        texto = if (uiState.abaSelecionada == AbaFaturas.ABERTAS) {
                            "Nenhuma fatura aberta neste mês."
                        } else {
                            "Nenhuma fatura fechada neste mês."
                        }
                    )
                }
            }

            items(
                items = faturas,
                key = { "fatura_${uiState.abaSelecionada}_${it.cartao.id}_${it.mesAno}" }
            ) { fatura ->
                CardFaturaCompleta(
                    fatura = fatura,
                    expandida = fatura.cartao.id in uiState.cartoesExpandidos,
                    visivel = uiState.valoresVisiveis,
                    onExpandir = {
                        viewModel.alternarCartao(fatura.cartao.id)
                    },
                    onPagar = {
                        faturaParaPagar = fatura
                        contaSelecionada = null
                    }
                )
            }

            item(key = "titulo_fixas") {
                TituloSecao(texto = "Despesas fixas")
            }

            item(key = "card_fixas") {
                CardDespesasFixas(
                    despesas = uiState.despesasFixas,
                    visivel = uiState.valoresVisiveis
                )
            }
        }
    }

    faturaParaPagar?.let { fatura ->
        DialogoPagamento(
            fatura = fatura,
            contas = contasDisponiveis,
            selecionada = contaSelecionada,
            visivel = uiState.valoresVisiveis,
            processando = processandoPagamento,
            onSelecionarConta = { conta ->
                contaSelecionada = conta
            },
            onCancelar = {
                if (!processandoPagamento) {
                    faturaParaPagar = null
                    contaSelecionada = null
                }
            },
            onConfirmar = {
                val conta = contaSelecionada ?: return@DialogoPagamento

                processandoPagamento = true

                viewModel.pagarFatura(
                    cartaoId = fatura.cartao.id,
                    contaId = conta.id
                ) { erro ->
                    processandoPagamento = false

                    if (erro == null) {
                        faturaParaPagar = null
                        contaSelecionada = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                "Fatura paga com sucesso."
                            )
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(erro)
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun TopBarTransacoes(
    onVoltar: () -> Unit,
    onToggleValores: () -> Unit,
    valoresVisiveis: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEEF2EF)) // Cor de fundo no mesmo padrão
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

            // Título e Subtítulo
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Transações",
                    color = Color(0xFF123C3A),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Acompanhe suas movimentações", // Subtítulo para manter a estrutura visual do design
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Botão de Visibilidade (Olho) na direita
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                tonalElevation = 0.dp,
                border = BorderStroke(1.dp, Color(0xFFE6EFEA)),
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onToggleValores() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (valoresVisiveis) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Ocultar ou mostrar valores",
                        tint = Color(0xFF2F6F62),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SeletorMes(
    mes: YearMonth,
    onAnterior: () -> Unit,
    onProximo: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onAnterior,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Mês anterior",
                tint = CorTexto,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = mes.formatarMes(),
            color = CorTexto,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onProximo,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "Próximo mês",
                tint = CorTexto,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun CardSaldoPrincipal(
    saldoAtual: Long,
    saldoInicial: Long,
    despesas: Long,
    visivel: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CorCardSaldoDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Círculos decorativos
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 24.dp, y = (-40).dp)
                    .background(CorCardSaldoAccent.copy(alpha = 0.18f), shape = CircleShape)
                    .zIndex(0f)
            )
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 64.dp, y = (-8).dp)
                    .background(Color.White.copy(alpha = 0.03f), shape = CircleShape)
                    .zIndex(0f)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .zIndex(1f)
            ) {
                Text(
                    text = "Saldo disponível",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = saldoAtual.formatarMoeda(visivel),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Bloco Receitas
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CorFundoIconeReceita
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = CorReceitaValor,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(16.dp)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Receitas",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = saldoInicial.formatarMoeda(visivel),
                            color = CorReceitaValor,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    // Divisor Vertical
                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .width(1.dp)
                            .background(Color.White.copy(alpha = 0.15f))
                    )

                    // Bloco Despesas
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CorFundoIconeDespesa
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = CorDespesaValor,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(16.dp)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Despesas",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = despesas.formatarMoeda(visivel),
                            color = CorDespesaValor,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

// Substituir a função existente TituloSecao por esta
@Composable
private fun TituloSecao(texto: String) {
    Text(
        text = texto.uppercase(), // imagem mostra texto em caixa alta pequena
        color = Color(0xFF8A9A9A), // tom de cinza
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.2.sp
    )
}

@Composable
private fun CardConta(
    conta: ContaSaldo,
    visivel: Boolean
) {
    // obter contexto para buscar drawable pelo nome
    val context = LocalContext.current

    val logoRes = remember(conta.instituicaoChave) {
        val nomeArquivo = if (
            conta.instituicaoChave.contains("caixa", ignoreCase = true) ||
            conta.instituicaoChave.equals("cx", ignoreCase = true)
        ) {
            "cef"
        } else {
            conta.instituicaoChave
        }

        context.resources.getIdentifier(nomeArquivo, "drawable", context.packageName)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge circular com ícone ou iniciais
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF0F4EF)),
                contentAlignment = Alignment.Center
            ) {
                if (logoRes != 0) {
                    Icon(
                        painter = painterResource(id = logoRes),
                        contentDescription = conta.nome,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = conta.nome.take(2).uppercase(),
                        color = conta.corHex.toColor(),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = conta.nome,
                    color = Color(0xFF123C3A),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = when (conta.tipo) {
                        TipoContaSaldo.CONTA -> "Conta bancária"
                        TipoContaSaldo.CARTEIRA -> "Carteira"
                        TipoContaSaldo.SALDO_RESERVADO -> "Saldo reservado"
                    },
                    color = Color(0xFF7D8B88),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = conta.saldoCentavos.formatarMoeda(visivel),
                color = Color(0xFF123C3A),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun AbasFaturas(
    selecionada: AbaFaturas,
    onSelecionar: (AbaFaturas) -> Unit,
    abertosCount: Int,
    fechadosCount: Int
) {
    // Card externo que engloba as duas abas
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(30),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp), // Espaçamento interno para a aba verde não encostar na borda
            verticalAlignment = Alignment.CenterVertically
        ) {
            @Composable
            fun AbaItem(
                titulo: String,
                quantidade: Int,
                isSelecionada: Boolean,
                onClick: () -> Unit
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(30))
                        .clickable { onClick() },
                    color = if (isSelecionada) Color(0xFF225F44) else Color.Transparent, // Verde escuro se selecionada
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = titulo,
                            color = if (isSelecionada) Color.White else Color(0xFF7D8B99),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Badge com a quantidade
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    color = if (isSelecionada) Color.White.copy(alpha = 0.2f) else Color(0xFFF3F4F6),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = quantidade.toString(),
                                color = if (isSelecionada) Color.White else Color(0xFF7D8B99),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Aba Faturas Abertas
            AbaItem(
                titulo = "Faturas Abertas",
                quantidade = abertosCount,
                isSelecionada = selecionada == AbaFaturas.ABERTAS,
                onClick = { onSelecionar(AbaFaturas.ABERTAS) }
            )

            // Aba Faturas Fechadas
            AbaItem(
                titulo = "Faturas Fechadas",
                quantidade = fechadosCount,
                isSelecionada = selecionada == AbaFaturas.FECHADAS,
                onClick = { onSelecionar(AbaFaturas.FECHADAS) }
            )
        }
    }
}

@Composable
private fun CardTotalFaturas(
    total: Long,
    quantidade: Int,
    visivel: Boolean,
    aba: AbaFaturas
) {
    val isAberto = aba == AbaFaturas.ABERTAS

    // Cores baseadas na aba selecionada (quente/laranja para abertas, verde/frio para fechadas)
    val bgColor = if (isAberto) Color(0xFFFFF9E5) else Color(0xFFE9EFEA)
    val borderColor = if (isAberto) Color(0xFFFFE0B2) else Color(0xFFD4E0D8)
    val textColorMain = if (isAberto) Color(0xFFC25501) else Color(0xFF0F5A4A)
    val textColorSub = if (isAberto) Color(0xFFE58735) else Color(0xFF4A7D71)

    val titulo = if (isAberto) "Total em aberto" else "Total fechado"

    val subtitulo = if (isAberto) {
        if (quantidade == 1) "1 cartão pendente" else "$quantidade cartões pendentes"
    } else {
        if (quantidade == 1) "1 cartão" else "$quantidade cartões"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = titulo,
                    color = textColorMain,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitulo,
                    color = textColorSub,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = total.formatarMoeda(visivel),
                color = textColorMain,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
private fun CardFaturaCompleta(
    fatura: FaturaCartao,
    expandida: Boolean,
    visivel: Boolean,
    onExpandir: () -> Unit,
    onPagar: () -> Unit
) {
    val cartao = fatura.cartao
    val limite = cartao.limiteCentavos
    val usado = fatura.totalCentavos.coerceAtMost(limite)
    val percentual = if (limite > 0L) (usado * 100 / limite).toFloat() / 100f else 0f
    val disponivel = (limite - usado).coerceAtLeast(0L)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Column(Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val context = LocalContext.current
                    val logoRes = remember(cartao.marcaChave) {
                        val nomeArquivo = cartao.marcaChave
                        context.resources.getIdentifier(nomeArquivo, "drawable", context.packageName)
                    }

                    // Ícone menor e proporcional para não cortar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (logoRes == 0) cartao.corHex.toColor() else Color(0xFFF0F4EF)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (logoRes != 0) {
                            Icon(
                                painter = painterResource(id = logoRes),
                                contentDescription = cartao.nome,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = cartao.nome.take(2).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = cartao.nome,
                                color = Color(0xFF111827),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(Modifier.width(8.dp))

                            // Badge "Pendente" / "Pago" mais achatado
                            if (!fatura.paga) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFFFBEB),
                                    border = BorderStroke(1.dp, Color(0xFFFDE68A))
                                ) {
                                    Text(
                                        text = "Pendente",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                        color = Color(0xFFD97706),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF0FDF4),
                                    border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                                ) {
                                    Text(
                                        text = "Pago",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                        color = Color(0xFF16A34A),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Vence ${cartao.diaVencimento} de ${fatura.mesAno.format(DateTimeFormatter.ofPattern("MMM", Locale("pt","BR"))).lowercase()}.",
                            color = Color(0xFF9CA3AF),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = fatura.totalCentavos.formatarMoeda(visivel),
                            color = Color(0xFF111827),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "de ${limite.formatarMoeda(true)}",
                            color = Color(0xFF9CA3AF),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val percentualInt = (percentual * 100).toInt()
                    Text(
                        text = "$percentualInt% do limite utilizado",
                        color = Color(0xFF9CA3AF),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "${disponivel.formatarMoeda(true)} disponível",
                        color = Color(0xFF9CA3AF),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Barra de progresso limpa feita por Canvas (sem bolinhas nas pontas)
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                ) {
                    // Fundo da barra
                    drawRect(color = Color(0xFFF3F4F6))
                    // Preenchimento proporcional sem pontos extras
                    drawRect(
                        color = Color(0xFF225F44),
                        size = androidx.compose.ui.geometry.Size(
                            width = size.width * percentual.coerceIn(0f, 1f),
                            height = size.height
                        )
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = diasParaVencerTexto(fatura),
                    color = Color(0xFF9CA3AF),
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = onExpandir,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Text("Ver fatura", color = Color(0xFF1F2937), fontWeight = FontWeight.SemiBold)
                    }

                    androidx.compose.material3.Button(
                        onClick = onPagar,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF225F44))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pagar fatura", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// helper para calcular texto dias para vencer (simples)
private fun diasParaVencerTexto(fatura: FaturaCartao): String {
    val hoje = java.time.LocalDate.now()

    val vencimento = fatura.mesAno.atDay(
        fatura.cartao.diaVencimento.coerceAtMost(
            fatura.mesAno.lengthOfMonth()
        )
    )

    val dias = java.time.temporal.ChronoUnit.DAYS.between(
        hoje,
        vencimento
    )

    return when {
        dias < 0 -> "Vencida"
        dias == 0L -> "Vence Hoje"
        dias == 1L -> "Falta 1 dia para vencer"
        else -> "Faltam $dias dias para vencer"
    }
}

@Composable
private fun ItemDespesaCartao(
    despesa: DespesaDetalhada,
    visivel: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = despesa.dataCompra.formatarDia(),
            color = CorTexto,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(52.dp)
        )

        Column(Modifier.weight(1f)) {
            Text(
                text = despesa.descricao,
                color = CorTexto,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = despesa.categoriaNome,
                color = CorTexto.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall
            )
        }

        Text(
            text = despesa.valor.formatarMoeda(visivel),
            color = CorTexto,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CardDespesasFixas(
    despesas: List<DespesaDetalhada>,
    visivel: Boolean
) {
    var expandida by remember { mutableStateOf(false) }
    val total = despesas.sumOf { it.valor }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = CorCard)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandida = !expandida },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = CorPrincipal,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = "Despesas fixas",
                    modifier = Modifier.weight(1f),
                    color = CorTexto,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = total.formatarMoeda(visivel),
                    color = CorTexto,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = if (expandida) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = "Expandir despesas fixas",
                    tint = CorTexto
                )
            }

            if (expandida) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = CorPrincipal.copy(alpha = 0.18f)
                )

                if (despesas.isEmpty()) {
                    Text(
                        text = "Nenhuma despesa fixa neste mês.",
                        color = CorTexto.copy(alpha = 0.7f)
                    )
                } else {
                    despesas.forEach { despesa ->
                        ItemDespesaCartao(
                            despesa = despesa,
                            visivel = visivel
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TextoVazio(texto: String) {
    Text(
        text = texto,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        color = CorTexto.copy(alpha = 0.7f)
    )
}

@Composable
private fun DialogoPagamento(
    fatura: FaturaCartao,
    contas: List<ContaSaldo>,
    selecionada: ContaSaldo?,
    visivel: Boolean,
    processando: Boolean,
    onSelecionarConta: (ContaSaldo) -> Unit,
    onCancelar: () -> Unit,
    onConfirmar: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onCancelar) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Pagar fatura", fontWeight = FontWeight.Bold, color = CorTexto)
                Spacer(Modifier.height(12.dp))

                // resumo da fatura em topo
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F9F7))) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        // ícone cartão
                        val context = LocalContext.current
                        val logoRes = remember(fatura.cartao.marcaChave) {
                            context.resources.getIdentifier(fatura.cartao.marcaChave, "drawable", context.packageName)
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF0F4EF)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (logoRes != 0) {
                                Icon(painter = painterResource(id = logoRes), contentDescription = fatura.cartao.nome, tint = Color.Unspecified, modifier = Modifier.size(24.dp))
                            } else {
                                Text(text = fatura.cartao.nome.take(2).uppercase(), color = fatura.cartao.corHex.toColor(), fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = fatura.cartao.nome, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "Fatura - vence ${fatura.mesAno.format(DateTimeFormatter.ofPattern("dd 'de' MMM", Locale("pt", "BR")))}",
                                color = CorTexto.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(text = fatura.totalCentavos.formatarMoeda(true), fontWeight = FontWeight.Bold, color = CorTexto)
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text("Debitar de", color = CorTexto.copy(alpha = 0.8f), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                contas.forEach { conta ->
                    val isSelected = conta.id == selecionada?.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onSelecionarConta(conta) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFEFF7EF) else Color.White),
                        border = if (isSelected) BorderStroke(1.dp, CorPrincipal) else null
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            val context = LocalContext.current
                            val nomeArquivo = if (conta.instituicaoChave.contains("caixa", ignoreCase = true) || conta.instituicaoChave.equals("cx", ignoreCase = true)) "cef" else conta.instituicaoChave
                            val logoRes = remember(nomeArquivo) { context.resources.getIdentifier(nomeArquivo, "drawable", context.packageName) }

                            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFF0F4EF)), contentAlignment = Alignment.Center) {
                                if (logoRes != 0) {
                                    Icon(painter = painterResource(id = logoRes), contentDescription = conta.nome, tint = Color.Unspecified, modifier = Modifier.size(22.dp))
                                } else {
                                    Text(text = conta.nome.take(2).uppercase(), color = conta.corHex.toColor(), fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = conta.nome, color = CorTexto)
                                Text(text = conta.saldoCentavos.formatarMoeda(true), color = CorTexto.copy(alpha = 0.6f), style = MaterialTheme.typography.bodySmall)
                            }

                            // indicador rádio simples
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) CorPrincipal else Color(0xFFF0F0F0)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // saldo após pagamento (se tiver conta selecionada)
                if (selecionada != null) {
                    val novoSaldo = selecionada.saldoCentavos - fatura.totalCentavos
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F6F1)), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Saldo após pagamento", color = CorTexto.copy(alpha = 0.7f))
                            Spacer(Modifier.weight(1f))
                            Text(text = novoSaldo.formatarMoeda(true), fontWeight = FontWeight.Bold, color = CorPrincipal)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // confirmar botão verde full width
                androidx.compose.material3.Button(
                    onClick = onConfirmar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = CorPrincipal)
                ) {
                    Text(text = if (processando) "Pagando..." else "Confirmar pagamento de ${fatura.totalCentavos.formatarMoeda(true)}", color = Color.White)
                }

                Spacer(Modifier.height(8.dp))

                // cancelar
                TextButton(onClick = onCancelar) {
                    Text("Cancelar", color = CorTexto)
                }
            }
        }
    }
}

private fun Long.formatarMoeda(visivel: Boolean): String {
    if (!visivel) return "R$ •••••"

    return NumberFormat
        .getCurrencyInstance(Locale("pt", "BR"))
        .format(this / 100.0)
}

private fun Long.formatarDia(): String {
    return Instant
        .ofEpochMilli(this)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("dd/MM"))
}


private fun String.toColor(): Color = try {
    Color(android.graphics.Color.parseColor(this))
} catch (_: IllegalArgumentException) {
    Color(0xFF5F8D84) // fallback
}

private fun YearMonth.formatarMes(): String {
    return format(
        DateTimeFormatter.ofPattern(
            "MMMM yyyy",
            Locale("pt", "BR")
        )
    ).replaceFirstChar {
        it.titlecase(Locale("pt", "BR"))
    }
}