package com.example.controlegastos.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.controlegastos.R
import com.example.controlegastos.domain.model.DespesaDetalhada

class NotificadorVencimentos(
    private val context: Context
) {

    fun criarCanal() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            CANAL_ID,
            "Vencimentos",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Avisos de despesas próximas do vencimento"
        }

        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        )

        notificationManager.createNotificationChannel(channel)
    }

    fun mostrarVencimentos(despesas: List<DespesaDetalhada>) {
        if (despesas.isEmpty()) {
            return
        }

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val titulo = if (despesas.size == 1) {
            "1 despesa próxima do vencimento"
        } else {
            "${despesas.size} despesas próximas do vencimento"
        }

        val linhas = despesas
            .take(5)
            .joinToString(separator = "\n") { despesa ->
                "• ${despesa.descricao}"
            }

        val notificacao = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titulo)
            .setContentText("Toque para conferir suas pendências.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(linhas)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat
            .from(context)
            .notify(NOTIFICACAO_ID, notificacao)
    }

    companion object {
        const val CANAL_ID = "vencimentos"
        private const val NOTIFICACAO_ID = 1001
    }
}