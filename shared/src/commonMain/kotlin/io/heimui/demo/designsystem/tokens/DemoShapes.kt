package io.heimui.demo.designsystem.tokens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Corner radii.
 *
 * Passed to `MaterialTheme` so SDK components that read `MaterialTheme.shapes` — the error card,
 * dialogs — match the host app instead of falling back to Material defaults.
 */
object DemoShapes {
    val shapes = Shapes(
        extraSmall = RoundedCornerShape(4.dp),
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(16.dp),
        extraLarge = RoundedCornerShape(24.dp),
    )
}
