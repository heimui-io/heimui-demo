package io.heimui.demo.designsystem.tokens

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing scale.
 *
 * A named scale rather than literals scattered through composables: `DemoSpacing.md` survives a
 * design review, `16.dp` in forty files does not.
 *
 * Note the asymmetry with colours and typography: HeimUI has **no spacing token registry**.
 * Payload padding and spacing are plain integers in dp, so a server can emit any value it wants.
 * That is a real gap — if consistent spacing matters to you, keep the numbers your server emits
 * aligned with this scale, and consider validating them in your payload CI.
 */
object DemoSpacing {
    val none: Dp = 0.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
    val xxl: Dp = 48.dp

    /** Values a payload is expected to use for `padding` and `spacing`, in dp. */
    val payloadScale: List<Int> = listOf(0, 4, 8, 16, 24, 32, 48)
}
