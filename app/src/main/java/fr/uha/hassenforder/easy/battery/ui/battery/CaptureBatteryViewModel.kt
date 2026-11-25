package fr.uha.hassenforder.easy.battery.ui.battery

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.android.flow.Result
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.android.ui.app.UITitle
import fr.uha.hassenforder.easy.android.ui.app.UITitleBuilder
import fr.uha.hassenforder.easy.battery.model.Battery
import fr.uha.hassenforder.easy.battery.repository.BatteryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaptureBatteryViewModel @Inject constructor (
    private val repository: BatteryRepository,
    private val navigator: Navigator
): ViewModel() {

    val uiTitleBuilder = UITitleBuilder()

    private val _uiTitle : StateFlow<UITitle> = uiTitleBuilder.uiTitle.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UITitle()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _battery: StateFlow<Battery?> = repository.capture
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @Immutable
    data class UIState(
        val uiTitle : UITitle,
        val battery : Battery?,
    ) {
        companion object {
            fun create (uiTitle : UITitle, battery : Battery?) : UIState {
                return UIState(uiTitle, battery)
            }
        }

    }

    val uiState : StateFlow<Result<UIState>> = combine (_uiTitle, _battery)
        { t, b ->
            Result.Success(content = UIState.create(t, b))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    sealed class UIEvent {
        data object OnBack: UIEvent()
        data object OnCapture: UIEvent()
        data object OnClear: UIEvent()
        data object OnSave: UIEvent()
    }

    fun send (uiEvent : UIEvent) = viewModelScope.launch {
        if (uiState.value !is Result.Success) return@launch
        when (uiEvent) {
            is UIEvent.OnBack -> navigator.navigateUp()
            is UIEvent.OnCapture -> repository.doCapture()
            is UIEvent.OnClear -> repository.clear()
            is UIEvent.OnSave -> repository.create()
        }
    }

}
