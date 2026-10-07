package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.components.AvatarView
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MintGreenContainer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
  onRegisterSuccess: (UserEntity) -> Unit,
  onBackToLogin: () -> Unit,
  onRegisterAttempt: suspend (String, String, String, String, String, String, String) -> Result<UserEntity>,
  modifier: Modifier = Modifier
) {
  var fullName by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var selectedRole by remember { mutableStateOf("BUYER") } // BUYER or SELLER
  var locationName by remember { mutableStateOf("Kathmandu, Nepal") }
  var locationPermissionGranted by remember { mutableStateOf(true) }
  var selectedAvatarIndex by remember { mutableStateOf(0) }
  val avatarOptions = listOf("avatar_green", "avatar_blue", "avatar_amber", "avatar_teal")

  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Create New Account", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBackToLogin, modifier = Modifier.testTag("register_back_to_login_button")) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back to Login")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { innerPadding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
        .verticalScroll(rememberScrollState())
        .padding(20.dp)
    ) {
      // Role Switcher Cards
      Text(
        text = "Select Account Type",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Card(
          onClick = { selectedRole = "BUYER" },
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (selectedRole == "BUYER") MintGreenContainer else MaterialTheme.colorScheme.surface
          ),
          border = if (selectedRole == "BUYER") CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ForestGreenPrimary)) else null,
          modifier = Modifier
            .weight(1f)
            .testTag("register_role_buyer")
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              Icons.Default.ShoppingBag,
              contentDescription = null,
              tint = if (selectedRole == "BUYER") ForestGreenPrimary else Color.Gray,
              modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              "Buyer",
              fontWeight = FontWeight.Bold,
              color = if (selectedRole == "BUYER") ForestGreenPrimary else MaterialTheme.colorScheme.onSurface
            )
            Text(
              "Buys Scrap",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Card(
          onClick = { selectedRole = "SELLER" },
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (selectedRole == "SELLER") MintGreenContainer else MaterialTheme.colorScheme.surface
          ),
          border = if (selectedRole == "SELLER") CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ForestGreenPrimary)) else null,
          modifier = Modifier
            .weight(1f)
            .testTag("register_role_seller")
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              Icons.Default.Sell,
              contentDescription = null,
              tint = if (selectedRole == "SELLER") ForestGreenPrimary else Color.Gray,
              modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              "Seller",
              fontWeight = FontWeight.Bold,
              color = if (selectedRole == "SELLER") ForestGreenPrimary else MaterialTheme.colorScheme.onSurface
            )
            Text(
              "Sells Scrap",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Profile Photo / Avatar Selection
      Text(
        text = "Profile Avatar",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        avatarOptions.forEachIndexed { index, avatarKey ->
          val isSelected = selectedAvatarIndex == index
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(
                when (index) {
                  0 -> ForestGreenPrimary.copy(alpha = 0.2f)
                  1 -> Color(0xFF1976D2).copy(alpha = 0.2f)
                  2 -> Color(0xFFFFA000).copy(alpha = 0.2f)
                  else -> Color(0xFF00796B).copy(alpha = 0.2f)
                }
              )
              .clickable { selectedAvatarIndex = index }
              .then(
                if (isSelected) Modifier.border(2.dp, ForestGreenPrimary, CircleShape) else Modifier
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = if (isSelected) ForestGreenPrimary else Color.Gray,
              modifier = Modifier.size(28.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Input Fields
      OutlinedTextField(
        value = fullName,
        onValueChange = { fullName = it; errorMessage = null },
        label = { Text("Full Name *") },
        placeholder = { Text("e.g. Ramesh Karki") },
        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("register_fullname_input")
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = phone,
        onValueChange = { phone = it; errorMessage = null },
        label = { Text("Phone Number *") },
        placeholder = { Text("e.g. 9801234567") },
        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("register_phone_input")
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email Address (Optional)") },
        placeholder = { Text("e.g. ramesh@example.com") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("register_email_input")
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Auto User ID Preview
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.VpnKey, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              "Unique User ID",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              "A unique ID (e.g. HK-XXXX) will be generated upon registration.",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = password,
        onValueChange = { password = it; errorMessage = null },
        label = { Text("Password *") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("register_password_input")
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = confirmPassword,
        onValueChange = { confirmPassword = it; errorMessage = null },
        label = { Text("Confirm Password *") },
        leadingIcon = { Icon(Icons.Default.LockClock, contentDescription = null) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("register_confirm_password_input")
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Location Permission Checkbox
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Checkbox(
            checked = locationPermissionGranted,
            onCheckedChange = { locationPermissionGranted = it },
            modifier = Modifier.testTag("register_location_permission_checkbox")
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              "Enable Location Permission",
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.sp
            )
            Text(
              "Allows calculating exact distance to scrap buyers/sellers in Kathmandu valley.",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      if (errorMessage != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = errorMessage ?: "",
          color = MaterialTheme.colorScheme.error,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Register Button
      Button(
        onClick = {
          if (fullName.isBlank()) {
            errorMessage = "Please enter your full name."
            return@Button
          }
          if (phone.isBlank()) {
            errorMessage = "Please enter your phone number."
            return@Button
          }
          if (password.length < 6) {
            errorMessage = "Password must be at least 6 characters."
            return@Button
          }
          if (password != confirmPassword) {
            errorMessage = "Passwords do not match."
            return@Button
          }

          isLoading = true
          scope.launch {
            val result = onRegisterAttempt(
              fullName,
              phone,
              email,
              password,
              selectedRole,
              avatarOptions[selectedAvatarIndex],
              locationName
            )
            isLoading = false
            if (result.isSuccess) {
              onRegisterSuccess(result.getOrThrow())
            } else {
              errorMessage = result.exceptionOrNull()?.message ?: "Registration failed."
            }
          }
        },
        enabled = !isLoading,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("register_submit_button")
      ) {
        if (isLoading) {
          CircularProgressIndicator(
            modifier = Modifier.size(22.dp),
            color = MaterialTheme.colorScheme.onPrimary
          )
        } else {
          Icon(Icons.Default.Check, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Complete Registration", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
