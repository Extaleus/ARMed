package com.example.armed

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.armed.theme.ARSceneViewComposeTheme
import com.google.ar.core.AugmentedImage
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import kotlinx.coroutines.launch

class QRActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("myLog", "onCreate")
        setContent {
            ARSceneViewComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ARComposable(resources, this.packageName)
                }
            }
        }
    }
}

//Model
private var kModelFile = ""

private val models: ArrayMap<String, String> = arrayMapOf(
    "qrexample" to "models/damaged_helmet.glb",
    "orthoped" to "models/bin.glb",
//    "cardiologist" to "https://sceneview.github.io/assets/models/DamagedHelmet.glb",
)

private fun createBitmap(resource: Resources, key: String, packageName: String): Bitmap? {
    val resID: Int = resource.getIdentifier(key, "drawable", packageName)
    Log.d("my", "key: $key, result: $resID")
    return if (resID != 0) {
        BitmapFactory.decodeResource(
            resource,
            resID
        )
    } else {
        BitmapFactory.decodeResource(
            resource,
            R.drawable.map_hospital
        )
    }
}

@Composable
fun ARComposable(resource: Resources, packageName: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        var isLoading by remember { mutableStateOf(false) }
        val planeRenderer by remember { mutableStateOf(false) } // Изменено здесь
        val engine = rememberEngine()
        val modelLoader = rememberModelLoader(engine)
        val childNodes = rememberNodes()
        val coroutineScope = rememberCoroutineScope()

        ARScene(
            modifier = Modifier.fillMaxSize(),
            childNodes = childNodes,
            engine = engine,
            modelLoader = modelLoader,
            planeRenderer = planeRenderer,
            sessionConfiguration = { session, config ->
                val augmentedImageDatabase = AugmentedImageDatabase(session)
                for (key in models.keys) {
                    augmentedImageDatabase.addImage(key, createBitmap(resource, key, packageName))
                }
                config.augmentedImageDatabase = augmentedImageDatabase
                config.depthMode =
                    when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        true -> Config.DepthMode.AUTOMATIC
                        else -> Config.DepthMode.DISABLED
                    }
                config.instantPlacementMode =
                    Config.InstantPlacementMode.DISABLED
                config.lightEstimationMode =
                    Config.LightEstimationMode.ENVIRONMENTAL_HDR
                session.configure(config)
            },
            onSessionUpdated = { _, frame ->
                if (childNodes.isNotEmpty()) return@ARScene

                val updatedImages =
                    frame.getUpdatedTrackables(AugmentedImage::class.java)
                for (image in updatedImages) {
                    if (image.name != null && image.trackingState == TrackingState.TRACKING) {
                        Log.d("myLog", "TRACKING && NAME != NULL")
                        kModelFile = models.getValue(image.name)
                        Log.d("myLog", "${image.name}   ${models.getValue(image.name)}")
                        isLoading = true
                        childNodes += AnchorNode(
                            engine = engine,
                            anchor = image.createAnchor(image.centerPose)
                        ).apply {
                            isEditable = true
                            coroutineScope.launch {
                                modelLoader.loadModelInstance(kModelFile)?.let {
                                    addChildNode(
                                        ModelNode(
                                            modelInstance = it,
// Scale to fit in a 0.5 meters cube
                                            scaleToUnits = 0.5f,
// Bottom origin instead of center so the
// model base is on floor
                                            centerOrigin = Position(y = -0.5f)
                                        ).apply {
                                            isEditable = true
                                        }
                                    )
                                }
                            }
                        }
                        isLoading = false


                    }
                }
            }
        )
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Center),
                color = androidx.compose.ui.graphics.Color.Cyan
            )
        }
    }
}


//STABLE W SCAN
//@Composable
//fun ARComposable() {
//    Box(
//        modifier = Modifier.fillMaxSize(),
//    ) {
//        var isLoading by remember { mutableStateOf(false) }
//        var planeRenderer by remember { mutableStateOf(true) }
//        val engine = rememberEngine()
//        val modelLoader = rememberModelLoader(engine)
//        val childNodes = rememberNodes()
//        val coroutineScope = rememberCoroutineScope()
//
//        ARScene(
//            modifier = Modifier.fillMaxSize(),
//            childNodes = childNodes,
//            engine = engine,
//            modelLoader = modelLoader,
//            planeRenderer = planeRenderer,
//            sessionConfiguration = { session, config ->
//                config.depthMode =
//                    when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
//                        true -> Config.DepthMode.AUTOMATIC
//                        else -> Config.DepthMode.DISABLED
//                    }
//                config.instantPlacementMode = Config.InstantPlacementMode.DISABLED
//                config.lightEstimationMode =
//                    Config.LightEstimationMode.ENVIRONMENTAL_HDR
//            },
//            onSessionUpdated = { _, frame ->
//                if (childNodes.isNotEmpty()) return@ARScene
//
//                frame.getUpdatedPlanes()
//                    .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
//                    ?.let { plane ->
//                        isLoading = true
//                        childNodes += AnchorNode(
//                            engine = engine,
//                            anchor = plane.createAnchor(plane.centerPose)
//                        ).apply {
//                            isEditable = true
//                            coroutineScope.launch {
//                                modelLoader.loadModelInstance(kModelFile)?.let {
//                                    addChildNode(
//                                        ModelNode(
//                                            modelInstance = it,
//                                            // Scale to fit in a 0.5 meters cube
//                                            scaleToUnits = 0.5f,
//                                            // Bottom origin instead of center so the
//                                            // model base is on floor
//                                            centerOrigin = Position(y = -0.5f)
//                                        ).apply {
//                                            isEditable = true
//                                        }
//                                    )
//                                }
//                                planeRenderer = false
//                                isLoading = false
//                            }
//                        }
//                    }
//            }
//        )
//        if (isLoading) {
//            CircularProgressIndicator(
//                modifier = Modifier
//                    .size(64.dp)
//                    .align(Alignment.Center),
//                color = Color.Magenta
//            )
//        }
//    }
//}

