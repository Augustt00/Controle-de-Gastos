@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)

package com.example.controlegastos.ui.edicao

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.controlegastos.R
import com.example.controlegastos.domain.model.Categoria
import com.example.controlegastos.domain.model.ContaSaldo
import com.example.controlegastos.domain.model.TipoContaSaldo
import java.util.Locale

// ====== CORES / CONSTANTES DE ESTILO ======
private val CorEdicao = Color(0xFF2F6F62)
private val CorTextoEdicao = Color(0xFF123C3A)
private val CorFundo = Color(0xFFF4F7F3)
private val CorCardClaro = Color(0xFFFFFFFF)
private val CorCardStat = Color(0xFFF0F4EF)
private val CorPillBg = Color(0xFF1B5B3A)
private val CorTetoChipBg = Color(0xFF153B33)
private val CorPillHeight = 44.dp
private val CorBordaCampo = Color(0xFF9AA9A2)
private val CorTextoPlaceholder = Color(0xFF9AA9A2)
private val CorChipTexto = Color(0xFF66736E)
private val CorBordaChip = Color(0xFFD0D7D3)

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
            TopBarEdicao(
                onVoltar = onVoltar,
                titulo = "Edição",
                descricao = when (secaoSelecionada) {
                    0 -> "Gerencie as categorias dos seus gastos"
                    1 -> "Configure os cartões utilizados"
                    2 -> "Cadastre contas, carteira e saldo reservado"
                    else -> ""
                }
            ) {
                AbasEdicao(
                    secaoSelecionada = secaoSelecionada,
                    onSelecionarSecao = { secaoSelecionada = it }
                )
            }
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
                    .padding(innerPadding)
                    .background(CorFundo),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (secaoSelecionada) {
                    0 -> {
                        item {
                            CategoriasContent(
                                uiState = uiState,
                                onSelecionarSugerida = viewModel::selecionarCategoriaSugerida,
                                onNomeAlterado = viewModel::atualizarNomeCategoria,
                                onTetoAlterado = viewModel::atualizarTetoCategoria,
                                onSalvar = viewModel::salvarCategoria,
                                onAlternarAtivacao = { categoria, ativa ->
                                    viewModel.alterarAtivacaoCategoria(categoria, ativa)
                                },
                                onRemoverCategoria = { categoria ->
                                    viewModel.excluirCategoria(categoria.id)
                                },
                                onSelecionarEmoji = viewModel::atualizarIconeCategoria
                            )
                        }
                    }
                    1 -> {
                        val cartoesAtivos = uiState.cartoes.filter { it.ativo }
                        val cartoesInativos = uiState.cartoes.filter { !it.ativo }

                        val totalLimiteCentavos = uiState.cartoes.sumOf { it.limiteCentavos }
                        val totalDisponivelCentavos = cartoesAtivos.sumOf { (it.limiteCentavos).coerceAtLeast(0L) }

                        fun alterarAtivacaoPorCartao(
                            cartao: com.example.controlegastos.domain.model.Cartao,
                            ativo: Boolean
                        ) {
                            instituicoesPredefinidas
                                .firstOrNull { it.chave == cartao.marcaChave }
                                ?.let { viewModel.alterarAtivacaoCartao(it, ativo) }
                        }

                        item {
                            ResumoCartoesCard(
                                limiteDisponivelCentavos = totalDisponivelCentavos,
                                totalLimiteCentavos = totalLimiteCentavos,
                                ativos = cartoesAtivos.size
                            )
                        }

                        cartoesAtivos.forEach { cartao ->
                            item(key = "ativo_${cartao.id}") {
                                val limiteCentavos = cartao.limiteCentavos
                                val usadoCentavos = 0L
                                val disponivelCentavos = (limiteCentavos - usadoCentavos).coerceAtLeast(0L)

                                CartaoDetalhado(
                                    cartao = cartao,
                                    disponivelCentavos = disponivelCentavos,
                                    usadoCentavos = usadoCentavos,
                                    limiteCentavos = limiteCentavos,
                                    ativo = true,
                                    onAtivacaoAlterada = { novo ->
                                        alterarAtivacaoPorCartao(cartao, novo)
                                    },
                                    onSalvarDatas = { fechamento, vencimento ->
                                        viewModel.editarConfiguracaoCartao(cartao)
                                        viewModel.atualizarDiasCartao(
                                            fechamento.toString(),
                                            vencimento.toString()
                                        )
                                        viewModel.salvarConfiguracaoCartao()
                                    }
                                )
                            }
                        }

                        item {
                            InativasSectionCartoes(
                                cartoesInativos = cartoesInativos,
                                onAtivarCartao = { id, ativo ->
                                    uiState.cartoes
                                        .firstOrNull { it.id == id }
                                        ?.let { alterarAtivacaoPorCartao(it, ativo) }
                                },
                                onExcluirCartao = viewModel::excluirCartao,
                                onAdicionarCartao = { instituicao, fechamento, vencimento, limiteCentavos ->
                                    viewModel.adicionarCartao(
                                        instituicaoChave = instituicao.chave,
                                        nome = instituicao.nome,
                                        diaFechamento = fechamento,
                                        diaVencimento = vencimento,
                                        limiteCentavos = limiteCentavos
                                    )
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
fun InativasSectionCartoes(
    cartoesInativos: List<com.example.controlegastos.domain.model.Cartao>,
    onAtivarCartao: (Int, Boolean) -> Unit,
    onExcluirCartao: (Int) -> Unit,
    onAdicionarCartao: (InstituicaoPredefinida, Int, Int, Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var dialogCartaoSelecionado by remember {
        mutableStateOf<com.example.controlegastos.domain.model.Cartao?>(null)
    }
    var mostrarNovoCartao by remember { mutableStateOf(false) }
    val cor = Color(0xFF7B8C86)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("DESATIVADOS", color = cor, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(6.dp))
            Text("•", color = cor, fontSize = 10.sp)
            Spacer(Modifier.width(6.dp))
            Text(cartoesInativos.size.toString(), color = cor)
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = if (expanded) "Recolher" else "Expandir",
                tint = cor,
                modifier = Modifier.size(20.dp)
            )
        }

        if (expanded) {
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                cartoesInativos.forEach { cartao ->
                    CartaoInativoRow(
                        cartao = cartao,
                        onAtivar = { onAtivarCartao(cartao.id, it) },
                        onLongDelete = { dialogCartaoSelecionado = cartao }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (!mostrarNovoCartao) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(BorderStroke(1.dp, CorBordaChip), RoundedCornerShape(12.dp))
                    .clickable { mostrarNovoCartao = true },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("+ Adicionar cartão", color = CorChipTexto)
                }
            }
        } else {
            val corner = 12.dp
            val strokeWidthPx = 1.5f
            val dashIntervals = floatArrayOf(15f, 10f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(corner))
                    .clickable { mostrarNovoCartao = false }
                    .drawBehind {
                        val paint = Paint().apply {
                            color = CorEdicao
                            style = PaintingStyle.Stroke
                            strokeWidth = strokeWidthPx * density
                            pathEffect = PathEffect.dashPathEffect(
                                intervals = dashIntervals,
                                phase = 0f
                            )
                        }
                        val radius = corner.toPx()
                        drawIntoCanvas { canvas ->
                            canvas.drawRoundRect(
                                left = 0f,
                                top = 0f,
                                right = size.width,
                                bottom = size.height,
                                radiusX = radius,
                                radiusY = radius,
                                paint = paint
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancelar",
                        tint = CorEdicao,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Cancelar",
                        color = CorEdicao,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (mostrarNovoCartao) {
            Spacer(Modifier.height(12.dp))
            NovoCartaoForm(
                onSave = { instituicao, fechamento, vencimento, limiteCentavos ->
                    onAdicionarCartao(instituicao, fechamento, vencimento, limiteCentavos)
                    mostrarNovoCartao = false
                }
            )
        }
    }

    dialogCartaoSelecionado?.let { cartao ->
        ConfirmacaoExcluirDialog(
            cartaoNome = cartao.nome,
            onConfirm = {
                onExcluirCartao(cartao.id)
                dialogCartaoSelecionado = null
            },
            onDismiss = { dialogCartaoSelecionado = null }
        )
    }
}

private fun parseCurrencyToCentavos(input: String): Long {
    val digits = input.filter { it.isDigit() }
    if (digits.isBlank()) return 0L
    return if (digits.length <= 2) {
        digits.toLong()
    } else {
        val reais = digits.dropLast(2).toLong()
        val cents = digits.takeLast(2).toLong()
        reais * 100 + cents
    }
}


private fun formatarTextoMoeda(input: String): String {
    val apenasDigitos = input.filter { it.isDigit() }
    if (apenasDigitos.isBlank()) return ""

    val valorLong = apenasDigitos.toLongOrNull() ?: 0L
    val reais = valorLong / 100
    val centavos = valorLong % 100

    // Formata os reais com separador de milhar (ponto)
    val reaisFormatados = java.text.NumberFormat.getIntegerInstance(Locale("pt", "BR")).format(reais)

    return "%s,%02d".format(reaisFormatados, centavos)
}
@Composable
private fun NovoCartaoForm(
    onSave: (InstituicaoPredefinida, Int, Int, Long) -> Unit
) {
    var selecionada by remember {
        mutableStateOf<InstituicaoPredefinida?>(
            instituicoesPredefinidas.firstOrNull()
        )
    }
    var fechamentoText by remember { mutableStateOf("") }
    var vencimentoText by remember { mutableStateOf("") }
    var limiteText by remember { mutableStateOf("") } // Armazena o valor formatado

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Novo cartão",
                    color = CorTextoEdicao,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            androidx.compose.material3.HorizontalDivider(
                color = CorCardStat.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Cartões dos bancos",
                style = MaterialTheme.typography.labelMedium,
                color = CorTextoPlaceholder
            )

            Spacer(Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                instituicoesPredefinidas.forEach { instituicao ->
                    val isSelected = instituicao == selecionada
                    val context = LocalContext.current

                    val logoRes = remember(instituicao.chave) {
                        val nomeArquivo =
                            if (
                                instituicao.sigla == "CX" ||
                                instituicao.chave.contains("caixa", ignoreCase = true)
                            ) {
                                "cef"
                            } else {
                                instituicao.chave
                            }

                        context.resources.getIdentifier(
                            nomeArquivo,
                            "drawable",
                            context.packageName
                        )
                    }

                    val iconeTint = if (instituicao.chave == "picpay") {
                        Color(0xFF01C66A)
                    } else {
                        Color.Unspecified
                    }

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color.White else Color(0xFFF4F7F3)
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) {
                                    instituicao.cor
                                } else {
                                    Color(0xFFE0E8E3)
                                },
                                shape = CircleShape
                            )
                            .clickable {
                                selecionada = instituicao
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (logoRes != 0) {
                            Icon(
                                painter = painterResource(id = logoRes),
                                contentDescription = instituicao.nome,
                                tint = iconeTint,
                                modifier = Modifier.size(30.dp)
                            )
                        } else {
                            Text(
                                text = instituicao.sigla,
                                color = instituicao.cor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = selecionada?.let { "${it.nome} selecionado" }
                    ?: "Nenhuma instituição",
                color = CorTextoPlaceholder,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CampoCartaoCinza(
                    titulo = "Fecha no dia",
                    valor = fechamentoText,
                    placeholder = "Ex: 19",
                    onValueChange = {
                        fechamentoText = it.filter(Char::isDigit)
                    },
                    modifier = Modifier.weight(1f)
                )

                CampoCartaoCinza(
                    titulo = "Vence no dia",
                    valor = vencimentoText,
                    placeholder = "Ex: 26",
                    onValueChange = {
                        vencimentoText = it.filter(Char::isDigit)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            CampoCartaoCinza(
                titulo = "Limite do cartão",
                valor = limiteText,
                placeholder = "0,00",
                onValueChange = { novoTexto ->
                    limiteText = formatarTextoMoeda(novoTexto)
                },
                modifier = Modifier.fillMaxWidth(),
                prefixo = "R$ "
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    val fechamento = fechamentoText.toIntOrNull() ?: 1
                    val vencimento = vencimentoText.toIntOrNull() ?: 1

                    // Remove vírgula/pontos e preserva apenas os centavos.
                    val limiteCentavos = limiteText
                        .filter { it.isDigit() }
                        .toLongOrNull()
                        ?: 0L

                    selecionada?.let { instituicao ->
                        onSave(
                            instituicao,
                            fechamento,
                            vencimento,
                            limiteCentavos
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Salvar cartão")
            }
        }
    }
}

@Composable
private fun CampoCartaoCinza(
    titulo: String,
    valor: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    prefixo: String? = null
) {
    Column(modifier = modifier) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = CorTextoPlaceholder
        )
        Spacer(Modifier.height(4.dp))

        androidx.compose.foundation.text.BasicTextField(
            value = valor,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = CorTextoPlaceholder,
                fontSize = 14.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, CorBordaCampo, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp)
                ) {
                    if (prefixo != null) {
                        Text(
                            text = prefixo,
                            color = CorTextoPlaceholder,
                            fontSize = 14.sp
                        )
                    }

                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (valor.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = CorTextoPlaceholder,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
    }
}

@Composable
fun TopBarEdicao(
    onVoltar: () -> Unit,
    titulo: String,
    descricao: String,
    bottomContent: @Composable () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(Color(0xFFF4F7F3))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                tonalElevation = 0.dp,
                border = BorderStroke(1.dp, Color(0xFFE6EFEA)),
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onVoltar() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color(0xFF2F6F62),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = titulo,
                    color = Color(0xFF123C3A),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = descricao,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        bottomContent()
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
private fun CategoriasContent(
    uiState: EdicaoUiState,
    onSelecionarSugerida: (CategoriaSugerida) -> Unit,
    onNomeAlterado: (String) -> Unit,
    onTetoAlterado: (String) -> Unit,
    onSalvar: () -> Unit,
    onAlternarAtivacao: (Categoria, Boolean) -> Unit,
    onRemoverCategoria: (Categoria) -> Unit,
    onSelecionarEmoji: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {

        val ativasCount = uiState.categorias.count { it.ativa }
        val inativasCount = uiState.categorias.count { !it.ativa }
        val comTetoCount = uiState.categorias.count { it.tetoMensal != null }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                StatCard(number = ativasCount, label = "Ativas")
            }
            Box(modifier = Modifier.weight(1f)) {
                StatCard(
                    number = inativasCount,
                    label = "Inativas",
                    numberColor = Color.Gray,
                    labelColor = Color.Gray
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                StatCard(number = comTetoCount, label = "Com teto")
            }
        }

        Spacer(Modifier.height(8.dp))

        var busca by remember { mutableStateOf("") }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Pesquisar",
                    tint = Color(0xFF9AA9A2),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.width(8.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (busca.isEmpty()) {
                        Text(
                            text = "Buscar categoria...",
                            color = Color(0xFF9AA9A2),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    androidx.compose.foundation.text.BasicTextField(
                        value = busca,
                        onValueChange = { busca = it },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color(0xFF9AA9A2),
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (busca.isNotBlank()) {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Limpar",
                        tint = Color(0xFF9AA9A2),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { busca = "" }
                    )
                }
            }
        }

        val ativosFiltrados = uiState.categorias
            .filter { it.ativa }
            .filter { busca.isBlank() || it.nome.contains(busca, ignoreCase = true) }

        val inativosFiltrados = uiState.categorias
            .filter { !it.ativa }
            .filter { busca.isBlank() || it.nome.contains(busca, ignoreCase = true) }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "ATIVAS",
            modifier = Modifier.padding(top = 6.dp),
            color = CorTextoEdicao,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (ativosFiltrados.isEmpty()) {
                Text("Nenhuma categoria encontrada.", color = CorTextoEdicao.copy(alpha = 0.6f))
            } else {
                ativosFiltrados.forEach { categoria ->
                    CategoriaPill(
                        categoria = categoria,
                        onClick = { },
                        onToggle = { ativa -> onAlternarAtivacao(categoria, ativa) },
                        onRemove = { onRemoverCategoria(categoria) }
                    )
                }
            }
        }

        InativasSection(
            inativas = inativosFiltrados,
            onToggle = { categoria, ativa -> onAlternarAtivacao(categoria, ativa) }
        )

        NovoCategoriaCard(
            uiState = uiState,
            onSelecionarSugerida = onSelecionarSugerida,
            onNomeAlterado = onNomeAlterado,
            onTetoAlterado = onTetoAlterado,
            onSalvar = onSalvar,
            onSelecionarEmoji = onSelecionarEmoji
        )
    }
}

@Composable
private fun StatCard(
    number: Int,
    label: String,
    numberColor: Color = CorEdicao,
    labelColor: Color = CorTextoEdicao.copy(alpha = 0.7f)
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = number.toString(),
                color = numberColor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = label,
                color = labelColor,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun CategoriaPill(
    categoria: Categoria,
    onClick: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onRemove: (() -> Unit)? = null
) {
    val pillHeight = 34.dp
    val pillShape = RoundedCornerShape(12.dp)

    val context = LocalContext.current
    val resId = remember(categoria.iconeChave) {
        context.resources.getIdentifier(categoria.iconeChave, "drawable", context.packageName)
    }

    Box(modifier = Modifier.wrapContentSize(), contentAlignment = Alignment.TopEnd) {
        Card(
            shape = pillShape,
            colors = CardDefaults.cardColors(containerColor = CorPillBg),
            modifier = Modifier
                .height(pillHeight)
                .widthIn(min = 88.dp, max = 220.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 12.dp)
            ) {
                when {
                    resId != 0 -> {
                        Icon(
                            painter = painterResource(id = resId),
                            contentDescription = categoria.nome,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    categoria.iconeChave.ehEmoji() -> {
                        Text(
                            text = categoria.iconeChave,
                            fontSize = 17.sp,
                            maxLines = 1
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = iconeCategoria(categoria.iconeChave),
                            contentDescription = categoria.nome,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = categoria.nome,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium
                )

                categoria.tetoMensal?.let { teto ->
                    Spacer(Modifier.width(8.dp))
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
                        modifier = Modifier.defaultMinSize(minHeight = 22.dp)
                    ) {
                        Text(
                            text = teto.formatarMoeda(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        onRemove?.let {
            Box(
                modifier = Modifier
                    .offset(x = 6.dp, y = (-6).dp)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF222222).copy(alpha = 0.7f))
                    .clickable { it() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remover",
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}

@Composable
private fun InativasSection(
    inativas: List<Categoria>,
    onToggle: (Categoria, Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val corTextoInativo = Color(0xFF7B8C86)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = !expanded
                }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "INATIVAS",
                color = corTextoInativo,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "•",
                color = corTextoInativo,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = inativas.size.toString(),
                color = corTextoInativo,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = if (expanded) {
                    Icons.Default.ArrowDropUp
                } else {
                    Icons.Default.ArrowDropDown
                },
                contentDescription = if (expanded) {
                    "Recolher categorias inativas"
                } else {
                    "Expandir categorias inativas"
                },
                tint = corTextoInativo,
                modifier = Modifier.size(20.dp)
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (inativas.isEmpty()) {
                    Text(
                        text = "Nenhuma categoria inativa.",
                        color = corTextoInativo
                    )
                } else {
                    inativas.forEach { categoria ->
                        CategoryInactivePill(
                            categoria = categoria,
                            onToggle = { ativa ->
                                onToggle(categoria, ativa)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun CategoryInactivePill(
    categoria: Categoria,
    onToggle: (Boolean) -> Unit
) {
    val corner = 12.dp
    val strokeWidthPx = 1.6f
    val dashIntervals = floatArrayOf(8f, 6f)
    val corInativa = Color(0xFF7B8C86)

    Box(
        modifier = Modifier
            .wrapContentSize()
            .height(36.dp)
            .clip(RoundedCornerShape(corner))
            .clickable {
                onToggle(true)
            }
            .drawBehind {
                val paint = Paint().apply {
                    color = corInativa.copy(alpha = 0.65f)
                    style = PaintingStyle.Stroke
                    strokeWidth = strokeWidthPx * density
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = dashIntervals,
                        phase = 0f
                    )
                }

                val radius = corner.toPx()

                drawIntoCanvas { canvas ->
                    canvas.drawRoundRect(
                        left = 0f,
                        top = 0f,
                        right = size.width,
                        bottom = size.height,
                        radiusX = radius,
                        radiusY = radius,
                        paint = paint
                    )
                }
            }
    ) {
        Row(
            modifier = Modifier.padding(
                start = 10.dp,
                end = 10.dp,
                top = 6.dp,
                bottom = 6.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                categoria.iconeChave.ehEmoji() -> {
                    Text(
                        text = categoria.iconeChave,
                        fontSize = 17.sp,
                        maxLines = 1
                    )
                }
                else -> {
                    Icon(
                        imageVector = iconeCategoria(categoria.iconeChave),
                        contentDescription = categoria.nome,
                        tint = corInativa,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = categoria.nome,
                color = corInativa,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun String.ehEmoji(): Boolean {
    return any { caractere ->
        caractere.code > 255
    }
}

@Composable
private fun NovoCategoriaCard(
    uiState: EdicaoUiState,
    onSelecionarSugerida: (CategoriaSugerida) -> Unit,
    onNomeAlterado: (String) -> Unit,
    onTetoAlterado: (String) -> Unit,
    onSalvar: () -> Unit,
    onSelecionarEmoji: (String) -> Unit
) {
    var mostrarPicker by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = CorCardClaro
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Nova categoria",
                color = CorTextoEdicao,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            androidx.compose.material3.HorizontalDivider(
                color = CorCardStat.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    val fundoEngrenagem = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFF0F5F1)
                        )
                    )

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(fundoEngrenagem)
                            .border(
                                width = 1.dp,
                                color = Color(0xFFE0E8E3),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                mostrarPicker = !mostrarPicker
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val emojiSelecionado = uiState.novoIconeCategoria

                        if (
                            emojiSelecionado.isNotBlank() &&
                            emojiSelecionado.any { caractere -> caractere.code > 255 }
                        ) {
                            Text(
                                text = emojiSelecionado,
                                fontSize = 26.sp,
                                maxLines = 1
                            )
                        } else {
                            Icon(
                                painter = painterResource(
                                    id = R.drawable.engrenagem
                                ),
                                contentDescription = "Escolher ícone da categoria",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    EmojiPickerDropdown(
                        expanded = mostrarPicker,
                        onDismiss = {
                            mostrarPicker = false
                        },
                        onSelect = { emoji ->
                            onSelecionarEmoji(emoji)
                            mostrarPicker = false
                        }
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedTextField(
                    value = uiState.novaCategoriaNome,
                    onValueChange = onNomeAlterado,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    placeholder = {
                        Text(
                            text = "Nome da categoria",
                            color = CorTextoPlaceholder
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CorTextoEdicao,
                        unfocusedTextColor = CorTextoEdicao,
                        focusedPlaceholderColor = CorTextoPlaceholder,
                        unfocusedPlaceholderColor = CorTextoPlaceholder,
                        focusedBorderColor = CorBordaCampo,
                        unfocusedBorderColor = CorBordaCampo,
                        cursorColor = CorEdicao
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val preSelecionadas = remember {
                categoriasSugeridas
                    .sortedBy { it.nome.length }
                    .take(4)
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                preSelecionadas.forEach { sugerida ->
                    val selecionada =
                        sugerida.nome == uiState.novaCategoriaNome

                    FilterChip(
                        selected = selecionada,
                        onClick = {
                            onSelecionarSugerida(sugerida)
                        },
                        label = {
                            Text(
                                text = sugerida.nome,
                                color = if (selecionada) {
                                    Color.White
                                } else {
                                    CorChipTexto
                                }
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selecionada) {
                                CorEdicao
                            } else {
                                CorBordaChip
                            }
                        ),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White,
                            selectedContainerColor = CorEdicao,
                            labelColor = CorChipTexto,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.novaCategoriaTetoTexto,
                onValueChange = onTetoAlterado,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                placeholder = {
                    Text(
                        text = "Teto mensal em R$ (opcional)",
                        color = CorTextoPlaceholder
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = CorTextoEdicao,
                    unfocusedTextColor = CorTextoEdicao,
                    focusedPlaceholderColor = CorTextoPlaceholder,
                    unfocusedPlaceholderColor = CorTextoPlaceholder,
                    focusedBorderColor = CorBordaCampo,
                    unfocusedBorderColor = CorBordaCampo,
                    cursorColor = CorEdicao
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSalvar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Adicionar categoria")
            }
        }
    }
}

@Composable
private fun EmojiPickerDropdown(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    val emojis = listOf(
        "🍔", "🍕", "🍣", "🛒", "💻",
        "🎮", "🎬", "🎁", "🏠", "❤️",
        "🏋️", "✈️", "🐶", "🏖️", "🎓",
        "🛠️", "🧾", "💡", "⚽", "🎧",
        "🛏️", "🚗", "🛍️", "💊", "🍿",
        "📦", "💰", "🎉", "🐾", "📚"
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFFE1E7E3)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            emojis.forEach { emoji ->
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF4F7F3))
                        .clickable {
                            onSelect(emoji)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )
                }
            }
        }
    }
}

@Composable
private fun ResumoCartoesCard(
    limiteDisponivelCentavos: Long = 0L,
    totalLimiteCentavos: Long = 0L,
    ativos: Int
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CorEdicao),
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "LIMITE TOTAL DISPONÍVEL",
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = limiteDisponivelCentavos.formatarMoeda(),
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$ativos ${if (ativos == 1) "cartão ativo" else "cartões ativos"}",
                    color = Color.White.copy(alpha = 0.95f),
                    style = MaterialTheme.typography.bodySmall
                )

                if (totalLimiteCentavos > 0L) {
                    Spacer(Modifier.width(12.dp))
                    Text(text = "•", color = Color.White.copy(alpha = 0.6f))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Limite total ${totalLimiteCentavos.formatarMoeda()}",
                        color = Color.White.copy(alpha = 0.95f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun CartaoDetalhado(
    cartao: com.example.controlegastos.domain.model.Cartao,
    disponivelCentavos: Long,
    usadoCentavos: Long,
    limiteCentavos: Long,
    ativo: Boolean,
    onAtivacaoAlterada: (Boolean) -> Unit,
    onSalvarDatas: (Int, Int) -> Unit
) {
    val corSubtitulo = CorTextoPlaceholder
    val corLabelCinza = CorTextoPlaceholder
    val progresso = remember(disponivelCentavos, usadoCentavos) {
        val total = (usadoCentavos + disponivelCentavos).coerceAtLeast(1L)
        (usadoCentavos.toDouble() / total.toDouble()).toFloat().coerceIn(0f, 1f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val context = LocalContext.current
                val logoRes = remember(cartao.marcaChave) {
                    val nomeArquivo = if (cartao.marcaChave.contains("caixa", ignoreCase = true) || cartao.marcaChave == "cx") {
                        "cef"
                    } else {
                        cartao.marcaChave
                    }
                    context.resources.getIdentifier(nomeArquivo, "drawable", context.packageName)
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF0F4EF)),
                    contentAlignment = Alignment.Center
                ) {
                    if (logoRes != 0) {
                        val iconeTint = if (cartao.marcaChave == "picpay") Color(0xFF01C66A) else Color.Unspecified
                        Icon(
                            painter = painterResource(id = logoRes),
                            contentDescription = cartao.nome,
                            tint = iconeTint,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = cartao.nome.take(2).uppercase(),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = cartao.nome,
                            color = CorTextoEdicao,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.width(8.dp))

                        if (ativo) {
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE9FBF0)),
                                modifier = Modifier.height(22.dp)
                            ) {
                                Text(
                                    text = "ATIVO",
                                    color = CorEdicao,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Fecha dia ${cartao.diaFechamento} • Vence dia ${cartao.diaVencimento}",
                        color = corSubtitulo,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.width(8.dp))

                Box(
                    modifier = Modifier.size(width = 46.dp, height = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Switch(
                        checked = ativo,
                        onCheckedChange = { novo -> onAtivacaoAlterada(novo) },
                        modifier = Modifier
                            .then(Modifier.size(46.dp, 28.dp))
                            .scale(0.9f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "DISPONÍVEL", color = corLabelCinza, style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = disponivelCentavos.formatarMoeda(),
                        color = CorEdicao,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "USADO", color = corLabelCinza, style = MaterialTheme.typography.labelSmall)
                    Text(text = usadoCentavos.formatarMoeda(), color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progresso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(6.dp)),
                trackColor = Color(0xFFECEFF0),
                color = CorEdicao
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Limite ${limiteCentavos.formatarMoeda()}",
                color = corLabelCinza,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(10.dp))

            Divider()

            Spacer(Modifier.height(6.dp))

            var editarExpandido by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { editarExpandido = !editarExpandido }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✏️",
                    fontSize = 16.sp
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Editar ciclo de cobrança",
                    color = CorEdicao,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = if (editarExpandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (editarExpandido) "Recolher" else "Expandir",
                    tint = corLabelCinza
                )
            }

            if (editarExpandido) {
                Spacer(Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                ) {
                    var fechamentoText by remember { mutableStateOf(cartao.diaFechamento.toString()) }
                    var vencimentoText by remember { mutableStateOf(cartao.diaVencimento.toString()) }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Fecha no dia",
                                color = corLabelCinza,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(Modifier.height(6.dp))
                            androidx.compose.foundation.text.BasicTextField(
                                value = fechamentoText,
                                onValueChange = { fechamentoText = it.filter { ch -> ch.isDigit() } },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = CorTextoEdicao,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .border(1.dp, Color(0xFFE1E7E3), RoundedCornerShape(8.dp))
                                    .background(Color.White, RoundedCornerShape(8.dp)),
                                decorationBox = { innerTextField ->
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        innerTextField()
                                    }
                                }
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Vence no dia",
                                color = corLabelCinza,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(Modifier.height(6.dp))
                            androidx.compose.foundation.text.BasicTextField(
                                value = vencimentoText,
                                onValueChange = { vencimentoText = it.filter { ch -> ch.isDigit() } },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = CorTextoEdicao,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .border(1.dp, Color(0xFFE1E7E3), RoundedCornerShape(8.dp))
                                    .background(Color.White, RoundedCornerShape(8.dp)),
                                decorationBox = { innerTextField ->
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        innerTextField()
                                    }
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val f = fechamentoText.toIntOrNull() ?: cartao.diaFechamento
                            val v = vencimentoText.toIntOrNull() ?: cartao.diaVencimento
                            onSalvarDatas(f, v)
                            editarExpandido = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = CorEdicao
                        )
                    ) {
                        Text("Salvar alterações", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CartaoInativoRow(
    cartao: com.example.controlegastos.domain.model.Cartao,
    onAtivar: (Boolean) -> Unit,
    onLongDelete: () -> Unit
) {
    val corInativa = Color(0xFF7B8C86)
    val context = LocalContext.current
    val logoRes = remember(cartao.marcaChave) {
        val nomeArquivo = if (cartao.marcaChave.contains("caixa", ignoreCase = true) || cartao.marcaChave == "cx") {
            "cef"
        } else {
            cartao.marcaChave
        }
        context.resources.getIdentifier(nomeArquivo, "drawable", context.packageName)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .combinedClickable(
                onClick = { onAtivar(true) },
                onLongClick = onLongDelete
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F4EF)),
            contentAlignment = Alignment.Center
        ) {
            if (logoRes != 0) {
                val iconeTint = if (cartao.marcaChave == "picpay") Color(0xFF01C66A) else Color.Unspecified
                Icon(
                    painter = painterResource(id = logoRes),
                    contentDescription = cartao.nome,
                    tint = iconeTint,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = cartao.nome.take(1).uppercase(),
                    color = corInativa,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cartao.nome,
                color = CorTextoEdicao,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Toque para ativar",
                color = corInativa,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Box(
            modifier = Modifier.size(width = 46.dp, height = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Switch(
                checked = false,
                onCheckedChange = onAtivar,
                modifier = Modifier
                    .size(46.dp, 28.dp)
                    .scale(0.9f)
            )
        }
    }
}

@Composable
private fun ConfirmacaoExcluirDialog(
    cartaoNome: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Excluir cartão") },
        text = {
            Text(
                "Tem certeza que deseja excluir o cartão \"$cartaoNome\"? " +
                        "Essa ação não poderá ser desfeita."
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Excluir", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun EditorDatasCartao(
    uiState: EdicaoUiState,
    onDiasAlterados: (String, String) -> Unit,
    onSalvar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CorEdicao.copy(alpha = 0.10f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Datas da fatura",
                color = CorTextoEdicao,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = uiState.diaFechamentoTexto,
                    onValueChange = { novo -> onDiasAlterados(novo, uiState.diaVencimentoTexto) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Fecha dia") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.diaVencimentoTexto,
                    onValueChange = { novo -> onDiasAlterados(uiState.diaFechamentoTexto, novo) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Vence dia") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            Button(onClick = onSalvar, modifier = Modifier.fillMaxWidth()) {
                Text("Salvar datas")
            }
        }
    }
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
private fun AbasEdicao(
    secaoSelecionada: Int,
    onSelecionarSecao: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE6EFEA)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AbasEdicaoItem(
                texto = "Categorias",
                icone = R.drawable.tags,
                selecionada = secaoSelecionada == 0,
                onClick = { onSelecionarSecao(0) },
                modifier = Modifier.weight(1f)
            )

            AbasEdicaoItem(
                texto = "Cartões",
                icone = R.drawable.card,
                selecionada = secaoSelecionada == 1,
                onClick = { onSelecionarSecao(1) },
                modifier = Modifier.weight(1f)
            )

            AbasEdicaoItem(
                texto = "Saldo",
                icone = R.drawable.bank,
                selecionada = secaoSelecionada == 2,
                onClick = { onSelecionarSecao(2) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AbasEdicaoItem(
    texto: String,
    @DrawableRes icone: Int,
    selecionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (selecionada) CorEdicao else Color.Transparent
    val contentColor = if (selecionada) Color.White else CorTextoEdicao

    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = icone),
                contentDescription = texto,
                tint = Color.Unspecified,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = texto,
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selecionada) FontWeight.SemiBold else FontWeight.Medium
            )
        }
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