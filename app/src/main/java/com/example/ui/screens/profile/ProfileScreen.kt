package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReviewEntity
import com.example.data.model.UserEntity
import com.example.ui.components.AvatarView
import com.example.ui.components.RatingStars
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MintGreenContainer
import com.example.ui.theme.OnMintGreenContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  user: UserEntity,
  isOwnProfile: Boolean,
  reviews: List<ReviewEntity>,
  onSwitchRole: (String) -> Unit,
  onUpdateProfile: (String, String, String, String, String) -> Unit,
  onLogout: () -> Unit,
  onBack: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var showEditProfileDialog by remember { mutableStateOf(false) }
  var editFullName by remember { mutableStateOf(user.fullName) }
  var editPhone by remember { mutableStateOf(user.phone) }
  var editEmail by remember { mutableStateOf(user.email) }
  var editBio by remember { mutableStateOf(user.bio) }
  var editLocation by remember { mutableStateOf(user.location) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(if (isOwnProfile) "My Profile" else "Trader Profile", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          if (onBack != null) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("profile_back_button")) {
              Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
          }
        },
        actions = {
          if (isOwnProfile) {
            IconButton(
              onClick = { showEditProfileDialog = true },
              modifier = Modifier.testTag("edit_profile_icon_button")
            ) {
              Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
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
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Profile Header Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            AvatarView(name = user.fullName, sizeDp = 76)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = user.fullName,
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            )

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              modifier = Modifier.padding(top = 4.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
              ) {
                Text(
                  text = "ID: ${user.id}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              if (user.isPhoneVerified) {
                VerifiedBadge(text = "Phone Verified")
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.LocationOn, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(user.location, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = user.bio,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
              modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // 3 Key Stats
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RatingStars(rating = user.rating)
                Text("Trader Rating", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  "${user.completedDeals}",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = ForestGreenPrimary
                )
                Text("Deals Done", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  user.memberSince,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
                Text("Member Since", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }
        }
      }

      // 2. Role Switcher (If own profile)
      if (isOwnProfile) {
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("Current Trading Role", fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                listOf("BUYER" to "Buyer Mode", "SELLER" to "Seller Mode").forEach { (roleKey, label) ->
                  val isSelected = user.role.equals(roleKey, ignoreCase = true)
                  OutlinedButton(
                    onClick = { onSwitchRole(roleKey) },
                    colors = ButtonDefaults.outlinedButtonColors(
                      containerColor = if (isSelected) MintGreenContainer else Color.Transparent
                    ),
                    modifier = Modifier.weight(1f).testTag("profile_role_switch_$roleKey")
                  ) {
                    if (isSelected) {
                      Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = ForestGreenPrimary)
                      Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                      label,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      color = if (isSelected) ForestGreenPrimary else MaterialTheme.colorScheme.onSurface
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 3. Contact & Phone Info Card
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Contact Information", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Phone, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Text("Phone: ${user.phone}", fontSize = 13.sp)
            }

            if (user.email.isNotBlank()) {
              Spacer(modifier = Modifier.height(8.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Email: ${user.email}", fontSize = 13.sp)
              }
            }
          }
        }
      }

      // 4. Reviews Received Section
      item {
        Row(
          modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Reviews & Feedback", fontWeight = FontWeight.Bold, fontSize = 16.sp)
          Text("${reviews.size} reviews", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }

      if (reviews.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
              Text("No reviews yet. Complete scrap deals to receive ratings!", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            }
          }
        }
      } else {
        items(reviews, key = { it.id }) { rev ->
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(rev.reviewerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                RatingStars(rating = rev.rating)
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(rev.comment, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        }
      }

      // 5. Logout Button (If own profile)
      if (isOwnProfile) {
        item {
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedButton(
            onClick = onLogout,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("profile_logout_button")
          ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out of Hamro Kabadi", fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  }

  // Edit Profile Dialog
  if (showEditProfileDialog) {
    AlertDialog(
      onDismissRequest = { showEditProfileDialog = false },
      title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          OutlinedTextField(
            value = editFullName,
            onValueChange = { editFullName = it },
            label = { Text("Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("edit_name_input")
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = editPhone,
            onValueChange = { editPhone = it },
            label = { Text("Phone Number") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("edit_phone_input")
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = editEmail,
            onValueChange = { editEmail = it },
            label = { Text("Email (Optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = editLocation,
            onValueChange = { editLocation = it },
            label = { Text("Location") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = editBio,
            onValueChange = { editBio = it },
            label = { Text("Bio") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateProfile(editFullName, editPhone, editEmail, editBio, editLocation)
            showEditProfileDialog = false
          },
          modifier = Modifier.testTag("save_profile_button")
        ) {
          Text("Save Changes")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditProfileDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
