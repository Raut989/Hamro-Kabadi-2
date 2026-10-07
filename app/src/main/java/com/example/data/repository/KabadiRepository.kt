package com.example.data.repository

import android.content.Context
import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

class KabadiRepository(context: Context) {
  private val db = AppDatabase.getDatabase(context)
  private val userDao = db.userDao()
  private val listingDao = db.listingDao()
  private val marketPriceDao = db.marketPriceDao()
  private val chatDao = db.chatDao()
  private val transactionDao = db.transactionDao()
  private val reviewDao = db.reviewDao()

  private val _currentUser = MutableStateFlow<UserEntity?>(null)
  val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

  // Simulated OTP storage (phone to generated OTP)
  private val otpCache = mutableMapOf<String, String>()

  init {
    // Check if initial user is already in DB; if not, initialize seed data asynchronously
    CoroutineScope(Dispatchers.IO).launch {
      val existing = userDao.getUserById("HK-1002")
      if (existing == null) {
        SeedData.defaultUsers.forEach { userDao.insertUser(it) }
        listingDao.insertAllListings(SeedData.defaultListings)
        marketPriceDao.insertAll(SeedData.defaultMarketPrices)
        transactionDao.insertAllTransactions(SeedData.defaultTransactions)
        reviewDao.insertAllReviews(SeedData.defaultReviews)
      }
      // Default logged in as demo Buyer "HK-1002" or null if user logs out
      _currentUser.value = userDao.getUserById("HK-1002") ?: SeedData.defaultUsers[1]
    }
  }

  // --- Auth & Profile ---
  suspend fun login(identifier: String, password: String): Result<UserEntity> {
    val cleanId = identifier.trim()
    val user = userDao.getUserByPhoneOrUserId(cleanId)
      ?: return Result.failure(Exception("Incorrect User ID or password."))

    val hashed = SecurityUtils.hashPassword(password)
    if (user.passwordHash != hashed) {
      return Result.failure(Exception("Incorrect User ID or password."))
    }

    _currentUser.value = user
    return Result.success(user)
  }

  suspend fun register(
    fullName: String,
    phone: String,
    email: String,
    password: String,
    role: String,
    avatarId: String,
    location: String
  ): Result<UserEntity> {
    val existing = userDao.getUserByPhone(phone.trim())
    if (existing != null) {
      return Result.failure(Exception("Phone number already registered. Please sign in."))
    }

    // Generate unique User ID e.g. HK-5842
    val randomId = "HK-" + Random.nextInt(1000, 9999)
    val newUser = UserEntity(
      id = randomId,
      fullName = fullName.trim(),
      phone = phone.trim(),
      email = email.trim(),
      passwordHash = SecurityUtils.hashPassword(password),
      role = role,
      avatarId = avatarId,
      rating = 5.0f,
      reviewCount = 0,
      completedDeals = 0,
      isPhoneVerified = true,
      memberSince = "Today",
      bio = if (role == "BUYER") "Recyclable materials buyer and processor." else "Household & workshop scrap seller.",
      location = location.ifBlank { "Kathmandu, Nepal" },
      latitude = 27.7172,
      longitude = 85.3240
    )

    userDao.insertUser(newUser)
    _currentUser.value = newUser
    return Result.success(newUser)
  }

  fun requestOtp(phone: String): String {
    val code = Random.nextInt(100000, 999999).toString()
    otpCache[phone.trim()] = code
    return code
  }

  fun verifyOtp(phone: String, otp: String): Boolean {
    val cached = otpCache[phone.trim()] ?: "123456" // Fallback demo code
    return cached == otp.trim() || otp.trim() == "123456"
  }

  suspend fun resetPassword(phone: String, newPassword: String): Boolean {
    val user = userDao.getUserByPhone(phone.trim()) ?: return false
    val updated = user.copy(passwordHash = SecurityUtils.hashPassword(newPassword))
    userDao.updateUser(updated)
    return true
  }

  fun logout() {
    _currentUser.value = null
  }

  suspend fun switchActiveRole(newRole: String) {
    val user = _currentUser.value ?: return
    val updated = user.copy(role = newRole)
    userDao.updateUser(updated)
    _currentUser.value = updated
  }

  suspend fun updateUserProfile(
    fullName: String,
    phone: String,
    email: String,
    bio: String,
    location: String
  ) {
    val current = _currentUser.value ?: return
    val updated = current.copy(
      fullName = fullName,
      phone = phone,
      email = email,
      bio = bio,
      location = location
    )
    userDao.updateUser(updated)
    _currentUser.value = updated
  }

  suspend fun getUserById(userId: String): UserEntity? {
    return userDao.getUserById(userId)
  }

  // --- Listings ---
  val allListings: Flow<List<ListingEntity>> = listingDao.getAllListings()

