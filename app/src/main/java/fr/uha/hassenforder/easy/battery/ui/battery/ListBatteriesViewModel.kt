package fr.uha.hassenforder.easy.battery.ui.battery

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.android.flow.Result
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.android.ui.app.UITitle
import fr.uha.hassenforder.easy.android.ui.app.UITitleBuilder
import fr.uha.hassenforder.easy.battery.model.Battery
import fr.uha.hassenforder.easy.battery.navigation.BatteryDestination
import fr.uha.hassenforder.easy.battery.repository.BatteryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

enum class ListFilter {
    ALL,
    LAST_DAY,
    LAST_TWO_DAYS,
    LAST_WEEK,
    LAST_TWO_WEEKS,
}

enum class ListMode {
    LIST,
    PLOT,
}

@HiltViewModel
class ListBatteriesViewModel @Inject constructor (
    private val repository: BatteryRepository,
    private val navigator: Navigator
): ViewModel() {

    val titleBuilder = UITitleBuilder()

    private val _uiTitle : StateFlow<UITitle> = titleBuilder.uiTitle.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UITitle()
    )

    private val _email = repository.email

    private val _subject = repository.subject

    private val _listMode = MutableStateFlow(ListMode.PLOT)

    private val _listFilter = MutableStateFlow(ListFilter.LAST_DAY)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _batteries: Flow<Result<List<Battery>>> = _listFilter
        .flatMapLatest { m ->
            when(m) {
                ListFilter.ALL -> repository.getAll()
                ListFilter.LAST_DAY -> repository.getLastDay()
                ListFilter.LAST_TWO_DAYS -> repository.getLastTwoDays()
                ListFilter.LAST_WEEK -> repository.getLastWeek()
                ListFilter.LAST_TWO_WEEKS -> repository.getLastTwoWeeks()
            }
        }
        .map { content -> Result.Success(content) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading()
        )

    @Immutable
    data class UIState (
        val uiTitle: UITitle,
        val listMode : ListMode,
        val listFilter : ListFilter,
        val batteries : List<Battery>,
    )

    val uiState : StateFlow<Result<UIState>> = combine(_uiTitle, _listMode, _listFilter, _batteries)
        { t, m, f, b ->
            when (b) {
                is Result.Success -> Result.Success(content = UIState(t, m, f, b.content))
                is Result.Error -> Result.Error(t)
                is Result.Loading -> Result.Loading(t)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading()
        )

    sealed class UIEvent {
        data object OnBack: UIEvent()
        data class OnMode(val newValue: ListMode): UIEvent()
        data class OnFilter(val newValue: ListFilter): UIEvent()
        data object OnCapture: UIEvent()
        data class OnShare(val context : Context): UIEvent()
        data class OnView(val newValue: Battery): UIEvent()
    }

    fun send (uiEvent : UIEvent) = viewModelScope.launch {
        when (uiEvent) {
            is UIEvent.OnBack -> navigator.navigateUp()
            is UIEvent.OnMode -> _listMode.value = uiEvent.newValue
            is UIEvent.OnFilter -> _listFilter.value = uiEvent.newValue
            is UIEvent.OnCapture -> navigator.navigate(
                destination = BatteryDestination.CaptureScreen,
            )
            is UIEvent.OnShare -> doShare(uiEvent.context)
            is UIEvent.OnView -> navigator.navigate(
                destination = BatteryDestination.ViewScreen(uiEvent.newValue.bid),
            )
        }
    }

    private suspend fun doShare(context : Context) {
        if (uiState.value !is Result.Success) return
        val batteries = (uiState.value as Result.Success).content.batteries
        val json = Json.encodeToString(batteries)
        val sendIntent: Intent = Intent().apply {
            action = ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, json)
            putExtra(Intent.EXTRA_EMAIL, _email.first())
            putExtra(Intent.EXTRA_SUBJECT, _subject.first())
            type = "text/json"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

}