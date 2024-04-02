package com.example.armed

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.tech.NfcF
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.armed.databinding.ActivityNfcBinding

class NFCActivity : AppCompatActivity() {
    private lateinit var bindingClass: ActivityNfcBinding

    private var intentFiltersArray: Array<IntentFilter>? = null
    private val techListsArray = arrayOf(arrayOf(NfcF::class.java.name))
    private val nfcAdapter: NfcAdapter? by lazy {
        NfcAdapter.getDefaultAdapter(this)
    }
    private var pendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        bindingClass = ActivityNfcBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)

        bindingClass.btnExit.setOnClickListener { finish() }

        try {
            pendingIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE
            )

            val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)

            try {
                ndef.addDataType("text/plain")
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("fail", e)
            }

            intentFiltersArray = arrayOf(ndef)

//            checkNFC()

        } catch (ex: Exception) {
            Toast.makeText(applicationContext, ex.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        if (this.isFinishing) {
            nfcAdapter?.disableForegroundDispatch(this)
        }
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        checkNFC()
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(
            this, pendingIntent, intentFiltersArray, techListsArray
        )
    }

    private var tagMessage = ""

    // stable older method
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val action = intent.action

        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {

            val parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

            with(parcelables) {
                try {
                    val inNdefMessage = this?.get(0) as NdefMessage
                    val inNdefRecords = inNdefMessage.records
                    //if there are many records, you can call inNdefRecords[1] as array
                    val ndefRecord0 = inNdefRecords[0]
                    val inMessage = String(ndefRecord0.payload)
                    tagMessage = inMessage.drop(3)
                    bindingClass.tvMessage.text = tagMessage

                } catch (ex: Exception) {
                    Toast.makeText(
                        applicationContext, "Not so fast", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun checkNFC() {
        if (nfcAdapter == null) {
            val builder = AlertDialog.Builder(this@NFCActivity, R.style.MyAlertDialogStyle)
            builder.setTitle(R.string.nfc_not_sup_title)
            builder.setMessage(R.string.nfc_not_sup_desc)
            builder.setPositiveButton("Вернуться") { _, _ -> finish() }
            val myDialog = builder.create()
            myDialog.setCanceledOnTouchOutside(false)
            myDialog.show()
            bindingClass.tvMessage.text = getString(R.string.nfc_not_sup)

        } else if (!nfcAdapter!!.isEnabled) {
            val builder = AlertDialog.Builder(this@NFCActivity, R.style.MyAlertDialogStyle)
            builder.setTitle(R.string.nfc_turn_on_title)
            builder.setMessage(R.string.nfc_turn_on_desc)
            builder.setPositiveButton("Настройки") { _, _ -> startActivity(Intent(Settings.ACTION_NFC_SETTINGS)) }
            builder.setNegativeButton("Отмена", null)
            val myDialog = builder.create()
            myDialog.setCanceledOnTouchOutside(false)
            myDialog.show()
            bindingClass.tvMessage.text = getString(R.string.nfc_turn_on)

        } else {
            bindingClass.tvMessage.text = getString(R.string.nfc)
        }
    }
}