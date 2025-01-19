import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.shakeit.data.domain.AuthRepository
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView

@Composable
fun QrScannerScreen(navController: NavController) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }
    val scannedUserId = remember { mutableStateOf<String?>(null) }
    val cameraPermissionGranted = remember { mutableStateOf(false) }

    // Permission Request
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            cameraPermissionGranted.value = isGranted
            if (!isGranted) {
                Toast.makeText(context, "Camera permission required", Toast.LENGTH_LONG).show()
                navController.popBackStack()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            cameraPermissionGranted.value = true
        }
    }



    if (cameraPermissionGranted.value) {
        Box(modifier = Modifier.fillMaxSize()) {
            BarcodeScannerView { scannedResult ->
                scannedUserId.value = scannedResult
                Log.d("QRScanner", "Scanned result: $scannedResult")
                if (scannedResult.isNotEmpty()) {
                    authRepository.addFriendByQr(
                        scannedResult,
                        onSuccess = {
                            Toast.makeText(context, "Friend added successfully!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onFailure = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    } else {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Requesting camera permission...", color = Color.White)
        }
    }
}

@Composable
fun BarcodeScannerView(onScanned: (String) -> Unit) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            CompoundBarcodeView(ctx).apply {
                this.barcodeView.decoderFactory = com.journeyapps.barcodescanner.DefaultDecoderFactory()

                decodeContinuous(object : BarcodeCallback {
                    override fun barcodeResult(result: BarcodeResult?) {
                        result?.let {
                            val scannedText = it.text
                            Log.d("QRScanner", "Scanned result: $scannedText")
                            onScanned(scannedText)
                        }
                    }

                    override fun possibleResultPoints(resultPoints: MutableList<com.google.zxing.ResultPoint>?) {}
                })
                resume()
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { barcodeView ->
            barcodeView.resume()
        }
    )
}




