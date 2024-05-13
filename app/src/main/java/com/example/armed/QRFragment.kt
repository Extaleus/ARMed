package com.example.armed

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.addAugmentedImage
import io.github.sceneview.ar.arcore.getUpdatedAugmentedImages
import io.github.sceneview.ar.node.AugmentedImageNode
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode

class MainFragment : Fragment(R.layout.fragment_ar) {

    // Создаем экземпляр класса ARSceneView для настройки и использования AR
    private lateinit var sceneView: ARSceneView

    private val augmentedImageNodes = mutableListOf<AugmentedImageNode>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnExitFrag = view.findViewById<ImageButton>(R.id.btnExitFrag)

        // Обработчик нажатия кнопки "Выход"
        btnExitFrag.setOnClickListener {
            sceneView.session?.pause()
            sceneView.session?.close()
            val intent = Intent(requireActivity(), MainMenuActivity::class.java)
            startActivity(intent)
        }

// Добавляем изображения в список изображений для обнаружения
sceneView = view.findViewById<ARSceneView>(R.id.sceneView).apply {
    configureSession { session, config ->
        config.addAugmentedImage(
            session, "rabbit",
            requireContext().assets.open("augmentedimages/rabbit.jpg")
                .use(BitmapFactory::decodeStream)
        )
                config.addAugmentedImage(
                    session, "cardiologist",
                    requireContext().assets.open("augmentedimages/cardiologist.jpg")
                        .use(BitmapFactory::decodeStream)
                )
            }

            // Поиск метки на изображении камеры
            onSessionUpdated = { _, frame ->
                frame.getUpdatedAugmentedImages().forEach { augmentedImage ->
                    if (augmentedImageNodes.none { it.imageName == augmentedImage.name }) {
                        val augmentedImageNode = AugmentedImageNode(engine, augmentedImage).apply {
                            // Добавление 3D модели в сцену
                            when (augmentedImage.name) {
                                "rabbit" -> addChildNode(
                                    ModelNode(
                                        modelInstance = modelLoader.createModelInstance(
                                            assetFileLocation = "models/nevrolog_model.glb"
                                        ),
                                        scaleToUnits = 0.3f,
                                        centerOrigin = Position(0.0f)
                                    )
                                )

                                "cardiologist" -> addChildNode(
                                    ModelNode(
                                        modelInstance = modelLoader.createModelInstance(
                                            assetFileLocation = "models/cadriolog_model.glb"
                                        ),
                                        scaleToUnits = 0.1f,
                                        centerOrigin = Position(0.0f),
                                    )
                                )
                            }
                        }
                        clearChildNodes()
                        environment =
                            environmentLoader.createHDREnvironment("augmentedimages/environment.hdr")!!
                        addChildNode(augmentedImageNode)
                        augmentedImageNodes += augmentedImageNode
                    }
                }
            }
        }
    }
}