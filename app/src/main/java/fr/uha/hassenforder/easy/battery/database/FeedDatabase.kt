package fr.uha.hassenforder.easy.battery.database

import android.util.Log
import fr.uha.hassenforder.easy.battery.model.Battery
import fr.uha.hassenforder.easy.battery.model.Plugged
import fr.uha.hassenforder.easy.battery.model.Source
import fr.uha.hassenforder.easy.battery.model.Status
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlin.time.Instant
import kotlin.time.Clock

class FeedDatabase (
    private val db : BatteryDatabase
) {

    suspend fun populateFrom(from : Instant) {
        val dao = db.getBatteryDao()
        Log.d("FeedDatabase", "populating from $from")
        val batteries = listOf(
            Battery(0, from.minus(180,DateTimeUnit.MINUTE, TimeZone.currentSystemDefault()), Source.PERIODIC, Status.NOT_CHARGING, Plugged.UNKNOWN, 100, 100),
            Battery(0, from.minus(165,DateTimeUnit.MINUTE, TimeZone.currentSystemDefault()), Source.PERIODIC, Status.NOT_CHARGING, Plugged.UNKNOWN,  90, 100),
            Battery(0, from.minus(150,DateTimeUnit.MINUTE, TimeZone.currentSystemDefault()), Source.PERIODIC, Status.NOT_CHARGING, Plugged.UNKNOWN,  80, 100),
            Battery(0, from.minus(120,DateTimeUnit.MINUTE, TimeZone.currentSystemDefault()), Source.PERIODIC, Status.NOT_CHARGING, Plugged.AC,  70, 100),
            Battery(0, from.minus( 90,DateTimeUnit.MINUTE, TimeZone.currentSystemDefault()), Source.PERIODIC, Status.CHARGING, Plugged.AC,  70, 100),
            Battery(0, from.minus( 75,DateTimeUnit.MINUTE, TimeZone.currentSystemDefault()), Source.PERIODIC, Status.CHARGING, Plugged.AC,  80, 100),
            Battery(0, from.minus( 45,DateTimeUnit.MINUTE, TimeZone.currentSystemDefault()), Source.PERIODIC, Status.CHARGING, Plugged.AC,  90, 100),
            Battery(0, from.minus( 30,DateTimeUnit.MINUTE, TimeZone.currentSystemDefault()), Source.PERIODIC, Status.CHARGING, Plugged.AC, 100, 100),
            Battery(0, from.minus( 15,DateTimeUnit.MINUTE, TimeZone.currentSystemDefault()), Source.PERIODIC, Status.NOT_CHARGING, Plugged.AC, 100, 100),
            Battery(0, from.minus(  0,DateTimeUnit.MINUTE, TimeZone.currentSystemDefault()), Source.PERIODIC, Status.NOT_CHARGING, Plugged.AC, 100, 100),
        )
        dao.create(batteries)
    }

    suspend fun populate(mode : Int) {
        if (mode == 1) populateFrom(from = Clock.System.now())
        if (mode == 2) populateFrom(from = Clock.System.now().minus(  5,DateTimeUnit.HOUR, TimeZone.currentSystemDefault()))
        if (mode == 3) populateFrom(from = Clock.System.now().minus(  12,DateTimeUnit.HOUR, TimeZone.currentSystemDefault()))
        if (mode == 4) populateFrom(from = Clock.System.now().minus(  25,DateTimeUnit.HOUR, TimeZone.currentSystemDefault()))
        if (mode == 5) populateFrom(from = Clock.System.now().minus(  32,DateTimeUnit.HOUR, TimeZone.currentSystemDefault()))
        if (mode == 6) populateFrom(from = Clock.System.now().minus(  52,DateTimeUnit.HOUR, TimeZone.currentSystemDefault()))
        if (mode == 7) populateFrom(from = Clock.System.now().minus(  60,DateTimeUnit.HOUR, TimeZone.currentSystemDefault()))
    }

    fun clear() {
        db.clearAllTables()
    }

    companion object {
    }
}