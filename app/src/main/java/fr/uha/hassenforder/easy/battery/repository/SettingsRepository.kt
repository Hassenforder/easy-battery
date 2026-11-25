package fr.uha.hassenforder.easy.battery.repository

import androidx.annotation.WorkerThread
import fr.uha.hassenforder.easy.battery.database.BatteryDatabase
import fr.uha.hassenforder.easy.battery.database.FeedDatabase
import fr.uha.hassenforder.easy.battery.datastore.SettingsDataStore
import fr.uha.hassenforder.easy.battery.provider.WorkerProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SettingsRepository (
    private val dispatcher: CoroutineDispatcher,
    private val db : BatteryDatabase,
    private val settingsDataStore : SettingsDataStore,
    private val workerProvider: WorkerProvider,
) {

    val period = settingsDataStore.getPeriod()

    val jobsInfo = workerProvider.jobsInfo

    val email = settingsDataStore.getEMail()

    val subject = settingsDataStore.getSubject()

    @WorkerThread
    suspend fun clearDatabase () = withContext(dispatcher) {
        FeedDatabase(db).clear()
    }

    @WorkerThread
    suspend fun populateDatabase (mode : Int) = withContext(dispatcher) {
        FeedDatabase(db).populate(mode)
    }

    @WorkerThread
    suspend fun savePeriod (period : Int) = withContext(dispatcher) {
        settingsDataStore.savePeriod(period)
    }

    @WorkerThread
    suspend fun saveEmail (email : String) = withContext(dispatcher) {
        settingsDataStore.saveEmail(email)
    }

    @WorkerThread
    suspend fun saveSubject (subject : String) = withContext(dispatcher) {
        settingsDataStore.saveSubject(subject)
    }

    @WorkerThread
    suspend fun startJob () = withContext(dispatcher) {
        workerProvider.launchPeriodicJob()
    }

    @WorkerThread
    suspend fun cancelJob () = withContext(dispatcher) {
        workerProvider.cancelPeriodicJob()
    }

    @WorkerThread
    suspend fun cancelAllJobs () = withContext(dispatcher) {
        workerProvider.cancelAll()
    }

}
