package com.davidgyoungtech.cdmdemo

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.companion.AssociationRequest
import android.companion.BluetoothLeDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import org.w3c.dom.Text
import java.util.*
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    var statusTextView: TextView? = null
    var button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        statusTextView = findViewById(R.id.status)
        button = findViewById(R.id.continueButton)
    }

    override fun onResume() {
        super.onResume()
        val deviceManager = this.getSystemService(Context.COMPANION_DEVICE_SERVICE) as CompanionDeviceManager
        if (deviceManager.associations.size == 0) {
            statusTextView?.text = "We will call CompanionDeviceManager associate.  Please make sure a connectable Bluetooth LE device is in the vicinity and choose it to go to the next step."
            button?.setOnClickListener {
                val requestBuilder = AssociationRequest.Builder()
                    .setSingleDevice(false)
                val pairingRequest: AssociationRequest = requestBuilder.build()
                Log.d(TAG, "calling companion device manager associate")
                deviceManager.associate(pairingRequest,
                    @RequiresApi(Build.VERSION_CODES.O)
                    object : CompanionDeviceManager.Callback() {
                        // Called when a device is found. Launch the IntentSender so the user
                        // can select the device they want to pair with.
                        override fun onDeviceFound(chooserLauncher: IntentSender) {
                            this@MainActivity.startIntentSenderForResult(chooserLauncher,
                                12345, null, 0, 0, 0)
                        }
                        override fun onFailure(error: CharSequence?) {
                            Log.e(TAG, "Failed to associate: "+error)
                            // Handle the failure.
                        }
                    }, Handler(Looper.getMainLooper())
                )
            }
        }
        else if (deviceManager.hasNotificationAccess(ComponentName(applicationContext, MyNotificationListenerService::class.java))) {
            Log.d(TAG,"CompanionDeviceManager hasNotificationAccess is now true.")
            statusTextView?.text = "CompanionDeviceManager hasNotificationAccess is now true.  Now go to Settings to view the \"Device & App Notifications\" pane for this app and see that two checkboxes are not checked.  If you monitor LogCat, you will see that MyNotificationListenerService never logs events about receiving notifications until these two checkboxes are checked."
            button?.setOnClickListener {
                val intent =
                    Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                startActivity(intent)
            }
        }
        else {
            Log.d(TAG,"CompanionDeviceManager hasNotificationAccess is false")
            statusTextView?.text = "Now we will call CompanionDeviceManager requestNotificationAccess"
            button?.setOnClickListener {
                Log.d(TAG,"Calling CompanionDeviceManager requestNotificationAccess")
                deviceManager.requestNotificationAccess(
                    ComponentName(
                        applicationContext,
                        MyNotificationListenerService::class.java
                    )
                )
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            12345 -> when(resultCode) {
                Activity.RESULT_OK -> {
                    var device: BluetoothDevice? =
                        data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE) as? BluetoothDevice
                    val scanResult: ScanResult? =
                        data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE) as? ScanResult
                    if (device == null && scanResult != null) {
                        device = scanResult?.device
                    }
                    statusTextView?.text = "Companion Device Manager just associated with "+device
                    Log.d(TAG, "Companion Device Manager just associated with "+device)
                }
                else -> {
                    statusTextView?.text = "Companion Device Manager association failed: "+resultCode
                    Log.e(TAG, "Companion Device Manager association failed: "+resultCode)
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }


    companion object {
        private const val TAG = "MainActivity"
    }
}