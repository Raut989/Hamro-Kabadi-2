package com.example.ui.screens.listings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.ScrapCategory
import com.example.ui.components.ScrapCategoryIcon
import com.example.ui.theme.CopperAmber
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MintGreenContainer
import com.example.ui.theme.OnMintGreenContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingScreen(
  marketPrices: List<MarketPriceEntity>,
  onBack: () -> Unit,
  onSubmitListing: (String, String, Double, Double, String, String, Double, Double, String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf(ScrapCategory.IRON) }
  var title by remember { mutableStateOf("") }
  var weightText by remember { mutableStateOf("") }
  var pricePerKgText by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var locationName by remember { mutableStateOf("New Baneshwor, Kathmandu") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  // Find matching market price
  val currentMarketPrice = marketPrices.find {
    it.categoryKey.equals(selectedCategory.name, ignoreCase = true)
  }

  // Pre-fill suggested price when category changes
  LaunchedEffect(selectedCategory) {
    if (pricePerKgText.isBlank() && currentMarketPrice != null) {
      pricePerKgText = currentMarketPrice.suggestedSellerPrice.toInt().toString()
    }
    if (title.isBlank() || title.startsWith("Scrap ")) {
      title = "${selectedCategory.displayName} Scrap Material"
    }
  }

  val weightVal = weightText.toDoubleOrNull() ?: 0.0
  val priceVal = pricePerKgText.toDoubleOrNull() ?: 0.0
  val totalVal = weightVal * priceVal

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Post Scrap Listing", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("create_back_button")) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
      // 1. Select Material Category
      Text("1. Select Material Category *", fontWeight = FontWeight.Bold, fontSize = 14.sp)
      Spacer(modifier = Modifier.height(8.dp))

      var categoryDropdownExpanded by remember { mutableStateOf(false) }
      ExposedDropdownMenuBox(
        expanded = categoryDropdownExpanded,
        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = "${selectedCategory.displayName} (${selectedCategory.name})",
          onValueChange = {},
          readOnly = true,
          leadingIcon = {
            ScrapCategoryIcon(categoryName = selectedCategory.name, modifier = Modifier.size(32.dp))
          },
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
          modifier = Modifier
            .menuAnchor()
            .fillMaxWidth()
            .testTag("create_category_dropdown")
        )

        ExposedDropdownMenu(
          expanded = categoryDropdownExpanded,
          onDismissRequest = { categoryDropdownExpanded = false }
        ) {
          ScrapCategory.entries.forEach { cat ->
            DropdownMenuItem(
              text = { Text(cat.displayName) },
              leadingIcon = { ScrapCategoryIcon(categoryName = cat.name, modifier = Modifier.size(28.dp)) },
              onClick = {
                selectedCategory = cat
                val mp = marketPrices.find { it.categoryKey.equals(cat.name, ignoreCase = true) }
                if (mp != null) {
                  pricePerKgText = mp.suggestedSellerPrice.toInt().toString()
                }
                categoryDropdownExpanded = false
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Automatic Market Price Card
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MintGreenContainer),
        modifier = Modifier.fillMaxWidth().testTag("create_market_price_card")
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.TrendingUp, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(28.dp))
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              "Current Market Rate: Rs. ${currentMarketPrice?.marketPricePerKg?.toInt() ?: 50} / KG",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = ForestGreenPrimary
            )
            Text(
              "Suggested selling price for fast buyer pickup: Rs. ${currentMarketPrice?.suggestedSellerPrice?.toInt() ?: 40} / KG",
              fontSize = 11.sp,
              color = OnMintGreenContainer
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Weight and Pricing
      Text("2. Quantity & Pricing *", fontWeight = FontWeight.Bold, fontSize = 14.sp)
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedTextField(
          value = weightText,
          onValueChange = { weightText = it; errorMessage = null },
          label = { Text("Weight (KG) *") },
          placeholder = { Text("e.g. 25") },
          leadingIcon = { Icon(Icons.Default.Scale, contentDescription = null) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .testTag("create_weight_input")
        )

        OutlinedTextField(
          value = pricePerKgText,
          onValueChange = { pricePerKgText = it; errorMessage = null },
          label = { Text("Price / KG (Rs.) *") },
          placeholder = { Text("e.g. 40") },
          leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .testTag("create_price_input")
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Live Total Calculation Card
      if (totalVal > 0) {
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
            Text("Total Estimated Value:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(
              "Rs. ${totalVal.toInt()}",
              fontSize = 16.sp,
              fontWeight = FontWeight.ExtraBold,
              color = CopperAmber
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 4. Listing Details
      Text("3. Listing Details *", fontWeight = FontWeight.Bold, fontSize = 14.sp)
      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = title,
        onValueChange = { title = it; errorMessage = null },
        label = { Text("Listing Title *") },
        placeholder = { Text("e.g. Clean Iron Grill Scrap") },
        leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("create_title_input")
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Description & Scrap Condition") },
        placeholder = { Text("Mention condition, clean or rusted, ground floor or roof...") },
        minLines = 3,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("create_description_input")
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = locationName,
        onValueChange = { locationName = it; errorMessage = null },
        label = { Text("Pickup Location in Kathmandu Valley *") },
        placeholder = { Text("e.g. Koteshwor, Kathmandu") },
        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
        trailingIcon = {
          IconButton(onClick = { locationName = "New Baneshwor, Kathmandu" }) {
            Icon(Icons.Default.MyLocation, contentDescription = "Use My Location", tint = ForestGreenPrimary)
          }
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("create_location_input")
      )

      if (errorMessage != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
      }

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = {
          if (title.isBlank()) {
            errorMessage = "Please enter a title."
            return@Button
          }
          if (weightVal <= 0) {
            errorMessage = "Please enter a valid weight in KG."
            return@Button
          }
          if (priceVal <= 0) {
            errorMessage = "Please enter a valid price per KG."
            return@Button
          }
          if (locationName.isBlank()) {
            errorMessage = "Please enter a pickup location."
            return@Button
          }

          onSubmitListing(
            title,
            selectedCategory.name,
            weightVal,
            priceVal,
            description,
            locationName,
            27.6934, // Default Kathmandu coords
            85.3412,
            "scrap_${selectedCategory.name.lowercase()}"
          )
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("create_submit_button")
      ) {
        Icon(Icons.Default.Publish, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Publish Scrap Listing", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
