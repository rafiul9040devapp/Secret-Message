package com.rafiul.secretmessage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafiul.secretmessage.database.MessageEntity
import com.rafiul.secretmessage.database.SecretDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel : ViewModel(), KoinComponent {

    private val db: SecretDatabase by inject()
    val dao = db.messageDAO()

    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages = _messages.asStateFlow()


    fun createMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.createMessage(MessageEntity(message = message))
        }
    }

    fun getAllMessage() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.getMessages().collect { result ->
                _messages.update { result }
            }
        }
    }

    fun deleteMessage(message: MessageEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteMessage(messageEntity = message)
        }
    }

}