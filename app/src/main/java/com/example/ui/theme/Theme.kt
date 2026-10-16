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

private val DarkColorScheme =
  darkColorScheme(
    primary = ElectricBluePrimaryDark,
    onPrimary = ElectricBlueOnPrimaryDark,
    primaryContainer = ElectricBlueContainerDark,
    onPrimaryContainer = ElectricBlueOnContainerDark,
    secondary = AmberEnergySecondaryDark,
    onSecondary = AmberEnergyOnSecondaryDark,
    secondaryContainer = AmberEnergyContainerDark,
    onSecondaryContainer = AmberEnergyOnContainerDark,
    tertiary = TealTechTertiaryDark,
    onTertiary = TealTechOnTertiaryDark,
    tertiaryContainer = TealTechContainerDark,
    onTertiaryContainer = TealTechOnContainerDark,
    background = NeutralDarkBackground,
    surface = NeutralDarkSurface,
    surfaceVariant = NeutralDarkSurfaceVariant,
    onBackground = NeutralDarkOnSurface,
    onSurface = NeutralDarkOnSurface,
    outline = NeutralDarkOutline
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ElectricBluePrimary,
    onPrimary = ElectricBlueOnPrimary,
    primaryContainer = ElectricBlueContainer,
    onPrimaryContainer = ElectricBlueOnContainer,
    secondary = AmberEnergySecondary,
    onSecondary = AmberEnergyOnSecondary,
    secondaryContainer = AmberEnergyContainer,
    onSecondaryContainer = AmberEnergyOnContainer,
    tertiary = TealTechTertiary,
    onTertiary = TealTechOnTertiary,
    tertiaryContainer = TealTechContainer,
    onTertiaryContainer = TealTechOnContainer,
    background = NeutralLightBackground,
    surface = NeutralLightSurface,
    surfaceVariant = NeutralLightSurfaceVariant,
    onBackground = NeutralLightOnSurface,
    onSurface = NeutralLightOnSurface,
    outline = NeutralLightOutline
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
