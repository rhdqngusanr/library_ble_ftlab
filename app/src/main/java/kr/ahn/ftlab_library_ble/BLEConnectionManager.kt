package kr.ahn.ftlab_library_ble

import android.bluetooth.BluetoothGattCharacteristic
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log


object BLEConnectionManager {
    private const val tag = "BLEConnectionManager"
    private var mOnBleConnectionListener: OnBleConnectionListener? = null
    private var mBLEService: BLEService? = null
    private var isBind = false
    private var mDataBLEForControl: BluetoothGattCharacteristic? = null
    private var mDataBLEForMeas: BluetoothGattCharacteristic? = null
    private var sendCount = 0
    //private var mDataBLEForLog: BluetoothGattCharacteristic? = null

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            Log.e(tag, "onServiceConnected")

            mBLEService = (service as BLEService.LocalBinder).getService()

            if (!mBLEService?.initialize()!!) {
                Log.e(tag, "Unable to initialize")
            }
            else{
                mOnBleConnectionListener?.onBleServiceOpen(true)
            }
        }
        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.e(tag, "onServiceDisconnected")
            mBLEService = null
        }
    }

    fun setListener(scanConnectionListenerOn: OnBleConnectionListener) {
        mOnBleConnectionListener = scanConnectionListenerOn
    }
    /**
     * Initialize Bluetooth service.
     */
    fun initBLEService(context: Context) {
        try {
            if (mBLEService == null) {
                val gattServiceIntent = Intent(context, BLEService::class.java)
                /*if (context != null) {
                    isBind = context.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
                }*/
                context.let{
                    isBind = context.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
                }
            }
        } catch (e: Exception) {
            Log.e(tag, e.message.toString())
        }
    }

    /**
     * Unbind BLE Service
     */
    fun unBindBLEService(context: Context) {
        /*if (mServiceConnection != null && isBind) {
            context.unbindService(mServiceConnection)
        }*/
        if (isBind){
            mServiceConnection.let{
                context.unbindService(mServiceConnection)
            }
        }
        mBLEService = null
    }

    /**
     * Connect to a BLE Device
     */
    fun connect(deviceAddress: String): Boolean {
        var result = false

        if (mBLEService != null) {
            result = mBLEService!!.connect(deviceAddress)
        }
        return result
    }

    /**
     * Disconnect
     */
    fun disconnect() {
        /*if (null != mBLEService) {
            mBLEService!!.disconnect()
            mBLEService = null
        }*/
        mBLEService?.let{
            mBLEService!!.disconnect()
            mBLEService = null
        }

    }

    fun send(cmd: Int) {
        /*if (mDataBLEForControl != null) {
            mDataBLEForControl!!.value = byteArrayOf(cmd.toByte())
            writeBLECharacteristic(mDataBLEForControl)
        }*/
        Log.e(tag, "send1 : $cmd")
        val sData = ByteArray(4)
        val checkSum = 255 - cmd

        sData[0] = cmd.toByte()
        sData[1] = 0.toByte()
        sData[2] = 0.toByte()
        sData[3] = checkSum.toByte()
        mDataBLEForControl.let{
            mDataBLEForControl!!.value = sData
            writeBLECharacteristic(mDataBLEForControl)
        }
    }
    fun sendOnlyCmd(cmd: Int) {
        /*if (mDataBLEForControl != null) {
            mDataBLEForControl!!.value = byteArrayOf(cmd.toByte())
            writeBLECharacteristic(mDataBLEForControl)
        }*/
        Log.e(tag, "send1 : $cmd")
        val sData = ByteArray(1)

        sData[0] = cmd.toByte()
        mDataBLEForControl.let{
            mDataBLEForControl!!.value = sData
            writeBLECharacteristic(mDataBLEForControl)
        }
    }

    fun send(inData: ByteArray) {
        /*if (mDataBLEForControl != null) {
            mDataBLEForControl!!.value = byteArrayOf(cmd.toByte())
            writeBLECharacteristic(mDataBLEForControl)
        }*/
        Log.e(tag, "send2 : ByteArray : ${inData[0]}")
        var checkSum = 0
        for (element in inData) {
            checkSum += element
        }
        checkSum = 255 - checkSum

        val sendByte = inData + checkSum.toByte()

        mDataBLEForControl.let{

            mDataBLEForControl!!.value = sendByte
            // mBluetoothGatt!!.writeCharacteristic(mDataBLEForControl)
            writeBLECharacteristic(mDataBLEForControl)
        }

        Log.e(tag, "sendCheckSum: ${checkSum.toByte()}")
    }

    fun send(cmd: Int ,cmdFlag: Boolean) {
        /*if (mDataBLEForControl != null) {
         mDataBLEForControl!!.value = byteArrayOf(cmd.toByte())
         writeBLECharacteristic(mDataBLEForControl)
     }*/
        var sData = ByteArray(1)
        sData[0] = cmd.toByte()

        if(cmdFlag){
            sData = ByteArray(2)
            val checkSum = 255 - cmd

            sData[0] = cmd.toByte()
            sData[1] = checkSum.toByte()
        }

        mDataBLEForControl.let {
            //mDataBLEForControl!!.value =  byteArrayOf(cmd.toByte())
            mDataBLEForControl!!.value = sData
            writeBLECharacteristic(mDataBLEForControl)
        }
    }

    fun send(inData: ByteArray , cmdFlag: Boolean) {

        ///////////////////////////////////////////////////
        /*if (mDataBLEForControl != null) {
            mDataBLEForControl!!.value = byteArrayOf(cmd.toByte())
            writeBLECharacteristic(mDataBLEForControl)
        }*/
        Log.e(tag, "send : ByteArray : ${inData[0].toUByte()}")
        var checkSum = 0
        if (cmdFlag) {
            for (element in inData) {
                checkSum += element
            }
            checkSum = 255 - checkSum
            val sendByte = inData + checkSum.toByte()

            mDataBLEForControl.let{

                mDataBLEForControl!!.value = sendByte
                // mBluetoothGatt!!.writeCharacteristic(mDataBLEForControl)
                writeBLECharacteristic(mDataBLEForControl)
            }

            Log.e(tag, "sendCheckSum: ${checkSum.toByte()}")
        }else {

            mDataBLEForControl.let {
                mDataBLEForControl!!.value = inData
                writeBLECharacteristic(mDataBLEForControl)
            }
        }
    }

    /**
     * Write BLE Characteristic.
     */
    private fun writeBLECharacteristic(characteristic: BluetoothGattCharacteristic?) {
        if (null != characteristic) {
            //if (mBLEService != null) {
            sendCount++
            Log.e("writeBLECharacteristic","send Count $sendCount")
            mBLEService.let{
                mBLEService?.writeCharacteristic(characteristic)
            }
        }
    }


    /**
     * findBLEGattService
     */
    fun findBLEGattService(mContext: Context) {
        if (mBLEService == null) {
            return
        }

        if (mBLEService!!.getSupportedGattServices() == null) {
            return
        }

        var uuid: String
        mDataBLEForControl = null
        mDataBLEForMeas = null
        //mDataBLEForLog = null

        val serviceList = mBLEService!!.getSupportedGattServices()

        if (serviceList != null) {
            var flagResult = false

            for (gattService in serviceList) {
                //if (gattService.getUuid().toString().equals(mContext.getString(R.string.char_uuid_emergency), ignoreCase = true)) {
                if (gattService.uuid.toString().equals(FtlabBleStruct.uuidEmergency, ignoreCase = true)) {
                    val gattCharacteristics = gattService.characteristics

                    for (gattCharacteristic in gattCharacteristics) {
                        uuid = if (gattCharacteristic.uuid != null) gattCharacteristic.uuid.toString() else ""

                        when(uuid){
                            FtlabBleStruct.uuidControl -> {
                                var newChar = gattCharacteristic
                                newChar = setProperties(0, newChar)
                               mDataBLEForControl = newChar
                                //mDataBLEForControl = gattService.getCharacteristic(gattCharacteristic.uuid)
                            }

                            //mContext.resources.getString(R.string.char_uuid_mes) -> {
                            FtlabBleStruct.uuidMeas -> {
                                var newChar = gattCharacteristic
                                newChar = setProperties(1, newChar)
                                mDataBLEForMeas = newChar
                                //mDataBLEForMeas = gattService.getCharacteristic(gattCharacteristic.uuid)
                            }

                           // mContext.resources.getString(R.string.char_uuid_log) -> {
                            /*"00001526-0000-1000-8000-00805f9b34fb" -> {
                                var newChar = gattCharacteristic
                                newChar = setProperties(1, newChar)
                                mDataBLEForLog = newChar
                                //mDataBLEForLog = gattService.getCharacteristic(gattCharacteristic.uuid)
                            }*/
                        }

                        /*if (uuid.equals(mContext.resources.getString(R.string.char_uuid_control), ignoreCase = true)) {
                            var newChar = gattCharacteristic
                            newChar = setProperties(newChar)
                            mDataBLEForControl = newChar
                        }
                        else if (uuid.equals(mContext.resources.getString(R.string.char_uuid_mes), ignoreCase = true)) {
                            var newChar = gattCharacteristic
                            newChar = setProperties(newChar)
                            mDataBLEForMeas = newChar
                        }
                        else if (uuid.equals(mContext.resources.getString(R.string.char_uuid_log), ignoreCase = true)) {
                            var newChar = gattCharacteristic
                            newChar = setProperties(newChar)
                            mDataBLEForLog = newChar
                        }*/

                        //if (mDataBLEForControl != null && mDataBLEForMeas != null && mDataBLEForLog != null){
                        if (mDataBLEForControl != null && mDataBLEForMeas != null){
                            flagResult = true
                            break
                        }
                    }
                }
            }

            Log.e(tag, "findBLEGattService flagResult= $flagResult")
            mOnBleConnectionListener?.onBleConnectionCompleted(flagResult)
        }

    }

    private fun setProperties(inType: Int,  gattCharacteristic: BluetoothGattCharacteristic):
            BluetoothGattCharacteristic {
        val characteristicProperties = gattCharacteristic.properties

        if (characteristicProperties and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
            try {
                Thread.sleep(200)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            mBLEService?.setCharacteristicNotification(gattCharacteristic, true)
        }

        if (characteristicProperties and BluetoothGattCharacteristic.PROPERTY_INDICATE > 0) {
            mBLEService?.setCharacteristicIndication(gattCharacteristic, true)
        }

        if (characteristicProperties and BluetoothGattCharacteristic.PROPERTY_WRITE > 0) {
            //gattCharacteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            gattCharacteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        }

        if (characteristicProperties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0) {
            gattCharacteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        }

        if (inType == 1){
            if (characteristicProperties and BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                try {
                    Thread.sleep(200)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                gattCharacteristic.writeType = BluetoothGattCharacteristic.PROPERTY_READ
            }
        }

        return gattCharacteristic
    }
}