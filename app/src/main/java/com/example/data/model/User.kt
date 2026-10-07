package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
  BUYER,
  SELLER,
  BOTH
}

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey
  val id: String, // e.g. "HK-1001"
  val fullName: String,
  val phone: String,
  val email: String,
  val passwordHash: String,
  val role: String, // "BUYER", "SELLER", "BOTH"
  val avatarId: String = "avatar_1",
  val rating: Float = 4.8f,
  val reviewCount: Int = 12,
  val completedDeals: Int = 18,
  val isPhoneVerified: Boolean = true,
  val memberSince: String = "Jan 2024",
  val bio: String = "Verified recyclable scrap trader in Kathmandu valley.",
  val location: String = "Kathmandu, Nepal",
  val latitude: Double = 27.7172,
  val longitude: Double = 85.3240
)
