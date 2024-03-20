package com.example.labwork3

import android.content.Context
import android.media.browse.MediaBrowser.MediaItem
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.labwork3.ui.theme.LabWork3Theme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
import com.google.android.exoplayer2.ui.StyledPlayerView


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LabWork3Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Gray
                ) {
                    Login(getVideoUri())
                }
            }
        }
    }

    private fun getVideoUri(): Uri {
        val rawId = resources.getIdentifier("clouds", "raw", packageName)
        val videoUri = "android.resource://$packageName/$rawId"
        return Uri.parse(videoUri)
    }
}

private fun Context.buildExoPlayer(uri: Uri) =
    com.google.android.exoplayer2.ExoPlayer.Builder(this).build().apply {
        setMediaItem(com.google.android.exoplayer2.MediaItem.fromUri(uri))
        repeatMode = com.google.android.exoplayer2.Player.REPEAT_MODE_ALL
        playWhenReady = true
        prepare()
    }

private fun Context.buildPlayerView(exoPlayer: com.google.android.exoplayer2.ExoPlayer) =
    StyledPlayerView(this).apply {
        player = exoPlayer
        layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        useController = false
        resizeMode = RESIZE_MODE_ZOOM
    }

@Composable
fun Login(video: Uri) {
    val context = LocalContext.current
    val passwordFocusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current
    val exoPlayer = remember {
        context.buildExoPlayer(video)
    }

    DisposableEffect(
        AndroidView(
            factory = { it.buildPlayerView(exoPlayer) },
            modifier = Modifier.fillMaxSize()
        )
    ){
        onDispose {
            exoPlayer.release()
        }
    }
    ProvideWindowInsets {


        Column(
            Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextInput(
                InputType.Name,
                keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() })
            )
            TextInput(
                InputType.Password,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                focusRequester = passwordFocusRequester
            )
            Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                Text("Sign In", Modifier.padding(vertical = 8.dp))
            }
            Divider(
                color = Color.Black.copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 48.dp)
            )
            Spacer(modifier = Modifier.padding(vertical = 40.dp))
        }
    }
}

sealed class InputType(
    val label: String,
    val icon: ImageVector,
    val keyboardOptions: KeyboardOptions,
    val visualTransformation: VisualTransformation
) {
    object Name : InputType(
        label = "Username",
        icon = Icons.Default.Person,
        KeyboardOptions(imeAction = ImeAction.Next),
        visualTransformation = VisualTransformation.None
    )

    object Password : InputType(
        label = "Password",
        icon = Icons.Default.Lock,
        KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Password),
        visualTransformation = PasswordVisualTransformation()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInput(
    inputType: InputType,
    focusRequester: FocusRequester? = null,
    keyboardActions: KeyboardActions,
) {
    var value: String by remember { mutableStateOf("") }
    TextField(
        value = value,
        onValueChange = { value = it },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester ?: FocusRequester()),
        leadingIcon = { Icon(imageVector = inputType.icon, null) },
        label = { Text(text = inputType.label) },
        shape = Shapes().small,
        colors = TextFieldDefaults.textFieldColors(

            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            containerColor = Color.White,
        ),
        singleLine = true,
        keyboardOptions = inputType.keyboardOptions,
        visualTransformation = inputType.visualTransformation,
        keyboardActions = keyboardActions,
    )
}