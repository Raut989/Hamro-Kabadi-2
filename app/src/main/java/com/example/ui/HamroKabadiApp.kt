package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.data.model.*
import com.example.data.repository.KabadiRepository
import com.example.ui.screens.auth.ForgotPasswordScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.screens.chat.ChatScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.listings.CreateListingScreen
import com.example.ui.screens.listings.ListingDetailScreen
import com.example.ui.screens.market.MarketPriceScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.transactions.TransactionsScreen
import kotlinx.coroutines.launch

sealed class Screen {
  object Login : Screen()
  object Register : Screen()
  object ForgotPassword : Screen()
  object Home : Screen()
  object MarketPrices : Screen()
  object Transactions : Screen()
  object Profile : Screen()
  data class ListingDetail(val listingId: String) : Screen()
  object CreateListing : Screen()
  data class Chat(val listingId: String) : Screen()
  data class ViewUserProfile(val userId: String) : Screen()
}

enum class NavigationTab(val label: String, val icon: ImageVector, val tag: String) {
  HOME("Home", Icons.Default.Home, "nav_home"),
  MARKET("Market Rates", Icons.Default.TrendingUp, "nav_market"),
  TRANSACTIONS("Deals", Icons.Default.ReceiptLong, "nav_transactions"),
  PROFILE("Profile", Icons.Default.Person, "nav_profile")
}

