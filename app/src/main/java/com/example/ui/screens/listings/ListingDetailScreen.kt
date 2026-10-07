package com.example.ui.screens.listings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ListingEntity
import com.example.data.model.MarketPriceEntity
import com.example.data.model.UserEntity
import com.example.ui.components.*
import com.example.ui.theme.CopperAmber
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MintGreenContainer
import com.example.ui.theme.OnMintGreenContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailScreen(
  listing: ListingEntity,
  currentUser: UserEntity?,
  marketPrice: MarketPriceEntity?,
  distanceKm: Double,
  onBack: () -> Unit,
  onStartChat: () -> Unit,
  onViewSellerProfile: (String) -> Unit,
  onMarkAsSold: () -> Unit,
  onDeleteListing: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isOwner = currentUser?.id == listing.sellerId

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(listing.title, maxLines = 1, fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_button")) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          if (isOwner) {
            IconButton(
              onClick = onDeleteListing,
              modifier = Modifier.testTag("detail_delete_button")
            ) {
              Icon(Icons.Default.Delete, contentDescription = "Delete Listing", tint = MaterialTheme.colorScheme.error)
            }
          }
        }
      )
    },
    bottomBar = {
      Surface(
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          if (isOwner) {
            Button(
              onClick = onMarkAsSold,
              enabled = listing.status != "SOLD",
              colors = ButtonDefaults.buttonColors(containerColor = if (listing.status == "SOLD") Color.Gray else ForestGreenPrimary),
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("detail_mark_sold_button")
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(if (listing.status == "SOLD") "Item Sold" else "Mark as Sold", fontWeight = FontWeight.Bold)
            }
          } else {
            // Call Seller Button
            OutlinedButton(
              onClick = {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${listing.sellerPhone}"))
                context.startActivity(intent)
              },
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("detail_call_seller_button")
            ) {
              Icon(Icons.Default.Phone, contentDescription = null, tint = ForestGreenPrimary)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Call Seller", color = ForestGreenPrimary, fontWeight = FontWeight.Bold)
            }

            // Chat & Negotiate Button
            Button(
              onClick = onStartChat,
              modifier = Modifier
                .weight(1.3f)
                .height(48.dp)
                .testTag("detail_chat_negotiate_button")
            ) {
              Icon(Icons.Default.Chat, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Chat & Negotiate", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
      // 1. Scrap Item Header Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            ScrapCategoryIcon(categoryName = listing.category, modifier = Modifier.size(54.dp))
            DistanceIndicator(distanceKm = distanceKm)
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = listing.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(4.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MintGreenContainer
            ) {
              Text(
                text = "${listing.weightKg} KG",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = OnMintGreenContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }

            Text(
              text = "Category: ${listing.category}",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (listing.status != "ACTIVE") {
              TransactionStatusChip(status = listing.status)
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
          HorizontalDivider()
          Spacer(modifier = Modifier.height(16.dp))

          // Price Comparison Section (Market Price vs Asking Price)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = "Seller's Asking Price",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "Rs. ${listing.pricePerKg.toInt()} / KG",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CopperAmber
              )
              Text(
                text = "Total: Rs. ${listing.totalPrice.toInt()}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "Govt / Market Rate",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              val marketRate = marketPrice?.marketPricePerKg ?: listing.pricePerKg
              Text(
                text = "Rs. ${marketRate.toInt()} / KG",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary
              )
              Text(
                text = "Suggested buy: Rs. ${(marketPrice?.suggestedBuyerPrice ?: (marketRate * 0.95)).toInt()}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Description Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("Material Description", fontWeight = FontWeight.Bold, fontSize = 15.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = listing.description.ifBlank { "No additional notes provided by seller." },
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3. Pickup Location & Map Navigation Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Pickup Location", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            OutlinedButton(
              onClick = {
                val uri = Uri.parse("geo:${listing.latitude},${listing.longitude}?q=${listing.latitude},${listing.longitude}(${Uri.encode(listing.locationName)})")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
              },
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.height(34.dp).testTag("detail_directions_button")
            ) {
              Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Directions", fontSize = 12.sp)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = ForestGreenPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(listing.locationName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
              Text("Lat: ${listing.latitude}, Lng: ${listing.longitude}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 4. Seller Profile Card
      Card(
        onClick = { onViewSellerProfile(listing.sellerId) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().testTag("detail_seller_profile_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("Seller Information", fontWeight = FontWeight.Bold, fontSize = 15.sp)
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              AvatarView(name = listing.sellerName, sizeDp = 48)
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(listing.sellerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  RatingStars(rating = listing.sellerRating, reviewCount = listing.sellerReviewCount)
                  Spacer(modifier = Modifier.width(6.dp))
                  VerifiedBadge(text = "Phone Verified")
                }
              }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = "View Profile", tint = Color.Gray)
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
