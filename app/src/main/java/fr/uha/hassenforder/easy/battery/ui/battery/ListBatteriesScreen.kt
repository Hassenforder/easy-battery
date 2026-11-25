package fr.uha.hassenforder.easy.battery.ui.battery

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.Lens
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ViewTimeline
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.uha.hassenforder.easy.android.ui.app.AppMenuEntry
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.android.ui.app.appMenuEntriesBuilder
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.battery.R
import fr.uha.hassenforder.easy.battery.ui.battery.ListBatteriesViewModel.UIState

@Composable
fun ListBatteriesScreen(
    vm: ListBatteriesViewModel = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_batteries)

    LaunchedEffect(Unit) {
        vm.titleBuilder.setScreenNameId(sid = R.string.title_list)
        vm.titleBuilder.setDefaultDocumentName(defaultDocumentName)
    }

    StateScreen(uiState) {
        SuccessListScreen(it, { vm.send(it) } )
    }
}

@Composable
private fun modeMenuEntries (
    listMode: ListMode,
    send : (listMode: ListMode) -> Unit
) : List<AppMenuEntry> {
    return appMenuEntriesBuilder(
        selected = listMode,
        itemValues = ListMode.entries.toTypedArray(),
        itemIcons = arrayOf(Icons.Outlined.ViewTimeline, Icons.AutoMirrored.Outlined.ViewList),
        onChange = send,
    )
}

@Composable
private fun filterMenuEntries (
    current: ListFilter,
    send : (listFilter: ListFilter) -> Unit
) : List<AppMenuEntry> {
    return appMenuEntriesBuilder(
        selected = current,
        itemValues = ListFilter.entries.toTypedArray(),
        itemLabels = stringArrayResource(R.array.list_filter),
        onChange = send,
    )
}

@Composable
private fun SuccessListScreen(
    uiState: UIState,
    send : (ListBatteriesViewModel.UIEvent) -> Unit,
) {
    val context = LocalContext.current
    val modeEntries = modeMenuEntries(
        uiState.listMode,
        { send(ListBatteriesViewModel.UIEvent.OnMode(it)) }
    )
    val filterEntries = filterMenuEntries(
        uiState.listFilter,
        { send(ListBatteriesViewModel.UIEvent.OnFilter(it)) }
    )
    val shareEntries = listOf (
        AppMenuEntry.ActionEntry(
            icon = Icons.Outlined.Share,
            listener = { send(ListBatteriesViewModel.UIEvent.OnShare(context)) }
        ),
    )
    val menuEntries = modeEntries + filterEntries + shareEntries
    Scaffold(
        topBar = { AppTopBar(uiState.uiTitle, { send(ListBatteriesViewModel.UIEvent.OnBack) }, menuEntries ) },
        floatingActionButton = {
            FloatingActionButton(onClick = { send(ListBatteriesViewModel.UIEvent.OnCapture) } ) {
                Icon(Icons.Outlined.Lens, contentDescription = "capture")
            }
        }
    ) { innerPadding ->
        when (uiState.listMode) {
            ListMode.LIST -> AsTextBatteriesScreen (uiState, send, Modifier.padding(innerPadding))
            ListMode.PLOT if uiState.batteries.isEmpty() -> AsEmptyBatteriesScreen (uiState, send, Modifier.padding(innerPadding))
            ListMode.PLOT -> AsPlotBatteriesScreen (uiState, send, Modifier.padding(innerPadding))
        }
    }
}
