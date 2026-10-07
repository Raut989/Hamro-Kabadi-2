package com.example.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ListingEntity
import com.example.data.model.ScrapCategory
import com.example.data.model.UserEntity
import com.example.ui.components.*
import com.example.ui.theme.CopperAmber
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MintGreenContainer
import com.example.ui.theme.OnMintGreenContainer

enum class SortOption(val label: String) {
  NEAREST("Nearest First"),
  NEWEST("Newest"),
  LOWEST_PRICE("Lowest Price/KG"),
  HIGHEST_WEIGHT("Highest Weight")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  currentUser: UserEntity?,
  listings: List<ListingEntity>,
  onListingClick: (ListingEntity) -> Unit,
  onCreateListingClick: () -> Unit,
  onNavigateToProfile: () -> Unit,
  onCalculateDistance: (Double, Double) -> Double,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf<String?>(null) } // null for All
  var selectedRadiusKm by remember { mutableStateOf(10.0) }
  var selectedSort by remember { mutableStateOf(SortOption.NEAREST) }
  var isMapViewActive by remember { mutableStateOf(false) }
  var showFilterSheet by remember { mutableStateOf(false) }
  var maxPriceFilter by remember { mutableStateOf(1000f) }
  var minWeightFilter by remember { mutableStateOf(0f) }

  val userLat = currentUser?.latitude ?: 27.7172
  val userLng = currentUser?.longitude ?: 85.3240
  val userLocationName = currentUser?.location ?: "Kathmandu, Nepal"

  // Filter & Sort Listings
  val filteredListings = remember(
    listings,
    searchQuery,
    selectedCategory,
    selectedRadiusKm,
    selectedSort,
    maxPriceFilter,
    minWeightFilter
  ) {
    listings
      .filter { listing ->
        val matchesQuery = searchQuery.isBlank() ||
            listing.title.contains(searchQuery, ignoreCase = true) ||
            listing.category.contains(searchQuery, ignoreCase = true) ||
            listing.locationName.contains(searchQuery, ignoreCase = true) ||
            listing.sellerName.contains(searchQuery, ignoreCase = true)

        val matchesCategory = selectedCategory == null ||
            listing.category.equals(selectedCategory, ignoreCase = true)

        val dist = onCalculateDistance(listing.latitude, listing.longitude)
        val matchesRadius = selectedRadiusKm >= 50.0 || dist <= selectedRadiusKm

        val matchesPrice = listing.pricePerKg <= maxPriceFilter
        val matchesWeight = listing.weightKg >= minWeightFilter

        matchesQuery && matchesCategory && matchesRadius && matchesPrice && matchesWeight
      }
      .sortedWith { a, b ->
        when (selectedSort) {
          SortOption.NEAREST -> {
            val distA = onCalculateDistance(a.latitude, a.longitude)
            val distB = onCalculateDistance(b.latitude, b.longitude)
            distA.compareTo(distB)
          }
          SortOption.NEWEST -> b.createdAt.compareTo(a.createdAt)
          SortOption.LOWEST_PRICE -> a.pricePerKg.compareTo(b.pricePerKg)
          SortOption.HIGHEST_WEIGHT -> b.weightKg.compareTo(a.weightKg)
        }
      }
  }

  Scaffold(
    floatingActionButton = {
      // FAB to create listing
      ExtendedFloatingActionButton(
        onClick = onCreateListingClick,
        containerColor = ForestGreenPrimary,
        contentColor = Color.White,
        modifier = Modifier.testTag("fab_create_listing")
      ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Sell Scrap", fontWeight = FontWeight.Bold)
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background),
      contentPadding = PaddingValues(bottom = 80.dp)
    ) {
      // 1. Top App Header
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.clickable { onNavigateToProfile() }
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(MintGreenContainer),
                contentAlignment = Alignment.Center
              ) {
                Image(
                  painter = painterResource(id = R.drawable.ic_kabadi_logo),
                  contentDescription = "Logo",
                  modifier = Modifier.size(32.dp).clip(CircleShape)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Hamro Kabadi",
                  fontWeight = FontWeight.Bold,
                  fontSize = 18.sp,
                  color = ForestGreenPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(13.dp)
                  )
                  Spacer(modifier = Modifier.width(2.dp))
                  Text(
                    text = userLocationName,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }

            // User Role Tag & Profile avatar
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MintGreenContainer
              ) {
                Text(
                  text = currentUser?.role ?: "BUYER",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = OnMintGreenContainer,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
              IconButton(
                onClick = onNavigateToProfile,
                modifier = Modifier.testTag("home_profile_button")
              ) {
                AvatarView(name = currentUser?.fullName ?: "User", sizeDp = 36)
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Search Bar
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search iron, copper, plastic, location...") },
            leadingIcon = {
              Icon(Icons.Default.Search, contentDescription = "Search", tint = ForestGreenPrimary)
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { searchQuery = "" }) {
                  Icon(Icons.Default.Close, contentDescription = "Clear")
                }
              } else {
                IconButton(
                  onClick = { showFilterSheet = true },
                  modifier = Modifier.testTag("home_filter_button")
                ) {
                  Icon(Icons.Default.Tune, contentDescription = "Filters", tint = ForestGreenPrimary)
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.background,
              unfocusedContainerColor = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("home_search_bar")
          )
        }
      }

      // 2. Hero Banner Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
          Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            Image(
              painter = painterResource(id = R.drawable.img_kabadi_hero),
              contentDescription = "Hamro Kabadi Marketplace Banner",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  androidx.compose.ui.graphics.Brush.horizontalGradient(
                    listOf(ForestGreenPrimary.copy(alpha = 0.88f), Color.Transparent)
                  )
                )
            )
            Column(
              modifier = Modifier
                .fillMaxHeight()
                .padding(14.dp),
              verticalArrangement = Arrangement.Center
            ) {
              Text(
                text = "Recycle & Earn Cash",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Get instant market rates for scrap metals,\npapers, cardboard & e-waste in Nepal.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
                lineHeight = 15.sp
              )
            }
          }
        }
      }

      // 3. Search Radius & View Mode Switcher
      item {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Radar, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Nearby Radius:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            // Map vs List toggle
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.testTag("toggle_map_view")
            ) {
              Row(
                modifier = Modifier.padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(14.dp),
                  color = if (!isMapViewActive) ForestGreenPrimary else Color.Transparent,
                  modifier = Modifier.clickable { isMapViewActive = false }
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      Icons.Default.ViewList,
                      contentDescription = null,
                      tint = if (!isMapViewActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      "List",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (!isMapViewActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(14.dp),
                  color = if (isMapViewActive) ForestGreenPrimary else Color.Transparent,
                  modifier = Modifier.clickable { isMapViewActive = true }
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      Icons.Default.Map,
                      contentDescription = null,
                      tint = if (isMapViewActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      "Map",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isMapViewActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Radius Chips (2 KM, 5 KM, 10 KM, 25 KM, All)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(2.0, 5.0, 10.0, 25.0, 100.0).forEach { km ->
              val label = if (km >= 100.0) "All Valley" else "${km.toInt()} KM"
              val isSelected = selectedRadiusKm == km
              FilterChip(
                selected = isSelected,
                onClick = { selectedRadiusKm = km },
                label = { Text(label, fontSize = 12.sp) },
                shape = RoundedCornerShape(16.dp),
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MintGreenContainer,
                  selectedLabelColor = OnMintGreenContainer
                ),
                modifier = Modifier.testTag("radius_chip_${km.toInt()}")
              )
            }
          }
        }
      }

      // 4. Interactive Map View (if toggled)
      if (isMapViewActive) {
        item {
          Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            InteractiveScrapMap(
              userLat = userLat,
              userLng = userLng,
              listings = filteredListings,
              selectedRadiusKm = selectedRadiusKm,
              onSelectListing = onListingClick,
              modifier = Modifier.testTag("home_interactive_map")
            )
          }
        }
      }

      // 5. Material Category Chips
      item {
        Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
          Text(
            text = "Material Categories",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
          )
          Spacer(modifier = Modifier.height(6.dp))

          LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // "All" chip
            item {
              FilterChip(
                selected = selectedCategory == null,
                onClick = { selectedCategory = null },
                label = { Text("All Scrap", fontWeight = FontWeight.SemiBold) },
                leadingIcon = {
                  Icon(Icons.Default.AllInclusive, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                shape = RoundedCornerShape(16.dp),
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = ForestGreenPrimary,
                  selectedLabelColor = Color.White
                ),
                modifier = Modifier.testTag("category_chip_all")
              )
            }

            items(ScrapCategory.entries) { cat ->
              val isSelected = selectedCategory?.equals(cat.name, ignoreCase = true) == true
              FilterChip(
                selected = isSelected,
                onClick = { selectedCategory = if (isSelected) null else cat.name },
                label = { Text(cat.displayName) },
                shape = RoundedCornerShape(16.dp),
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = ForestGreenPrimary,
                  selectedLabelColor = Color.White
                ),
                modifier = Modifier.testTag("category_chip_${cat.name.lowercase()}")
              )
            }
          }
        }
      }

      // 6. Section Header & Sorting
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Nearby Kabadi Listings",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${filteredListings.size} items available",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Sort selector
          var sortMenuExpanded by remember { mutableStateOf(false) }
          Box {
            OutlinedButton(
              onClick = { sortMenuExpanded = true },
              shape = RoundedCornerShape(16.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.height(34.dp).testTag("sort_button")
            ) {
              Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(selectedSort.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }

            DropdownMenu(
              expanded = sortMenuExpanded,
              onDismissRequest = { sortMenuExpanded = false }
            ) {
              SortOption.entries.forEach { option ->
                DropdownMenuItem(
                  text = { Text(option.label) },
                  onClick = {
                    selectedSort = option
                    sortMenuExpanded = false
                  }
                )
              }
            }
          }
        }
      }

      // 7. Listings Cards
      if (filteredListings.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                Icons.Default.SearchOff,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                "No Scrap Listings Found",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
              Text(
                "Try widening your search radius or selecting a different category.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
              )
              Spacer(modifier = Modifier.height(16.dp))
              Button(
                onClick = {
                  searchQuery = ""
                  selectedCategory = null
                  selectedRadiusKm = 100.0
                }
              ) {
                Text("Reset Filters")
              }
            }
          }
        }
      } else {
        items(filteredListings, key = { it.id }) { listing ->
          val distance = onCalculateDistance(listing.latitude, listing.longitude)
          ListingItemCard(
            listing = listing,
            distanceKm = distance,
            onClick = { onListingClick(listing) },
            modifier = Modifier
              .padding(horizontal = 16.dp, vertical = 6.dp)
              .testTag("listing_card_${listing.id}")
          )
        }
      }
    }
  }

  // Filter Bottom Sheet
  if (showFilterSheet) {
    ModalBottomSheet(
      onDismissRequest = { showFilterSheet = false }
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        Text("Filter Listings", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
          "Max Price: Rs. ${maxPriceFilter.toInt()} / KG",
          fontWeight = FontWeight.SemiBold,
          fontSize = 14.sp
        )
        Slider(
          value = maxPriceFilter,
          onValueChange = { maxPriceFilter = it },
          valueRange = 10f..1000f,
          steps = 99
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          "Min Weight: ${minWeightFilter.toInt()} KG",
          fontWeight = FontWeight.SemiBold,
          fontSize = 14.sp
        )
        Slider(
          value = minWeightFilter,
          onValueChange = { minWeightFilter = it },
          valueRange = 0f..200f,
          steps = 20
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedButton(
            onClick = {
              maxPriceFilter = 1000f
              minWeightFilter = 0f
            },
            modifier = Modifier.weight(1f)
          ) {
            Text("Reset")
          }
          Button(
            onClick = { showFilterSheet = false },
            modifier = Modifier.weight(1f)
          ) {
            Text("Apply")
          }
        }
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
fun ListingItemCard(
  listing: ListingEntity,
  distanceKm: Double,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    onClick = onClick,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          ScrapCategoryIcon(categoryName = listing.category, modifier = Modifier.size(46.dp))
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = listing.title,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
              ) {
                Text(
                  text = "${listing.weightKg} KG",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "• ${listing.category}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        // Price Badge
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "Rs. ${listing.pricePerKg.toInt()}/KG",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = CopperAmber
          )
          Text(
            text = "Total: Rs. ${listing.totalPrice.toInt()}",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
      Spacer(modifier = Modifier.height(10.dp))

      // Bottom Row: Distance & Seller Info
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Distance & Location
        Row(verticalAlignment = Alignment.CenterVertically) {
          DistanceIndicator(distanceKm = distanceKm)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = listing.locationName,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 140.dp)
          )
        }

        // Seller Name & Rating
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = listing.sellerName.split(" ").firstOrNull() ?: "Seller",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
          RatingStars(rating = listing.sellerRating)
        }
      }
    }
  }
}
