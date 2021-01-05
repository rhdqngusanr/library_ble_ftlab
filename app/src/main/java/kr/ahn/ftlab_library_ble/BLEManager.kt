package kr.ahn.ftlab_library_ble

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*

class BLEManager{
   // val TAG = "BLEManager"
    val mLog = AnkoLogger("BLEManager")
    private var mOnBleConnectionListener: OnBleConnectionListener? = null
    private var mOnBleDataParseListener: OnBleDataParseListener? = null
    private val cal: Calendar = Calendar.getInstance()

    fun setConnectionListener(bleConnectionListenerOn: OnBleConnectionListener) {
        mOnBleConnectionListener = bleConnectionListenerOn
    }

    fun setDataParseListener(dataParseListenerOn: OnBleDataParseListener) {
        mOnBleDataParseListener = dataParseListenerOn
    }


    val mGattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when ( intent.action){
                BLEConstants.ACTION_GATT_CONNECTED -> {
                    mLog.info("ACTION_GATT_CONNECTED")
                }
                BLEConstants.ACTION_GATT_DISCONNECTED -> {
                    mLog.info("ACTION_GATT_DISCONNECTED")
                    mOnBleConnectionListener?.onBleConnectionCompleted(false)
                }
                BLEConstants.ACTION_GATT_SERVICES_DISCOVERED -> {
                    mLog.info("ACTION_GATT_SERVICES_DISCOVERED")

                    try {
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    FtlabBleStruct.initFlag = true

                    BLEConnectionManager.findBLEGattService(context)
                }
                BLEConstants.ACTION_DATA_PARSE -> {
                    val data = intent.getByteArrayExtra(BLEConstants.EXTRA_DATA)
                    val uuId = intent.getStringExtra(BLEConstants.EXTRA_UUID)
                    mLog.info("ACTION_DATA_PARSE $data")

                    if (uuId == FtlabBleStruct.uuidMeas){
                        mOnBleDataParseListener?.onBleDataParse(data)
                    }
                }
            }
        }
    }

    fun makeGattUpdateIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BLEConstants.ACTION_GATT_CONNECTED)
        intentFilter.addAction(BLEConstants.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(BLEConstants.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(BLEConstants.ACTION_DATA_PARSE)
        //intentFilter.addAction(BLEConstants.ACTION_DATA_WRITTEN)

        return intentFilter
    }
}