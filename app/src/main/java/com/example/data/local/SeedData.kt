package com.example.data.local

import com.example.data.model.*
import java.security.MessageDigest

object SecurityUtils {
  fun hashPassword(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
    return hash.joinToString("") { "%02x".format(it) }
  }
}

object SeedData {
  val defaultUsers = listOf(
    UserEntity(
      id = "HK-1001",
      fullName = "Ram Bahadur Shrestha",
      phone = "9841234567",
      email = "ram.shrestha@gmail.com",
      passwordHash = SecurityUtils.hashPassword("password123"),
      role = "SELLER",
      avatarId = "avatar_ram",
      rating = 4.8f,
      reviewCount = 18,
      completedDeals = 24,
      isPhoneVerified = true,
      memberSince = "March 2023",
      bio = "Reliable scrap collector with regular bulk metals and home clearing scrap.",
      location = "Baneshwor, Kathmandu",
      latitude = 27.6934,
      longitude = 85.3412
    ),
    UserEntity(
      id = "HK-1002",
      fullName = "Suman Thapa",
      phone = "9851098765",
      email = "suman.recycle@gmail.com",
      passwordHash = SecurityUtils.hashPassword("password123"),
      role = "BUYER",
      avatarId = "avatar_suman",
      rating = 4.9f,
      reviewCount = 35,
      completedDeals = 42,
      isPhoneVerified = true,
      memberSince = "January 2023",
      bio = "Licensed recyclable scrap trader and recycler in Kathmandu & Lalitpur.",
      location = "Patan Dhoka, Lalitpur",
      latitude = 27.6744,
      longitude = 85.3235
    ),
    UserEntity(
      id = "HK-1003",
      fullName = "Bikash Metal Industries",
      phone = "9818765432",
      email = "bikash.scrap@outlook.com",
      passwordHash = SecurityUtils.hashPassword("password123"),
      role = "BOTH",
      avatarId = "avatar_bikash",
      rating = 4.7f,
      reviewCount = 12,
      completedDeals = 16,
      isPhoneVerified = true,
      memberSince = "August 2023",
      bio = "Bulk industrial scrap buyer and household e-waste consolidator.",
      location = "Koteshwor, Kathmandu",
      latitude = 27.6775,
      longitude = 85.3485
    )
  )

