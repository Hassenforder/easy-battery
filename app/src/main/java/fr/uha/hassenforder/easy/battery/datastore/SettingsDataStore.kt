package fr.uha.hassenforder.easy.battery.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

data class Settings(
    val period: Int,
    val jobId: UUID?,
    val email : String,
    val subject : String,
)

class SettingsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    /**
     * Optimized flow that maps all preferences once.
     * distinctUntilChanged() ensures we only emit when actual data changes.
     */
    val settings: Flow<Settings> = dataStore.data
        .catch {
            emit(emptyPreferences())
        }
        .map { preferences ->
            Settings(
                period = preferences[KEY_PERIOD] ?: DEFAULT_PERIOD,
                jobId = preferences[KEY_JOB_ID]?.let { 
                    try { UUID.fromString(it) } catch (e: Exception) { null } 
                },
                email = preferences[KEY_EMAIL] ?: DEFAULT_EMAIL,
                subject = preferences[KEY_SUBJECT] ?: DEFAULT_SUBJECT,
            )
        }
        .distinctUntilChanged()

    fun getPeriod(): Flow<Int> = settings.map { it.period }.distinctUntilChanged()

    fun getJobId(): Flow<UUID?> = settings.map { it.jobId }.distinctUntilChanged()

    fun getEMail(): Flow<String> = settings.map { it.email }.distinctUntilChanged()
    fun getSubject(): Flow<String> = settings.map { it.subject }.distinctUntilChanged()

    suspend fun savePeriod(period: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_PERIOD] = period
        }
    }

    suspend fun saveEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[KEY_EMAIL] = email
        }
    }

    suspend fun saveSubject(subject: String) {
        dataStore.edit { preferences ->
            preferences[KEY_SUBJECT] = subject
        }
    }

    /**
     * Returns the created UUID. Creation is now handled explicitly,
     * which is safer than hidden side effects in a Flow getter.
     */
    suspend fun createJobId(): UUID {
        val id = UUID.randomUUID()
        dataStore.edit { preferences ->
            preferences[KEY_JOB_ID] = id.toString()
        }
        return id
    }

    companion object {
        private val KEY_PERIOD = intPreferencesKey("period")
        private val KEY_JOB_ID = stringPreferencesKey("jobId")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_SUBJECT = stringPreferencesKey("subject")

        private const val DEFAULT_PERIOD = 30
        private const val DEFAULT_EMAIL = "michel.hassenforder@gmail.com"
        private const val DEFAULT_SUBJECT = "Battery data"
    }
}
