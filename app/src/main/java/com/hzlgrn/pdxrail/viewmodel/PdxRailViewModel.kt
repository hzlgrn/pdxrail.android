package com.hzlgrn.pdxrail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hzlgrn.pdxrail.data.repository.RailSystemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    fun buildMap() {
        viewModelScope.launch(Dispatchers.IO) {
            // Translate map features from storage to the Database
        }
    }

    fun flowMap() {
        viewModelScope.launch(Dispatchers.IO) {
            railSystemRepository.flowRailSystemMapItems().collect { mapItems ->
                Timber.d("collected ${mapItems.size} map items")
            }
        }
    }
}