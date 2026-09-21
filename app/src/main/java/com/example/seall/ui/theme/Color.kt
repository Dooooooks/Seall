package com.example.seall.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// --- Core Palette Hex Definitions ---
val SeallLightBase = Color(0xFFF6F4F0)      // Light Base
val SeallPrimary = Color(0xFF82B8B8)        // Primary / Accent
val SeallDarkContrast = Color(0xFF222222)   // Dark Contrast / Text

// --- Complementary Tones for States & Badges ---
val SeallPrimaryDark = Color(0xFF5E9696)
val SeallPrimaryContainer = Color(0xFFD6EAEA)
val SeallOnPrimaryContainer = Color(0xFF163838)

val SeallSurfaceLight = Color(0xFFFFFFFF)
val SeallSurfaceVariantLight = Color(0xFFEBE7DF)
val SeallOutlineLight = Color(0xFFD1CCC3)

val SeallSurfaceDark = Color(0xFF222222)
val SeallBackgroundDark = Color(0xFF161616)
val SeallSurfaceVariantDark = Color(0xFF2E2E2E)
val SeallOutlineDark = Color(0xFF4A4A4A)

// Functional Badges
val SeallPaidGreen = Color(0xFF58A389)
val SeallPaidGreenContainer = Color(0xFFDDF3EB)
val SeallUnpaidAmber = Color(0xFFD97736)
val SeallUnpaidAmberContainer = Color(0xFFFDECE1)

// --- Light Color Scheme ---
val LightColorScheme = lightColorScheme(
    primary = SeallPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = SeallPrimaryContainer,
    onPrimaryContainer = SeallOnPrimaryContainer,
    secondary = SeallPrimary,
    onSecondary = SeallDarkContrast,
    background = SeallLightBase,
    onBackground = SeallDarkContrast,
    surface = SeallSurfaceLight,
    onSurface = SeallDarkContrast,
    surfaceVariant = SeallSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF505050),
    outline = SeallOutlineLight
)

// --- Dark Color Scheme ---
val DarkColorScheme = darkColorScheme(
    primary = SeallPrimary,
    onPrimary = SeallDarkContrast,
    primaryContainer = Color(0xFF294747),
    onPrimaryContainer = SeallLightBase,
    secondary = SeallPrimary,
    onSecondary = SeallDarkContrast,
    background = SeallBackgroundDark,
    onBackground = SeallLightBase,
    surface = SeallSurfaceDark,
    onSurface = SeallLightBase,
    surfaceVariant = SeallSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFCCCCCC),
    outline = SeallOutlineDark
)
