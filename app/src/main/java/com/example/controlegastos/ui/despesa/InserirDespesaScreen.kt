@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class
)

package com.example.controlegastos.ui.despesa

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar

import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.controlegastos.domain.model.TipoLancamento
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.window.Popup
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown


@Composable
fun InserirDespesaScreen(
    onVoltar: () -> Unit,
    viewModel: InserirDespesaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()

        viewModel.uiState.collectLatest { estado ->
            estado.mensagemErro?.let { mensagem ->
                snackbarHostState.showSnackbar(mensagem)
            }

            if (estado.despesaSalvaComSucesso) {
                snackbarHostState.showSnackbar("Despesa salva com sucesso.")
                viewModel.consumirSucesso()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.padding(start = 8.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Nova despesa",
                            color = Color(0xFF0A1B3F),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 22.sp
                            )
                        )
                        Text(
                            text = "Insira suas despesas aqui",
                            color = Color(0xFF6B7280),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                navigationIcon = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        tonalElevation = 0.dp,
                        border = BorderStroke(1.dp, Color(0xFFE6EFEA)),
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(44.dp)
                            .clickable { onVoltar() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = Color(0xFF0A1B3F),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        FormularioDespesa(
            uiState = uiState,
            focusRequester = focusRequester,
            onValorAlterado = viewModel::atualizarValor,
            onDescricaoAlterada = viewModel::atualizarDescricao,
            onCategoriaSelecionada = viewModel::selecionarCategoria,
            onCartaoSelecionado = viewModel::selecionarCartao,
            onDataSelecionada = viewModel::atualizarDataCompra,
            onTipoLancamentoAlterado = viewModel::alterarTipoLancamento,
            onQuantidadeParcelasAlterada = viewModel::atualizarQuantidadeParcelas,
            onSalvar = viewModel::salvarDespesa,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun FormularioDespesa(
    uiState: InserirDespesaUiState,
    focusRequester: FocusRequester,
    onValorAlterado: (String) -> Unit,
    onDescricaoAlterada: (String) -> Unit,
    onCategoriaSelecionada: (Int) -> Unit,
    onCartaoSelecionado: (Int?) -> Unit,
    onDataSelecionada: (LocalDate) -> Unit,
    onTipoLancamentoAlterado: (TipoLancamento) -> Unit,
    onQuantidadeParcelasAlterada: (String) -> Unit,
    onSalvar: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ValorCard(
                valorTexto = uiState.valorTexto,
                onValorAlterado = onValorAlterado,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (uiState.tipoLancamento == TipoLancamento.PARCELADA) {
            item {
                OutlinedTextField(
                    value = uiState.quantidadeParcelas.toString(),
                    onValueChange = onQuantidadeParcelasAlterada,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Quantidade de parcelas") },
                    suffix = { Text("x") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }

        if (uiState.tipoLancamento == TipoLancamento.FIXA) {
            item {
                Text(
                    text = "Esta despesa será repetida por 12 meses.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            DetalhesCard(
                descricao = uiState.descricao,
                onDescricaoAlterada = onDescricaoAlterada,
                dataSelecionada = uiState.dataCompra,
                onDataSelecionada = onDataSelecionada,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Column {
                Text(
                    text = "CATEGORIA",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                SeletorCategoria(
                    uiState = uiState,
                    onCategoriaSelecionada = onCategoriaSelecionada
                )
            }
        }

        item {
            Button(
                onClick = onSalvar,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.salvando && !uiState.carregandoCategorias
            ) {
                if (uiState.salvando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (uiState.tipoLancamento == TipoLancamento.PARCELADA) {
                            "Salvar compra parcelada"
                        } else {
                            "Salvar despesa"
                        }
                    )
                }
            }
        }
    }
}



@Composable
private fun ValorCard(
    valorTexto: String,
    onValorAlterado: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val valorFormatado = valorTexto.formatarMoedaComCursor()
    val vazio = valorTexto.isBlank() || (valorTexto.toLongOrNull() ?: 0L) == 0L
    val textColor = if (vazio) Color(0xFF7B9B8A) else Color.White

    Box(
        modifier = modifier
            .height(104.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF225F44))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFF296A4D),
                radius = size.height * 0.9f,
                center = Offset(size.width * 0.85f, size.height * 0.1f)
            )
            drawCircle(
                color = Color(0xFF296A4D),
                radius = size.height * 0.7f,
                center = Offset(size.width, size.height * 0.9f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "VALOR DA DESPESA",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = textColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            BasicTextField(
                value = TextFieldValue(
                    text = valorFormatado,
                    selection = TextRange(valorFormatado.length)
                ),
                onValueChange = { novo ->
                    onValorAlterado(novo.text.filter(Char::isDigit))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = LocalTextStyle.current.copy(
                    color = textColor,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                ),
                cursorBrush = SolidColor(Color.White),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
private fun SeletorCategoria(
    uiState: InserirDespesaUiState,
    onCategoriaSelecionada: (Int) -> Unit
) {
    var aberto by remember { mutableStateOf(false) }

    val categoriasAtivas = remember(uiState.categorias) {
        uiState.categorias.filter { it.ativa }
    }

    val categoriaSelecionada = uiState.categoriaSelecionada
    val context = LocalContext.current
    val formatoCaixa = RoundedCornerShape(14.dp)
    val corBordaNormal = Color(0xFFD1D5DB)
    val corBordaAberta = Color(0xFF2962FF)

    fun obterResourceIcone(chave: String): Int {
        return context.resources.getIdentifier(
            chave.lowercase(),
            "drawable",
            context.packageName
        )
    }

    @Composable
    fun IconeCategoriaSelecionada(
        iconeChave: String,
        nomeCategoria: String
    ) {
        val resId = remember(iconeChave) {
            obterResourceIcone(iconeChave)
        }

        when {
            resId != 0 -> {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = nomeCategoria,
                    modifier = Modifier.size(24.dp)
                )
            }

            iconeChave.ehEmoji() -> {
                Text(
                    text = iconeChave,
                    fontSize = 22.sp
                )
            }

            else -> {
                Icon(
                    imageVector = iconeCategoria(iconeChave),
                    contentDescription = nomeCategoria,
                    tint = categoriaSelecionada?.corHex?.toComposeColor()
                        ?: Color(0xFF225F44),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = formatoCaixa,
                    clip = false
                )
                .clip(formatoCaixa)
                .background(Color.White)
                .border(
                    width = if (aberto) 2.dp else 1.dp,
                    color = if (aberto) corBordaAberta else corBordaNormal,
                    shape = formatoCaixa
                )
                .clickable { aberto = !aberto }
                .padding(
                    start = 20.dp,
                    end = 16.dp,
                    top = 14.dp,
                    bottom = 14.dp
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (categoriaSelecionada != null) {
                    IconeCategoriaSelecionada(
                        iconeChave = categoriaSelecionada.iconeChave ?: "",
                        nomeCategoria = categoriaSelecionada.nome
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                }

                Text(
                    text = categoriaSelecionada?.nome
                        ?: "Selecionar categoria...",
                    color = if (categoriaSelecionada == null) {
                        Color(0xFF9CA3AF)
                    } else {
                        Color(0xFF1F2937)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (aberto) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = if (aberto) {
                        "Fechar categorias"
                    } else {
                        "Abrir categorias"
                    },
                    tint = Color(0xFF374151),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (aberto) {
            Popup(
                alignment = Alignment.TopStart,
                onDismissRequest = { aberto = false },
                properties = PopupProperties(
                    focusable = true,
                    dismissOnClickOutside = true,
                    dismissOnBackPress = true
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 68.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(14.dp),
                            clip = false
                        )
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(14.dp)
                        )
                ) {
                    if (categoriasAtivas.isEmpty()) {
                        Text(
                            text = "Nenhuma categoria ativa",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            color = Color(0xFF6B7280),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        categoriasAtivas.forEachIndexed { index, categoria ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onCategoriaSelecionada(categoria.id)
                                        aberto = false
                                    }
                                    .padding(
                                        horizontal = 24.dp,
                                        vertical = 14.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconeCategoriaSelecionada(
                                    iconeChave = categoria.iconeChave ?: "",
                                    nomeCategoria = categoria.nome
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = categoria.nome,
                                    color = Color(0xFF1F2937),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            if (index < categoriasAtivas.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 18.dp),
                                    thickness = 1.dp,
                                    color = Color(0xFFE5E7EB)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun DetalhesCard(
    descricao: String,
    onDescricaoAlterada: (String) -> Unit,
    dataSelecionada: LocalDate,
    onDataSelecionada: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarCalendario by remember { mutableStateOf(false) }

    val hoje = LocalDate.now()
    val ontem = hoje.minusDays(1)
    val selecionadoHoje = dataSelecionada == hoje
    val selecionadoOntem = dataSelecionada == ontem

    var textoData by remember(dataSelecionada) {
        mutableStateOf(
            String.format(
                "%02d%02d%04d",
                dataSelecionada.dayOfMonth,
                dataSelecionada.monthValue,
                dataSelecionada.year
            )
        )
    }

    val labelData = when {
        selecionadoHoje -> "Hoje"
        selecionadoOntem -> "Ontem"
        else -> dataSelecionada.format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "DETALHES",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF6B7280),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Surface(
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 1.dp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Ícone de edição",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicTextField(
                        value = descricao,
                        onValueChange = onDescricaoAlterada,
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFF1F2937)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (descricao.isEmpty()) {
                                Text(
                                    text = "Descrição (ex: Almoço no Outback)",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 16.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                HorizontalDivider(
                    color = Color(0xFFF3F4F6),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = "Ícone de calendário",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = labelData,
                        color = Color(0xFF1F2937),
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selecionadoHoje) {
                                Color(0xFF225F44)
                            } else {
                                Color(0xFFF3F4F6)
                            }
                        ) {
                            Text(
                                text = "Hoje",
                                color = if (selecionadoHoje) {
                                    Color.White
                                } else {
                                    Color(0xFF4B5563)
                                },
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .clickable { onDataSelecionada(hoje) }
                                    .padding(
                                        horizontal = 14.dp,
                                        vertical = 8.dp
                                    )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selecionadoOntem) {
                                Color(0xFF225F44)
                            } else {
                                Color(0xFFF3F4F6)
                            }
                        ) {
                            Text(
                                text = "Ontem",
                                color = if (selecionadoOntem) {
                                    Color.White
                                } else {
                                    Color(0xFF4B5563)
                                },
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .clickable { onDataSelecionada(ontem) }
                                    .padding(
                                        horizontal = 14.dp,
                                        vertical = 8.dp
                                    )
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = textoData,
                        onValueChange = { novo ->
                            val digits = novo.filter { it.isDigit() }.take(8)
                            textoData = digits

                            if (digits.length == 8) {
                                val d = digits.substring(0, 2).toIntOrNull()
                                val m = digits.substring(2, 4).toIntOrNull()
                                val y = digits.substring(4, 8).toIntOrNull()

                                if (d != null && m != null && y != null) {
                                    try {
                                        onDataSelecionada(LocalDate.of(y, m, d))
                                    } catch (_: Exception) {
                                        // Data inválida: aguarda nova entrada.
                                    }
                                }
                            }
                        },
                        visualTransformation = DateTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedBorderColor = Color(0xFF225F44)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                        trailingIcon = {
                            IconButton(
                                onClick = { mostrarCalendario = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarToday,
                                    contentDescription = "Selecionar no calendário",
                                    tint = Color(0xFF6B7280)
                                )
                            }
                        }
                    )

                    if (mostrarCalendario) {
                        Popup(
                            alignment = Alignment.TopStart,
                            properties = PopupProperties(
                                focusable = true,
                                dismissOnClickOutside = true
                            ),
                            onDismissRequest = {
                                mostrarCalendario = false
                            }
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                shadowElevation = 6.dp,
                                color = Color.White,
                                modifier = Modifier
                                    .padding(top = 64.dp)
                                    .padding(horizontal = 16.dp)
                            ) {
                                Column {
                                    val datePickerState = rememberDatePickerState(
                                        initialSelectedDateMillis = dataSelecionada
                                            .atStartOfDay(ZoneOffset.UTC)
                                            .toInstant()
                                            .toEpochMilli()
                                    )

                                    LaunchedEffect(
                                        datePickerState.selectedDateMillis
                                    ) {
                                        datePickerState.selectedDateMillis?.let { millis ->
                                            val novaData = Instant.ofEpochMilli(millis)
                                                .atZone(ZoneOffset.UTC)
                                                .toLocalDate()

                                            if (novaData != dataSelecionada) {
                                                onDataSelecionada(novaData)
                                                mostrarCalendario = false
                                            }
                                        }
                                    }

                                    DatePicker(
                                        state = datePickerState,
                                        title = null,
                                        headline = null,
                                        showModeToggle = false,
                                        colors = DatePickerDefaults.colors(
                                            containerColor = Color.White,
                                            selectedDayContainerColor = Color(0xFF225F44),
                                            todayDateBorderColor = Color(0xFF225F44),
                                            todayContentColor = Color(0xFF225F44)
                                        )
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                            .padding(bottom = 16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        TextButton(
                                            onClick = {
                                                mostrarCalendario = false
                                            }
                                        ) {
                                            Text(
                                                "Limpar",
                                                color = Color(0xFF1976D2)
                                            )
                                        }

                                        TextButton(
                                            onClick = {
                                                onDataSelecionada(LocalDate.now())
                                                mostrarCalendario = false
                                            }
                                        ) {
                                            Text(
                                                "Hoje",
                                                color = Color(0xFF1976D2)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class DateTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(8)
        var out = ""

        for (i in digits.indices) {
            out += digits[i]
            if (i == 1 || i == 3) out += "/"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 3) return offset + 1
                if (offset <= 8) return offset + 2
                return 10
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                if (offset <= 10) return offset - 2
                return 8
            }
        }

        return TransformedText(
            AnnotatedString(out),
            offsetMapping
        )
    }
}


private fun String?.ehEmoji(): Boolean {
    return this?.any { caractere -> caractere.code > 255 } ?: false
}

private fun String.toComposeColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (_: IllegalArgumentException) {
        Color(0xFF225F44)
    }
}

private fun iconeCategoria(chave: String): ImageVector {
    return when (chave.lowercase()) {
        "alimentacao", "alimentação", "fastfood" -> Icons.Default.Fastfood
        "loja_online" -> Icons.Default.ShoppingBag
        "streaming" -> Icons.Default.Videocam
        "academia" -> Icons.Default.FitnessCenter
        "transporte" -> Icons.Default.DirectionsCar
        "moradia" -> Icons.Default.Home
        "saude", "saúde" -> Icons.Default.Favorite
        "educacao", "educação" -> Icons.Default.School
        "lazer" -> Icons.Default.Celebration
        "assinaturas" -> Icons.Default.Subscriptions
        "pets" -> Icons.Default.Pets
        "presentes" -> Icons.Default.CardGiftcard
        "viagem" -> Icons.Default.Flight
        "contas" -> Icons.Default.ReceiptLong
        else -> Icons.Default.Category
    }
}


private fun String.formatarMoedaComCursor(): String {
    val valorCentavos = toLongOrNull() ?: 0L

    return "R$ %d,%02d".format(
        valorCentavos / 100,
        valorCentavos % 100
    )
}