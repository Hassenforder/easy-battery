package fr.uha.hassenforder.easy.battery.worker

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import fr.uha.hassenforder.easy.battery.model.Battery
import fr.uha.hassenforder.easy.battery.model.Source
import fr.uha.hassenforder.easy.battery.repository.BatteryRepository

@HiltWorker
class PeriodicBatteryWorker @AssistedInject constructor (
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val batteryRepository: BatteryRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        doCapture()
        return Result.success()
    }

    private suspend fun doCapture() {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            appContext.registerReceiver(null, ifilter)
        }
        val capture = Battery.create(Source.PERIODIC, batteryStatus)
        if (capture != null) batteryRepository.create(capture)
    }

}
