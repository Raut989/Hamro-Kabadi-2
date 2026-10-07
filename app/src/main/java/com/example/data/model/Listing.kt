package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ScrapCategory(val displayName: String, val iconName: String) {
  IRON("Iron", "iron"),
  STEEL("Steel", "steel"),
  ALUMINIUM("Aluminium", "aluminium"),
  COPPER("Copper", "copper"),
  BRASS("Brass", "brass"),
  PLASTIC("Plastic", "plastic"),
  PAPER("Paper", "paper"),
  CARDBOARD("Cardboard", "cardboard"),
  GLASS("Glass", "glass"),
  E_WASTE("Electronic Waste", "ewaste"),
  BATTERIES("Batteries", "battery");

  companion object {
    fun fromName(name: String): ScrapCategory {
      return entries.firstOrNull {
        it.name.equals(name, ignoreCase = true) || it.displayName.equals(name, ignoreCase = true)
      } ?: IRON
    }
  }
}

@Entity(tableName = "listings")
data class ListingEntity(
  @PrimaryKey
  val id: String,
  val sellerId: String,
  val sellerName: String,
  val sellerPhone: String,
  val sellerRating: Float = 4.8f,
  val sellerReviewCount: Int = 14,
  val title: String,
  val category: String, // String representation of ScrapCategory
  val weightKg: Double,
  val pricePerKg: Double,
  val totalPrice: Double,
  val description: String,
  val locationName: String,
  val latitude: Double,
  val longitude: Double,
  val photoDrawable: String = "photo_default",
  val status: String = "ACTIVE", // ACTIVE, NEGOTIATING, SOLD
  val createdAt: Long = System.currentTimeMillis()
)
