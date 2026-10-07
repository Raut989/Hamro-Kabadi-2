package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
  @PrimaryKey
  val id: String,
  val targetUserId: String,
  val reviewerId: String,
  val reviewerName: String,
  val transactionId: String,
  val rating: Float, // 1.0 to 5.0
  val comment: String,
  val createdAt: Long = System.currentTimeMillis()
)