@Composable
fun HamroKabadiApp() {
  val context = LocalContext.current
  val repository = remember { KabadiRepository(context) }
  val scope = rememberCoroutineScope()

  val currentUser by repository.currentUser.collectAsState()
  val allListings by repository.allListings.collectAsState(initial = emptyList())
  val marketPrices by repository.allMarketPrices.collectAsState(initial = emptyList())

  // Transactions for current user
  val transactions by remember(currentUser) {
    if (currentUser != null) repository.getUserTransactions(currentUser!!.id)
    else kotlinx.coroutines.flow.flowOf(emptyList())
  }.collectAsState(initial = emptyList())

  // Navigation Stack
  var backStack by remember {
    mutableStateOf(listOf<Screen>(Screen.Home))
  }
  val currentScreen = backStack.lastOrNull() ?: Screen.Home

  // Selected bottom tab
  val currentTab = when (currentScreen) {
    is Screen.Home -> NavigationTab.HOME
    is Screen.MarketPrices -> NavigationTab.MARKET
    is Screen.Transactions -> NavigationTab.TRANSACTIONS
    is Screen.Profile -> NavigationTab.PROFILE
    else -> null
  }

  fun navigateTo(screen: Screen) {
    backStack = backStack + screen
  }

  fun navigateBack() {
    if (backStack.size > 1) {
      backStack = backStack.dropLast(1)
    }
  }

  fun switchTab(tab: NavigationTab) {
    val target = when (tab) {
      NavigationTab.HOME -> Screen.Home
      NavigationTab.MARKET -> Screen.MarketPrices
      NavigationTab.TRANSACTIONS -> Screen.Transactions
      NavigationTab.PROFILE -> Screen.Profile
    }
    // Replace stack with target screen
    backStack = listOf(target)
  }

  // Handle system back button
  BackHandler(enabled = backStack.size > 1) {
    navigateBack()
  }

  // If user is null and not on auth screens, force Login
  val isAuthScreen = currentScreen is Screen.Login ||
      currentScreen is Screen.Register ||
      currentScreen is Screen.ForgotPassword

  if (currentUser == null && !isAuthScreen) {
    backStack = listOf(Screen.Login)
  }

  Scaffold(
    bottomBar = {
      // Show bottom navigation on primary tabs when logged in
      if (currentUser != null && currentTab != null) {
        NavigationBar(modifier = Modifier.testTag("main_bottom_nav")) {
          NavigationTab.entries.forEach { tab ->
            NavigationBarItem(
              selected = currentTab == tab,
              onClick = { switchTab(tab) },
              icon = { Icon(tab.icon, contentDescription = tab.label) },
              label = { Text(tab.label) },
              modifier = Modifier.testTag(tab.tag)
            )
          }
        }
      }
    }
  ) { innerPadding ->
    when (currentScreen) {
      is Screen.Login -> {
        LoginScreen(
          onLoginSuccess = { backStack = listOf(Screen.Home) },
          onNavigateToRegister = { navigateTo(Screen.Register) },
          onNavigateToForgotPassword = { navigateTo(Screen.ForgotPassword) },
          onLoginAttempt = { id, pass ->
            val res = repository.login(id, pass)
            if (res.isSuccess) Result.success(Unit) else Result.failure(res.exceptionOrNull() ?: Exception())
          },
          modifier = Modifier.padding(innerPadding)
        )
      }

      is Screen.Register -> {
        RegisterScreen(
          onRegisterSuccess = { backStack = listOf(Screen.Home) },
          onBackToLogin = { navigateBack() },
          onRegisterAttempt = { name, phone, email, pass, role, avatar, loc ->
            repository.register(name, phone, email, pass, role, avatar, loc)
          },
          modifier = Modifier.padding(innerPadding)
        )
      }

      is Screen.ForgotPassword -> {
        ForgotPasswordScreen(
          onRequestOtp = { phone -> repository.requestOtp(phone) },
          onVerifyOtp = { phone, otp -> repository.verifyOtp(phone, otp) },
          onResetPassword = { phone, newPass -> repository.resetPassword(phone, newPass) },
          onBackToLogin = {
            backStack = listOf(Screen.Login)
          },
          modifier = Modifier.padding(innerPadding)
        )
      }

      is Screen.Home -> {
        HomeScreen(
          currentUser = currentUser,
          listings = allListings,
          onListingClick = { listing -> navigateTo(Screen.ListingDetail(listing.id)) },
          onCreateListingClick = { navigateTo(Screen.CreateListing) },
          onNavigateToProfile = { navigateTo(Screen.Profile) },
          onCalculateDistance = { lat, lng ->
            val uLat = currentUser?.latitude ?: 27.7172
            val uLng = currentUser?.longitude ?: 85.3240
            repository.calculateDistanceKm(uLat, uLng, lat, lng)
          },
          modifier = Modifier.padding(innerPadding)
        )
      }

      is Screen.MarketPrices -> {
        MarketPriceScreen(
          marketPrices = marketPrices,
          onUpdateMarketPrice = { categoryKey, newRate ->
            scope.launch { repository.updateMarketPrice(categoryKey, newRate) }
          },
          modifier = Modifier.padding(innerPadding)
        )
      }

      is Screen.Transactions -> {
        TransactionsScreen(
          currentUser = currentUser,
          transactions = transactions,
          onUpdateStatus = { txnId, status ->
            scope.launch { repository.updateTransactionStatus(txnId, status) }
          },
          onSubmitReview = { txnId, targetUserId, rating, comment ->
            scope.launch {
              currentUser?.let { me ->
                repository.addReview(txnId, targetUserId, me.id, me.fullName, rating, comment)
              }
            }
          },
          modifier = Modifier.padding(innerPadding)
        )
      }

      is Screen.Profile -> {
        val userReviews by remember(currentUser) {
          if (currentUser != null) repository.getReviewsForUser(currentUser!!.id)
          else kotlinx.coroutines.flow.flowOf(emptyList())
        }.collectAsState(initial = emptyList())

        if (currentUser != null) {
          ProfileScreen(
            user = currentUser!!,
            isOwnProfile = true,
            reviews = userReviews,
            onSwitchRole = { newRole ->
              scope.launch { repository.switchActiveRole(newRole) }
            },
            onUpdateProfile = { name, phone, email, bio, loc ->
              scope.launch { repository.updateUserProfile(name, phone, email, bio, loc) }
            },
            onLogout = {
              repository.logout()
              backStack = listOf(Screen.Login)
            },
            onBack = null,
            modifier = Modifier.padding(innerPadding)
          )
        }
      }

      is Screen.ListingDetail -> {
        val targetListing = allListings.find { it.id == currentScreen.listingId }
        if (targetListing != null) {
          val mp = marketPrices.find { it.categoryKey.equals(targetListing.category, ignoreCase = true) }
          val uLat = currentUser?.latitude ?: 27.7172
          val uLng = currentUser?.longitude ?: 85.3240
          val distance = repository.calculateDistanceKm(uLat, uLng, targetListing.latitude, targetListing.longitude)

          ListingDetailScreen(
            listing = targetListing,
            currentUser = currentUser,
            marketPrice = mp,
            distanceKm = distance,
            onBack = { navigateBack() },
            onStartChat = { navigateTo(Screen.Chat(targetListing.id)) },
            onViewSellerProfile = { sellerId -> navigateTo(Screen.ViewUserProfile(sellerId)) },
            onMarkAsSold = {
              scope.launch { repository.markListingAsSold(targetListing.id) }
            },
            onDeleteListing = {
              scope.launch {
                repository.deleteListing(targetListing.id)
                navigateBack()
              }
            },
            modifier = Modifier.padding(innerPadding)
          )
        } else {
          navigateBack()
        }
      }

      is Screen.CreateListing -> {
        CreateListingScreen(
          marketPrices = marketPrices,
          onBack = { navigateBack() },
          onSubmitListing = { title, cat, weight, price, desc, loc, lat, lng, photo ->
            scope.launch {
              val created = repository.createListing(title, cat, weight, price, desc, loc, lat, lng, photo)
              navigateBack()
              navigateTo(Screen.ListingDetail(created.id))
            }
          },
          modifier = Modifier.padding(innerPadding)
        )
      }

      is Screen.Chat -> {
        val targetListing = allListings.find { it.id == currentScreen.listingId }
        val messages by repository.getMessagesForListing(currentScreen.listingId).collectAsState(initial = emptyList())

        if (targetListing != null && currentUser != null) {
          val isSeller = currentUser!!.id == targetListing.sellerId
          val otherUserId = if (isSeller) "HK-1002" else targetListing.sellerId

          ChatScreen(
            listing = targetListing,
            currentUser = currentUser!!,
            messages = messages,
            onSendMessage = { text ->
              scope.launch {
                repository.sendTextMessage(
                  targetListing.id,
                  currentUser!!.id,
                  currentUser!!.fullName,
                  otherUserId,
                  text
                )
              }
            },
            onSendOffer = { offerPrice ->
              scope.launch {
                repository.sendOfferMessage(
                  targetListing.id,
                  currentUser!!.id,
                  currentUser!!.fullName,
                  otherUserId,
                  offerPrice,
                  targetListing.weightKg
                )
              }
            },
            onAcceptOffer = { offerMsg ->
              scope.launch {
                val seller = if (isSeller) currentUser!! else repository.getUserById(targetListing.sellerId) ?: currentUser!!
                val buyer = if (!isSeller) currentUser!! else repository.getUserById("HK-1002") ?: currentUser!!
                repository.acceptOffer(offerMsg, targetListing, buyer, seller)
              }
            },
            onDeclineOffer = { offerMsg ->
              scope.launch { repository.declineOffer(offerMsg) }
            },
            onShareLocation = { addr, lat, lng ->
              scope.launch {
                repository.sendLocationMessage(
                  targetListing.id,
                  currentUser!!.id,
                  currentUser!!.fullName,
                  otherUserId,
                  addr,
                  lat,
                  lng
                )
              }
            },
            onBack = { navigateBack() },
            modifier = Modifier.padding(innerPadding)
          )
        } else {
          navigateBack()
        }
      }

      is Screen.ViewUserProfile -> {
        var profileUser by remember { mutableStateOf<UserEntity?>(null) }
        LaunchedEffect(currentScreen.userId) {
          profileUser = repository.getUserById(currentScreen.userId)
        }
        val userReviews by repository.getReviewsForUser(currentScreen.userId).collectAsState(initial = emptyList())

        if (profileUser != null) {
          ProfileScreen(
            user = profileUser!!,
            isOwnProfile = profileUser!!.id == currentUser?.id,
            reviews = userReviews,
            onSwitchRole = {},
            onUpdateProfile = { _, _, _, _, _ -> },
            onLogout = {},
            onBack = { navigateBack() },
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}
