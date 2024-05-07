package com.example.armed

import android.content.res.Resources
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.armed.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity() {
    private lateinit var bindingClass: ActivityMapBinding

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var gestureDetector: GestureDetector

    private var factor = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMapBinding.inflate(layoutInflater)

        setContentView(bindingClass.root)
        
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
        gestureDetector = GestureDetector(this, MoveListener())

        bindingClass.btnWard.setOnClickListener { onClickCabinet(R.string.ward) }
        bindingClass.btnPediatrics.setOnClickListener { onClickCabinet(R.string.pediatrics) }
        bindingClass.btnLabs.setOnClickListener { onClickCabinet(R.string.labs) }
        bindingClass.btnObstetrics.setOnClickListener { onClickCabinet(R.string.obstetrics) }
        bindingClass.btnOperating.setOnClickListener { onClickCabinet(R.string.operating) }
        bindingClass.btnSurgical.setOnClickListener { onClickCabinet(R.string.surgical) }
        bindingClass.btnOrthoped.setOnClickListener { onClickCabinet(R.string.orthopedInfo) }
        bindingClass.btnWaitingRoom.setOnClickListener { onClickCabinet(R.string.waitingRoom) }
        bindingClass.btnRadiology.setOnClickListener { onClickCabinet(R.string.radiology) }
        bindingClass.btnIntensiveCare.setOnClickListener { onClickCabinet(R.string.intensiveCare) }
        bindingClass.btnResus.setOnClickListener { onClickCabinet(R.string.resus) }
        bindingClass.btnExamin.setOnClickListener { onClickCabinet(R.string.examine) }
        bindingClass.btnAmbulanceTriage.setOnClickListener { onClickCabinet(R.string.AmbulanceTriage) }
        bindingClass.btnWalking.setOnClickListener { onClickCabinet(R.string.AmbulanceTriage) }

        bindingClass.btnExitCLInfo.setOnClickListener { bindingClass.clInfo.visibility = View.GONE }
        bindingClass.btnExit.setOnClickListener { finish() }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount > 1) {
            scaleGestureDetector.onTouchEvent(event)
        } else {
            gestureDetector.onTouchEvent(event)
        }
        return super.onTouchEvent(event)
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            factor *= detector.scaleFactor
            factor = factor.coerceIn(0.8f, 2.5f)
            bindingClass.clMap.scaleX = factor
            bindingClass.clMap.scaleY = factor
            return true
        }
    }

    inner class MoveListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            val newX = bindingClass.clMap.x - distanceX
            val newY = bindingClass.clMap.y - distanceY

            // Получаем размеры экрана
            val displayMetrics = Resources.getSystem().displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels

            // Получаем размеры вашего элемента
            val elementWidth = bindingClass.clMap.width
            val elementHeight = bindingClass.clMap.height

            // Вычисляем границы, которые элемент может превысить
            val boundaryX = screenWidth - elementWidth / 4
            val boundaryY = screenHeight - elementHeight / 4

            // Проверяем, не выходит ли элемент за пределы экрана
            if (newX >= -elementWidth * 3 / 4 && newX <= boundaryX) {
                bindingClass.clMap.x = newX
            }
            if (newY >= -elementHeight * 3 / 4 && newY <= boundaryY) {
                bindingClass.clMap.y = newY
            }

            return true
        }
    }

    private fun onClickCabinet(newText: Int) {
        bindingClass.clInfo.visibility = View.VISIBLE
        bindingClass.tvInfo.text = getString(newText)
    }
}