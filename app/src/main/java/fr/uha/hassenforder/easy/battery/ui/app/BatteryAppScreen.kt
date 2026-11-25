package fr.uha.hassenforder.easy.battery.ui.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery4Bar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Battery4Bar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import fr.uha.hassenforder.easy.android.model.FlexibleIcon
import fr.uha.hassenforder.easy.android.model.IconRender
import fr.uha.hassenforder.easy.android.ui.app.AppNavigatorScreen
import fr.uha.hassenforder.easy.android.ui.app.PrimaryNavigation
import fr.uha.hassenforder.easy.battery.R
import fr.uha.hassenforder.easy.battery.navigation.BatteryDestination
import fr.uha.hassenforder.easy.battery.navigation.GreetingDestination
import fr.uha.hassenforder.easy.battery.navigation.SettingsDestination

@Composable
fun BatteryAppScreen(
    vm: AppViewModel = hiltViewModel(),
) {
    AppNavigatorScreen(vm.navigator, primaryNavigations)
}

private val primaryNavigations = listOf (
    PrimaryNavigation(
        destination = GreetingDestination.GreetingScreen,
        icon = IconRender(
            focused = FlexibleIcon(vector = Icons.Filled.Home),
            unfocused = FlexibleIcon(vector = Icons.Outlined.Home)
        ),
        labelId = R.string.app_name
    ),
    PrimaryNavigation(
        destination = BatteryDestination.ListScreen,
        icon = IconRender(focused = FlexibleIcon(vector= Icons.Filled.Battery4Bar), unfocused = FlexibleIcon(vector= Icons.Outlined.Battery4Bar)),
        labelId = R.string.action_batteries
    ),
    PrimaryNavigation(
        destination = SettingsDestination.SettingsScreen,
        icon = IconRender(focused = FlexibleIcon(vector= Icons.Filled.Settings), unfocused = FlexibleIcon(vector= Icons.Outlined.Settings)),
        labelId = R.string.action_settings
    ),
)
