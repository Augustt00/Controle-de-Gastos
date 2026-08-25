package com.example.controlegastos.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.controlegastos.domain.repository.DespesaRepository
import com.example.controlegastos.notification.NotificadorVencimentos
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZoneOffset

@HiltWorker
class VerificarVencimentosWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val despesaRepository: DespesaRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val hoje = LocalDate.now(ZoneOffset.UTC)

            val dataInicioEpoch = hoje
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()

            val dataFimEpoch = hoje
                .plusDays(7)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()

            val pendencias = despesaRepository
                .observarPendenciasDetalhadas(
                    dataInicioEpoch = dataInicioEpoch,
                    dataFimEpoch = dataFimEpoch
                )
                .first()

            NotificadorVencimentos(appContext).apply {
                criarCanal()
                mostrarVencimentos(pendencias)
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}