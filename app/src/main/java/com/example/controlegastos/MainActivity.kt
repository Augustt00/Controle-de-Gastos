package com.example.controlegastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.controlegastos.ui.ControleGastosApp
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.example.controlegastos.ui.theme.ControleGastosTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val solicitadorPermissaoNotificacao =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) {
                    // Não precisamos fazer nada aqui.
                    // O app continuará funcionando mesmo se a permissão for negada.
                }

            LaunchedEffect(Unit) {
                val precisaSolicitarPermissao =
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED

                if (precisaSolicitarPermissao) {
                    solicitadorPermissaoNotificacao.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }

            ControleGastosTheme {
                ControleGastosApp()
            }
        }
    }
}