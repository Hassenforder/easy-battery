package fr.uha.hassenforder.easy.battery.ui.battery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import fr.uha.hassenforder.easy.android.ui.UIConverter.formatWithContext
import fr.uha.hassenforder.easy.battery.R
import fr.uha.hassenforder.easy.battery.model.Battery
import kotlin.time.ExperimentalTime

@Composable
private fun BatteryCustomField (battery: Battery?, titleId : Int, convert : @Composable (Battery) -> String) {
    Row (
        modifier = Modifier.fillMaxWidth()
    ){
        Text(stringResource(titleId), modifier = Modifier.weight(1f))
        if (battery != null) {
            Text(convert(battery), modifier = Modifier.weight(1f))
        } else {
            Text(stringResource(R.string.no_value), modifier = Modifier.weight(1f))
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun BatteryField(battery: Battery?) {
    Column {
        BatteryCustomField(battery, R.string.id) { it.bid.toString() }
        Row (
            modifier = Modifier.fillMaxWidth()
        ){
            Text(stringResource(R.string.timestamp), modifier = Modifier.weight(1f))
            if (battery != null) {
                val context = LocalContext.current
                val timestampAsString = remember(battery.timestamp, context) {
                    battery.timestamp.formatWithContext(context, default = "Unknown date")
                }
                Text(timestampAsString, modifier = Modifier.weight(1f))
            } else {
                Text(stringResource(R.string.no_value), modifier = Modifier.weight(1f))
            }
        }
        BatteryCustomField(battery, R.string.source) { stringArrayResource(R.array.sourceValues)[it.source.ordinal] }
        BatteryCustomField(battery, R.string.status) { stringArrayResource(R.array.statusValues)[it.status.ordinal] }
        BatteryCustomField(battery, R.string.plugged) { stringArrayResource(R.array.pluggedValues)[it.plugged.ordinal] }
        BatteryCustomField(battery, R.string.level) { it.level.toString() }
        BatteryCustomField(battery, R.string.scale) { it.scale.toString() }
    }
}
