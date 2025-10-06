package com.debdut.app_bar_animations

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun AnimatedAppBarWithScrollDirection(
    modifier: Modifier,
    toolbarHeight: Dp,
    scrollOffset: Float = 10f,
    animationSpec: AnimationSpec<Float> = tween(durationMillis = 300),
    toolBarComposable: @Composable () -> Unit,
    contentComposable: @Composable () -> Unit
) {
    val state = rememberCollapsableNestedScrollConnectionAsState(scrollOffset)
    val scrollDirection by derivedStateOf { state.scrollDirection.value }

    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.toPx() }

    val animatedToolbarOffset by animateFloatAsState(
        targetValue = if (scrollDirection == ScrollDirection.UP) -toolbarHeightPx else 0f,
        animationSpec = animationSpec, label = "toolbarOffset"
    )

    Box(
        modifier = modifier
            .nestedScroll(state.scrollConnection)
    ) {
        contentComposable()
        Box(
            modifier = Modifier
                .height(toolbarHeight)
                .graphicsLayer {
                    translationY = animatedToolbarOffset
                }
        ) {
            toolBarComposable()
        }
    }
}

@Composable
private fun rememberCollapsableNestedScrollConnectionAsState(scrollOffset: Float): ScrollConnectionWithDirectionPayload {
    val listScrollDirection = remember { mutableStateOf(ScrollDirection.NONE) }
    var accumulatedScroll by remember { mutableStateOf(0f) }

    val scrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Add the vertical scroll delta to the accumulator
                accumulatedScroll += available.y

                // Determine direction only when the threshold is passed
                if (accumulatedScroll < -scrollOffset) {
                    listScrollDirection.value = ScrollDirection.UP
                    accumulatedScroll = 0f // Reset accumulator
                } else if (accumulatedScroll > scrollOffset) {
                    listScrollDirection.value = ScrollDirection.DOWN
                    accumulatedScroll = 0f // Reset accumulator
                }

                // We don't consume any scroll, so we return Offset.Zero
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // Reset accumulator if the scroll ends
                if (source == NestedScrollSource.Fling) {
                     accumulatedScroll = 0f
                }
                return Offset.Zero
            }
        }
    }
    return ScrollConnectionWithDirectionPayload(
        scrollConnection = scrollConnection,
        scrollDirection = listScrollDirection
    )
}

internal data class ScrollConnectionWithDirectionPayload(
    val scrollConnection: NestedScrollConnection,
    val scrollDirection: State<ScrollDirection>
)

internal enum class ScrollDirection {
    UP,
    DOWN,
    NONE
}
