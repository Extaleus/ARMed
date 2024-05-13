package com.example.armed

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.armed.databinding.ActivityMainMenuBinding
import com.example.armed.databinding.ActivityMainMenuBinding.inflate

class MainMenuActivity : AppCompatActivity() {
    private lateinit var bindingClass: ActivityMainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingClass = inflate(layoutInflater)
        setContentView(bindingClass.root)

        bindingClass.btnQR.setOnClickListener {
            val intent = Intent(this, ARActivity::class.java)
            startActivity(intent)
        }

        bindingClass.btnNFC.setOnClickListener {
            val intent = Intent(this, NFCActivity::class.java)
            startActivity(intent)
        }

        bindingClass.btnMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        bindingClass.btnExit.setOnClickListener { onExitPressed() }

    }

    private fun onExitPressed() {
        AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
            .setTitle(R.string.exitQuestion)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes) { _, _ -> super.finishAndRemoveTask() }.create()
            .show()
    }

}