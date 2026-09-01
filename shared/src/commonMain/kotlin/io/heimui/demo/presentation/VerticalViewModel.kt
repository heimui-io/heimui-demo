package io.heimui.demo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.heimui.demo.domain.model.DemoVertical
import io.heimui.demo.domain.repository.DemoCatalogRepository
import io.heimui.demo.devtools.SduiSourceInspector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * State for one vertical: which tab is selected, and the raw payload viewer.
 *
 * The composable that renders this holds no state of its own — it reads [uiState] and calls
 * intents. That is what makes the flow testable without a device, and what keeps a rotation from
 * resetting the selected tab.
 */
class VerticalViewModel(
    private val verticalId: String,
    private val catalog: DemoCatalogRepository,
    private val sourceInspector: SduiSourceInspector,
) : ViewModel() {

    data class UiState(
        val verticalId: String,
        val vertical: DemoVertical? = null,
        val selectedTabIndex: Int = 0,
        val isSourceSheetOpen: Boolean = false,
        val rawJson: String? = null,
        val isLoadingRawJson: Boolean = false,
    ) {
        /** Falls back to the raw id so an unknown vertical shows something instead of blank. */
        val title: String get() = vertical?.title ?: verticalId
        val tabs get() = vertical?.tabs.orEmpty()
        val currentScreenId: String? get() = tabs.getOrNull(selectedTabIndex)?.screenId
    }

    private val _uiState = MutableStateFlow(UiState(verticalId = verticalId))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val vertical = catalog.verticalById(verticalId)
            _uiState.update { it.copy(vertical = vertical) }
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index, rawJson = null) }
    }

    fun onToggleSourceSheet(open: Boolean) {
        _uiState.update { it.copy(isSourceSheetOpen = open) }
        if (open) loadRawJson()
    }

    /** Loads the payload behind the current screen, for the "view source" sheet. */
    private fun loadRawJson() {
        val screenId = _uiState.value.currentScreenId ?: return
        _uiState.update { it.copy(isLoadingRawJson = true) }
        viewModelScope.launch {
            val result = sourceInspector.sourceOf(screenId)
            _uiState.update {
                it.copy(
                    isLoadingRawJson = false,
                    rawJson = result.getOrElse { error -> "Could not load the payload: ${error.message}" }
                )
            }
        }
    }
}
