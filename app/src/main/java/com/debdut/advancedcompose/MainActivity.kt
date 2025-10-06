package com.debdut.advancedcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.debdut.advancedcompose.ui.theme.AdvancedComposeTheme
import com.debdut.app_bar_animations.AnimatedAppBarWithScrollDirection
import com.debdut.app_bar_animations.AnimatedCollapsableAppBarWithScrollOffset

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdvancedComposeTheme {
                CollapsingToolbarScreenWithScrollOffset()
            }
        }
    }
}

@Preview
@Composable
fun CollapsingToolbarScreenWithScrollOffset() {
    val toolbarHeight = 60.dp
    AnimatedAppBarWithScrollDirection(
        modifier = Modifier
            .fillMaxSize(),
        toolbarHeight = toolbarHeight,
        toolBarComposable = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxSize(),
                title = { Text("Collapsible Toolbar") }
            )
        }
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
    }
}

@Preview
@Composable
fun CollapsingToolbarScreenWithScrollDirection() {
    val toolbarHeight = 60.dp
    AnimatedCollapsableAppBarWithScrollOffset(
        modifier = Modifier
            .fillMaxSize(),
        toolbarHeight = toolbarHeight,
        toolBarComposable = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxSize(),
                title = { Text("Collapsible Toolbar") }
            )
        }
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
    }
}
