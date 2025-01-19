package com.example.shakeit.ui.elements

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix


import com.google.firebase.auth.FirebaseAuth

@Composable
fun QrGenerationPage(navController: NavController) {
    val userId = remember { FirebaseAuth.getInstance().currentUser?.uid ?: "UNKNOWN" }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background
        Background()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your QR Code",
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val qrCodeBitmap = generateQrCode(userId)

            if (qrCodeBitmap != null) {
                Image(
                    bitmap = qrCodeBitmap.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier.size(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            CustomButton(
                text = "Back",
                fontSize = 20,
                onClick = { navController.navigateUp() },
                textColor = Color.White
            )
        }
    }
}
fun generateQrCode(data: String): Bitmap? {
    return try {
        val size = 500
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
            data,
            BarcodeFormat.QR_CODE,
            size,
            size
        )
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.Black.toArgb() else Color.White.toArgb())
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
