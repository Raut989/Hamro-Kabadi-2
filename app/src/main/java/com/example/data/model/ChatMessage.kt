package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
  @PrimaryKey
  val id: String,
  val listingId: String,
  val senderId: String,
  val senderName: String,
  val receiverId: String,
  val messageType: String = "TEXT", // "TEXT", "OFFER", "LOCATION", "SYSTEM"
  val content: String,
  val offerPricePerKg: Double? = null,
  val offerTotalPrice: Double? = null,
  val offerStatus: String? = null, // "PENDING", "ACCEPTED", "DECLINED"
  val locationAddress: String? = null,
  val locationLatitude: Double? = null,
  val locationLongitude: Double? = null,
  val timestamp: Long = System.currentTimeMillis()
)
