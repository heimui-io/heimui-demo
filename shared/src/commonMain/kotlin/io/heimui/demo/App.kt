package io.heimui.demo

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.heimui.core.data.repository.MockHeimScreenRepository
import io.heimui.core.domain.model.component.CustomComponent
import io.heimui.core.presentation.HeimScreen
import io.heimui.core.presentation.designsystem.HeimBrandTokens
import io.heimui.core.presentation.designsystem.HeimTheme
import io.heimui.core.presentation.registry.HeimCustomComponentRegistry
import io.heimui.demo.screens.DemoScreenCatalog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val tabs = remember {
        listOf(
            "Store & Deals" to "ecommerce_home",
            "Fintech KYC" to "fintech_kyc",
            "Food Feed" to "food_delivery",
            "Paywall" to "paywall_plans",
            "Primitives" to "components_playground"
        )
    }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isJsonSheetOpen by remember { mutableStateOf(false) }

    val mockRepository = remember {
        MockHeimScreenRepository(
            jsonProvider = { screenId -> DemoScreenCatalog.getScreenJson(screenId) },
            simulatedDelayMillis = 150L
        )
    }

    // 1. Custom Brand Colors
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

    // 2. Custom Component Extension Registration
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

    HeimTheme(
        colorScheme = darkThemeColors,
        brandTokens = customBrandTokens,
        customComponentRegistry = customRegistry
    ) {
        val currentScreenId = tabs[selectedTabIndex].second

        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🐾 HeimUI",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color(0xFF00E5FF)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF4F46E5), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "SHOWCASE",
                                        fontSize = 10.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF0B0F19),
                            titleContentColor = Color.White
                        )
                    )

                    PrimaryScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color(0xFF0B0F19),
                        contentColor = Color(0xFF00E5FF),
                        edgePadding = 12.dp
                    ) {
                        tabs.forEachIndexed { index, (title, _) ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        text = title,
                                        color = if (selectedTabIndex == index) Color(0xFF00E5FF) else Color(0xFF94A3B8)
                                    )
                                }
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { isJsonSheetOpen = true },
                    containerColor = Color(0xFF00E5FF),
                    contentColor = Color(0xFF0B0F19)
                ) {
                    Text(text = "</>", style = MaterialTheme.typography.titleMedium)
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFF0B0F19))
            ) {
                HeimScreen(
                    screenId = currentScreenId,
                    repository = mockRepository,
                    onAction = { /* Handle navigation or analytics */ },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Raw JSON Viewer Modal
            if (isJsonSheetOpen) {
                ModalBottomSheet(
                    onDismissRequest = { isJsonSheetOpen = false },
                    containerColor = Color(0xFF161D2F)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "⚡ Live SDUI JSON Payload",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF00E5FF)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0B0F19), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = DemoScreenCatalog.getScreenJson(currentScreenId) ?: "{}",
                                color = Color(0xFFE2E8F0),
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
}

@Composable
fun CustomStockChartRenderer(
    component: CustomComponent,
    modifier: Modifier = Modifier
) {
    val ticker = component.data["ticker"]?.asString ?: "TICKER"
    val price = component.data["price"]?.asDouble ?: 0.0
    val change = component.data["change"]?.asString ?: "+0.0%"

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
