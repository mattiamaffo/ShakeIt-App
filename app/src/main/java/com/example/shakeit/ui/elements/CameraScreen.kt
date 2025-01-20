import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import org.opencv.core.Core
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.example.shakeit.ui.elements.Background
import org.opencv.core.CvType
import java.io.OutputStream


@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            Log.d("CameraScreen", "Captured image width: ${bitmap.width}, height: ${bitmap.height}")
            imageBitmap = applyCartoonEffect(bitmap)
        } else {
            Log.e("CameraScreen", "Bitmap is null or invalid")
            Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background
        Background()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            imageBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Cartoon Image",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { launcher.launch() }) {
                Text("Capture Image")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                imageBitmap?.let { saveImageToGallery(context, it) }
            }) {
                Text("Save Image")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { navController.navigateUp() }) {
                Text("Back")
            }
        }
    }
}

fun applyCartoonEffect(bitmap: Bitmap): Bitmap {
    val convertedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val src = Mat()
    Utils.bitmapToMat(convertedBitmap, src)

    // Convert to CV_8UC3 (RGB)
    if (src.type() != CvType.CV_8UC3) {
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2RGB)
    }

    // Convert to grayscale
    val gray = Mat()
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGB2GRAY)

    // Apply median blur
    Imgproc.medianBlur(gray, gray, 7)

    // Detect edges using adaptive thresholding
    val edges = Mat()
    Imgproc.adaptiveThreshold(gray, edges, 255.0, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 9, 9.0)

    // Apply bilateral filter to smooth colors
    val color = Mat()
    Imgproc.bilateralFilter(src, color, 9, 75.0, 75.0)

    // Convert edges to color image
    val edgesColor = Mat()
    Imgproc.cvtColor(edges, edgesColor, Imgproc.COLOR_GRAY2RGB)

    // Combine edges with the color image
    val cartoon = Mat()
    Core.bitwise_and(color, edgesColor, cartoon)

    val cartoonBitmap = Bitmap.createBitmap(cartoon.cols(), cartoon.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(cartoon, cartoonBitmap)

    // Cleanup
    src.release()
    gray.release()
    edges.release()
    color.release()
    edgesColor.release()
    cartoon.release()

    return cartoonBitmap
}



fun saveImageToGallery(context: Context, bitmap: Bitmap) {
    val filename = "cartoon_face_${System.currentTimeMillis()}.png"
    val fos: OutputStream?

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/CartoonFaces")
        }
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        fos = resolver.openOutputStream(imageUri!!)
    } else {
        val imagesDir = context.getExternalFilesDir(null)?.path + "/CartoonFaces"
        val file = java.io.File(imagesDir, filename)
        fos = file.outputStream()
    }

    if (fos != null) {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
    }
    fos?.flush()
    fos?.close()
    Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
}

