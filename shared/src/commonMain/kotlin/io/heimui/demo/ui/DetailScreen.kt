package io.heimui.demo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.heimui.core.domain.model.action.HeimAction
import heimui_demo.shared.generated.resources.Res
import heimui_demo.shared.generated.resources.cd_back
import heimui_demo.shared.generated.resources.detail_default_title
import io.heimui.core.presentation.HeimScreen
import org.jetbrains.compose.resources.stringResource

/**
 * A server-driven detail screen.
 *
 * This is the smallest complete example of the integration model, and the one worth reading
 * first:
 *
 * 1. A payload dispatched `navigate` with a `screen_id` and `params`.
 * 2. The app turned that into a destination — see `DemoNavigationViewModel.navigateTo`.
 * 3. Here, those params are handed to [HeimScreen] as [queryParams], and the SDK appends them to
 *    the request: `.../screens/ecommerce/product_detail.json?product_id=sku_neural_x1`.
 * 4. The backend answers with the detail for *that* item.
 *
 * Note how little the app knows: it never learns what a product is, what fields it has, or how
 * the detail looks. It only knows how to route. Everything else is the payload's business — which
 * is the entire point of server-driven UI.
 *
 * Changing `queryParams` reloads the screen, so navigating to a different product reuses this
 * composable and simply refetches.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    screenId: String,
    params: Map<String, String>,
    onBack: () -> Unit,
    onAction: (HeimAction) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    // The server sends a human title in params when it has one; the app just
                    // displays whatever arrived, with a sane default.
                    Text(
                        text = params["title"] ?: stringResource(Res.string.detail_default_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            HeimScreen(
                screenId = screenId,
                queryParams = params, // travels to the backend as ?product_id=…
                onAction = onAction,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
