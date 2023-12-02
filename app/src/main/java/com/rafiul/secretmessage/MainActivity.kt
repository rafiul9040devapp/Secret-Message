package com.rafiul.secretmessage

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.rafiul.secretmessage.ui.theme.SecretMessageTheme

class MainActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SecretMessageTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val context = LocalContext.current
                    val viewModel by viewModels<MainViewModel>()

                    val messages by viewModel.messages.collectAsState()


                    val dialogOpen = remember {
                        mutableStateOf(false)
                    }


                    val authorizedUser = remember {
                        mutableStateOf(false)
                    }

                    OnLifecycleEvent { owner, event ->

                        if (event == Lifecycle.Event.ON_PAUSE) {
                            dialogOpen.value = false
                            authorizedUser.value = false
                        }
                    }


                    val authorizationProcess: () -> Unit = {
                        BioMetricHelper.showPrompt(this) {
                            authorizedUser.value = true
                        }
                    }

                    LaunchedEffect(Unit) {
                        authorizationProcess()
                    }

                    val blurValue by animateDpAsState(
                        targetValue = if (authorizedUser.value) 0.dp else 15.dp,
                        animationSpec = tween(500), label = ""
                    )



                    if (dialogOpen.value) {
                        Dialog(onDismissRequest = { dialogOpen.value = false }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .imePadding(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(all = 16.dp)
                                ) {
                                    val secretMessage = remember {
                                        mutableStateOf("")
                                    }

                                    OutlinedTextField(
                                        value = secretMessage.value,
                                        onValueChange = {
                                            secretMessage.value = it
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        label = {
                                            Text(text = "Secret Message")
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null
                                            )
                                        },
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = Color.White,
                                            focusedLeadingIconColor = Color.White,
                                            focusedLabelColor = Color.White,
                                            unfocusedLabelColor = Color(0xffcccccc),
                                            unfocusedLeadingIconColor = Color(0xffcccccc),
                                            unfocusedBorderColor = Color(0xffcccccc),
                                            textColor = Color.White
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Button(
                                            onClick = {
                                                if (secretMessage.value.isNotEmpty()) {
                                                    viewModel.createMessage(message = secretMessage.value)
                                                    dialogOpen.value = false
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Message Can't be Empty",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.secondary
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(text = "ADD", color = Color.Green)

                                        }

                                        Button(
                                            onClick = {
                                                dialogOpen.value = false
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.secondary
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(text = "Cancel", color = Color.Red)

                                        }
                                    }

                                }

                            }
                        }
                    }




                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        floatingActionButton = {
                            Column {


                                FloatingActionButton(
                                    onClick = {
                                        if (authorizedUser.value) {
                                            dialogOpen.value = true
                                        } else {
                                            authorizationProcess()
                                        }
                                    },
                                    containerColor = MaterialTheme.colorScheme.primary
                                ) {
                                    if (authorizedUser.value) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }

                                }


//                                AnimatedVisibility(visible = !authorizedUser.value) {
//                                    FloatingActionButton(
//                                        onClick = authorizationProcess,
//                                        containerColor = MaterialTheme.colorScheme.primary
//                                    ) {
//                                        Icon(
//                                            imageVector = Icons.Default.Lock,
//                                            contentDescription = null,
//                                            tint = Color.White
//                                        )
//                                    }
//
//                                }
//
//                                Spacer(modifier = Modifier.size(8.dp))
//
//                                FloatingActionButton(
//                                    onClick = {
//
//                                        if (authorizedUser.value) {
//                                            dialogOpen.value = true
//                                        } else {
//
//                                            Toast.makeText(
//                                                context,
//                                                "Unlock The App To Add ANY Secret Messages",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                        }
//
//                                    },
//                                    containerColor = MaterialTheme.colorScheme.primary
//                                ) {
//                                    Icon(
//                                        imageVector = Icons.Default.Add,
//                                        contentDescription = null,
//                                        tint = Color.White
//                                    )
//                                }

                            }
                        }

                    ) { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(messages.reversed(), key = { it.id ?: 0 }) {

                                    Box(
                                        modifier = Modifier
                                            .animateItemPlacement(
                                                animationSpec = tween(
                                                    500
                                                )
                                            )
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.primary)
                                            .blur(
                                                blurValue,
                                                edgeTreatment = BlurredEdgeTreatment.Unbounded
                                            )
                                    ) {

                                        AnimatedVisibility(visible = authorizedUser.value) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {

                                                Text(text = it.message, color = Color(0xffcccccc))

                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = null,
                                                    modifier = Modifier.clickable {
                                                        viewModel.deleteMessage(it)
                                                    },
                                                    tint = Color.Red
                                                )


                                            }
                                        }
                                    }

                                }
                            }

                        }

                    }
                }
            }
        }
    }
}

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {

    val eventHandler = rememberUpdatedState(newValue = onEvent)
    val lifecycleOwner = rememberUpdatedState(newValue = LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

}

