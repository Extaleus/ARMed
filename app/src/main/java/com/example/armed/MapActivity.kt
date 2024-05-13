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

        // Обработчики нажатий на кабинеты
        bindingClass.btnWard.setOnClickListener { onClickCabinet(R.string.ward, "Кабинет № 14") }
        bindingClass.btnPediatrics.setOnClickListener { onClickCabinet(R.string.pediatrics, "Кабинет № 13") }
        bindingClass.btnLabs.setOnClickListener { onClickCabinet(R.string.labs, "Кабинет № 8") }
        bindingClass.btnObstetrics.setOnClickListener { onClickCabinet(R.string.obstetrics, "Кабинет № 12") }
        bindingClass.btnOperating.setOnClickListener { onClickCabinet(R.string.operating, "Кабинет № 6") }
        bindingClass.btnSurgical.setOnClickListener { onClickCabinet(R.string.surgical, "Кабинет № 1") }
        bindingClass.btnOrthoped.setOnClickListener { onClickCabinet(R.string.orthopedInfo, "Кабинет № 2") }
        bindingClass.btnWaitingRoom.setOnClickListener { onClickCabinet(R.string.waitingRoom, "Кабинет № 3") }
        bindingClass.btnRadiology.setOnClickListener { onClickCabinet(R.string.radiology, "Кабинет № 11") }
        bindingClass.btnIntensiveCare.setOnClickListener { onClickCabinet(R.string.intensiveCare, "Кабинет № 5") }
        bindingClass.btnResus.setOnClickListener { onClickCabinet(R.string.resus, "Кабинет № 4") }
        bindingClass.btnExamin.setOnClickListener { onClickCabinet(R.string.examine, "Кабинет № 9") }
        bindingClass.btnAmbulanceTriage.setOnClickListener { onClickCabinet(R.string.AmbulanceTriage1, "Кабинет № 7") }
        bindingClass.btnWalking.setOnClickListener { onClickCabinet(R.string.AmbulanceTriage, "Кабинет № 10") }

        bindingClass.btnExitCLInfo.setOnClickListener { bindingClass.clInfo.visibility = View.GONE }
        bindingClass.btnExit.setOnClickListener { finish() }
    }

    // Обработчики событий касания
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount > 1) {
            scaleGestureDetector.onTouchEvent(event)
        } else {
            gestureDetector.onTouchEvent(event)
        }
        return super.onTouchEvent(event)
    }

    // Обработчики событий масштабирования
    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            factor *= detector.scaleFactor
            factor = factor.coerceIn(0.8f, 2.5f)
            bindingClass.clMap.scaleX = factor
            bindingClass.clMap.scaleY = factor
            return true
        }
    }

    // Обработчики событий перемещения
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

    // Обработчики нажатий на кабинеты
    private fun onClickCabinet(newText: Int, newTitle: String) {
        bindingClass.clInfo.visibility = View.VISIBLE
        bindingClass.tvInfoTitle.text = newTitle
        bindingClass.tvInfo.text = getString(newText)
    }
}