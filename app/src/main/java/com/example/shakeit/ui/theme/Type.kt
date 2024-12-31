package com.example.shakeit.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.shakeit.R

val Pontiac = FontFamily(
    Font(R.font.fontspring_pontiac_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.fontspring_pontiac_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.fontspring_pontiac_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.fontspring_pontiac_black, FontWeight.Black, FontStyle.Normal)
)

val Montserrat = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.montserrat_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.montserrat_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.montserrat_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
)

// Set di stili di testo personalizzati
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Pontiac,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Pontiac,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
        fontStyle = FontStyle.Italic,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Pontiac,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        fontStyle = FontStyle.Italic,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),

    titleLarge = TextStyle(
        fontFamily = Pontiac,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),

    headlineMedium = TextStyle(
        fontFamily = Pontiac,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.15.sp
    ),
)

data class CustomTypography(
    val montserratR: TextStyle,
    val montserratI: TextStyle,
    val montserratM: TextStyle,
    val montserratSB: TextStyle,
    val montserratSBi: TextStyle,
)

val MyTypography = CustomTypography(
    montserratR = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.15.sp
    ),
    montserratI = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    montserratM = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),
    montserratSB = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontStyle = FontStyle.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    montserratSBi = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontStyle = FontStyle.Italic,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    ),
)

