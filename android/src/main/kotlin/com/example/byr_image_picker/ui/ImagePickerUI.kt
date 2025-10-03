package com.example.byr_image_picker.ui

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun ImagePickerView(
    modifier: Modifier = Modifier,
    isPartialPermission: Boolean = false,
    albumsMap: Map<String, List<Uri>>,
    onClose: () -> Unit,
    onCloseWithUri: (Uri) -> Unit
) {
    val context = LocalContext.current

    // 当前选中相册
    var selectedAlbum by rememberSaveable { mutableStateOf(albumsMap.keys.firstOrNull() ?: "") }
    // 当前相册对应的 Uri 列表
    val currentUris by remember(selectedAlbum) {
        derivedStateOf { albumsMap[selectedAlbum].orEmpty() }
    }

    var isShow by remember { mutableStateOf(false) }

    val angle by animateFloatAsState(
        targetValue = if (isShow) 180f else 0f,
        animationSpec = tween(durationMillis = 200) // 转一圈时长
    )

    val albums = albumsMap.keys.toList()

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(70.dp))

        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column {
                // 上面的栏目 关闭按钮 和相册名称
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        modifier = Modifier.padding(2.dp),
                        onClick = onClose
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Surface(
                        modifier = Modifier.clickable(
                            // 关掉点击动画
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { isShow = !isShow }
                        ),
                        color = Color.Gray.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = selectedAlbum
                            )
                            Icon(
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(
                                        angle
                                    ),
                                imageVector = Icons.Filled.KeyboardArrowUp,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }

                    }
                    Spacer(Modifier.weight(2f))
                }

                // 这个是下拉菜单，自带动画
                AnimatedVisibility(
                    visible = isShow,
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(albums) { albumName ->
                            Surface(
                                onClick = { selectedAlbum = albumName },   // 整行可点
                                shape = RectangleShape,              // 通栏分割线风格
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = albumName,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                )
                            }
                        }
                    }
                }

                // 展示框
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    // ai告诉我这样写可以优化加载速度，不知道是不是真的
                    items(
                        count = currentUris.size,
                        key = { currentUris[it] },
                        contentType = { "photo" }
                    ) { index ->
                        val uri = currentUris[index]
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uri)
                                .crossfade(true)           // 淡入动画
                                .size(256)       // 按需原尺寸；想再快就写 .size(256)
                                .memoryCacheKey(uri.toString())
                                .build(),          // 直接把原始 URI 给 Coil
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clickable(
                                    onClick = {
                                        Toast.makeText(
                                            context,
                                            uri.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onCloseWithUri(uri)
                                    }
                                )
                        )
                    }
                }
                // 这个目的是撑满底部空间
                Spacer(modifier = Modifier.weight(1f))
                if (isPartialPermission) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.LightGray
                    ) {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = "您以设置只访问部分照片，建议改为「允许访问所有照片」"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImagesPickerView(
    modifier: Modifier = Modifier,
    isPartialPermission: Boolean = false,
    albumsMap: Map<String, List<Uri>>,
    onClose: () -> Unit,
    onCloseWithUri: (Uri) -> Unit
) {
    val context = LocalContext.current

    // 当前选中相册
    var selectedAlbum by rememberSaveable { mutableStateOf(albumsMap.keys.firstOrNull() ?: "") }
    // 当前相册对应的 Uri 列表
    val currentUris by remember(selectedAlbum) {
        derivedStateOf { albumsMap[selectedAlbum].orEmpty() }
    }

    var isShow by remember { mutableStateOf(false) }

    val angle by animateFloatAsState(
        targetValue = if (isShow) 180f else 0f,
        animationSpec = tween(durationMillis = 200) // 转一圈时长
    )

    val albums = albumsMap.keys.toList()

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(70.dp))

        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column {
                // 上面的栏目 关闭按钮 和相册名称
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        modifier = Modifier.padding(2.dp),
                        onClick = onClose
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Surface(
                        modifier = Modifier.clickable(
                            // 关掉点击动画
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { isShow = !isShow }
                        ),
                        color = Color.Gray.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = selectedAlbum
                            )
                            Icon(
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(
                                        angle
                                    ),
                                imageVector = Icons.Filled.KeyboardArrowUp,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }

                    }
                    Spacer(Modifier.weight(2f))
                }

                // 这个是下拉菜单，自带动画
                AnimatedVisibility(
                    visible = isShow,
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(albums) { albumName ->
                            Surface(
                                onClick = { selectedAlbum = albumName },   // 整行可点
                                shape = RectangleShape,              // 通栏分割线风格
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = albumName,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                )
                            }
                        }
                    }
                }

                // 展示框
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    // ai告诉我这样写可以优化加载速度，不知道是不是真的
                    items(
                        count = currentUris.size,
                        key = { currentUris[it] },
                        contentType = { "photo" }
                    ) { index ->
                        val uri = currentUris[index]
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uri)
                                .crossfade(true)           // 淡入动画
                                .size(256)       // 按需原尺寸；想再快就写 .size(256)
                                .memoryCacheKey(uri.toString())
                                .build(),          // 直接把原始 URI 给 Coil
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clickable(
                                    onClick = {
                                        Toast.makeText(
                                            context,
                                            uri.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onCloseWithUri(uri)
                                    }
                                )
                        )
                    }
                }
                // 这个目的是撑满底部空间
                Spacer(modifier = Modifier.weight(1f))
                if (isPartialPermission) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.LightGray
                    ) {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = "您以设置只访问部分照片，建议改为「允许访问所有照片」"
                        )
                    }
                }
            }
        }
    }
}
