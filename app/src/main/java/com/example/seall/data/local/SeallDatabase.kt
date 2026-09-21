package com.example.seall.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.seall.data.model.Order

@Database(
    entities = [Order::class],
    version = 1,
    exportSchema = false
)
abstract class SeallDatabase : RoomDatabase() {

    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: SeallDatabase? = null

        fun getInstance(context: Context): SeallDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SeallDatabase::class.java,
                    "seall.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
