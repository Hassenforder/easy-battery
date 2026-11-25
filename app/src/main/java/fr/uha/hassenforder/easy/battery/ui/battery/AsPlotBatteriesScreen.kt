package fr.uha.hassenforder.easy.battery.ui.battery

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.LegendItem
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.vicoTheme
import fr.uha.hassenforder.easy.battery.model.Battery
import fr.uha.hassenforder.easy.battery.ui.battery.ListBatteriesViewModel.UIState
import kotlin.math.abs

private fun buildTimeStamps (batteries : List<Battery>) : List<Double> {
    val last = batteries.lastOrNull()?.timestamp ?: return emptyList()
    return batteries.map {
        battery -> battery.timestamp.minus(last).inWholeMinutes.toDouble()
    }
}

private fun buildLevels (batteries : List<Battery>) : List<Double> {
    return batteries.map {
            battery ->
        battery.level.toDouble()
    }
}

private fun buildScales (batteries : List<Battery>) : List<Double> {
    return batteries.map {
            battery ->
        battery.scale.toDouble()
    }
}

private fun buildCharging (batteries : List<Battery>) : List<Double> {
    return batteries.map {
            battery -> if(battery.isCharging) 100.0 else 0.0
    }
}

private fun xLabelConverter (value : Double) : String {
    val days = abs((value / 24 / 60).toInt())
    val hours = abs((value / 60 % 24).toInt())
    val minutes = abs((value % 60).toInt())
    val converted = StringBuilder()
    if (value < 0) {
        converted.append("-")
    }
    if (days != 0) {
        converted.append(days)
        converted.append("d")
    }
    if (days != 0 || hours != 0) {
        converted.append(hours)
        converted.append("h")
    }
    if (days != 0 || hours != 0 || minutes != 0) {
        converted.append(minutes)
        converted.append("m")
    }
    if (converted.isEmpty()) {
        converted.append("0")
    }
    return converted.toString()
}

private val SeriesLabelKey = ExtraStore.Key<Set<String>>()

object ChartColors {
    val red = Color(0xFFEB4E3D)
    val orange = Color(0xFFF19A37)
    val yellow = Color(0xFFF7CE46)
    val green = Color(0xFF65C466)
    val mint = Color(0xFF5AC4BD)
    val teal = Color(0xFF5AAEC4)
    val cyan = Color(0xFF59ABE1)
    val blue = Color(0xFF3478F6)
    val indigo = Color(0xFF5856CE)
    val purple = Color(0xFFA357D7)
    val pink = Color(0xFFEB445A)
    val brown = Color(0xFF9D8563)
}
private val colorList = listOf(
    ChartColors.green,
    ChartColors.blue,
    ChartColors.purple,
    ChartColors.red,
    ChartColors.yellow,
)

@Composable
private fun ComposeBasicLineChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    val legendItemLabelComponent = rememberTextComponent(TextStyle(vicoTheme.textColor, 12.sp))
    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    LineCartesianLayer.LineProvider.series(
                        colorList.map { color ->
                            LineCartesianLayer.rememberLine (
                                fill = LineCartesianLayer.LineFill.single(Fill(color)),
/*
                                dataLabel = rememberTextComponent (
                                    style = TextStyle(color = color),
                                ),
*/
                                pointProvider = LineCartesianLayer.PointProvider.single(
                                    LineCartesianLayer.Point(
                                        ShapeComponent(
                                            fill = Fill(color),
                                            shape = CircleShape,
                                            strokeFill = Fill(Color.Black),
                                            strokeThickness = 1.dp,
                                        ),
                                        size = 8.dp,
                                    ),
                                ),

                            )
                        }
                    ),
                ),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = CartesianValueFormatter { context, x, _ -> xLabelConverter(x) },
                ),
                legend =
                    rememberHorizontalLegend(
                        items = { extraStore ->
                            extraStore[SeriesLabelKey].forEachIndexed { index, label ->
                                add(
                                    LegendItem(
                                        ShapeComponent(Fill(colorList[index]), CircleShape),
                                        legendItemLabelComponent,
                                        label,
                                    )
                                )
                            }
                        },
                        padding = Insets(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp),
                    ),
            ),
        modelProducer = modelProducer,
        modifier = modifier,
        scrollState = rememberVicoScrollState(scrollEnabled = false)
    )
}

@Composable
fun BatteryChart(batteries: List<Battery>, modifier: Modifier) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(batteries) {
        modelProducer.runTransaction {
            val x = buildTimeStamps(batteries)
            lineModel {
                series(x = x, y = buildLevels(batteries))
                series(x = x, y = buildScales(batteries))
                series(x = x, y = buildCharging(batteries))
            }
            extras { extraStore ->
                extraStore[SeriesLabelKey] = setOf("level", "scale", "charging")
            }
        }
    }
    ComposeBasicLineChart(modelProducer, modifier.fillMaxWidth())
}

@Composable
fun AsPlotBatteriesScreen(
    uiState: UIState,
    send : (ListBatteriesViewModel.UIEvent) -> Unit,
    modifier : Modifier = Modifier,
) {
    BatteryChart(uiState.batteries, modifier)
}
