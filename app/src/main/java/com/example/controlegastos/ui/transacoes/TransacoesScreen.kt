@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.controlegastos.ui.transacoes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

private val CorPrincipal = Color(0xFF5F8D84)
private val CorTexto = Color(0xFF123C3A)
private val CorFundoSaldo = Color(0xFFE1EBE7)
private val CorCard = Color(0xFFE6EFEA)
private val CorDespesa = Color(0xFFD44D2A)

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
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Transações",
                        color = CorTexto,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = CorTexto
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::alternarValores) {
                        Icon(
                            imageVector = if (uiState.valoresVisiveis) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = "Ocultar ou mostrar valores",
                            tint = CorTexto
                        )
                    }
                }
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

            item(key = "titulo_cartoes") {
                TituloSecao(texto = "Cartão de crédito")
            }

            item(key = "abas_faturas") {
                AbasFaturas(
                    selecionada = uiState.abaSelecionada,
                    onSelecionar = viewModel::selecionarAbaFaturas
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
private fun SeletorMes(
    mes: YearMonth,
    onAnterior: () -> Unit,
    onProximo: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onAnterior) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Mês anterior",
                tint = CorTexto,
                modifier = Modifier.size(22.dp)
            )
        }

        Text(
            text = mes.formatarMes(),
            color = CorTexto,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onProximo) {
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "Próximo mês",
                tint = CorTexto,
                modifier = Modifier.size(22.dp)
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CorFundoSaldo)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Saldo total",
                color = CorTexto,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = saldoAtual.formatarMoeda(visivel),
                color = CorPrincipal,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider(color = CorPrincipal.copy(alpha = 0.18f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Saldo positivo", color = CorTexto)
                Text(
                    text = saldoInicial.formatarMoeda(visivel),
                    color = CorTexto,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Despesas", color = CorTexto)
                Text(
                    text = despesas.formatarMoeda(visivel),
                    color = CorDespesa,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun TituloSecao(texto: String) {
    Text(
        text = texto,
        color = CorTexto,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun CardConta(
    conta: ContaSaldo,
    visivel: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = CorCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = null,
                tint = CorPrincipal,
                modifier = Modifier.size(28.dp)
            )

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = conta.nome,
                    color = CorTexto,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = conta.tipo.name.replace("_", " "),
                    color = CorTexto.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = conta.saldoCentavos.formatarMoeda(visivel),
                color = CorTexto,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AbasFaturas(
    selecionada: AbaFaturas,
    onSelecionar: (AbaFaturas) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AbaFatura(
            texto = "Faturas abertas",
            selecionada = selecionada == AbaFaturas.ABERTAS,
            modifier = Modifier.weight(1f),
            onClick = { onSelecionar(AbaFaturas.ABERTAS) }
        )

        AbaFatura(
            texto = "Faturas fechadas",
            selecionada = selecionada == AbaFaturas.FECHADAS,
            modifier = Modifier.weight(1f),
            onClick = { onSelecionar(AbaFaturas.FECHADAS) }
        )
    }
}

@Composable
private fun AbaFatura(
    texto: String,
    selecionada: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(50),
        color = if (selecionada) CorPrincipal else CorCard
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            color = if (selecionada) Color.White else CorTexto,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CardTotalFaturas(
    total: Long,
    quantidade: Int,
    visivel: Boolean,
    aba: AbaFaturas
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CorFundoSaldo),
        shape = RoundedCornerShape(15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (aba == AbaFaturas.ABERTAS) "Total em aberto" else "Total fechado",
                    color = CorTexto,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$quantidade cartão(ões)",
                    color = CorTexto.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = total.formatarMoeda(visivel),
                color = CorTexto,
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = CorCard)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onExpandir),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = CorPrincipal,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = fatura.cartao.nome,
                        color = CorTexto,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Vencimento dia ${fatura.cartao.diaVencimento}",
                        color = CorTexto.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = fatura.totalCentavos.formatarMoeda(visivel),
                    color = CorTexto,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = if (expandida) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = "Expandir despesas",
                    tint = CorTexto
                )
            }

            if (expandida) {
                Spacer(Modifier.size(10.dp))
                HorizontalDivider(color = CorPrincipal.copy(alpha = 0.18f))

                fatura.despesas.forEach { despesa ->
                    ItemDespesaCartao(
                        despesa = despesa,
                        visivel = visivel
                    )
                }

                if (!fatura.paga) {
                    TextButton(
                        onClick = onPagar,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pagar fatura", color = CorPrincipal)
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = CorPrincipal,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Fatura paga", color = CorPrincipal, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
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
    val saldoSuficiente = selecionada?.saldoCentavos
        ?.let { it >= fatura.totalCentavos }
        ?: false

    AlertDialog(
        onDismissRequest = onCancelar,
        title = {
            Text("Pagar fatura", color = CorTexto, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = fatura.cartao.nome,
                    color = CorTexto,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Total: ${fatura.totalCentavos.formatarMoeda(visivel)}",
                    color = CorTexto
                )
                HorizontalDivider()
                Text(
                    text = "Selecione a conta de pagamento:",
                    color = CorTexto
                )

                if (contas.isEmpty()) {
                    Text(
                        text = "Nenhuma conta disponível.",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                contas.forEach { conta ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelecionarConta(conta) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (conta.id == selecionada?.id) {
                                Icons.Default.RadioButtonChecked
                            } else {
                                Icons.Default.RadioButtonUnchecked
                            },
                            contentDescription = null,
                            tint = CorPrincipal
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = conta.nome,
                            modifier = Modifier.weight(1f),
                            color = CorTexto
                        )

                        Text(
                            text = conta.saldoCentavos.formatarMoeda(visivel),
                            color = CorTexto,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                if (selecionada != null && !saldoSuficiente) {
                    Text(
                        text = "Saldo insuficiente para pagar esta fatura.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmar,
                enabled = !processando && saldoSuficiente
            ) {
                Text(if (processando) "Pagando..." else "Confirmar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancelar,
                enabled = !processando
            ) {
                Text("Cancelar")
            }
        }
    )
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