package com.takeoff.iot.modbus.serialport.service;

/**
 * @description: 串口存在有效数据监听
 * @projectName: iot-modbus
 * @package: com.takeoff.iot.modbus.serialport.utils
 * @className: SerialPortManager
 * @author: yw
 * @date: 2024/8/18 16:49
 */
public interface DataAvailableListener {
    /**
     * 串口存在有效数据
     */
    void dataAvailable();
}
