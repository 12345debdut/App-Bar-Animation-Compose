package com.debdut.advancedcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.debdut.advancedcompose.ui.theme.AdvancedComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdvancedComposeTheme {
                CollapsingToolbarScreen()
            }
        }
    }
}

@Composable
fun CollapsingToolbarScreen() {
    val toolbarHeight = 60.dp
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.toPx() }
    val toolbarOffsetHeightPx = remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = toolbarHeight)
        ) {
            items(50) { index ->
                Text(
                    text = "List Item $index",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                Divider()
            }
        }

        TopAppBar(
            modifier = Modifier
                .height(toolbarHeight)
                .graphicsLayer {
                    translationY = toolbarOffsetHeightPx.floatValue
                },
            title = { Text("Collapsible Toolbar") }
        )
    }
}
