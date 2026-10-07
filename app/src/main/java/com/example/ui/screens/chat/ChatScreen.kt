package com.example.ui.screens.chat

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ListingEntity
import com.example.data.model.UserEntity
import com.example.ui.components.AvatarView
import com.example.ui.components.ScrapCategoryIcon
import com.example.ui.theme.CopperAmber
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MintGreenContainer
import com.example.ui.theme.OnMintGreenContainer
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
  listing: ListingEntity,
  currentUser: UserEntity,
  messages: List<ChatMessageEntity>,
  onSendMessage: (String) -> Unit,
  onSendOffer: (Double) -> Unit,
  onAcceptOffer: (ChatMessageEntity) -> Unit,
  onDeclineOffer: (ChatMessageEntity) -> Unit,
  onShareLocation: (String, Double, Double) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val listState = rememberLazyListState()
  val scope = rememberCoroutineScope()

  var messageInput by remember { mutableStateOf("") }
  var showOfferDialog by remember { mutableStateOf(false) }
  var offerPriceInput by remember { mutableStateOf(listing.pricePerKg.toInt().toString()) }

  // Recipient info
  val otherPartyName = if (currentUser.id == listing.sellerId) "Buyer" else listing.sellerName
  val otherPartyPhone = if (currentUser.id == listing.sellerId) "9851098765" else listing.sellerPhone

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarView(name = otherPartyName, sizeDp = 36)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(otherPartyName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              Text("Re: ${listing.title}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("chat_back_button")) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          // Phone Call Button
          IconButton(
            onClick = {
              val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$otherPartyPhone"))
              context.startActivity(intent)
            },
            modifier = Modifier.testTag("chat_call_button")
          ) {
            Icon(Icons.Default.Phone, contentDescription = "Call", tint = ForestGreenPrimary)
          }
        }
      )
    },
    bottomBar = {
      Surface(
        tonalElevation = 6.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(8.dp)) {
          // Action Shortcut Chips (Negotiate Offer & Share Pickup Location)
          Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            AssistChip(
              onClick = { showOfferDialog = true },
              label = { Text("Negotiate Price", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
              leadingIcon = {
                Icon(Icons.Default.PriceCheck, contentDescription = null, tint = CopperAmber, modifier = Modifier.size(16.dp))
              },
              modifier = Modifier.testTag("chat_negotiate_chip")
            )

            AssistChip(
              onClick = {
                onShareLocation(
                  "Pickup Spot at ${listing.locationName}",
                  listing.latitude,
                  listing.longitude
                )
              },
              label = { Text("Share Location", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
              leadingIcon = {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(16.dp))
              },
              modifier = Modifier.testTag("chat_share_location_chip")
            )
          }

          // Message Input Field
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = messageInput,
              onValueChange = { messageInput = it },
              placeholder = { Text("Type scrap negotiation message...") },
              modifier = Modifier
                .weight(1f)
                .testTag("chat_input_field"),
              shape = RoundedCornerShape(24.dp),
              singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
              onClick = {
                if (messageInput.isNotBlank()) {
                  onSendMessage(messageInput.trim())
                  messageInput = ""
                }
              },
              enabled = messageInput.isNotBlank(),
              modifier = Modifier
                .size(48.dp)
                .background(if (messageInput.isNotBlank()) ForestGreenPrimary else Color.LightGray, CircleShape)
                .testTag("chat_send_button")
            ) {
              Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
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
    ) {
      // Listing Context Strip
      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            ScrapCategoryIcon(categoryName = listing.category, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(listing.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text("${listing.weightKg} KG • Asking: Rs. ${listing.pricePerKg.toInt()}/KG", fontSize = 11.sp, color = CopperAmber)
            }
          }
          Text("Total: Rs. ${listing.totalPrice.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
        }
      }

      // Chat Messages List
      LazyColumn(
        state = listState,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        if (messages.isEmpty()) {
          item {
            Box(
              modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                "Start negotiating or agree on pickup timing below.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
              )
            }
          }
        }

        items(messages, key = { it.id }) { msg ->
          val isMe = msg.senderId == currentUser.id
          val isSystem = msg.messageType == "SYSTEM"

          if (isSystem) {
            // System message card
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MintGreenContainer),
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
              Text(
                text = msg.content,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = OnMintGreenContainer,
                modifier = Modifier.padding(10.dp)
              )
            }
          } else if (msg.messageType == "OFFER") {
            // Special Price Offer Card
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
              ),
              elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
              modifier = Modifier
                .fillMaxWidth(0.88f)
                .align(if (isMe) Alignment.End else Alignment.Start)
                .testTag("chat_offer_card_${msg.id}")
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = CopperAmber)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Price Negotiation Offer", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  }
                  if (msg.offerStatus != null) {
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = when (msg.offerStatus) {
                        "ACCEPTED" -> MintGreenContainer
                        "DECLINED" -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                      }
                    ) {
                      Text(
                        text = msg.offerStatus,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (msg.offerStatus == "ACCEPTED") ForestGreenPrimary else Color.DarkGray,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = "Offered: Rs. ${msg.offerPricePerKg?.toInt() ?: 0} / KG",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = CopperAmber
                )
                Text(
                  text = "Total Deal: Rs. ${msg.offerTotalPrice?.toInt() ?: 0} for ${listing.weightKg} KG",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium
                )

                // If offer is pending and sent by the other party, show Accept / Decline buttons!
                if (!isMe && msg.offerStatus == "PENDING") {
                  Spacer(modifier = Modifier.height(12.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    OutlinedButton(
                      onClick = { onDeclineOffer(msg) },
                      modifier = Modifier.weight(1f).height(36.dp).testTag("decline_offer_button")
                    ) {
                      Text("Decline", fontSize = 12.sp)
                    }
                    Button(
                      onClick = { onAcceptOffer(msg) },
                      modifier = Modifier.weight(1f).height(36.dp).testTag("accept_offer_button")
                    ) {
                      Text("Accept Offer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }
          } else if (msg.messageType == "LOCATION") {
            // Location Card
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
              modifier = Modifier.fillMaxWidth(0.85f).align(if (isMe) Alignment.End else Alignment.Start)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.LocationOn, contentDescription = null, tint = ForestGreenPrimary)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Shared Pickup Spot", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(msg.locationAddress ?: "", fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                  onClick = {
                    val uri = Uri.parse("geo:${msg.locationLatitude ?: listing.latitude},${msg.locationLongitude ?: listing.longitude}")
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                  },
                  modifier = Modifier.fillMaxWidth().height(34.dp)
                ) {
                  Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Navigate to Location", fontSize = 12.sp)
                }
              }
            }
          } else {
            // Standard Text Message Bubble
            Box(
              modifier = Modifier.fillMaxWidth(),
              contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
            ) {
              Surface(
                shape = RoundedCornerShape(
                  topStart = 16.dp,
                  topEnd = 16.dp,
                  bottomStart = if (isMe) 16.dp else 4.dp,
                  bottomEnd = if (isMe) 4.dp else 16.dp
                ),
                color = if (isMe) ForestGreenPrimary else MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
              ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)) {
                  Text(
                    text = msg.content,
                    color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  // Negotiate Offer Dialog
  if (showOfferDialog) {
    val enteredPrice = offerPriceInput.toDoubleOrNull() ?: 0.0
    val totalEstimated = enteredPrice * listing.weightKg

    AlertDialog(
      onDismissRequest = { showOfferDialog = false },
      title = { Text("Make Price Offer", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text(
            "Original asking price is Rs. ${listing.pricePerKg.toInt()} / KG for ${listing.weightKg} KG (${listing.category}).",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = offerPriceInput,
            onValueChange = { offerPriceInput = it },
            label = { Text("Your Proposed Price / KG (Rs.)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("dialog_offer_input")
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            "Total Offer Amount: Rs. ${totalEstimated.toInt()}",
            fontWeight = FontWeight.Bold,
            color = CopperAmber,
            fontSize = 14.sp
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (enteredPrice > 0) {
              onSendOffer(enteredPrice)
              showOfferDialog = false
            }
          },
          modifier = Modifier.testTag("dialog_submit_offer_button")
        ) {
          Text("Send Offer")
        }
      },
      dismissButton = {
        TextButton(onClick = { showOfferDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
