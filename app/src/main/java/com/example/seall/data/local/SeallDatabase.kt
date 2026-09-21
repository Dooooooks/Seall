package com.example.seall.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.seall.data.model.Ingredient
import com.example.seall.data.model.Order
import com.example.seall.data.model.StockIngredient
import com.example.seall.data.model.StockItem

@Database(
    entities = [Order::class, StockItem::class, Ingredient::class, StockIngredient::class],
    version = 5,
    exportSchema = false
)
abstract class SeallDatabase : RoomDatabase() {

    abstract fun orderDao(): OrderDao
    abstract fun stockDao(): StockDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun stockIngredientDao(): StockIngredientDao

    companion object {
        @Volatile
        private var INSTANCE: SeallDatabase? = null

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `ingredients` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `price` REAL NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `orders` ADD COLUMN `itemsSummary` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `orders` ADD COLUMN `itemsJson` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `orders` ADD COLUMN `totalItemCount` INTEGER NOT NULL DEFAULT 1")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `stock_ingredients` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `stockId` INTEGER NOT NULL,
                        `name` TEXT NOT NULL,
                        `quantity` TEXT NOT NULL,
                        `cost` REAL NOT NULL DEFAULT 0.0,
                        FOREIGN KEY(`stockId`) REFERENCES `stocks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_stock_ingredients_stockId` ON `stock_ingredients` (`stockId`)")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `stocks` ADD COLUMN `quantity` INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getInstance(context: Context): SeallDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SeallDatabase::class.java,
                    "seall.db"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
