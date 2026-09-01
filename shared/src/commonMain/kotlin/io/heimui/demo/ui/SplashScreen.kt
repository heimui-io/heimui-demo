package io.heimui.demo.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.sp
import heimui_demo.shared.generated.resources.Res
import heimui_demo.shared.generated.resources.app_tagline
import heimui_demo.shared.generated.resources.app_name
import heimui_demo.shared.generated.resources.heimui_avatar
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(
    onFinish: () -> Unit
) {
    val scale = remember { Animatable(0.85f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
        delay(900)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            // Official Heimdall Cyberpunk .jpg Avatar
            Image(
                painter = painterResource(Res.drawable.heimui_avatar),
                contentDescription = "Heimdall Official Logo",
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(36.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.app_name),
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(Res.string.app_tagline),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
