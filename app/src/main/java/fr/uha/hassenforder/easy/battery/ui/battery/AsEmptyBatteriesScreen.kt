package fr.uha.hassenforder.easy.battery.ui.battery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uha.hassenforder.easy.battery.R
import fr.uha.hassenforder.easy.battery.ui.battery.ListBatteriesViewModel.UIState

@Composable
fun AsEmptyBatteriesScreen(
    uiState: UIState,
    send : (ListBatteriesViewModel.UIEvent) -> Unit,
    modifier : Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            stringResource(R.string.empty_batteries),
            modifier = Modifier.fillMaxWidth().padding(8.dp, 32.dp),
            color = Color.Red,
            fontSize = 24.sp,
        )
    }
}
