package com.example.armed

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit

class ARActivity : AppCompatActivity(R.layout.activity_ar) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val builder = AlertDialog.Builder(this@ARActivity, R.style.MyAlertDialogStyle)
        builder.setTitle(R.string.qr_alert)
        builder.setPositiveButton(R.string.ok) { _, _ ->
            supportFragmentManager.commit {
                add(R.id.containerFragment, MainFragment::class.java, Bundle())
            }
        }
        val myDialog = builder.create()
        myDialog.setCanceledOnTouchOutside(false)
        myDialog.show()
    }
}