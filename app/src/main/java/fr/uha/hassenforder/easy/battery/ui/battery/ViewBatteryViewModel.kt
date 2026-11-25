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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewBatteryViewModel @Inject constructor (
    private val repository: BatteryRepository,
    private val navigator: Navigator
): ViewModel() {

    val uiTitleBuilder = UITitleBuilder()

    private val _uiTitle : StateFlow<UITitle> = uiTitleBuilder.uiTitle.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UITitle()
    )

    private val _id: MutableStateFlow<Long> = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _battery: StateFlow<Result<Battery>> = _id
        .flatMapLatest { id -> repository.getById(id) }
        .map { battery ->
            if (battery != null) {
                Result.Success(content = battery)
            } else {
                Result.Error()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    @Immutable
    data class UIState(
        val uiTitle : UITitle,
        val battery : Battery,
    ) {
        companion object {
            fun create (uiTitle : UITitle, battery : Battery) : UIState {
                return UIState(uiTitle, battery)
            }
        }

    }

    val uiState : StateFlow<Result<UIState>> = combine (_uiTitle, _battery)
        { t, b ->
            when (b) {
                is Result.Success -> Result.Success(content = UIState.create(t, b.content))
                is Result.Error -> Result.Error(t)
                is Result.Loading -> Result.Loading(t)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    sealed class UIEvent {
        data object OnBack: UIEvent()
    }

    fun send (uiEvent : UIEvent) = viewModelScope.launch {
        if (uiState.value !is Result.Success) return@launch
        when (uiEvent) {
            is UIEvent.OnBack -> navigator.navigateUp()
        }
    }

    fun load (bid : Long) = viewModelScope.launch {
        _id.value = bid
    }

}
