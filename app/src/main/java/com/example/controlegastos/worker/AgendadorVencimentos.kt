package com.example.controlegastos.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object AgendadorVencimentos {

    fun agendar(context: Context) {
        val requisicao = PeriodicWorkRequestBuilder<
                VerificarVencimentosWorker
                >(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        ).build()

        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                NOME_TRABALHO,
                ExistingPeriodicWorkPolicy.UPDATE,
                requisicao
            )
    }

    private const val NOME_TRABALHO = "verificar_vencimentos_diariamente"
}