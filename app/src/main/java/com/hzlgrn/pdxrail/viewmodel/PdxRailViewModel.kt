package com.hzlgrn.pdxrail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hzlgrn.pdxrail.data.repository.RailSystemRepository
import com.hzlgrn.pdxrail.data.repository.viewmodel.RailSystemMapItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PdxRailViewModel @Inject constructor(
    private val railSystemRepository: RailSystemRepository,
): ViewModel() {

    private val _drawerShouldBeOpened = MutableStateFlow(false)
    val drawerShouldBeOpened = _drawerShouldBeOpened.asStateFlow()

    fun openDrawer() {
        _drawerShouldBeOpened.value = true
    }

    fun resetOpenDrawerAction() {
        _drawerShouldBeOpened.value = false
    }

    sealed class RailSystemMapState {
        object Idle: RailSystemMapState()
        object Loading: RailSystemMapState()
        data class Display(val mapItems: ImmutableList<RailSystemMapItem>): RailSystemMapState()
    }
    private val _railSystemMap = MutableStateFlow<RailSystemMapState>(RailSystemMapState.Idle)
    val railSystemMap = _railSystemMap.asStateFlow()
    private var _flowMapJob: Job? = null
        set(job) {
            field?.cancel()
            if (field == null) {
                _railSystemMap.value = RailSystemMapState.Idle
            }
            field = job
        }
    @OptIn(FlowPreview::class)
    fun flowRailSystemMap() {
        _flowMapJob = viewModelScope.launch {
            _railSystemMap.value = RailSystemMapState.Loading
            withContext(Dispatchers.IO) {
                railSystemRepository.flowRailSystemMapItems().debounce(timeoutMillis = 3000).collect { mapItems ->
                    Timber.d("collected ${mapItems.size} map items")
                    RailSystemMapState.Display(mapItems.toImmutableList()).let { display ->
                        withContext(Dispatchers.Main) {
                            _railSystemMap.value = display
                        }
                    }
                }
            }
        }
    }
}