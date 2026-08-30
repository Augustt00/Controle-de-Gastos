@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class
)

package com.example.controlegastos.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import com.example.controlegastos.ui.categoria.CategoriaScreen
import com.example.controlegastos.ui.dashboard.DashboardScreen
import com.example.controlegastos.ui.despesa.InserirDespesaScreen
import com.example.controlegastos.ui.pendencias.PendenciasScreen
import com.example.controlegastos.ui.timeline.TimelineScreen
import com.example.controlegastos.ui.settings.SettingsScreen
import com.example.controlegastos.ui.gastos.GastosScreen
import com.example.controlegastos.ui.edicao.EdicaoScreen
import com.example.controlegastos.ui.transacoes.TransacoesScreen


@Composable
fun AppNavigator() {
    Navigator(
        screen = DashboardVoyagerScreen()
    )
}

private class DashboardVoyagerScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        DashboardScreen(
            onGerenciarCategorias = {
                navigator?.push(CategoriasScreen())
            },
            onAdicionarDespesa = {
                navigator?.push(InserirDespesaVoyagerScreen())
            },
            onVerTodasTransacoes = {
                navigator?.push(TransacoesVoyagerScreen())
            },
            onVerProjecoes = {
                navigator?.push(TimelineVoyagerScreen())
            },
            onVerPendencias = {
                navigator?.push(GastosVoyagerScreen())
            },
            onAbrirConfiguracoes = {
                navigator?.push(SettingsVoyagerScreen())
            },
            onAbrirCartoes = {
                navigator?.push(EdicaoVoyagerScreen())
            }
        )
    }
}

private class CategoriasScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        CategoriaScreen(
            onVoltar = {
                navigator?.pop()
            }
        )
    }
}

private class InserirDespesaVoyagerScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        InserirDespesaScreen(
            onVoltar = {
                navigator?.pop()
            }
        )
    }
}

private class EdicaoVoyagerScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        EdicaoScreen(
            onVoltar = {
                navigator?.pop()
            }
        )
    }
}

private class TransacoesVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        TransacoesScreen(onVoltar = { navigator?.pop() })
    }
}

private class TimelineVoyagerScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        TimelineScreen(
            onVoltar = {
                navigator?.pop()
            }
        )
    }
}

private class SettingsVoyagerScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        SettingsScreen(
            onVoltar = {
                navigator?.pop()
            }
        )
    }
}


private class CartoesVoyagerScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Cartão") },
                    navigationIcon = {
                        TextButton(onClick = { navigator?.pop() }) {
                            Text(text = "Voltar")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "O gerenciamento de cartões será a próxima melhoria.",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

private class PendenciasVoyagerScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        PendenciasScreen(
            onVoltar = {
                navigator?.pop()
            }
        )
    }
}

private class GastosVoyagerScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        GastosScreen(
            onVoltar = {
                navigator?.pop()
            }
        )
    }
}