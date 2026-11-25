package fr.uha.hassenforder.easy.battery.ui.settings

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.android.flow.Result
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.battery.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor (
    private val repository: SettingsRepository,
    private val navigator: Navigator
): ViewModel() {

    private val _period = repository.period

    private val _jobsInfo = repository.jobsInfo

    private val _email = repository.email

    private val _subject = repository.subject

    @Immutable
    data class UIState (
        val period : Int,
        val jobsInfo : List<WorkInfo>,
        val email : String,
        val subject : String,
    ) {
        companion object {
            fun create(period : Int, jobsInfo : List<WorkInfo>, email : String, subject : String): UIState {
                return UIState(period, jobsInfo, email, subject)
            }
        }
    }

    val uiState : StateFlow<Result<UIState>> = combine (_period, _jobsInfo, _email, _subject) { p, j, e, s ->
        Result.Success(UIState.create(p, j, e, s))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Result.Loading()
    )

    sealed class UIEvent {
        data object OnUp: UIEvent()
        data object OnDatabaseClear: UIEvent()
        data class OnDatabaseFeed(val mode : Int): UIEvent()
        data class OnPeriodChanged(val period : Int): UIEvent()
        data class OnEmailChanged(val email : String): UIEvent()
        data class OnSubjectChanged(val subject : String): UIEvent()
        data object OnJobStart: UIEvent()
        data object OnJobCancel: UIEvent()
        data object OnJobCancelAll: UIEvent()
    }

    fun send (uiEvent : UIEvent) = viewModelScope.launch {
        when (uiEvent) {
            is UIEvent.OnUp -> navigator.navigateUp()
            UIEvent.OnDatabaseClear -> repository.clearDatabase()
            is UIEvent.OnDatabaseFeed -> repository.populateDatabase (uiEvent.mode)
            is UIEvent.OnPeriodChanged -> {
                if (uiEvent.period == _period.first()) return@launch
                repository.savePeriod(uiEvent.period)
            }
            is UIEvent.OnEmailChanged -> {
                if (uiEvent.email == _email.first()) return@launch
                repository.saveEmail(uiEvent.email)
            }
            is UIEvent.OnSubjectChanged -> {
                if (uiEvent.subject == _subject.first()) return@launch
                repository.saveSubject(uiEvent.subject)
            }
            UIEvent.OnJobStart -> repository.startJob ()
            UIEvent.OnJobCancel -> repository.cancelJob ()
            UIEvent.OnJobCancelAll -> repository.cancelAllJobs ()
        }
    }

}