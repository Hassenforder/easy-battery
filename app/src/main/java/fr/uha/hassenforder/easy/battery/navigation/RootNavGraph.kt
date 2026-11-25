package fr.uha.hassenforder.easy.battery.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

fun EntryProviderScope<NavKey>.rootNavGraph () {
    greetingNavGraph()
    batteryNavGraph()
    settingsNavGraph()
}