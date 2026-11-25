package fr.uha.hassenforder.easy.battery.ui.battery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.battery.R

@Composable
fun ViewBatteryScreen (
    vm: ViewBatteryViewModel = hiltViewModel(),
    bid : Long
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_battery)

    LaunchedEffect(Unit) {
        vm.uiTitleBuilder.setScreenNameId(sid = R.string.title_view)
        vm.uiTitleBuilder.setDefaultDocumentName(defaultDocumentName)
        vm.load(bid)
    }

    StateScreen(state = uiState) {
        SuccessViewBatteryScreen(it, { vm.send(it) })
    }
}

@Composable
private fun SuccessViewBatteryScreen(
    uiState: ViewBatteryViewModel.UIState,
    send : (ViewBatteryViewModel.UIEvent) -> Unit,
) {
    Scaffold(
        topBar = { AppTopBar(uiState.uiTitle, { send(ViewBatteryViewModel.UIEvent.OnBack) } ) },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            BatteryField(uiState.battery)
        }
    }
}
