package fr.uha.hassenforder.easy.battery.ui.battery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lens
import androidx.compose.material.icons.outlined.Power
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uha.hassenforder.easy.android.ui.UIConverter.formatWithContext
import fr.uha.hassenforder.easy.android.ui.lazy.SwipeableItem
import fr.uha.hassenforder.easy.battery.R
import fr.uha.hassenforder.easy.battery.model.Battery
import fr.uha.hassenforder.easy.battery.model.Source
import fr.uha.hassenforder.easy.battery.ui.battery.ListBatteriesViewModel.UIState
import kotlin.time.ExperimentalTime

@Composable
fun AsTextBatteriesScreen(
    uiState: UIState,
    send : (ListBatteriesViewModel.UIEvent) -> Unit,
    modifier : Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = uiState.batteries,
            key = { battery -> battery.bid }
        ) { item ->
            SwipeableItem(
                onEdit = { send(ListBatteriesViewModel.UIEvent.OnView(item)) },
            ) {
                BatteryItem(item, uiState.listMode)
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun BatteryItem (
    battery: Battery,
    listMode : ListMode,
) {
    val source : ImageVector =
        when (battery.source) {
            Source.CAPTURE -> Icons.Outlined.Lens
            Source.POWER_RECEIVER -> Icons.Outlined.Power
            Source.PERIODIC -> Icons.Outlined.Work
            Source.UNKNOWN -> Icons.Outlined.QuestionMark
        }
    ListItem(
        modifier = Modifier.fillMaxWidth(),
        leadingContent = {
            Icon(imageVector = source, contentDescription = "source", modifier = Modifier.size(24.dp) )
        },
        supportingContent = {
            Row (
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringArrayResource(R.array.statusValues)[battery.status.ordinal])
                if (battery.isCharging) {
                    Text(stringArrayResource(R.array.pluggedValues)[battery.plugged.ordinal])
                }
                Text(battery.level.toString())
                Text("/")
                Text(battery.scale.toString())
            }
        },
        content = {
            val context = LocalContext.current
            val timestampAsString = remember(battery.timestamp, context) {
                battery.timestamp.formatWithContext(context, default = "Unknown date")
            }
            Text(timestampAsString, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        },
    )
}
