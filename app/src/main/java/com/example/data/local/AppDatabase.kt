package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    UserEntity::class,
    ListingEntity::class,
    MarketPriceEntity::class,
    ChatMessageEntity::class,
    TransactionEntity::class,
    ReviewEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
  abstract fun listingDao(): ListingDao
  abstract fun marketPriceDao(): MarketPriceDao
  abstract fun chatDao(): ChatDao
  abstract fun transactionDao(): TransactionDao
  abstract fun reviewDao(): ReviewDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "hamro_kabadi_db"
        )
          .addCallback(object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
              super.onCreate(db)
              CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.let { database ->
                  SeedData.defaultUsers.forEach { database.userDao().insertUser(it) }
                  database.listingDao().insertAllListings(SeedData.defaultListings)
                  database.marketPriceDao().insertAll(SeedData.defaultMarketPrices)
                  database.transactionDao().insertAllTransactions(SeedData.defaultTransactions)
                  database.reviewDao().insertAllReviews(SeedData.defaultReviews)
                }
              }
            }
          })
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
