package com.debdut.advancedcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.core.graphics.rotationMatrix
import com.debdut.advancedcompose.ui.theme.AdvancedComposeTheme
import com.google.accompanist.pager.*
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.flow.asFlow
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdvancedComposeTheme {
                DismissibleAppBar()
            }
        }
    }
}
@Composable
fun DismissibleAppBar(){
    val appBarHeight = 60.dp
    var height by remember {
        mutableStateOf(0f)
    }
    val density = LocalDensity.current
    val animatedHeight by animateDpAsState(targetValue = with(density){height.toDp()})
    Column(modifier = Modifier.fillMaxSize()){
        TopAppBar(modifier = Modifier
            .background(Color.Red)
            .height(height = animatedHeight)
        ){}
        LazyScrollView(onOffsetChanged = {
            Log.d("OFFSET","$it")
            height = it
        }, appBarHeight = appBarHeight)
    }
}

@Composable
fun rememberNestedScrollConnection(onOffsetChanged:(Float)->Unit,appBarHeight:Float) = remember {
    var currentHeight = appBarHeight
    object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            Log.d("AVAILABLE","$available")
            currentHeight = (currentHeight+available.y).coerceIn(minimumValue = 0f, maximumValue = appBarHeight)
            return if(abs(currentHeight) == appBarHeight || abs(currentHeight) == 0f){
                super.onPreScroll(available, source)
            }else{
                onOffsetChanged(currentHeight)
                available
            }
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            if(available.y<0){
                onOffsetChanged(0f)
            }else{
                onOffsetChanged(appBarHeight)
            }
            return super.onPreFling(available)
        }
    }
}

@Composable
fun LazyScrollView(onOffsetChanged: (Float) -> Unit, appBarHeight: Dp){
    val pixelValue = with(LocalDensity.current){appBarHeight.toPx()}
    val nestedScrollState = rememberNestedScrollConnection(onOffsetChanged = onOffsetChanged, appBarHeight = pixelValue)
    LaunchedEffect(key1 = Unit, block = {
        onOffsetChanged(pixelValue)
    })
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Blue)
        .nestedScroll(nestedScrollState)){
        LazyColumn(modifier = Modifier.fillMaxSize()){
            items(count = 50){index ->
                Text(text = "Some $index", modifier = Modifier.padding(8.dp), color = MaterialTheme.colors.onBackground)
            }
        }
    }

}

fun Offset.roundToIntOffset():IntOffset{
    return IntOffset(x = this.x.roundToInt(),y = this.y.roundToInt())
}









