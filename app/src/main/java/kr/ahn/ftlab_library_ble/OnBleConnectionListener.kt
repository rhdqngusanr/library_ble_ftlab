package kr.ahn.ftlab_library_ble


interface OnBleConnectionListener {

    /**
     * Scan Completed -
     *
     * @param deviceDataList - Send available devices as a list to the init Activity
     * The List Contain, device name and mac address,
     */
    fun onBleServiceOpen(result: Boolean)
    fun onBleConnectionCompleted(result: Boolean)

}