package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = MedixTealPrimaryDark,
  onPrimary = MedixTealOnPrimaryDark,
  primaryContainer = MedixTealContainerDark,
  onPrimaryContainer = MedixTealOnContainerDark,
  secondary = MedixCyanSecondaryDark,
  onSecondary = MedixCyanOnSecondaryDark,
  secondaryContainer = MedixCyanContainerDark,
  onSecondaryContainer = MedixCyanOnContainerDark,
  tertiary = MedixTertiaryContainer,
  onTertiary = MedixOnTertiaryContainer,
  background = MedixBgDark,
  onBackground = MedixOnSurfaceDark,
  surface = MedixSurfaceDark,
  onSurface = MedixOnSurfaceDark,
  surfaceVariant = MedixSurfaceVariantDark,
  outline = MedixOutlineDark,
  error = EmergencyRed,
  errorContainer = EmergencyRedContainer,
  onErrorContainer = EmergencyRedOnContainer
)

private val LightColorScheme = lightColorScheme(
  primary = MedixTealPrimary,
  onPrimary = MedixTealOnPrimary,
  primaryContainer = MedixTealContainer,
  onPrimaryContainer = MedixTealOnContainer,
  secondary = MedixCyanSecondary,
  onSecondary = MedixCyanOnSecondary,
  secondaryContainer = MedixCyanContainer,
  onSecondaryContainer = MedixCyanOnContainer,
  tertiary = MedixTertiary,
  onTertiary = MedixOnTertiary,
  background = MedixBgLight,
  onBackground = MedixOnSurfaceLight,
  surface = MedixSurfaceLight,
  onSurface = MedixOnSurfaceLight,
  surfaceVariant = MedixSurfaceVariantLight,
  outline = MedixOutlineLight,
  error = EmergencyRed,
  errorContainer = EmergencyRedContainer,
  onErrorContainer = EmergencyRedOnContainer
)

@Composable
fun MedixTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Healthcare theme uses calibrated medical teal palette by default
  content: @Composable () -> Unit,
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

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MedixTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
