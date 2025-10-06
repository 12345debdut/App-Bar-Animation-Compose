package com.debdut.app_bar_animations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun AnimatedCollapsableAppBar(
    modifier: Modifier,
    toolbarHeight: Dp,
    toolBarComposable: @Composable () -> Unit,
    contentComposable: @Composable () -> Unit
) {
    val state =
        rememberCollapsableNestedScrollConnectionAsState(toolbarHeight = toolbarHeight)
    Box(
        modifier = modifier
            .nestedScroll(state.scrollConnection)
    ) {
        contentComposable()
        Box(
            modifier = Modifier
                .height(toolbarHeight)
                .graphicsLayer {
                    translationY = state.heightState.floatValue
                }) {
            toolBarComposable()
        }
    }
}

@Composable
internal fun rememberCollapsableNestedScrollConnectionAsState(toolbarHeight: Dp): NestedScrollConnectionState {
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.toPx() }
    val toolbarOffsetHeightPx = remember { mutableFloatStateOf(0f) }
    val scrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val oldOffset = toolbarOffsetHeightPx.floatValue
                // Don't intercept if scrolling down or already fully collapsed.
                if (available.y > 0 || oldOffset == -toolbarHeightPx) {
                    return Offset.Zero
                }

                val newOffset = (oldOffset + available.y).coerceIn(-toolbarHeightPx, 0f)
                toolbarOffsetHeightPx.floatValue = newOffset
                val consumed = newOffset - oldOffset
                return Offset(0f, consumed)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val oldOffset = toolbarOffsetHeightPx.floatValue
                // Don't intercept if scrolling up or already fully expanded.
                if (available.y < 0 || oldOffset == 0f) {
                    return Offset.Zero
                }

                val newOffset = (oldOffset + available.y).coerceIn(-toolbarHeightPx, 0f)
                toolbarOffsetHeightPx.floatValue = newOffset
                val consumed = newOffset - oldOffset
                return Offset(0f, consumed)
            }
        }
    }
    return NestedScrollConnectionState(
        scrollConnection = scrollConnection,
        heightState = toolbarOffsetHeightPx
    )
}

data class NestedScrollConnectionState(
    val heightState: FloatState,
    val scrollConnection: NestedScrollConnection,
)