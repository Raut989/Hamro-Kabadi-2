package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
  suspend fun getUserById(userId: String): UserEntity?

  @Query("SELECT * FROM users WHERE id = :query OR phone = :query LIMIT 1")
  suspend fun getUserByPhoneOrUserId(query: String): UserEntity?

  @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
  suspend fun getUserByPhone(phone: String): UserEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity)

  @Update
  suspend fun updateUser(user: UserEntity)
}

@Dao
interface ListingDao {
  @Query("SELECT * FROM listings ORDER BY createdAt DESC")
  fun getAllListings(): Flow<List<ListingEntity>>

  @Query("SELECT * FROM listings WHERE id = :listingId LIMIT 1")
  suspend fun getListingById(listingId: String): ListingEntity?

  @Query("SELECT * FROM listings WHERE sellerId = :sellerId ORDER BY createdAt DESC")
  fun getListingsBySeller(sellerId: String): Flow<List<ListingEntity>>

  @Query("SELECT * FROM listings WHERE category = :category ORDER BY createdAt DESC")
  fun getListingsByCategory(category: String): Flow<List<ListingEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertListing(listing: ListingEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllListings(listings: List<ListingEntity>)

  @Update
  suspend fun updateListing(listing: ListingEntity)

  @Query("DELETE FROM listings WHERE id = :listingId")
  suspend fun deleteListing(listingId: String)
}

@Dao
interface MarketPriceDao {
  @Query("SELECT * FROM market_prices ORDER BY materialName ASC")
  fun getAllPrices(): Flow<List<MarketPriceEntity>>

  @Query("SELECT * FROM market_prices WHERE categoryKey = :categoryKey LIMIT 1")
  suspend fun getPriceByCategory(categoryKey: String): MarketPriceEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(price: MarketPriceEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(prices: List<MarketPriceEntity>)
}

@Dao
interface ChatDao {
  @Query("SELECT * FROM chat_messages WHERE listingId = :listingId ORDER BY timestamp ASC")
  fun getMessagesForListing(listingId: String): Flow<List<ChatMessageEntity>>

  @Query("SELECT * FROM chat_messages WHERE senderId = :userId OR receiverId = :userId ORDER BY timestamp DESC")
  fun getAllUserMessages(userId: String): Flow<List<ChatMessageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: ChatMessageEntity)

  @Update
  suspend fun updateMessage(message: ChatMessageEntity)
}

@Dao
interface TransactionDao {
  @Query("SELECT * FROM transactions WHERE buyerId = :userId OR sellerId = :userId ORDER BY updatedAt DESC")
  fun getTransactionsForUser(userId: String): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
  suspend fun getTransactionById(id: String): TransactionEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransaction(transaction: TransactionEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllTransactions(transactions: List<TransactionEntity>)

  @Update
  suspend fun updateTransaction(transaction: TransactionEntity)
}

@Dao
interface ReviewDao {
  @Query("SELECT * FROM reviews WHERE targetUserId = :userId ORDER BY createdAt DESC")
  fun getReviewsForUser(userId: String): Flow<List<ReviewEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReview(review: ReviewEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllReviews(reviews: List<ReviewEntity>)
}
