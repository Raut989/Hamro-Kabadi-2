package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "market_prices")
data class MarketPriceEntity(
  @PrimaryKey
  val categoryKey: String, // e.g. "IRON", "COPPER", etc.
  val materialName: String,
  val marketPricePerKg: Double,
  val suggestedSellerPrice: Double,
  val suggestedBuyerPrice: Double,
  val unit: String = "KG",
  val updatedDate: String,
  val priceHistory: String = "45,47,48,50" // Comma-separated past prices for trend
)
