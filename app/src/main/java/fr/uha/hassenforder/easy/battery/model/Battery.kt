package fr.uha.hassenforder.easy.battery.model

import android.content.Intent
import android.os.BatteryManager
import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.Long
import kotlin.time.Clock

enum class Plugged {
    AC,
    USB,
    WIRELESS,
    UNKNOWN
}

enum class Status {
    CHARGING,
    DISCHARGING,
    FULL,
    NOT_CHARGING,
    UNKNOWN
}

enum class Source {
    CAPTURE,
    POWER_RECEIVER,
    PERIODIC,
    UNKNOWN,
}

@Entity(tableName = "batteries")
@Immutable
@Serializable
data class Battery(
    @PrimaryKey(autoGenerate = true)
    val bid : Long = 0,
    val timestamp: Instant = Clock.System.now(),
    val source: Source,
    val status: Status,
    val plugged: Plugged,
    val level: Int,
    val scale: Int,
) {

    @Ignore
    val isCharging = status == Status.CHARGING || status == Status.FULL

    companion object {

        private fun createStatus(statusCode : Int): Status {
            return when (statusCode) {
                BatteryManager.BATTERY_STATUS_CHARGING -> Status.CHARGING
                BatteryManager.BATTERY_STATUS_DISCHARGING -> Status.DISCHARGING
                BatteryManager.BATTERY_STATUS_FULL -> Status.FULL
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> Status.NOT_CHARGING
                else -> Status.UNKNOWN
            }
        }

        private fun createPlugged(pluggedCode : Int): Plugged {
            return when (pluggedCode) {
                BatteryManager.BATTERY_PLUGGED_AC -> Plugged.AC
                BatteryManager.BATTERY_PLUGGED_USB -> Plugged.USB
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> Plugged.WIRELESS
                else -> Plugged.UNKNOWN
            }
        }

        fun create(source : Source, batteryStatus: Intent?): Battery? {
            if (batteryStatus == null) return null
            val statusCode: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val pluggedCode: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val level: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            return Battery(
                bid = 0,
                timestamp = Clock.System.now(),
                source = source,
                status = createStatus(statusCode),
                plugged = createPlugged(pluggedCode),
                level = level,
                scale = scale,
            )
        }
    }
}
