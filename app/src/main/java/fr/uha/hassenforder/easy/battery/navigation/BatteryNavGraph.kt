package fr.uha.hassenforder.easy.battery.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import fr.uha.hassenforder.easy.battery.ui.battery.CaptureBatteryScreen
import fr.uha.hassenforder.easy.battery.ui.battery.ListBatteriesScreen
import fr.uha.hassenforder.easy.battery.ui.battery.ViewBatteryScreen
import kotlinx.serialization.Serializable

sealed interface BatteryDestination : NavKey {
    @Serializable
    data object ListScreen: NavKey

    @Serializable
    data object CaptureScreen: NavKey

    @Serializable
    data class ViewScreen(val bid: Long): NavKey
}

fun EntryProviderScope<NavKey>.batteryNavGraph () {
    entry<BatteryDestination.ListScreen> {
        ListBatteriesScreen()
    }
    entry<BatteryDestination.CaptureScreen> {
        CaptureBatteryScreen()
    }
    entry<BatteryDestination.ViewScreen> {
        ViewBatteryScreen(bid = it.bid)
    }
}