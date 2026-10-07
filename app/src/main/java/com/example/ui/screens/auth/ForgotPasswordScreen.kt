package com.example.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MintGreenContainer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
  onRequestOtp: (String) -> String,
  onVerifyOtp: (String, String) -> Boolean,
  onResetPassword: suspend (String, String) -> Boolean,
  onBackToLogin: () -> Unit,
  modifier: Modifier = Modifier
) {
  var step by remember { mutableStateOf(1) } // 1: Phone, 2: OTP, 3: New Password, 4: Success
  var phone by remember { mutableStateOf("9851098765") }
  var otpCode by remember { mutableStateOf("") }
  var generatedOtpPreview by remember { mutableStateOf<String?>(null) }
  var newPassword by remember { mutableStateOf("") }
  var confirmNewPassword by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Reset Password", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBackToLogin, modifier = Modifier.testTag("forgot_password_back_button")) {
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
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Step indicator
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        listOf("Phone", "Verify OTP", "New Password").forEachIndexed { index, label ->
          val active = step >= (index + 1)
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (active) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.size(32.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  "${index + 1}",
                  color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                  fontWeight = FontWeight.Bold
                )
              }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 11.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal)
          }
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // STEP 1: Enter Phone Number
      AnimatedVisibility(visible = step == 1) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            Icons.Default.PhoneIphone,
            contentDescription = null,
            tint = ForestGreenPrimary,
            modifier = Modifier.size(56.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text("Enter Registered Phone", fontSize = 18.sp, fontWeight = FontWeight.Bold)
          Text(
            "We will send a 6-digit OTP verification code to verify your identity.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
          )

          Spacer(modifier = Modifier.height(16.dp))

          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it; errorMessage = null },
            label = { Text("Phone Number") },
            placeholder = { Text("98XXXXXXXX") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("forgot_phone_input")
          )

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
          }

          Spacer(modifier = Modifier.height(24.dp))

          Button(
            onClick = {
              if (phone.length < 7) {
                errorMessage = "Please enter a valid phone number."
                return@Button
              }
              val otp = onRequestOtp(phone)
              generatedOtpPreview = otp
              step = 2
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("send_otp_button")
          ) {
            Icon(Icons.Default.Send, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Send Verification OTP", fontWeight = FontWeight.Bold)
          }
        }
      }

      // STEP 2: Enter & Verify OTP
      AnimatedVisibility(visible = step == 2) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            Icons.Default.MarkEmailRead,
            contentDescription = null,
            tint = ForestGreenPrimary,
            modifier = Modifier.size(56.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text("Verify OTP Code", fontSize = 18.sp, fontWeight = FontWeight.Bold)
          Text(
            "Enter the 6-digit verification code sent to $phone",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Simulated SMS notification preview card
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MintGreenContainer),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Sms, contentDescription = null, tint = ForestGreenPrimary)
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  "SMS Verification Code",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = ForestGreenPrimary
                )
                Text(
                  "Hamro Kabadi Code: ${generatedOtpPreview ?: "123456"}",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 16.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          OutlinedTextField(
            value = otpCode,
            onValueChange = { otpCode = it; errorMessage = null },
            label = { Text("Enter 6-Digit OTP") },
            placeholder = { Text("e.g. ${generatedOtpPreview ?: "123456"}") },
            leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("otp_input")
          )

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
          }

          Spacer(modifier = Modifier.height(24.dp))

          Button(
            onClick = {
              if (otpCode.isBlank()) {
                errorMessage = "Please enter the OTP code."
                return@Button
              }
              val isValid = onVerifyOtp(phone, otpCode)
              if (isValid) {
                step = 3
              } else {
                errorMessage = "Invalid OTP code. Please recheck."
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("verify_otp_button")
          ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Verify OTP", fontWeight = FontWeight.Bold)
          }
        }
      }

      // STEP 3: Create New Password
      AnimatedVisibility(visible = step == 3) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            Icons.Default.LockReset,
            contentDescription = null,
            tint = ForestGreenPrimary,
            modifier = Modifier.size(56.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text("Set New Password", fontSize = 18.sp, fontWeight = FontWeight.Bold)
          Text(
            "Create and confirm your new secure password.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(16.dp))

          OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it; errorMessage = null },
            label = { Text("New Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("new_password_input")
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = confirmNewPassword,
            onValueChange = { confirmNewPassword = it; errorMessage = null },
            label = { Text("Confirm New Password") },
            leadingIcon = { Icon(Icons.Default.LockClock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("confirm_new_password_input")
          )

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
          }

          Spacer(modifier = Modifier.height(24.dp))

          Button(
            onClick = {
              if (newPassword.length < 6) {
                errorMessage = "Password must be at least 6 characters."
                return@Button
              }
              if (newPassword != confirmNewPassword) {
                errorMessage = "Passwords do not match."
                return@Button
              }
              isLoading = true
              scope.launch {
                val success = onResetPassword(phone, newPassword)
                isLoading = false
                if (success) {
                  step = 4
                  delay(1500)
                  onBackToLogin()
                } else {
                  errorMessage = "Phone number not found in our records."
                }
              }
            },
            enabled = !isLoading,
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("submit_reset_password_button")
          ) {
            if (isLoading) {
              CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
              Icon(Icons.Default.DoneAll, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Save New Password", fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // STEP 4: Success Message
      AnimatedVisibility(visible = step == 4) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(24.dp)
        ) {
          Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = ForestGreenPrimary,
            modifier = Modifier.size(72.dp)
          )
          Spacer(modifier = Modifier.height(16.dp))
          Text("Password Successfully Reset!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            "Returning you to the sign-in screen...",
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(24.dp))
          Button(onClick = onBackToLogin) {
            Text("Back to Sign In")
          }
        }
      }
    }
  }
}
