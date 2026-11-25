package fr.uha.hassenforder.easy.battery.provider

import androidx.annotation.WorkerThread
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import fr.uha.hassenforder.easy.battery.datastore.SettingsDataStore
import fr.uha.hassenforder.easy.battery.worker.PeriodicBatteryWorker
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class WorkerProvider (
    private val dispatcher: CoroutineDispatcher,
    private val settingsDataStore : SettingsDataStore,
    private val workManager : WorkManager,
) {

    val jobsInfo : Flow<List<WorkInfo>> =
        workManager.getWorkInfosByTagFlow(PERIODIC_WORK_TAG)

    private suspend fun createPeriodicRequest(): PeriodicWorkRequest {
        val currentSettings = settingsDataStore.settings.first()
        val request : PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<PeriodicBatteryWorker>(
                repeatInterval = currentSettings.period.toLong(),
                repeatIntervalTimeUnit = TimeUnit.MINUTES,
            )
            .addTag(PERIODIC_WORK_TAG)
            .build()
        return request
    }

    @WorkerThread
    suspend fun launchPeriodicJob () = withContext(dispatcher) {
        workManager.enqueueUniquePeriodicWork(
            PERIODIC_WORK_TAG,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            createPeriodicRequest()
        )
    }

    @WorkerThread
    suspend fun cancelPeriodicJob () = withContext(dispatcher) {
        workManager.cancelAllWorkByTag(PERIODIC_WORK_TAG)
    }

    @WorkerThread
    suspend fun cancelAll () = withContext(dispatcher) {
        workManager.cancelAllWork()
    }

    companion object {
        private const val PERIODIC_WORK_TAG : String = "Battery Poller"
    }

}
