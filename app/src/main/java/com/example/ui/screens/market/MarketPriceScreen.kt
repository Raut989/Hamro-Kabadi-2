package com.example.ui.screens.market

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketPriceEntity
import com.example.ui.components.ScrapCategoryIcon
import com.example.ui.theme.CopperAmber
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MintGreenContainer
import com.example.ui.theme.OnMintGreenContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketPriceScreen(
  marketPrices: List<MarketPriceEntity>,
  onUpdateMarketPrice: (String, Double) -> Unit,
  modifier: Modifier = Modifier
) {
  var editingPriceEntity by remember { mutableStateOf<MarketPriceEntity?>(null) }
  var newPriceInput by remember { mutableStateOf("") }
  var searchQuery by remember { mutableStateOf("") }

  val filteredPrices = remember(marketPrices, searchQuery) {
    if (searchQuery.isBlank()) marketPrices
    else marketPrices.filter {
      it.materialName.contains(searchQuery, ignoreCase = true) ||
          it.categoryKey.contains(searchQuery, ignoreCase = true)
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Official Market Rates", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("दैनिक कवाडी बजार भाउ (नेपाल)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        },
        actions = {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MintGreenContainer,
            modifier = Modifier.padding(end = 12.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Verified, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Live Updated", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnMintGreenContainer)
            }
          }
        }
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Info Banner
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                "Standard Kathmandu Recyclable Scrap Rates",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = ForestGreenPrimary
              )
              Text(
                "Prices fluctuate based on global metal commodity benchmarks and local foundry demands.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp
              )
            }
          }
        }
      }

      // Search Bar
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search material rates (Iron, Copper, Paper...)") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = null)
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("market_search_input")
        )
      }

      // Rates List
      items(filteredPrices, key = { it.categoryKey }) { priceItem ->
        MarketPriceCard(
          priceItem = priceItem,
          onAdminEditClick = {
            editingPriceEntity = priceItem
            newPriceInput = priceItem.marketPricePerKg.toInt().toString()
          },
          modifier = Modifier.testTag("market_price_card_${priceItem.categoryKey.lowercase()}")
        )
      }
    }
  }

  // Admin Price Update Dialog
  if (editingPriceEntity != null) {
    val target = editingPriceEntity!!
    AlertDialog(
      onDismissRequest = { editingPriceEntity = null },
      title = {
        Text("Update ${target.materialName} Rate", fontWeight = FontWeight.Bold)
      },
      text = {
        Column {
          Text(
            "Enter the new official benchmark market price per KG. Suggested selling and buying rates will automatically re-calibrate.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(14.dp))
          OutlinedTextField(
            value = newPriceInput,
            onValueChange = { newPriceInput = it },
            label = { Text("Benchmark Market Price (Rs./KG)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("admin_price_input")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val num = newPriceInput.toDoubleOrNull()
            if (num != null && num > 0) {
              onUpdateMarketPrice(target.categoryKey, num)
              editingPriceEntity = null
            }
          },
          modifier = Modifier.testTag("admin_save_price_button")
        ) {
          Text("Update Rate")
        }
      },
      dismissButton = {
        TextButton(onClick = { editingPriceEntity = null }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun MarketPriceCard(
  priceItem: MarketPriceEntity,
  onAdminEditClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          ScrapCategoryIcon(categoryName = priceItem.categoryKey, modifier = Modifier.size(44.dp))
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = priceItem.materialName,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "Updated: ${priceItem.updatedDate}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        // Admin Edit Button
        IconButton(
          onClick = onAdminEditClick,
          modifier = Modifier.size(32.dp).testTag("edit_rate_button_${priceItem.categoryKey}")
        ) {
          Icon(Icons.Default.Edit, contentDescription = "Edit Price", tint = ForestGreenPrimary, modifier = Modifier.size(18.dp))
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
      Spacer(modifier = Modifier.height(12.dp))

      // 3 Columns: Official Market Price, Suggested Seller, Suggested Buyer
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Market Price
        Column {
          Text("Market Benchmark", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            "Rs. ${priceItem.marketPricePerKg.toInt()} / ${priceItem.unit}",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = ForestGreenPrimary
          )
        }

        // Suggested Seller Price
        Column {
          Text("Suggested Seller", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            "Rs. ${priceItem.suggestedSellerPrice.toInt()} / ${priceItem.unit}",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = CopperAmber
          )
        }

        // Suggested Buyer Price
        Column(horizontalAlignment = Alignment.End) {
          Text("Suggested Buyer", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            "Rs. ${priceItem.suggestedBuyerPrice.toInt()} / ${priceItem.unit}",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      // Price History sparkline badges
      if (priceItem.priceHistory.isNotBlank()) {
        Spacer(modifier = Modifier.height(10.dp))
        val historyList = priceItem.priceHistory.split(",").filter { it.isNotBlank() }
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("7-Day Trend: ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Spacer(modifier = Modifier.width(6.dp))
          historyList.takeLast(5).forEachIndexed { idx, priceStr ->
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.padding(horizontal = 2.dp)
            ) {
              Text(
                "Rs. $priceStr",
                fontSize = 10.sp,
                fontWeight = if (idx == historyList.size - 1) FontWeight.Bold else FontWeight.Normal,
                color = if (idx == historyList.size - 1) ForestGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
            }
          }
        }
      }
    }
  }
}