  val defaultMarketPrices = listOf(
    MarketPriceEntity(
      categoryKey = "IRON",
      materialName = "Iron Scrap (फलाम)",
      marketPricePerKg = 50.0,
      suggestedSellerPrice = 40.0,
      suggestedBuyerPrice = 48.0,
      unit = "KG",
      updatedDate = "Today, 10:00 AM",
      priceHistory = "44,46,48,47,50"
    ),
    MarketPriceEntity(
      categoryKey = "STEEL",
      materialName = "Stainless Steel (स्टिल)",
      marketPricePerKg = 65.0,
      suggestedSellerPrice = 55.0,
      suggestedBuyerPrice = 62.0,
      unit = "KG",
      updatedDate = "Today, 10:00 AM",
      priceHistory = "58,60,62,64,65"
    ),
    MarketPriceEntity(
      categoryKey = "ALUMINIUM",
      materialName = "Aluminium (आल्मुनियम)",
      marketPricePerKg = 160.0,
      suggestedSellerPrice = 140.0,
      suggestedBuyerPrice = 155.0,
      unit = "KG",
      updatedDate = "Yesterday",
      priceHistory = "145,150,152,158,160"
    ),
    MarketPriceEntity(
      categoryKey = "COPPER",
      materialName = "Copper Wire & Pipe (तामा)",
      marketPricePerKg = 850.0,
      suggestedSellerPrice = 780.0,
      suggestedBuyerPrice = 830.0,
      unit = "KG",
      updatedDate = "Today, 09:30 AM",
      priceHistory = "810,820,835,840,850"
    ),
    MarketPriceEntity(
      categoryKey = "BRASS",
      materialName = "Brass Utensils (पित्तल)",
      marketPricePerKg = 520.0,
      suggestedSellerPrice = 470.0,
      suggestedBuyerPrice = 505.0,
      unit = "KG",
      updatedDate = "Oct 05, 2026",
      priceHistory = "490,500,505,515,520"
    ),
    MarketPriceEntity(
      categoryKey = "PLASTIC",
      materialName = "Rigid & Pet Plastic (प्लास्टिक)",
      marketPricePerKg = 28.0,
      suggestedSellerPrice = 22.0,
      suggestedBuyerPrice = 26.0,
      unit = "KG",
      updatedDate = "Oct 06, 2026",
      priceHistory = "24,25,26,27,28"
    ),
    MarketPriceEntity(
      categoryKey = "PAPER",
      materialName = "Old Newspapers & Books (कागज/पत्रिका)",
      marketPricePerKg = 18.0,
      suggestedSellerPrice = 14.0,
      suggestedBuyerPrice = 17.0,
      unit = "KG",
      updatedDate = "Today, 08:00 AM",
      priceHistory = "15,16,16,17,18"
    ),
    MarketPriceEntity(
      categoryKey = "CARDBOARD",
      materialName = "Cardboard Cartons (कार्टुन)",
      marketPricePerKg = 15.0,
      suggestedSellerPrice = 12.0,
      suggestedBuyerPrice = 14.0,
      unit = "KG",
      updatedDate = "Today, 08:00 AM",
      priceHistory = "12,13,13,14,15"
    ),
    MarketPriceEntity(
      categoryKey = "GLASS",
      materialName = "Glass Bottles & Cullet (सिसा)",
      marketPricePerKg = 8.0,
      suggestedSellerPrice = 5.0,
      suggestedBuyerPrice = 7.0,
      unit = "KG",
      updatedDate = "Oct 04, 2026",
      priceHistory = "6,7,7,8,8"
    ),
    MarketPriceEntity(
      categoryKey = "E_WASTE",
      materialName = "Electronic Waste (ई-फोहोर)",
      marketPricePerKg = 120.0,
      suggestedSellerPrice = 95.0,
      suggestedBuyerPrice = 115.0,
      unit = "KG",
      updatedDate = "Today, 10:15 AM",
      priceHistory = "100,105,110,115,120"
    ),
    MarketPriceEntity(
      categoryKey = "BATTERIES",
      materialName = "Lead-Acid Batteries (ब्याट्री)",
      marketPricePerKg = 110.0,
      suggestedSellerPrice = 90.0,
      suggestedBuyerPrice = 105.0,
      unit = "KG",
      updatedDate = "Oct 05, 2026",
      priceHistory = "95,100,102,108,110"
    )
  )

