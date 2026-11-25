package fr.uha.hassenforder.easy.battery.provider

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import fr.uha.hassenforder.easy.battery.model.Battery
import fr.uha.hassenforder.easy.battery.model.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class BatteryProvider (
    private val context: Context,
) {
    private val _capture: MutableStateFlow<Battery?> = MutableStateFlow(null)
    val capture: Flow<Battery?> = _capture

    fun doCapture() {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        _capture.value = Battery.create(Source.CAPTURE, batteryStatus)
    }

    fun doClear() {
        _capture.value = null
    }

}