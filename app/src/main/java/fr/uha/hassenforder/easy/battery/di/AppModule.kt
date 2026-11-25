package fr.uha.hassenforder.easy.battery.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.android.navigation.NavigatorType
import fr.uha.hassenforder.easy.battery.database.BatteryDatabase
import fr.uha.hassenforder.easy.battery.database.dao.BatteryDao
import fr.uha.hassenforder.easy.battery.datastore.SettingsDataStore
import fr.uha.hassenforder.easy.battery.navigation.GreetingDestination
import fr.uha.hassenforder.easy.battery.navigation.rootNavGraph
import fr.uha.hassenforder.easy.battery.provider.BatteryProvider
import fr.uha.hassenforder.easy.battery.provider.WorkerProvider
import fr.uha.hassenforder.easy.battery.repository.BatteryRepository
import fr.uha.hassenforder.easy.battery.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob())

    @Singleton
    @Provides
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) = BatteryDatabase.create(appContext)

    @Singleton
    @Provides
    fun provideBatteryDao(db: BatteryDatabase) = db.getBatteryDao()

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context) : DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            produceFile = { context.preferencesDataStoreFile("battery_preferences") }
        )
    }

    @Singleton
    @Provides
    fun provideSettingsDataStore(store: DataStore<Preferences>) : SettingsDataStore = SettingsDataStore(store)

    @Singleton
    @Provides
    fun provideWorkManager(@ApplicationContext context: Context) : WorkManager {
        return WorkManager.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideNavigator() : Navigator = Navigator(
        startDestination = GreetingDestination.GreetingScreen,
        type = NavigatorType.SINGLETOP,
        navigationGraph = { rootNavGraph() }
    )

    @Singleton
    @Provides
    fun provideBatteryProvider(
        @ApplicationContext context: Context
    ) = BatteryProvider(context)

    @Singleton
    @Provides
    fun provideWorkerRepository(
        ioDispatcher: CoroutineDispatcher,
        settingsDataStore : SettingsDataStore,
        workManager : WorkManager
    ) = WorkerProvider(ioDispatcher, settingsDataStore, workManager)

    @Singleton
    @Provides
    fun provideBatteryRepository(
        ioDispatcher: CoroutineDispatcher,
        batteryDao: BatteryDao,
        dataProvider: BatteryProvider,
        settingsDataStore : SettingsDataStore,
    ) = BatteryRepository(ioDispatcher, batteryDao, dataProvider, settingsDataStore)

    @Singleton
    @Provides
    fun provideSettingsRepository(
        ioDispatcher: CoroutineDispatcher,
        database: BatteryDatabase,
        settingsDataStore : SettingsDataStore,
        workerProvider: WorkerProvider,
    ) = SettingsRepository(ioDispatcher, database, settingsDataStore, workerProvider)

}