  val defaultListings = listOf(
    ListingEntity(
      id = "LST-301",
      sellerId = "HK-1001",
      sellerName = "Ram Bahadur Shrestha",
      sellerPhone = "9841234567",
      sellerRating = 4.7f,
      sellerReviewCount = 18,
      title = "Clean Iron Scrap Rods & Sheets",
      category = "IRON",
      weightKg = 25.0,
      pricePerKg = 40.0,
      totalPrice = 1000.0,
      description = "Dismantled construction grill rods and window scrap. Clean metal with minimal rust. Ready for instant pickup in Baneshwor.",
      locationName = "New Baneshwor, Kathmandu",
      latitude = 27.6934,
      longitude = 85.3412,
      photoDrawable = "scrap_iron",
      status = "ACTIVE",
      createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 3
    ),
    ListingEntity(
      id = "LST-302",
      sellerId = "HK-1001",
      sellerName = "Ram Bahadur Shrestha",
      sellerPhone = "9841234567",
      sellerRating = 4.7f,
      sellerReviewCount = 18,
      title = "Pure Stripped Copper Electrical Wires",
      category = "COPPER",
      weightKg = 12.0,
      pricePerKg = 780.0,
      totalPrice = 9360.0,
      description = "High grade copper wiring from office renovation. Stripped and weighed on digital scale. Negotiable for quick pickup.",
      locationName = "Shankhamul, Lalitpur",
      latitude = 27.6820,
      longitude = 85.3340,
      photoDrawable = "scrap_copper",
      status = "ACTIVE",
      createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 5
    ),
    ListingEntity(
      id = "LST-303",
      sellerId = "HK-1003",
      sellerName = "Bikash Metal Industries",
      sellerPhone = "9818765432",
      sellerRating = 4.8f,
      sellerReviewCount = 14,
      title = "Office Old Cartons & Corrugated Cardboard",
      category = "CARDBOARD",
      weightKg = 150.0,
      pricePerKg = 13.0,
      totalPrice = 1950.0,
      description = "Tied and bundled shipping boxes from department store. Completely dry and sorted. Ground floor easy loading access.",
      locationName = "Koteshwor Chowk, Kathmandu",
      latitude = 27.6775,
      longitude = 85.3485,
      photoDrawable = "scrap_cardboard",
      status = "ACTIVE",
      createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 12
    ),
    ListingEntity(
      id = "LST-304",
      sellerId = "HK-1003",
      sellerName = "Bikash Metal Industries",
      sellerPhone = "9818765432",
      sellerRating = 4.8f,
      sellerReviewCount = 14,
      title = "Aluminium Window Frame Extrusions",
      category = "ALUMINIUM",
      weightKg = 45.0,
      pricePerKg = 142.0,
      totalPrice = 6390.0,
      description = "Clean aluminium profiles from showroom remodeling. No glass attached, cut into convenient transportable pieces.",
      locationName = "Tinkune, Kathmandu",
      latitude = 27.6845,
      longitude = 85.3520,
      photoDrawable = "scrap_aluminium",
      status = "ACTIVE",
      createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 20
    ),
    ListingEntity(
      id = "LST-305",
      sellerId = "HK-1001",
      sellerName = "Ram Bahadur Shrestha",
      sellerPhone = "9841234567",
      sellerRating = 4.7f,
      sellerReviewCount = 18,
      title = "Heavy Brass Utensils & Fittings",
      category = "BRASS",
      weightKg = 18.0,
      pricePerKg = 475.0,
      totalPrice = 8550.0,
      description = "Old broken brass taps, plumbing joints and old utensils. Genuine heavy yellow brass.",
      locationName = "Patan Dhoka, Lalitpur",
      latitude = 27.6744,
      longitude = 85.3235,
      photoDrawable = "scrap_brass",
      status = "ACTIVE",
      createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24
    ),
    ListingEntity(
      id = "LST-306",
      sellerId = "HK-1003",
      sellerName = "Bikash Metal Industries",
      sellerPhone = "9818765432",
      sellerRating = 4.8f,
      sellerReviewCount = 14,
      title = "Computer CPUs, Power Supplies & Motherboards",
      category = "E_WASTE",
      weightKg = 60.0,
      pricePerKg = 98.0,
      totalPrice = 5880.0,
      description = "Assorted non-working computer parts, circuit boards, server chassis and telecom power supplies.",
      locationName = "Maitighar, Kathmandu",
      latitude = 27.6920,
      longitude = 85.3210,
      photoDrawable = "scrap_ewaste",
      status = "ACTIVE",
      createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 28
    ),
    ListingEntity(
      id = "LST-307",
      sellerId = "HK-1001",
      sellerName = "Ram Bahadur Shrestha",
      sellerPhone = "9841234567",
      sellerRating = 4.7f,
      sellerReviewCount = 18,
      title = "Old Daily Newspapers & Magazines Bundle",
      category = "PAPER",
      weightKg = 85.0,
      pricePerKg = 15.0,
      totalPrice = 1275.0,
      description = "Dry Kantipur, Republica, and office papers tied in clean 10kg bundles. Ready for pickup.",
      locationName = "Kupondole, Lalitpur",
      latitude = 27.6870,
      longitude = 85.3160,
      photoDrawable = "scrap_paper",
      status = "ACTIVE",
      createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 36
    ),
    ListingEntity(
      id = "LST-308",
      sellerId = "HK-1003",
      sellerName = "Bikash Metal Industries",
      sellerPhone = "9818765432",
      sellerRating = 4.8f,
      sellerReviewCount = 14,
      title = "Industrial Rigid Plastic Drums & Crates",
      category = "PLASTIC",
      weightKg = 70.0,
      pricePerKg = 24.0,
      totalPrice = 1680.0,
      description = "Clean HDPE/PP high density plastic scrap, washed empty containers.",
      locationName = "Balkhu, Kathmandu",
      latitude = 27.6830,
      longitude = 85.2990,
      photoDrawable = "scrap_plastic",
      status = "ACTIVE",
      createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 48
    )
  )

