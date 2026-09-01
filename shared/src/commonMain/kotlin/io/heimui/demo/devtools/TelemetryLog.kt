package io.heimui.demo.devtools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import heimui_demo.shared.generated.resources.Res
import heimui_demo.shared.generated.resources.telemetry_empty
import org.jetbrains.compose.resources.stringResource

/**
 * Live view of what the SDK reported while the screen was on display.
 *
 * Worth having in a showcase because two of these events are how a backend team finds out it is
 * shipping broken SDUI *before* users do: `payload` (the client had to repair what it received)
 * and `icon` (a name the app's provider does not know). Neither is visible any other way — the
 * screen renders, just not as the author intended.
 *
 * A real app sends the same events to Crashlytics or Datadog; the panel is the local equivalent.
 */
@Composable
internal fun TelemetryLog(
    entries: List<DemoTelemetryObserver.Entry>,
    modifier: Modifier = Modifier,
) {
    if (entries.isEmpty()) {
        Text(
            text = stringResource(Res.string.telemetry_empty),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier,
        )
        return
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        entries.forEach { entry ->
            val accent = if (entry.isWarning) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            }
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = entry.label,
                    color = accent,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    // Fixed width so the details line up into a column and scan like a log.
                    modifier = Modifier
                        .width(72.dp)
                        .background(
                            accent.copy(alpha = 0.12f),
                            RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
                Text(
                    text = entry.detail,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .fillMaxWidth(),
                )
            }
        }
    }
}
