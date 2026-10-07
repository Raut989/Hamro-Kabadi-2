package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
  primary = ForestGreenPrimary,
  onPrimary = Color.White,
  primaryContainer = MintGreenContainer,
  onPrimaryContainer = OnMintGreenContainer,
  secondary = TealSecondary,
  onSecondary = Color.White,
  secondaryContainer = TealSecondaryContainer,
  onSecondaryContainer = OnTealSecondaryContainer,
  tertiary = CopperAmber,
  onTertiary = OnCopperAmber,
  tertiaryContainer = CopperAmberContainer,
  onTertiaryContainer = Color(0xFF4E1600),
  background = EcoBackground,
  onBackground = EcoTextPrimary,
  surface = EcoSurface,
  onSurface = EcoTextPrimary,
  surfaceVariant = EcoSurfaceVariant,
  onSurfaceVariant = EcoTextSecondary,
  outline = EcoDivider
)

private val DarkColorScheme = darkColorScheme(
  primary = ForestGreenDarkPrimary,
  onPrimary = Color(0xFF00390E),
  primaryContainer = Color(0xFF005318),
  onPrimaryContainer = Color(0xFFA5F4AC),
  secondary = Color(0xFF80CBC4),
  onSecondary = Color(0xFF003731),
  secondaryContainer = Color(0xFF004D40),
  onSecondaryContainer = Color(0xFFB2DFDB),
  tertiary = Color(0xFFFFB74D),
  onTertiary = Color(0xFF4E1600),
  background = EcoDarkBackground,
  onBackground = EcoDarkTextPrimary,
  surface = EcoDarkSurface,
  onSurface = EcoDarkTextPrimary,
  surfaceVariant = EcoDarkSurfaceVariant,
  onSurfaceVariant = EcoDarkTextSecondary
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Set to false to preserve brand green identity
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
