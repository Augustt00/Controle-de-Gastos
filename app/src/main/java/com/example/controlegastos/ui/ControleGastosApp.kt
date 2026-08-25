package com.example.controlegastos.ui

import androidx.compose.runtime.Composable
import com.example.controlegastos.ui.navigation.AppNavigator
import com.example.controlegastos.ui.theme.ControleGastosTheme

@Composable
fun ControleGastosApp() {
    ControleGastosTheme {
        AppNavigator()
    }
}