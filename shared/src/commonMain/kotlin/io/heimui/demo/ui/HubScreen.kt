package io.heimui.demo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.sp
import heimui_demo.shared.generated.resources.Res
import heimui_demo.shared.generated.resources.hub_badge
import heimui_demo.shared.generated.resources.app_name
import heimui_demo.shared.generated.resources.heimui_avatar
import io.heimui.core.domain.model.action.HeimAction
import io.heimui.core.presentation.HeimScreen
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HubScreen(
    hubScreenId: String,
    onAction: (HeimAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(Res.drawable.heimui_avatar),
                            contentDescription = "HeimUI Logo",
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(Res.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.hub_badge),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            HeimScreen(
                screenId = hubScreenId,
                                onAction = onAction,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
