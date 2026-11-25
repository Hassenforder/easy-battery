package fr.uha.hassenforder.easy.battery.repository

import androidx.annotation.WorkerThread
import fr.uha.hassenforder.easy.battery.database.dao.BatteryDao
import fr.uha.hassenforder.easy.battery.datastore.SettingsDataStore
import fr.uha.hassenforder.easy.battery.model.Battery
import fr.uha.hassenforder.easy.battery.provider.BatteryProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

class BatteryRepository(
    private val dispatcher: CoroutineDispatcher,
    private val batteryDao: BatteryDao,
    private val batteryProvider: BatteryProvider,
    private val settingsDataStore : SettingsDataStore,
) {

    fun getAll(): Flow<List<Battery>> {
        return batteryDao.getAll()
    }

    fun getLastDay(): Flow<List<Battery>> {
        return batteryDao.getAllAfter(midnight())
    }

    fun getLastTwoDays(): Flow<List<Battery>> {
        return batteryDao.getAllAfter(daysAgo(1))
    }

    fun getLastWeek(): Flow<List<Battery>> {
        return batteryDao.getAllAfter(daysAgo(6))
    }

    fun getLastTwoWeeks(): Flow<List<Battery>> {
        return batteryDao.getAllAfter(daysAgo(13))
    }

    fun getById(id: Long): Flow<Battery?> {
        return batteryDao.getById(id)
    }

    val capture: Flow<Battery?> = batteryProvider.capture

    // These should ideally be flows to react to changes,
    // but keeping current style for consistency
    val email = settingsDataStore.getEMail()
    val subject = settingsDataStore.getSubject()

    @WorkerThread
    suspend fun doCapture() = withContext(dispatcher) {
        batteryProvider.doCapture()
    }

    @WorkerThread
    suspend fun clear() = withContext(dispatcher) {
        batteryProvider.doClear()
    }

    @WorkerThread
    suspend fun create(capture : Battery) = withContext(dispatcher) {
        batteryDao.create(capture)
    }

    @WorkerThread
    suspend fun create() = withContext(dispatcher) {
        capture.first()?.let { batteryDao.create(it) }
    }

    @WorkerThread
    suspend fun delete(battery: Battery) = withContext(dispatcher) {
        batteryDao.delete(battery)
    }

    companion object {
        /**
         * Returns the Instant corresponding to the start of the current day in the local timezone.
         */
        private fun midnight(): Instant {
            val tz = TimeZone.currentSystemDefault()
            return Clock.System.now().toLocalDateTime(tz).date.atStartOfDayIn(tz)
        }

        /**
         * Returns the midnight of N days ago.
         */
        private fun daysAgo(days: Int): Instant {
            val tz = TimeZone.currentSystemDefault()
            return midnight().minus(days, DateTimeUnit.DAY, tz)
        }
    }
}
