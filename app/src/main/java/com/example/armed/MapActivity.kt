package com.example.armed

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
//        bindingClass.btnPassageway.setOnClickListener { onClickCabinet(R.string.passageway) }
        bindingClass.btnObstetrics.setOnClickListener { onClickCabinet(R.string.obstetrics) }
        bindingClass.btnOperating.setOnClickListener { onClickCabinet(R.string.operating) }
        bindingClass.btnSurgical.setOnClickListener { onClickCabinet(R.string.surgical) }
//        bindingClass.btnPassageway1.setOnClickListener { onClickCabinet(R.string.passageway) }
        bindingClass.btnOrthoped.setOnClickListener { onClickCabinet(R.string.orthopedInfo) }
        bindingClass.btnWaitingRoom.setOnClickListener { onClickCabinet(R.string.waitingRoom) }
        bindingClass.btnRadiology.setOnClickListener { onClickCabinet(R.string.radiology) }
        bindingClass.btnIntensiveCare.setOnClickListener { onClickCabinet(R.string.intensiveCare) }
//        bindingClass.btnPassageway2.setOnClickListener { onClickCabinet(R.string.passageway) }
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
            factor = factor.coerceIn(0.8f, 10.0f)
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
            bindingClass.clMap.x -= distanceX
            bindingClass.clMap.y -= distanceY
            return true
        }
    }

    private fun onClickCabinet(newText: Int) {
        bindingClass.clInfo.visibility = View.VISIBLE
        bindingClass.tvInfo.text = getString(newText)
    }
}