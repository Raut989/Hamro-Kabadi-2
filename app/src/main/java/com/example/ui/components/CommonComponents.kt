package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScrapCategory
import com.example.data.model.TransactionStatus
import com.example.ui.theme.*

@Composable
fun ScrapCategoryIcon(
  categoryName: String,
  modifier: Modifier = Modifier,
  tint: Color = MaterialTheme.colorScheme.primary,
  containerColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
  val (icon, bg) = when (categoryName.uppercase()) {
    "IRON" -> Icons.Default.Build to Color(0xFFE0E0E0)
    "STEEL" -> Icons.Default.Hardware to Color(0xFFCFD8DC)
    "ALUMINIUM" -> Icons.Default.ViewInAr to Color(0xFFD7CCC8)
    "COPPER" -> Icons.Default.ElectricBolt to Color(0xFFFFCCBC)
    "BRASS" -> Icons.Default.EmojiEvents to Color(0xFFFFF9C4)
    "PLASTIC" -> Icons.Default.LocalDrink to Color(0xFFB3E5FC)
    "PAPER" -> Icons.Default.Description to Color(0xFFFFF3E0)
    "CARDBOARD" -> Icons.Default.Inventory2 to Color(0xFFFFE0B2)
    "GLASS" -> Icons.Default.Liquor to Color(0xFFC8E6C9)
    "E_WASTE", "ELECTRONIC WASTE" -> Icons.Default.Memory to Color(0xFFE1BEE7)
    "BATTERIES" -> Icons.Default.BatteryChargingFull to Color(0xFFFFCDD2)
    else -> Icons.Default.Recycling to Color(0xFFD4EDDA)
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(containerColor.takeIf { it != Color.Unspecified } ?: bg)
      .padding(8.dp),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = categoryName,
      tint = tint,
      modifier = Modifier.size(24.dp)
    )
  }
}

@Composable
fun RatingStars(
  rating: Float,
  reviewCount: Int? = null,
  modifier: Modifier = Modifier
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
  ) {
    Icon(
      imageVector = Icons.Filled.Star,
      contentDescription = "Rating",
      tint = Color(0xFFFFB300),
      modifier = Modifier.size(16.dp)
    )
    Spacer(modifier = Modifier.width(3.dp))
    Text(
      text = String.format("%.1f", rating),
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface
    )
    if (reviewCount != null) {
      Text(
        text = " ($reviewCount)",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
fun DistanceIndicator(
  distanceKm: Double,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
    modifier = modifier
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Icon(
        imageVector = Icons.Default.NearMe,
        contentDescription = "Distance",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(13.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "${distanceKm} KM away",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
      )
    }
  }
}

@Composable
fun VerifiedBadge(
  modifier: Modifier = Modifier,
  text: String = "Verified"
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = MintGreenContainer,
    modifier = modifier
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
      Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = "Verified",
        tint = ForestGreenPrimary,
        modifier = Modifier.size(12.dp)
      )
      Spacer(modifier = Modifier.width(3.dp))
      Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = OnMintGreenContainer
      )
    }
  }
}

@Composable
fun TransactionStatusChip(
  status: String,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor, label) = when (status.uppercase()) {
    "PENDING" -> Triple(Color(0xFFFFF3E0), StatusPending, "Pending")
    "NEGOTIATING" -> Triple(Color(0xFFEDE7F6), Color(0xFF512DA8), "Negotiating")
    "CONFIRMED" -> Triple(Color(0xFFE1F5FE), StatusConfirmed, "Confirmed")
    "PICKED_UP" -> Triple(Color(0xFFE0F2F1), TealSecondary, "Picked Up")
    "COMPLETED" -> Triple(MintGreenContainer, StatusCompleted, "Completed")
    "CANCELLED" -> Triple(Color(0xFFFFEBEE), StatusCancelled, "Cancelled")
    else -> Triple(Color(0xFFF5F5F5), Color.DarkGray, status)
  }

  Surface(
    shape = RoundedCornerShape(8.dp),
    color = bgColor,
    modifier = modifier
  ) {
    Text(
      text = label,
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
    )
  }
}

@Composable
fun AvatarView(
  name: String,
  modifier: Modifier = Modifier,
  sizeDp: Int = 42
) {
  val initials = name.split(" ")
    .filter { it.isNotBlank() }
    .take(2)
    .map { it.first().uppercase() }
    .joinToString("")
    .ifEmpty { "HK" }

  Box(
    modifier = modifier
      .size(sizeDp.dp)
      .clip(CircleShape)
      .background(MaterialTheme.colorScheme.primaryContainer),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = initials,
      fontSize = (sizeDp / 2.5).sp,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onPrimaryContainer
    )
  }
}