  suspend fun getListingById(id: String): ListingEntity? {
    return listingDao.getListingById(id)
  }

  fun getListingsBySeller(sellerId: String): Flow<List<ListingEntity>> {
    return listingDao.getListingsBySeller(sellerId)
  }

  suspend fun createListing(
    title: String,
    category: String,
    weightKg: Double,
    pricePerKg: Double,
    description: String,
    locationName: String,
    latitude: Double,
    longitude: Double,
    photoDrawable: String
  ): ListingEntity {
    val seller = _currentUser.value
    val sellerId = seller?.id ?: "HK-1001"
    val sellerName = seller?.fullName ?: "Ram Bahadur Shrestha"
    val sellerPhone = seller?.phone ?: "9841234567"
    val sellerRating = seller?.rating ?: 4.8f
    val sellerReviews = seller?.reviewCount ?: 12

    val listingId = "LST-" + Random.nextInt(400, 9999)
    val entity = ListingEntity(
      id = listingId,
      sellerId = sellerId,
      sellerName = sellerName,
      sellerPhone = sellerPhone,
      sellerRating = sellerRating,
      sellerReviewCount = sellerReviews,
      title = title,
      category = category,
      weightKg = weightKg,
      pricePerKg = pricePerKg,
      totalPrice = weightKg * pricePerKg,
      description = description,
      locationName = locationName,
      latitude = latitude,
      longitude = longitude,
      photoDrawable = photoDrawable,
      status = "ACTIVE",
      createdAt = System.currentTimeMillis()
    )
    listingDao.insertListing(entity)
    return entity
  }

  suspend fun updateListing(listing: ListingEntity) {
    listingDao.updateListing(listing)
  }

  suspend fun deleteListing(id: String) {
    listingDao.deleteListing(id)
  }

  suspend fun markListingAsSold(id: String) {
    val listing = listingDao.getListingById(id) ?: return
    listingDao.updateListing(listing.copy(status = "SOLD"))
  }

  // --- Market Prices ---
  val allMarketPrices: Flow<List<MarketPriceEntity>> = marketPriceDao.getAllPrices()

  suspend fun getMarketPrice(categoryKey: String): MarketPriceEntity? {
    return marketPriceDao.getPriceByCategory(categoryKey)
  }

  suspend fun updateMarketPrice(categoryKey: String, newMarketPrice: Double) {
    val existing = marketPriceDao.getPriceByCategory(categoryKey) ?: return
    val sellerSuggested = (newMarketPrice * 0.82).roundTo(1)
    val buyerSuggested = (newMarketPrice * 0.95).roundTo(1)
    val newHistory = existing.priceHistory + ",${newMarketPrice.toInt()}"

    val updated = existing.copy(
      marketPricePerKg = newMarketPrice,
      suggestedSellerPrice = sellerSuggested,
      suggestedBuyerPrice = buyerSuggested,
      updatedDate = "Just now",
      priceHistory = newHistory
    )
    marketPriceDao.insertOrUpdate(updated)
  }

  // --- Chat & Negotiations ---
  fun getMessagesForListing(listingId: String): Flow<List<ChatMessageEntity>> {
    return chatDao.getMessagesForListing(listingId)
  }

  suspend fun sendTextMessage(
    listingId: String,
    senderId: String,
    senderName: String,
    receiverId: String,
    text: String
  ) {
    val msg = ChatMessageEntity(
      id = "MSG-" + Random.nextInt(10000, 99999),
      listingId = listingId,
      senderId = senderId,
      senderName = senderName,
      receiverId = receiverId,
      messageType = "TEXT",
      content = text
    )
    chatDao.insertMessage(msg)
  }

  suspend fun sendOfferMessage(
    listingId: String,
    senderId: String,
    senderName: String,
    receiverId: String,
    offerPricePerKg: Double,
    weightKg: Double
  ) {
    val total = offerPricePerKg * weightKg
    val msg = ChatMessageEntity(
      id = "MSG-" + Random.nextInt(10000, 99999),
      listingId = listingId,
      senderId = senderId,
      senderName = senderName,
      receiverId = receiverId,
      messageType = "OFFER",
      content = "Price Offer: Rs. $offerPricePerKg / KG (Total: Rs. ${total.toInt()})",
      offerPricePerKg = offerPricePerKg,
      offerTotalPrice = total,
      offerStatus = "PENDING"
    )
    chatDao.insertMessage(msg)
  }

