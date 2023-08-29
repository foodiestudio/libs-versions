package com.example.app

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private class Photo(val id: Int, val url: String)

@Composable
fun PhotoGrid(modifier: Modifier = Modifier) {
    val photos by rememberSaveable {
        mutableStateOf(List(100) { Photo(it, randomSampleImageUrl()) })
    }
    val selectedIds = rememberSaveable { mutableStateOf(emptySet<Int>()) }
    val inSelectionMode by remember { derivedStateOf { selectedIds.value.isNotEmpty() } }

    val state = rememberLazyGridState()

    val autoScrollSpeed = remember { mutableStateOf(0f) }

    LaunchedEffect(autoScrollSpeed.value) {
        if (autoScrollSpeed.value != 0f) {
            while (isActive) {
                state.scrollBy(autoScrollSpeed.value)
                // 这个刷新间隔要小一点，接近当前刷新率
                delay(16)
            }
        }
    }

    LazyVerticalGrid(
        state = state,
        modifier = modifier
            .fillMaxSize()
            .photoGridDragHandler(
                state,
                selectedIds,
                autoScrollSpeed = autoScrollSpeed,
                autoScrollThreshold = with(LocalDensity.current) { 24.dp.toPx() }),
        columns = GridCells.Adaptive(minSize = 100.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        items(photos, key = { it.id }) {
            Surface(
//                tonalElevation = 3.dp,
                modifier = Modifier.aspectRatio(1f)
            ) {
                val selected = selectedIds.value.contains(it.id)
                ImageItem(it, selected, inSelectionMode, Modifier
                    .semantics {
                        if (!inSelectionMode) {
                            onLongClick("Select") {
                                selectedIds.value += it.id
                                true
                            }
                        }
                    }
                    .then(if (inSelectionMode) {
                        Modifier.toggleable(
                            value = selected,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null, // do not show a ripple
                            onValueChange = { doSelect ->
                                if (doSelect) {
                                    selectedIds.value += it.id
                                } else {
                                    selectedIds.value -= it.id
                                }
                            }
                        )
                    } else {
                        Modifier
                    }))
            }
        }
    }
}

// https://picsum.photos/seed/110/128/128
fun randomSampleImageUrl() = "https://picsum.photos/seed/${(0..100000).random()}/128/128"

@Composable
private fun ImageItem(
    photo: Photo,
    inSelectionMode: Boolean,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.aspectRatio(1f),
        color = MaterialTheme.colors.surface
//        tonalElevation = 3.dp
    ) {
        Box {
            val transition = updateTransition(selected, label = "selected")
            val padding: Dp by transition.animateDp(label = "padding") { selected ->
                if (selected) 10.dp else 0.dp
            }
            val roundedCornerShape by transition.animateDp(label = "corner") { selected ->
                if (selected) 16.dp else 0.dp
            }

            val imgModifier = if (inSelectionMode) {
                Modifier
                    .matchParentSize()
                    .padding(padding)
                    .clip(RoundedCornerShape(roundedCornerShape.value))
            } else {
                Modifier
                    .matchParentSize()
            }

            AsyncImage(
                model = photo.url,
                contentDescription = null,
                modifier = imgModifier
            )
            if (inSelectionMode) {
                if (selected) {
                    val bgColor = MaterialTheme.colors.surface
                    Icon(
                        Icons.Filled.CheckCircle,
                        tint = MaterialTheme.colors.primary,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .border(2.dp, bgColor, CircleShape)
                            .clip(CircleShape)
                            .background(bgColor)
                    )
                } else {
                    Icon(
                        Icons.Filled.RadioButtonUnchecked,
                        tint = Color.White.copy(alpha = 0.7f),
                        contentDescription = null,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }
    }
}

/**
 * 检测长按后拖动的轨迹,有点类似文本的长按选择
 *
 * 当在边界的时候根据[autoScrollThreshold]会触发自动滚动，不在触发区的话，speed 调整为 0
 */
private fun Modifier.photoGridDragHandler(
    lazyGridState: LazyGridState,
    selectedIds: MutableState<Set<Int>>,
    autoScrollSpeed: MutableState<Float>,
    autoScrollThreshold: Float // Px
) = pointerInput(Unit) {
    var initialKey: Int? = null
    var currentKey: Int? = null
    detectDragGesturesAfterLongPress(
        onDragStart = { offset ->
            lazyGridState.gridItemKeyAtPosition(offset)?.let {
                if (!selectedIds.value.contains(it)) {
                    initialKey = it
                    currentKey = it
                    selectedIds.value = selectedIds.value.plus(it)
                }
            }
        },
        onDragCancel = {
            initialKey = null
            currentKey = null
        },
        onDragEnd = {
            initialKey = null
            currentKey = null
        },
        onDrag = { change, _ ->
            if (initialKey != null) {
                val distFromBottom =
                    lazyGridState.layoutInfo.viewportSize.height - change.position.y
                val distFromTop = change.position.y
                val speedFraction = 0.5f
                autoScrollSpeed.value = when {
                    distFromBottom < autoScrollThreshold -> (autoScrollThreshold - distFromBottom) * speedFraction
                    distFromTop < autoScrollThreshold -> -(autoScrollThreshold - distFromTop) * speedFraction
                    else -> 0f
                }

                lazyGridState.gridItemKeyAtPosition(change.position)?.let {
                    if (currentKey != it) {
                        selectedIds.value =
                            (minOf(initialKey!!, it)..maxOf(initialKey!!, it)).toSet()
                        currentKey = it
                    }
                }
            }
        }
    )
}

fun LazyGridState.gridItemKeyAtPosition(hitPoint: Offset): Int? = layoutInfo.visibleItemsInfo.find {
    it.size.toIntRect().contains(hitPoint.round() - it.offset)
}?.key as Int?

