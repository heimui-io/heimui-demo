package io.heimui.demo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import heimui_demo.shared.generated.resources.Res
import heimui_demo.shared.generated.resources.heimui_avatar
import io.heimui.core.domain.model.action.HeimAction
import io.heimui.core.presentation.HeimScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.heimui.demo.devtools.SduiSourceInspector
import io.heimui.demo.domain.repository.DemoCatalogRepository
import io.heimui.demo.presentation.VerticalViewModel
import heimui_demo.shared.generated.resources.cd_back
import heimui_demo.shared.generated.resources.cd_view_source
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainVerticalScreen(
    verticalId: String,
    catalog: DemoCatalogRepository,
    sourceInspector: SduiSourceInspector,
    onBack: () -> Unit,
    onAction: (HeimAction) -> Unit
) {
    // `key` scopes the ViewModel to the vertical: navigating to a different one gets a fresh
    // instance instead of leaking the previous tab selection.
    val viewModel: VerticalViewModel = viewModel(key = verticalId) {
        VerticalViewModel(verticalId, catalog, sourceInspector)
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // This composable owns no state of its own: it renders `state` and forwards intents.
    val headerTitle = state.title
    val tabs = state.tabs
    val selectedTabIndex = state.selectedTabIndex
    val isJsonSheetOpen = state.isSourceSheetOpen
    val rawJsonContent = state.rawJson ?: if (state.isLoadingRawJson) "Loading…" else ""
    val currentScreenId = state.currentScreenId

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(Res.drawable.heimui_avatar),
                            contentDescription = "HeimUI Logo",
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = headerTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.cd_back),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, tabSpec ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { viewModel.onTabSelected(index) },
                        icon = {
                            Text(text = tabSpec.icon, fontSize = 20.sp)
                        },
                        label = {
                            Text(
                                text = tabSpec.title,
                                fontSize = 11.sp,
                                color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onToggleSourceSheet(true) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = stringResource(Res.string.cd_view_source),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // The server-driven screen. Everything above is app chrome; this is the payload.
            if (currentScreenId != null) {
                HeimScreen(
                    screenId = currentScreenId,
                    onAction = onAction,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Live JSON Inspector Modal
        if (isJsonSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.onToggleSourceSheet(false) },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "⚡ Remote SDUI JSON ($currentScreenId)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = if (rawJsonContent.isNotBlank()) rawJsonContent else "Loading remote payload from GitHub Raw...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
