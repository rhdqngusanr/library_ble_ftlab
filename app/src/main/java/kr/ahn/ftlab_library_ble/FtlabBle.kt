package kr.ahn.ftlab_library_ble

import android.content.Context
import android.widget.Toast

open class FtlabBle: OnDeviceScanListener {

    var deviceScanList = ArrayList<BleDeviceData>()

    fun scanStart(inContext: Context,containSnName:String){
        if (!BLEDeviceManager.init(inContext)) {
            Toast.makeText(inContext,"BLE NOT SUPPORTED",Toast.LENGTH_SHORT).show()
            return
        }
        BLEDeviceManager.setListener(this)

        BLEDeviceManager.scanBLEDevice(containSnName)
    }
    fun scanClear(){
        deviceScanList.clear()
    }

    fun scanComplete(deviceDataList: BleDeviceData) : ArrayList<BleDeviceData>{
        deviceScanList.add(deviceDataList)

        return deviceScanList
    }


    override fun onScanCompleted(deviceDataList: BleDeviceData) {
        scanComplete(deviceDataList)
    }

}