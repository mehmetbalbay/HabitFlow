package com.habitflow.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.Font as GoogleFontFamilyFont

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = com.habitflow.core.designsystem.R.array.com_google_android_gms_fonts_certs
)

private val ubuntu = GoogleFont("Ubuntu")

val UbuntuFontFamily = FontFamily(
    GoogleFontFamilyFont(googleFont = ubuntu, fontProvider = provider, weight = FontWeight.Light, style = FontStyle.Normal),
    GoogleFontFamilyFont(googleFont = ubuntu, fontProvider = provider, weight = FontWeight.Normal, style = FontStyle.Normal),
    GoogleFontFamilyFont(googleFont = ubuntu, fontProvider = provider, weight = FontWeight.Medium, style = FontStyle.Normal),
    GoogleFontFamilyFont(googleFont = ubuntu, fontProvider = provider, weight = FontWeight.SemiBold, style = FontStyle.Normal),
    GoogleFontFamilyFont(googleFont = ubuntu, fontProvider = provider, weight = FontWeight.Bold, style = FontStyle.Normal)
)

val HabitFlowTypography = Typography(
    displayLarge = TextStyle(fontFamily = UbuntuFontFamily, fontWeight = FontWeight.Bold, fontSize = 48.sp),
    displayMedium = TextStyle(fontFamily = UbuntuFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 40.sp),
    displaySmall = TextStyle(fontFamily = UbuntuFontFamily, fontWeight = FontWeight.Medium, fontSize = 34.sp),
    headlineLarge = TextStyle(fontFamily = UbuntuFontFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = UbuntuFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 28.sp),
    headlineSmall = TextStyle(fontFamily = UbuntuFontFamily, fontWeight = FontWeight.Medium, fontSize = 24.sp),
    titleLarge = TextStyle(fontFamily = UbuntuFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = UbuntuFontFamily, fontWeight = FontWeight.Medium, fontSize = 18.sp),
    titleSmall = TextStyle(fontFamily = UbuntuFontFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    bodyLarge = TextStyle(fontFamily = UbuntuFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = UbuntuFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = UbuntuFontFamily, fontWeight = FontWeight.Light, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = UbuntuFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = UbuntuFontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = UbuntuFontFamily, fontWeight = FontWeight.Light, fontSize = 11.sp)
)

