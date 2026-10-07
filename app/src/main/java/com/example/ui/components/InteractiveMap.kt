package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ListingEntity
import com.example.ui.theme.CopperAmber
import com.example.ui.theme.ForestGreenPrimary
import kotlin.math.*

@Composable
fun InteractiveScrapMap(
  userLat: Double,
  userLng: Double,
  listings: List<ListingEntity>,
  selectedRadiusKm: Double,
  onSelectListing: (ListingEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedListing by remember { mutableStateOf<ListingEntity?>(null) }
  var mapScale by remember { mutableStateOf(1.0f) }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(340.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(Color(0xFFE8F0E8))
  ) {
    // Map Canvas
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(listings, selectedRadiusKm, mapScale) {
          detectTapGestures { tapOffset ->
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            // Base scale: degrees to pixels. 1 km ~ 0.009 degrees lat
            val kmInPx = (size.width / (selectedRadiusKm * 2.5f)) * mapScale

            var closest: ListingEntity? = null
            var minDistance = 45.0 // tap threshold in px

            listings.forEach { listing ->
              val dLat = (listing.latitude - userLat)
              val dLng = (listing.longitude - userLng)
              // Approx km
              val kmY = (-dLat * 111.0f).toFloat()
              val kmX = (dLng * 111.0f * cos(Math.toRadians(userLat))).toFloat()

              val pinX = centerX + kmX * kmInPx
              val pinY = centerY + kmY * kmInPx

              val dist = sqrt((tapOffset.x - pinX).toDouble().pow(2) + (tapOffset.y - pinY).toDouble().pow(2))
              if (dist < minDistance) {
                minDistance = dist
                closest = listing
              }
            }

            selectedListing = closest
          }
        }
    ) {
      val centerX = size.width / 2f
      val centerY = size.height / 2f
      val kmInPx = (size.width / (selectedRadiusKm.toFloat() * 2.5f)) * mapScale

      // Background Grid / Topo Lines
      val gridSpacing = 40f
      for (x in 0..(size.width / gridSpacing).toInt()) {
        drawLine(
          color = Color(0xFFD5E3D5),
          start = Offset(x * gridSpacing, 0f),
          end = Offset(x * gridSpacing, size.height),
          strokeWidth = 1f
        )
      }
      for (y in 0..(size.height / gridSpacing).toInt()) {
        drawLine(
          color = Color(0xFFD5E3D5),
          start = Offset(0f, y * gridSpacing),
          end = Offset(size.width, y * gridSpacing),
          strokeWidth = 1f
        )
      }

      // Stylized Ring Road / River curves for Kathmandu topography
      val riverPath = Path().apply {
        moveTo(0f, centerY + 80f)
        cubicTo(
          centerX - 100f, centerY + 20f,
          centerX + 40f, centerY + 140f,
          size.width, centerY + 60f
        )
      }
      drawPath(
        path = riverPath,
        color = Color(0xFFB0D5D0),
        style = Stroke(width = 6f)
      )

      val roadPath = Path().apply {
        moveTo(centerX - 160f, centerY - 140f)
        cubicTo(
          centerX, centerY - 80f,
          centerX + 120f, centerY + 30f,
          centerX + 180f, size.height
        )
      }
      drawPath(
        path = roadPath,
        color = Color(0xFFE4DAC7),
        style = Stroke(width = 8f)
      )

      // Radius circle ring
      val radiusPx = (selectedRadiusKm.toFloat() * kmInPx)
      drawCircle(
        color = ForestGreenPrimary.copy(alpha = 0.08f),
        radius = radiusPx,
        center = Offset(centerX, centerY)
      )
      drawCircle(
        color = ForestGreenPrimary.copy(alpha = 0.45f),
        radius = radiusPx,
        center = Offset(centerX, centerY),
        style = Stroke(width = 2f)
      )

      // User location radar pulse and pin
      drawCircle(
        color = Color(0xFF1976D2).copy(alpha = 0.2f),
        radius = 28f,
        center = Offset(centerX, centerY)
      )
      drawCircle(
        color = Color(0xFF1976D2),
        radius = 8f,
        center = Offset(centerX, centerY)
      )
      drawCircle(
        color = Color.White,
        radius = 3.5f,
        center = Offset(centerX, centerY)
      )

      // Listing pins
      listings.forEach { listing ->
        val dLat = (listing.latitude - userLat)
        val dLng = (listing.longitude - userLng)
        val kmY = -dLat * 111.0f
        val kmX = dLng * 111.0f * cos(Math.toRadians(userLat)).toFloat()

        val pinX = (centerX + kmX * kmInPx).toFloat()
        val pinY = (centerY + kmY * kmInPx).toFloat()

        // Check if within bounds
        if (pinX in 0f..size.width && pinY in 0f..size.height) {
          val isSelected = selectedListing?.id == listing.id
          val pinColor = if (isSelected) CopperAmber else ForestGreenPrimary

          // Pin head
          drawCircle(
            color = pinColor,
            radius = if (isSelected) 14f else 10f,
            center = Offset(pinX, pinY - 10f)
          )
          drawCircle(
            color = Color.White,
            radius = if (isSelected) 6f else 4f,
            center = Offset(pinX, pinY - 10f)
          )
          // Pin base
          drawLine(
            color = pinColor,
            start = Offset(pinX, pinY - 10f),
            end = Offset(pinX, pinY),
            strokeWidth = 3f
          )
        }
      }
    }

    // Top Controls Bar (Zoom, Location, Count)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        shadowElevation = 2.dp
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.PinDrop,
            contentDescription = null,
            tint = ForestGreenPrimary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${listings.size} Kabadi scrap points in ${selectedRadiusKm.toInt()} KM",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      // Zoom Buttons
      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        IconButton(
          onClick = { mapScale = (mapScale * 1.25f).coerceAtMost(2.5f) },
          modifier = Modifier
            .size(34.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), CircleShape)
            .testTag("zoom_in_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
        }
        IconButton(
          onClick = { mapScale = (mapScale / 1.25f).coerceAtLeast(0.6f) },
          modifier = Modifier
            .size(34.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), CircleShape)
            .testTag("zoom_out_button")
        ) {
          Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
        }
      }
    }

    // Selected Pin Card Popup at Bottom
    AnimatedVisibility(
      visible = selectedListing != null,
      enter = slideInVertically { it } + fadeIn(),
      exit = slideOutVertically { it } + fadeOut(),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(12.dp)
    ) {
      selectedListing?.let { item ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("map_listing_popup_card")
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                ScrapCategoryIcon(categoryName = item.category, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = "${item.weightKg} KG • Rs. ${item.pricePerKg.toInt()}/KG (Total: Rs. ${item.totalPrice.toInt()})",
                    fontSize = 12.sp,
                    color = CopperAmber,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }

              IconButton(
                onClick = { selectedListing = null },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.LocationOn,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = item.locationName,
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 1
                )
              }

              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Directions button
                OutlinedButton(
                  onClick = {
                    val uri = Uri.parse("geo:${item.latitude},${item.longitude}?q=${item.latitude},${item.longitude}(${Uri.encode(item.title)})")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(intent)
                  },
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                  modifier = Modifier.height(32.dp).testTag("map_directions_button")
                ) {
                  Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Directions", fontSize = 11.sp)
                }

                // View Details
                Button(
                  onClick = { onSelectListing(item) },
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                  modifier = Modifier.height(32.dp).testTag("map_view_listing_button")
                ) {
                  Text("View Item", fontSize = 11.sp)
                }
              }
            }
          }
        }
      }
    }
  }
}