//package com.example.armed
//
//import android.content.res.Resources
//import android.graphics.BitmapFactory
//import android.os.Bundle
//import androidx.activity.compose.setContent
//import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Modifier
//import com.example.armed.theme.ARSceneViewComposeTheme
//import com.google.ar.core.AugmentedImage
//import com.google.ar.core.AugmentedImageDatabase
//import com.google.ar.core.Config
//import com.google.ar.core.TrackingState
//import io.github.sceneview.ar.ARScene
//import io.github.sceneview.ar.node.AnchorNode
//import io.github.sceneview.math.Position
//import io.github.sceneview.node.ModelNode
//import io.github.sceneview.rememberEngine
//import io.github.sceneview.rememberModelLoader
//import io.github.sceneview.rememberNodes
//import kotlinx.coroutines.launch
//import me.dm7.barcodescanner.zbar.Result
//import me.dm7.barcodescanner.zbar.ZBarScannerView
//
//class QRActivity : AppCompatActivity(), ZBarScannerView.ResultHandler {
//    private lateinit var zbView: ZBarScannerView
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        zbView = ZBarScannerView(this)
//        setContentView(zbView)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        zbView.stopCamera()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        zbView.setResultHandler(this)
//        zbView.startCamera()
//    }
//
//    override fun handleResult(result: Result?) {
////        kModelFile = result?.contents.toString()
////        kModelFile = "https://sceneview.github.io/assets/models/DamagedHelmet.glb"
//        kModelFile = "assets/models/damaged_helmet.glb"
//        zbView.stopCamera()
//
//        setContent {
//            ARSceneViewComposeTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    ARComposable(resources)
//                }
//            }
//        }
//    }
//}
//
////Model
//private var kModelFile = ""
//
//@Composable
//fun ARComposable(resource: Resources) {
//    Box(
//        modifier = Modifier.fillMaxSize(),
//    ) {
//        val planeRenderer by remember { mutableStateOf(false) } // Изменено здесь
//        val engine = rememberEngine()
//        val modelLoader = rememberModelLoader(engine)
//        val childNodes = rememberNodes()
//        val coroutineScope = rememberCoroutineScope()
//
//        ARScene(
//            modifier = Modifier.fillMaxSize(),
//            childNodes = childNodes,
//            engine = engine,
//            modelLoader = modelLoader,
//            planeRenderer = planeRenderer,
//            sessionConfiguration = { session, config ->
//// Загрузите ваше изображение из ресурсов
//                val bitmap = BitmapFactory.decodeResource(
//                    resource,
//                    R.drawable.qrexample
//                )
//                val augmentedImageDatabase = AugmentedImageDatabase(session)
//                augmentedImageDatabase.addImage("qrexample", bitmap)
//                config.augmentedImageDatabase = augmentedImageDatabase
//
//                config.depthMode =
//                    when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
//                        true -> Config.DepthMode.AUTOMATIC
//                        else -> Config.DepthMode.DISABLED
//                    }
//                config.instantPlacementMode =
//                    Config.InstantPlacementMode.DISABLED
//                config.lightEstimationMode =
//                    Config.LightEstimationMode.ENVIRONMENTAL_HDR
//            },
//            onSessionUpdated = { _, frame ->
//                if (childNodes.isNotEmpty()) return@ARScene
//
//                val updatedImages =
//                    frame.getUpdatedTrackables(AugmentedImage::class.java)
//                for (image in updatedImages) {
//                    if (image.name == "qrexample" && image.trackingState == TrackingState.TRACKING) {
//                        childNodes += AnchorNode(
//                            engine = engine,
//                            anchor = image.createAnchor(image.centerPose)
//                        ).apply {
//                            isEditable = true
//                            coroutineScope.launch {
//                                modelLoader.loadModelInstance(kModelFile)?.let {
//                                    addChildNode(
//                                        ModelNode(
//                                            modelInstance = it,
//// Scale to fit in a 0.5 meters cube
//                                            scaleToUnits = 0.5f,
//// Bottom origin instead of center so the
//// model base is on floor
//                                            centerOrigin = Position(y = -0.5f)
//                                        ).apply {
//                                            isEditable = true
//                                        }
//                                    )
//                                }
//                            }
////                            break
//                        }
//                    }
//                }
//            }
//        )
//    }
//}

