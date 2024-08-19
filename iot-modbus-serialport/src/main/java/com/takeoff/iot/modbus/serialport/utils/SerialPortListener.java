package com.takeoff.iot.modbus.serialport.utils;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.takeoff.iot.modbus.serialport.service.DataAvailableListener;

/**
 * @description:
 * @projectName: iot-modbus
 * @package: com.takeoff.iot.modbus.serialport.utils
 * @className: SerialPortListener
 * @author: yw
 * @date: 2024/8/18 16:50
 */
public class SerialPortListener implements SerialPortDataListener {

    private final DataAvailableListener mDataAvailableListener;

    public SerialPortListener(DataAvailableListener mDataAvailableListener) {
        this.mDataAvailableListener = mDataAvailableListener;
    }

    //必须是return这个才会开启串口工具的监听
    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (mDataAvailableListener != null) {
            mDataAvailableListener.dataAvailable();
        }
    }
}
