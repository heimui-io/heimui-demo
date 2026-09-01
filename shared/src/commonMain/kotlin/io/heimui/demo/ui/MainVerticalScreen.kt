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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import io.heimui.demo.data.HeimRemoteRepositoryProvider
import org.jetbrains.compose.resources.painterResource

data class VerticalTabSpec(
    val title: String,
    val screenId: String,
    val iconEmoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainVerticalScreen(
    verticalId: String,
    repository: HeimRemoteRepositoryProvider,
    onBack: () -> Unit,
    onAction: (HeimAction) -> Unit
) {
    val (headerTitle, tabs) = remember(verticalId) {
        when (verticalId) {
            "ecommerce" -> "🛒 E-Commerce & Deals" to listOf(
                VerticalTabSpec("Deals", "ecommerce_home", "🔥"),
                VerticalTabSpec("Catalog", "ecommerce_catalog", "📦"),
                VerticalTabSpec("Orders", "ecommerce_profile", "📜")
            )
            "fintech" -> "💳 Fintech & Banking" to listOf(
                VerticalTabSpec("Balance", "fintech_dashboard", "💳"),
                VerticalTabSpec("KYC Form", "fintech_kyc", "📝"),
                VerticalTabSpec("Limits", "fintech_limits", "🛡️")
            )
            "food" -> "🍔 Food Delivery" to listOf(
                VerticalTabSpec("Kitchens", "food_feed", "🍕"),
                VerticalTabSpec("Live Order", "food_tracking", "🛵"),
                VerticalTabSpec("Locations", "food_account", "📍")
            )
            "paywall" -> "💎 SaaS & Paywall" to listOf(
                VerticalTabSpec("Plans", "paywall_plans", "💎"),
                VerticalTabSpec("Team", "paywall_seats", "👥"),
                VerticalTabSpec("Usage", "paywall_usage", "⚡")
            )
            "storybook" -> "⚡ Primitives Storybook" to listOf(
                VerticalTabSpec("Primitives", "storybook_primitives", "🎨"),
                VerticalTabSpec("Plugin", "storybook_custom", "📈"),
                VerticalTabSpec("Tokens", "storybook_tokens", "🌈")
            )
            else -> "📱 Showcase View" to listOf(
                VerticalTabSpec("Tab 1", "ecommerce_home", "1️⃣"),
                VerticalTabSpec("Tab 2", "ecommerce_catalog", "2️⃣"),
                VerticalTabSpec("Tab 3", "ecommerce_profile", "3️⃣")
            )
        }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isJsonSheetOpen by remember { mutableStateOf(false) }
    var rawJsonContent by remember { mutableStateOf("") }

    val currentScreenId = tabs[selectedTabIndex].screenId

    LaunchedEffect(currentScreenId, isJsonSheetOpen) {
        if (isJsonSheetOpen) {
            try {
                rawJsonContent = repository.fetchRawJson(currentScreenId)
            } catch (e: Throwable) {
                rawJsonContent = "Failed to load raw JSON: ${e.message}"
            }
        }
    }

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
                                .border(1.dp, Color(0xFF00E5FF), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = headerTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text(text = "←", fontSize = 22.sp, color = Color(0xFF00E5FF))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B0F19),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF161D2F),
                contentColor = Color(0xFF00E5FF)
            ) {
                tabs.forEachIndexed { index, tabSpec ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Text(text = tabSpec.iconEmoji, fontSize = 20.sp)
                        },
                        label = {
                            Text(
                                text = tabSpec.title,
                                fontSize = 11.sp,
                                color = if (selectedTabIndex == index) Color(0xFF00E5FF) else Color(0xFF94A3B8)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00E5FF),
                            indicatorColor = Color(0xFF1E293B)
                        )
                    )
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
                repository = repository,
                onAction = onAction,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Live JSON Inspector Modal
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "⚡ Remote SDUI JSON ($currentScreenId)",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF00E5FF)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0B0F19), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = if (rawJsonContent.isNotBlank()) rawJsonContent else "Loading remote payload from GitHub Raw...",
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
