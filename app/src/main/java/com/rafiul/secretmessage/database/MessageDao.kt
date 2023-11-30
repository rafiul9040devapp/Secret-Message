package com.rafiul.secretmessage.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert
    fun createMessage(messageEntity: MessageEntity)

    @Query("SELECT * FROM messages")
    fun getMessages(): Flow<List<MessageEntity>>

    @Delete
    fun deleteMessage(messageEntity: MessageEntity)
}