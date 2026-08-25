package com.example.controlegastos.ui.navigation

sealed class AppScreen(
    val route: String
) {
    data object Inicio : AppScreen("inicio")
    data object Categorias : AppScreen("categorias")
    data object NovaDespesa : AppScreen("nova_despesa")
}