package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionStatus(val label: String) {
  PENDING("Pending"),
  NEGOTIATING("Negotiating"),
  CONFIRMED("Confirmed"),
  PICKED_UP("Picked Up"),
  COMPLETED("Completed"),
  CANCELLED("Cancelled")
}

@Entity(tableName = "transactions")
data class TransactionEntity(
  @PrimaryKey
  val id: String, // e.g. "TXN-8291"
  val listingId: String,
  val listingTitle: String,
  val category: String,
  val weightKg: Double,
  val agreedPricePerKg: Double,
  val totalAgreedPrice: Double,
  val buyerId: String,
  val buyerName: String,
  val buyerPhone: String,
  val sellerId: String,
  val sellerName: String,
  val sellerPhone: String,
  val pickupLocation: String,
  val pickupDateTime: String,
  val status: String = "CONFIRMED", // TransactionStatus
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val buyerReviewed: Boolean = false,
  val sellerReviewed: Boolean = false
)