  suspend fun acceptOffer(
    message: ChatMessageEntity,
    listing: ListingEntity,
    buyerUser: UserEntity,
    sellerUser: UserEntity
  ): TransactionEntity {
    // 1. Update message status
    val updatedMsg = message.copy(offerStatus = "ACCEPTED")
    chatDao.updateMessage(updatedMsg)

    // 2. Post system message confirming acceptance
    val agreedPerKg = message.offerPricePerKg ?: listing.pricePerKg
    val totalAgreed = message.offerTotalPrice ?: listing.totalPrice

    val systemMsg = ChatMessageEntity(
      id = "MSG-" + Random.nextInt(10000, 99999),
      listingId = listing.id,
      senderId = "SYSTEM",
      senderName = "Hamro Kabadi",
      receiverId = message.senderId,
      messageType = "SYSTEM",
      content = "🤝 Offer of Rs. $agreedPerKg/KG (Total Rs. ${totalAgreed.toInt()}) accepted! Transaction created."
    )
    chatDao.insertMessage(systemMsg)

    // 3. Create or update Transaction
    val txnId = "TXN-" + Random.nextInt(5000, 9999)
    val transaction = TransactionEntity(
      id = txnId,
      listingId = listing.id,
      listingTitle = listing.title,
      category = listing.category,
      weightKg = listing.weightKg,
      agreedPricePerKg = agreedPerKg,
      totalAgreedPrice = totalAgreed,
      buyerId = buyerUser.id,
      buyerName = buyerUser.fullName,
      buyerPhone = buyerUser.phone,
      sellerId = sellerUser.id,
      sellerName = sellerUser.fullName,
      sellerPhone = sellerUser.phone,
      pickupLocation = listing.locationName,
      pickupDateTime = "Ready for scheduling",
      status = "CONFIRMED"
    )
    transactionDao.insertTransaction(transaction)

    // 4. Update listing status to NEGOTIATING or SOLD
    listingDao.updateListing(listing.copy(status = "NEGOTIATING"))
    return transaction
  }

  suspend fun declineOffer(message: ChatMessageEntity) {
    val updated = message.copy(offerStatus = "DECLINED")
    chatDao.updateMessage(updated)
  }

  suspend fun sendLocationMessage(
    listingId: String,
    senderId: String,
    senderName: String,
    receiverId: String,
    address: String,
    lat: Double,
    lng: Double
  ) {
    val msg = ChatMessageEntity(
      id = "MSG-" + Random.nextInt(10000, 99999),
      listingId = listingId,
      senderId = senderId,
      senderName = senderName,
      receiverId = receiverId,
      messageType = "LOCATION",
      content = "Pickup Location: $address",
      locationAddress = address,
      locationLatitude = lat,
      locationLongitude = lng
    )
    chatDao.insertMessage(msg)
  }

  // --- Transactions ---
  fun getUserTransactions(userId: String): Flow<List<TransactionEntity>> {
    return transactionDao.getTransactionsForUser(userId)
  }

  suspend fun updateTransactionStatus(transactionId: String, status: TransactionStatus) {
    val existing = transactionDao.getTransactionById(transactionId) ?: return
    val updated = existing.copy(
      status = status.name,
      updatedAt = System.currentTimeMillis()
    )
    transactionDao.updateTransaction(updated)
  }

  suspend fun addReview(
    transactionId: String,
    targetUserId: String,
    reviewerId: String,
    reviewerName: String,
    rating: Float,
    comment: String
  ) {
    val revId = "REV-" + Random.nextInt(200, 9999)
    val review = ReviewEntity(
      id = revId,
      targetUserId = targetUserId,
      reviewerId = reviewerId,
      reviewerName = reviewerName,
      transactionId = transactionId,
      rating = rating,
      comment = comment
    )
    reviewDao.insertReview(review)

    // Update target user's rating & review count
    val target = userDao.getUserById(targetUserId)
    if (target != null) {
      val newCount = target.reviewCount + 1
      val newRating = ((target.rating * target.reviewCount) + rating) / newCount
      val updatedTarget = target.copy(
        reviewCount = newCount,
        rating = (newRating * 10).roundToInt() / 10f,
        completedDeals = target.completedDeals + 1
      )
      userDao.updateUser(updatedTarget)
    }

    // Mark transaction as reviewed
    val txn = transactionDao.getTransactionById(transactionId)
    if (txn != null) {
      val isBuyer = txn.buyerId == reviewerId
      val updatedTxn = if (isBuyer) txn.copy(buyerReviewed = true) else txn.copy(sellerReviewed = true)
      transactionDao.updateTransaction(updatedTxn)
    }
  }

  fun getReviewsForUser(userId: String): Flow<List<ReviewEntity>> {
    return reviewDao.getReviewsForUser(userId)
  }

  // Haversine distance in KM
  fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0 // Earth's radius in KM
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return (r * c).roundTo(1)
  }

  private fun Double.roundTo(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return (this * multiplier).roundToInt() / multiplier
  }
}
