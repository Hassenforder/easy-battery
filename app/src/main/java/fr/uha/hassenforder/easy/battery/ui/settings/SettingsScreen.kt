package fr.uha.hassenforder.easy.battery.ui.settings

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.uha.hassenforder.easy.android.ui.DecoratorProperties
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.android.ui.app.UITitle
import fr.uha.hassenforder.easy.android.ui.field.text.OutlinedIntField
import fr.uha.hassenforder.easy.android.ui.field.text.OutlinedTextField
import fr.uha.hassenforder.easy.android.ui.field.text.TextProperties
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.battery.R
import fr.uha.hassenforder.easy.android.ui.field.text.ActionFifteenUnitsLessControl
import fr.uha.hassenforder.easy.android.ui.field.text.ActionFifteenUnitsMoreControl
import fr.uha.hassenforder.easy.battery.ui.settings.SettingsViewModel.UIEvent

@Composable
fun SettingsScreen(
    vm: SettingsViewModel = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    StateScreen(uiState) {
        SettingsSuccessScreen (it, vm::send)
    }
}

@Composable
private fun SettingsSuccessScreen(
    uiState: SettingsViewModel.UIState,
    send : (UIEvent) -> Unit,
) {
    Scaffold(
        topBar = { AppTopBar(UITitle(screenNameId = R.string.title_settings), { send(
            UIEvent.OnUp) }) },
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 8.dp)
        ) {
            DatabaseFields(uiState, send)
            WorkerFields (uiState, send)
            ShareFields (uiState, send)
        }
    }

}

@Composable
private fun DatabaseFields (
    uiState: SettingsViewModel.UIState,
    send : (UIEvent) -> Unit,
) {
    Text(stringResource(id = R.string.database), fontSize = 16.sp, fontWeight = FontWeight.Bold)
    Button(onClick = { send(UIEvent.OnDatabaseClear) } ) {
        Text(stringResource(id = R.string.clear))
    }
    Text(stringResource(id = R.string.populate))
    Row (
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = { send(UIEvent.OnDatabaseFeed(1)) }) {
            Text(stringResource(id = R.string.populate_now))
        }
        Button(onClick = { send(UIEvent.OnDatabaseFeed(2)) }) {
            Text(stringResource(id = R.string.populate_back_5h))
        }
        Button(onClick = { send(UIEvent.OnDatabaseFeed(3)) }) {
            Text(stringResource(id = R.string.populate_back_12h))
        }
        Button(onClick = { send(UIEvent.OnDatabaseFeed(4)) }) {
            Text(stringResource(id = R.string.populate_back_day))
        }
        Button(onClick = { send(UIEvent.OnDatabaseFeed(5)) }) {
            Text(stringResource(id = R.string.populate_back_day))
        }
        Button(onClick = { send(UIEvent.OnDatabaseFeed(6)) }) {
            Text(stringResource(id = R.string.populate_back_2days))
        }
        Button(onClick = { send(UIEvent.OnDatabaseFeed(7)) }) {
            Text(stringResource(id = R.string.populate_back_2days))
        }
    }
}

@Composable
private fun WorkerFields (
    uiState: SettingsViewModel.UIState,
    send : (UIEvent) -> Unit,
) {
    Text(stringResource(id = R.string.repeat), fontSize = 16.sp, fontWeight = FontWeight.Bold)
    OutlinedIntField(
        value = uiState.period,
        onValueChange = { send(UIEvent.OnPeriodChanged(it)) },
        decorator = DecoratorProperties(labelId = R.string.period),
        modifier = Modifier.fillMaxWidth(),
        leading = {
            ActionFifteenUnitsLessControl()
        },
        trailing = {
            ActionFifteenUnitsMoreControl()
        },
    )
    WorkInfosField(uiState.jobsInfo)
    Row (
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(onClick = { send(UIEvent.OnJobStart) }) {
            Text(stringResource(id = R.string.start))
        }
        Button(onClick = { send(UIEvent.OnJobCancel) }) {
            Text(stringResource(id = R.string.cancel))
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { send(UIEvent.OnJobCancelAll) }) {
            Text(stringResource(id = R.string.cancel_all))
        }
    }
}

@Composable
private fun ShareFields (
    uiState: SettingsViewModel.UIState,
    send : (UIEvent) -> Unit,
) {
    Text(stringResource(id = R.string.share_parameters), fontSize = 16.sp, fontWeight = FontWeight.Bold)
    OutlinedTextField(
        uiState.email,
        { send(UIEvent.OnEmailChanged(it)) },
        properties = TextProperties(),
        modifier = Modifier.fillMaxWidth(),
        decorator = DecoratorProperties(
            labelId = R.string.email,
            hintId = R.string.email_hint
        )
    )
    OutlinedTextField(
        uiState.subject,
        { send(UIEvent.OnSubjectChanged(it)) },
        properties = TextProperties(),
        modifier = Modifier.fillMaxWidth(),
        decorator = DecoratorProperties(
            labelId = R.string.subject,
            hintId = R.string.subject_hint
        )
    )
}
