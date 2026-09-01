package io.heimui.demo

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
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.heimui.core.domain.model.action.HeimAction
import io.heimui.core.domain.model.action.NavigateAction
import io.heimui.core.domain.model.action.OpenUrlAction
import io.heimui.core.domain.model.component.CustomComponent
import io.heimui.core.presentation.designsystem.HeimBrandTokens
import io.heimui.core.presentation.designsystem.HeimTheme
import io.heimui.core.presentation.registry.HeimCustomComponentRegistry
import io.heimui.demo.data.HeimRemoteRepositoryProvider
import io.heimui.demo.ui.HubScreen
import io.heimui.demo.ui.MainVerticalScreen
import io.heimui.demo.ui.SplashScreen

sealed interface DemoNavigationState {
    data object Splash : DemoNavigationState
    data object Hub : DemoNavigationState
    data class Vertical(val verticalId: String) : DemoNavigationState
}

@Composable
fun App() {
    var currentNavState by remember { mutableStateOf<DemoNavigationState>(DemoNavigationState.Splash) }

    val remoteRepository = remember {
        HeimRemoteRepositoryProvider(
            baseUrl = "https://raw.githubusercontent.com/heimui-io/heimui-demo/main/sdui/"
        )
    }

    val customBrandTokens = remember {
        HeimBrandTokens(
            colors = mapOf(
                "primary" to Color(0xFF00E5FF),
                "secondary" to Color(0xFFA855F7),
                "background" to Color(0xFF0B0F19),
                "surface" to Color(0xFF161D2F)
            )
        )
    }

    val customRegistry = remember {
        HeimCustomComponentRegistry().apply {
            register("stock_chart") { component, _, _, modifier ->
                CustomStockChartRenderer(component = component, modifier = modifier)
            }
        }
    }

    val darkThemeColors = darkColorScheme(
        primary = Color(0xFF00E5FF),
        secondary = Color(0xFFA855F7),
        background = Color(0xFF0B0F19),
        surface = Color(0xFF161D2F),
        onBackground = Color(0xFFFFFFFF),
        onSurface = Color(0xFFFFFFFF)
    )

    fun handleHeimAction(action: HeimAction) {
        when (action) {
            is NavigateAction -> {
                currentNavState = DemoNavigationState.Vertical(action.screenId)
            }
            is OpenUrlAction -> {
                if (action.url.startsWith("heimui://showcase/")) {
                    val targetVertical = action.url.removePrefix("heimui://showcase/")
                    currentNavState = DemoNavigationState.Vertical(targetVertical)
                }
            }
            else -> Unit
        }
    }

    HeimTheme(
        colorScheme = darkThemeColors,
        brandTokens = customBrandTokens,
        customComponentRegistry = customRegistry
    ) {
        when (val state = currentNavState) {
            is DemoNavigationState.Splash -> {
                SplashScreen(
                    onFinish = { currentNavState = DemoNavigationState.Hub }
                )
            }
            is DemoNavigationState.Hub -> {
                HubScreen(
                    repository = remoteRepository,
                    onAction = { handleHeimAction(it) }
                )
            }
            is DemoNavigationState.Vertical -> {
                MainVerticalScreen(
                    verticalId = state.verticalId,
                    repository = remoteRepository,
                    onBack = { currentNavState = DemoNavigationState.Hub },
                    onAction = { handleHeimAction(it) }
                )
            }
        }
    }
}

@Composable
fun CustomStockChartRenderer(
    component: CustomComponent,
    modifier: Modifier = Modifier
) {
    val ticker = component.data["ticker"]?.asString ?: "HEIM"
    val price = component.data["price"]?.asDouble ?: 348.50
    val change = component.data["change"]?.asString ?: "+14.2%"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF4F46E5), RoundedCornerShape(12.dp))
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
                    color = Color(0xFF00E5FF)
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .background(Color(0xFF10B981), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = change, fontSize = 11.sp, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$$price USD",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Text(
                text = "Rendered dynamically via LocalHeimCustomComponentRegistry",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8)
            )
        }
    }
}
