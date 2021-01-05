package kr.ahn.ftlab_library_ble



open class FtlabBle {

    fun uuidSetting(uuidEmergency:String, uuidControl :String, uuidMeas:String){
        FtlabBleStruct.uuidEmergency = uuidEmergency
        FtlabBleStruct.uuidControl = uuidControl
        FtlabBleStruct.uuidMeas = uuidMeas
    }

}