  val defaultReviews = listOf(
    ReviewEntity(
      id = "REV-101",
      targetUserId = "HK-1001",
      reviewerId = "HK-1002",
      reviewerName = "Suman Thapa",
      transactionId = "TXN-8201",
      rating = 5.0f,
      comment = "Very polite seller! Metal weight was accurate as listed and pickup was smooth.",
      createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 72
    ),
    ReviewEntity(
      id = "REV-102",
      targetUserId = "HK-1001",
      reviewerId = "HK-1003",
      reviewerName = "Bikash Metal",
      transactionId = "TXN-8202",
      rating = 4.6f,
      comment = "Fair pricing and prompt communication. Recommended Kabadi seller.",
      createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 120
    ),
    ReviewEntity(
      id = "REV-103",
      targetUserId = "HK-1002",
      reviewerId = "HK-1001",
      reviewerName = "Ram Bahadur Shrestha",
      transactionId = "TXN-8201",
      rating = 5.0f,
      comment = "Suman arrived on time with his small truck and paid full agreed amount on the spot.",
      createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 70
    )
  )

  val defaultTransactions = listOf(
    TransactionEntity(
      id = "TXN-8201",
      listingId = "LST-301",
      listingTitle = "Clean Iron Scrap Rods",
      category = "IRON",
      weightKg = 25.0,
      agreedPricePerKg = 40.0,
      totalAgreedPrice = 1000.0,
      buyerId = "HK-1002",
      buyerName = "Suman Thapa",
      buyerPhone = "9851098765",
      sellerId = "HK-1001",
      sellerName = "Ram Bahadur Shrestha",
      sellerPhone = "9841234567",
      pickupLocation = "New Baneshwor, Kathmandu",
      pickupDateTime = "Today, 4:00 PM",
      status = "COMPLETED",
      createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 74,
      updatedAt = System.currentTimeMillis() - 1000 * 60 * 60 * 70,
      buyerReviewed = true,
      sellerReviewed = true
    ),
    TransactionEntity(
      id = "TXN-8202",
      listingId = "LST-303",
      listingTitle = "Office Old Cartons & Corrugated Cardboard",
      category = "CARDBOARD",
      weightKg = 150.0,
      agreedPricePerKg = 13.0,
      totalAgreedPrice = 1950.0,
      buyerId = "HK-1002",
      buyerName = "Suman Thapa",
      buyerPhone = "9851098765",
      sellerId = "HK-1003",
      sellerName = "Bikash Metal Industries",
      sellerPhone = "9818765432",
      pickupLocation = "Koteshwor Chowk, Kathmandu",
      pickupDateTime = "Tomorrow, 10:00 AM",
      status = "CONFIRMED",
      createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 10,
      updatedAt = System.currentTimeMillis() - 1000 * 60 * 60 * 10,
      buyerReviewed = false,
      sellerReviewed = false
    )
  )
}
