package com.example.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MintGreenContainer
import kotlinx.coroutines.launch
import com.example.ui.theme.OnMintGreenContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
  onLoginSuccess: () -> Unit,
  onNavigateToRegister: () -> Unit,
  onNavigateToForgotPassword: () -> Unit,
  onLoginAttempt: suspend (String, String) -> Result<Unit>,
  modifier: Modifier = Modifier
) {
  var identifier by remember { mutableStateOf("HK-1002") } // default prefilled demo buyer or 9851098765
  var password by remember { mutableStateOf("password123") }
  var passwordVisible by remember { mutableStateOf(false) }
  var selectedRoleTab by remember { mutableStateOf("BUYER") } // BUYER or SELLER
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 24.dp, vertical = 32.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    // Logo & Brand Header
    Box(
      modifier = Modifier
        .size(92.dp)
        .clip(CircleShape)
        .background(MintGreenContainer)
        .padding(8.dp),
      contentAlignment = Alignment.Center
    ) {
      Image(
        painter = painterResource(id = R.drawable.ic_kabadi_logo),
        contentDescription = "Hamro Kabadi Logo",
        modifier = Modifier
          .fillMaxSize()
          .clip(CircleShape),
        contentScale = ContentScale.Crop
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = "Hamro Kabadi",
      fontSize = 28.sp,
      fontWeight = FontWeight.Bold,
      color = ForestGreenPrimary
    )
    Text(
      text = "हाम्रो कवाडी • Smart Scrap Marketplace",
      fontSize = 14.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(28.dp))

    // Role Selector Tabs (Buyer vs Seller)
    Text(
      text = "Select Account Role",
      fontSize = 13.sp,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.align(Alignment.Start)
    )
    Spacer(modifier = Modifier.height(6.dp))

    SingleChoiceSegmentedButtonRow(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("role_selection_tabs")
    ) {
      SegmentedButton(
        selected = selectedRoleTab == "BUYER",
        onClick = {
          selectedRoleTab = "BUYER"
          if (identifier.isBlank() || identifier.startsWith("HK-")) {
            identifier = "HK-1002" // Demo Buyer Suman Thapa
          }
        },
        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
        modifier = Modifier.testTag("role_buyer_tab")
      ) {
        Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Buyer (खरीदकर्ता)", fontWeight = FontWeight.SemiBold)
      }

      SegmentedButton(
        selected = selectedRoleTab == "SELLER",
        onClick = {
          selectedRoleTab = "SELLER"
          if (identifier.isBlank() || identifier.startsWith("HK-")) {
            identifier = "HK-1001" // Demo Seller Ram Bahadur
          }
        },
        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
        modifier = Modifier.testTag("role_seller_tab")
      ) {
        Icon(Icons.Default.Sell, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Seller (विक्रेता)", fontWeight = FontWeight.SemiBold)
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Identifier Input (Phone or User ID)
    OutlinedTextField(
      value = identifier,
      onValueChange = {
        identifier = it
        errorMessage = null
      },
      label = { Text("Phone Number or User ID") },
      placeholder = { Text("e.g. 9841234567 or HK-1001") },
      leadingIcon = {
        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
      },
      singleLine = true,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("user_id_input")
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Password Input
    OutlinedTextField(
      value = password,
      onValueChange = {
        password = it
        errorMessage = null
      },
      label = { Text("Password") },
      leadingIcon = {
        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
      },
      trailingIcon = {
        IconButton(
          onClick = { passwordVisible = !passwordVisible },
          modifier = Modifier.testTag("toggle_password_visibility")
        ) {
          Icon(
            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = if (passwordVisible) "Hide password" else "Show password"
          )
        }
      },
      visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
      singleLine = true,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("password_input")
    )

    // Error Message
    if (errorMessage != null) {
      Spacer(modifier = Modifier.height(8.dp))
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("login_error_banner")
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = errorMessage ?: "",
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // Forgot Password Link
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp),
      contentAlignment = Alignment.CenterEnd
    ) {
      TextButton(
        onClick = onNavigateToForgotPassword,
        modifier = Modifier.testTag("forgot_password_button")
      ) {
        Text(
          text = "Forgot Password?",
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Sign In Button
    Button(
      onClick = {
        if (identifier.isBlank() || password.isBlank()) {
          errorMessage = "Please enter both User ID/Phone and password."
          return@Button
        }
        isLoading = true
        coroutineScope.launch {
          val result = onLoginAttempt(identifier, password)
          isLoading = false
          if (result.isSuccess) {
            onLoginSuccess()
          } else {
            errorMessage = result.exceptionOrNull()?.message ?: "Incorrect User ID or password."
          }
        }
      },
      enabled = !isLoading,
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("sign_in_button")
    ) {
      if (isLoading) {
        CircularProgressIndicator(
          modifier = Modifier.size(22.dp),
          color = MaterialTheme.colorScheme.onPrimary
        )
      } else {
        Icon(Icons.Default.Login, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Sign In as $selectedRoleTab",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Create New Account
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = "Don't have an account?",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      TextButton(
        onClick = onNavigateToRegister,
        modifier = Modifier.testTag("create_account_button")
      ) {
        Text(
          text = "Register Now",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Quick Test Demo Info Card
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = "Quick Demo Credentials",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "• Buyer: HK-1002 or 9851098765 / password123\n• Seller: HK-1001 or 9841234567 / password123",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 16.sp
        )
      }
    }
  }
}
