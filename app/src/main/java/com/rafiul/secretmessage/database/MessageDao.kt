package com.rafiul.secretmessage.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert
    suspend fun createMessage(messageEntity: MessageEntity)

    @Query("SELECT * FROM messages")
    suspend fun getMessages(): Flow<List<MessageEntity>>

    @Delete
    suspend fun deleteMessage(messageEntity: MessageEntity)
}