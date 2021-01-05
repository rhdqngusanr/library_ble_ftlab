package kr.ahn.ftlab_library_ble


interface OnBleDataParseListener {

    /**
     * Scan Completed -
     *
     * @param deviceDataList - Send available devices as a list to the init Activity
     * The List Contain, device name and mac address,
     */
    fun onBleDataParse(data: ByteArray?)
}