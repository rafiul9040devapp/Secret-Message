package com.rafiul.secretmessage

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rafiul.secretmessage.ui.theme.SecretMessageTheme
import org.koin.core.component.getScopeId

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

                    val viewModel by viewModels<MainViewModel>()

                    val messages by viewModel.messages.collectAsState()


                    val dialogOpen = remember {
                        mutableStateOf(false)
                    }

                    val authorized = remember {
                        mutableStateOf(false)
                    }

                    val authorization: () -> Unit = {
                        BioMetricHelper.showPrompt(this){
                            authorized.value =true
                        }
                    }

                    LaunchedEffect(Unit){
                        authorization()
                    }

                    val blurValue by animateDpAsState(
                        targetValue = if (authorized.value) 0.dp else 15.dp,
                        animationSpec = tween(500)
                    )



                    if (dialogOpen.value) {
                        Dialog(onDismissRequest = { dialogOpen.value = false }) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
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

                                    Button(
                                        onClick = {
                                            if (secretMessage.value.isNotEmpty()) {
                                                viewModel.createMessage(message = secretMessage.value)
                                                dialogOpen.value = false
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(text = "ADD", color = Color.White)

                                    }

                                }

                            }
                        }
                    }




                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        floatingActionButton = {
                            Column {
                                AnimatedVisibility(visible = !authorized.value) {
                                    FloatingActionButton(
                                        onClick = {
                                          authorization
                                        },
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }

                                }
                                FloatingActionButton(
                                    onClick = {
                                        dialogOpen.value = true
                                    },
                                    containerColor = MaterialTheme.colorScheme.primary
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }

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
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = it.message, color = Color(0xffcccccc))

                                            AnimatedVisibility(visible = authorized.value) {
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

