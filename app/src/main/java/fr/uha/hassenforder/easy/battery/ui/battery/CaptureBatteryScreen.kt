package fr.uha.hassenforder.easy.battery.ui.battery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.battery.R

@Composable
fun CaptureBatteryScreen (
    vm: CaptureBatteryViewModel = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_battery)

    LaunchedEffect(Unit) {
        vm.uiTitleBuilder.setScreenNameId(sid = R.string.title_capture)
        vm.uiTitleBuilder.setDefaultDocumentName(defaultDocumentName)
    }

    StateScreen(state = uiState) {
        SuccessCaptureBatteryScreen(it, { vm.send(it) })
    }

}

@Composable
private fun SuccessCaptureBatteryScreen(
    uiState: CaptureBatteryViewModel.UIState,
    send : (CaptureBatteryViewModel.UIEvent) -> Unit,
) {
    Scaffold(
        topBar = { AppTopBar(uiState.uiTitle, { send(CaptureBatteryViewModel.UIEvent.OnBack) }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 8.dp)
        ) {
            BatteryField (uiState.battery)
            Row {
                Button(onClick = { send(CaptureBatteryViewModel.UIEvent.OnCapture) }) {
                    Text(stringResource(R.string.action_capture))
                }
                Button(onClick = { send(CaptureBatteryViewModel.UIEvent.OnClear) }) {
                    Text(stringResource(R.string.action_clear))
                }
                Button(onClick = { send(CaptureBatteryViewModel.UIEvent.OnSave) }) {
                    Text(stringResource(R.string.action_save))
                }
            }
        }
    }
}
