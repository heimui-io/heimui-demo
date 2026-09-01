package io.heimui.demo.integration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.heimui.core.domain.model.action.HeimAction
import io.heimui.core.domain.model.action.ShowBottomSheetAction
import io.heimui.core.domain.model.action.ShowDialogAction
import io.heimui.core.presentation.modal.HeimModalPresenter
import io.heimui.demo.designsystem.tokens.DemoSpacing

/**
 * Dialogs and sheets in the app's own shape rather than stock Material.
 *
 * Modals are where a server-driven screen is most likely to look borrowed: the content comes from
 * the payload, but the frame around it is the app's, and a stock `AlertDialog` in an app with
 * heavily rounded corners reads as someone else's UI appearing inside yours.
 *
 * Only the frame is decided here. Titles, messages, button labels and what the buttons do all
 * stay in the payload — the presenter never inspects them, so a new dialog needs no client
 * release.
 */
class DemoModalPresenter : HeimModalPresenter {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun RenderBottomSheet(
        action: ShowBottomSheetAction,
        onDismiss: () -> Unit,
        onAction: (HeimAction) -> Unit,
        content: @Composable () -> Unit,
    ) {
        ModalBottomSheet(
            // A non-dismissible sheet must ignore the swipe and the scrim alike, or the flag only
            // half holds and the user finds the one gesture that escapes it.
            onDismissRequest = { if (action.isDismissible) onDismiss() },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DemoSpacing.lg, vertical = DemoSpacing.md),
            ) {
                action.title?.takeIf { it.isNotBlank() }?.let { title ->
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(DemoSpacing.md))
                }
                content()
                Spacer(modifier = Modifier.height(DemoSpacing.lg))
            }
        }
    }

    @Composable
    override fun RenderDialog(
        action: ShowDialogAction,
        onDismiss: () -> Unit,
        onAction: (HeimAction) -> Unit,
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            title = {
                Text(text = action.title, style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Text(text = action.message, style = MaterialTheme.typography.bodyMedium)
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Dismiss first: a confirm action that navigates would otherwise leave
                        // the dialog composed over the destination.
                        onDismiss()
                        action.confirmActions.forEach(onAction)
                    },
                    shape = RoundedCornerShape(DemoSpacing.md),
                ) {
                    Text(action.confirmText)
                }
            },
            dismissButton = action.dismissText?.let { label ->
                {
                    TextButton(
                        onClick = {
                            onDismiss()
                            action.dismissActions.forEach(onAction)
                        },
                    ) {
                        Text(label)
                    }
                }
            },
        )
    }
}
