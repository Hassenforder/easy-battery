package fr.uha.hassenforder.easy.battery.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.uha.hassenforder.easy.android.database.DatabaseTypeConverters
import fr.uha.hassenforder.easy.battery.database.dao.BatteryDao
import fr.uha.hassenforder.easy.battery.model.Battery

@TypeConverters(DatabaseTypeConverters::class)
@Database(
    entities = [
        Battery::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class BatteryDatabase : RoomDatabase() {

    abstract fun getBatteryDao () : BatteryDao

    companion object {
        private lateinit var instance : BatteryDatabase

        @Synchronized
        fun create (context : Context) : BatteryDatabase {
            instance = Room.databaseBuilder(context, BatteryDatabase::class.java, "battery.db").build()
            return instance
        }

        @Synchronized
        fun get () : BatteryDatabase {
            return instance
        }
    }

}
