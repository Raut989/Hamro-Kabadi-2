package com.example.ui.screens.transactions

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionStatus
import com.example.data.model.UserEntity
import com.example.ui.components.RatingStars
import com.example.ui.components.ScrapCategoryIcon
import com.example.ui.components.TransactionStatusChip
import com.example.ui.theme.CopperAmber
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MintGreenContainer
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
  currentUser: UserEntity?,
  transactions: List<TransactionEntity>,
  onUpdateStatus: (String, TransactionStatus) -> Unit,
  onSubmitReview: (String, String, Float, String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedFilter by remember { mutableStateOf("ALL") } // ALL, ACTIVE, COMPLETED
  var reviewDialogTxn by remember { mutableStateOf<TransactionEntity?>(null) }
  var ratingScore by remember { mutableStateOf(5.0f) }
  var reviewCommentInput by remember { mutableStateOf("") }

  val filteredTransactions = remember(transactions, selectedFilter) {
    when (selectedFilter) {
      "ACTIVE" -> transactions.filter { it.status != "COMPLETED" && it.status != "CANCELLED" }
      "COMPLETED" -> transactions.filter { it.status == "COMPLETED" }
      else -> transactions
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Transactions & Deals", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("कवाडी खरिद-बिक्री सम्झौता", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      // Filter Tabs
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf("ALL" to "All Deals", "ACTIVE" to "In Progress", "COMPLETED" to "Completed").forEach { (key, label) ->
          FilterChip(
            selected = selectedFilter == key,
            onClick = { selectedFilter = key },
            label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
            shape = RoundedCornerShape(16.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MintGreenContainer,
              selectedLabelColor = ForestGreenPrimary
            ),
            modifier = Modifier.testTag("txn_tab_${key.lowercase()}")
          )
        }
      }

      if (filteredTransactions.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(56.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("No transactions found", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
              "Agreed scrap deals will show up here with live status tracking.",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      } else {
        LazyColumn(
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          items(filteredTransactions, key = { it.id }) { txn ->
            val isBuyer = currentUser?.id == txn.buyerId
            val isReviewed = if (isBuyer) txn.buyerReviewed else txn.sellerReviewed

            TransactionCard(
              txn = txn,
              isBuyer = isBuyer,
              isReviewed = isReviewed,
              onStatusChange = { newStatus -> onUpdateStatus(txn.id, newStatus) },
              onOpenReview = { reviewDialogTxn = txn },
              modifier = Modifier.testTag("txn_card_${txn.id}")
            )
          }
        }
      }
    }
  }

  // Rate & Review Dialog
  if (reviewDialogTxn != null) {
    val targetTxn = reviewDialogTxn!!
    val isBuyer = currentUser?.id == targetTxn.buyerId
    val targetUserId = if (isBuyer) targetTxn.sellerId else targetTxn.buyerId
    val targetUserName = if (isBuyer) targetTxn.sellerName else targetTxn.buyerName

    AlertDialog(
      onDismissRequest = { reviewDialogTxn = null },
      title = { Text("Rate & Review $targetUserName", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text(
            "Share your experience dealing ${targetTxn.weightKg} KG ${targetTxn.category} scrap.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(14.dp))

          // Star Rating Selector
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
          ) {
            (1..5).forEach { star ->
              IconButton(onClick = { ratingScore = star.toFloat() }) {
                Icon(
                  imageVector = if (star <= ratingScore) Icons.Default.Star else Icons.Default.StarBorder,
                  contentDescription = "$star stars",
                  tint = Color(0xFFFFB300),
                  modifier = Modifier.size(32.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = reviewCommentInput,
            onValueChange = { reviewCommentInput = it },
            label = { Text("Write your feedback") },
            placeholder = { Text("Prompt pickup, fair weight measurement, polite trader...") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth().testTag("review_comment_input")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onSubmitReview(
              targetTxn.id,
              targetUserId,
              ratingScore,
              reviewCommentInput.ifBlank { "Great recyclable scrap transaction!" }
            )
            reviewDialogTxn = null
            reviewCommentInput = ""
          },
          modifier = Modifier.testTag("submit_review_button")
        ) {
          Text("Submit Review")
        }
      },
      dismissButton = {
        TextButton(onClick = { reviewDialogTxn = null }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun TransactionCard(
  txn: TransactionEntity,
  isBuyer: Boolean,
  isReviewed: Boolean,
  onStatusChange: (TransactionStatus) -> Unit,
  onOpenReview: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: ID & Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          ScrapCategoryIcon(categoryName = txn.category, modifier = Modifier.size(36.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(txn.listingTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("Deal ID: ${txn.id}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
        TransactionStatusChip(status = txn.status)
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Price & Weight Summary
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Agreed Rate", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
              "Rs. ${txn.agreedPricePerKg.toInt()} / KG (${txn.weightKg} KG)",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

          Column(horizontalAlignment = Alignment.End) {
            Text("Total Payable", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
              "Rs. ${txn.totalAgreedPrice.toInt()}",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 16.sp,
              color = CopperAmber
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Counterparty & Location
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = if (isBuyer) "Seller: ${txn.sellerName}" else "Buyer: ${txn.buyerName}",
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.LocationOn, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(2.dp))
          Text(txn.pickupLocation, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
      Spacer(modifier = Modifier.height(10.dp))

      // Status Progression & Action Buttons
      when (txn.status.uppercase()) {
        "CONFIRMED" -> {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = { onStatusChange(TransactionStatus.CANCELLED) },
              colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
              modifier = Modifier.weight(1f).height(38.dp).testTag("cancel_deal_button")
            ) {
              Text("Cancel Deal", fontSize = 12.sp)
            }
            Button(
              onClick = { onStatusChange(TransactionStatus.PICKED_UP) },
              modifier = Modifier.weight(1.2f).height(38.dp).testTag("confirm_pickup_button")
            ) {
              Text("Confirm Pickup", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
        "PICKED_UP" -> {
          Button(
            onClick = { onStatusChange(TransactionStatus.COMPLETED) },
            modifier = Modifier.fillMaxWidth().height(40.dp).testTag("mark_completed_button")
          ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Complete Transaction", fontWeight = FontWeight.Bold)
          }
        }
        "COMPLETED" -> {
          if (!isReviewed) {
            Button(
              onClick = onOpenReview,
              colors = ButtonDefaults.buttonColors(containerColor = CopperAmber),
              modifier = Modifier.fillMaxWidth().height(38.dp).testTag("rate_seller_button")
            ) {
              Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Rate & Review Trader", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          } else {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(Icons.Default.Verified, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Review Submitted • Deal Complete", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
            }
          }
        }
        "CANCELLED" -> {
          Text(
            "Transaction Cancelled",
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}
