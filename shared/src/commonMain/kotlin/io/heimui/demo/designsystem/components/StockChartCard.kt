package io.heimui.demo.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.heimui.core.domain.model.component.CustomComponent

/**
 * A native component the server can place inside a screen by name.
 *
 * This is the escape hatch: when SDUI primitives cannot express something — a chart, a map, a
 * camera view — the payload emits `{"type":"custom","name":"stock_chart","data":{...}}` and the
 * host renders whatever it wants. The server still controls *where* it appears and *what data*
 * it gets; the app controls how it looks.
 *
 * Registered in [io.heimui.demo.designsystem.DemoTheme].
 */
@Composable
internal fun StockChartCard(
    component: CustomComponent,
    modifier: Modifier = Modifier
) {
    val ticker = component.data["ticker"]?.asString ?: "HEIM"
    val price = component.data["price"]?.asDouble ?: 348.50
    val change = component.data["change"]?.asString ?: "+14.2%"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📈 Custom Plugin: $ticker Stock Widget",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = change, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$$price USD",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Rendered dynamically via LocalHeimCustomComponentRegistry",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
