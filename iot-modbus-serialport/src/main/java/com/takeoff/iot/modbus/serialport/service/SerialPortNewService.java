package com.takeoff.iot.modbus.serialport.service;

public interface SerialPortNewService {


    /**
     * 连接串口
     * @param port
     * @param baudRate
     * @param timeout
     * @param thread
     * @param sleepTime
     */
    void openComPort(String port, Integer baudRate, Integer timeout, Integer thread, int sleepTime);

    /**
     * netty连接串口
     * @param port
     * @param baudRate
     * @param thread
     */
    void openComPort(String port, Integer baudRate, Integer thread);

    /**
     * 关闭串口
     */
    void closeSerialPort();

    /**
     * 发送数据到串口
     * @param bytes
     */
    void serialPortSendData(byte[] bytes);
}
