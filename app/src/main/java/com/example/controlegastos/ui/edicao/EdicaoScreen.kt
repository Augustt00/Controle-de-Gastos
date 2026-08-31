@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class
)

package com.example.controlegastos.ui.edicao

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlegastos.R
import com.example.controlegastos.domain.model.Categoria
import com.example.controlegastos.domain.model.ContaSaldo
import com.example.controlegastos.domain.model.TipoContaSaldo
import androidx.compose.material3.DropdownMenu
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.sp

// ====== CORES / CONSTANTES DE ESTILO (ajuste HEX se quiser 1:1) ======
private val CorEdicao = Color(0xFF2F6F62) // verde principal
private val CorTextoEdicao = Color(0xFF123C3A)
private val CorFundo = Color(0xFFF4F7F3)
private val CorCardClaro = Color(0xFFFFFFFF)
private val CorCardStat = Color(0xFFF0F4EF)
private val CorPillBg = Color(0xFF1B5B3A) // cor das pills de categoria (escura)
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
                                onSelecionarEmoji = { chaveOrEmoji ->
                                    viewModel.atualizarIconeCategoria(chaveOrEmoji)
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

                        instituicoesPredefinidas.forEach { instituicao ->
                            item(key = instituicao.chave) {
                                val cartao = uiState.cartoes.firstOrNull {
                                    it.marcaChave == instituicao.chave
                                }
                                LinhaCartaoPredefinido(
                                    instituicao = instituicao,
                                    cartao = cartao,
                                    ativo = cartao?.ativo == true,
                                    onAtivacaoAlterada = { ativo ->
                                        viewModel.alterarAtivacaoCartao(instituicao, ativo)
                                    },
                                    onEditar = { cartaoExistente ->
                                        viewModel.editarConfiguracaoCartao(cartaoExistente)
                                    }
                                )
                            }

                            if (uiState.cartaoEmEdicao?.marcaChave == instituicao.chave) {
                                item(key = "${instituicao.chave}_editor") {
                                    EditorDatasCartao(
                                        uiState = uiState,
                                        onDiasAlterados = viewModel::atualizarDiasCartao,
                                        onSalvar = viewModel::salvarConfiguracaoCartao
                                    )
                                }
                            }
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
                                onAtivacaoAlterada = { ativa -> viewModel.alterarAtivacaoConta(conta, ativa) }
                            )
                        }
                    }
                }
            }
        }
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

        // ===== Estatísticas (3 cards) + Busca funcional =====
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
                        onClick = { /* abrir edição se desejar */ },
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
                    /*
                     * Caso 1:
                     * A categoria possui um arquivo drawable, como um Vector Asset XML.
                     */
                    resId != 0 -> {
                        Icon(
                            painter = painterResource(id = resId),
                            contentDescription = categoria.nome,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    /*
                     * Caso 2:
                     * O usuário selecionou um emoji no seletor.
                     */
                    categoria.iconeChave.ehEmoji() -> {
                        Text(
                            text = categoria.iconeChave,
                            fontSize = 17.sp,
                            maxLines = 1
                        )
                    }

                    /*
                     * Caso 3:
                     * Categoria criada com chave interna, como "lazer",
                     * "alimentacao", "viagem" etc.
                     */
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
        /*
         * Cabeçalho:
         * INATIVAS • 3 ▲
         *
         * Não usamos Arrangement.SpaceBetween.
         * Assim, a seta fica logo após o número,
         * em vez de ficar no extremo direito da tela.
         */
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

            /*
             * Ponto menor e cinza.
             */
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
                /*
                 * Ao tocar na categoria inativa:
                 * ativa = true
                 * portanto ela sai de INATIVAS
                 * e passa automaticamente para ATIVAS.
                 */
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

            /*
             * Não coloque ArrowDropDown aqui.
             *
             * Agora a categoria inteira é clicável:
             * Educação, Lazer, Saúde etc.
             * Ao tocar, ela é reativada.
             */
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

            /*
             * Linha 1:
             * SVG da engrenagem + campo Nome da categoria.
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                /*
                 * Box externo:
                 * mantém o menu de emojis ancorado no botão da engrenagem.
                 */
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

                    /*
                     * Deve existir somente UM EmojiPickerDropdown.
                     */
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

                /*
                 * Campo Nome:
                 * weight(1f) ocupa apenas o espaço restante da Row.
                 */
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

            /*
             * Sugestões: Pets, Saúde, Lazer e Viagem.
             */
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

            /*
             * Campo Teto mensal:
             * fica abaixo das sugestões, fora da Row da engrenagem.
             */
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
private fun LinhaCartaoPredefinido(
    instituicao: InstituicaoPredefinida,
    cartao: com.example.controlegastos.domain.model.Cartao?,
    ativo: Boolean,
    onAtivacaoAlterada: (Boolean) -> Unit,
    onEditar: (com.example.controlegastos.domain.model.Cartao) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BadgeInstituicao(instituicao)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        instituicao.nome,
                        color = CorTextoEdicao,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        if (ativo) "Cartão ativo" else "Cartão desativado",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(checked = ativo, onCheckedChange = onAtivacaoAlterada)
            }
            if (ativo && cartao != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Fecha dia ${cartao.diaFechamento} • Vence dia ${cartao.diaVencimento}",
                    color = CorTextoEdicao,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Toque para editar os dias",
                    modifier = Modifier.clickable { onEditar(cartao) },
                    color = CorEdicao,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
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
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE6EFEA)),
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