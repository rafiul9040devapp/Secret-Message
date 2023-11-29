package com.rafiul.secretmessage.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MessageEntity::class], version = 1)
abstract class SecretDatabase : RoomDatabase() {
    abstract fun messageDAO(): MessageDao
}