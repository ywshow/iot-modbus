package com.takeoff.iot.modbus.serialport.utils;

import com.fazecast.jSerialComm.SerialPort;
import com.google.common.collect.Lists;
import com.takeoff.iot.modbus.common.utils.JudgeEmptyUtils;
import com.takeoff.iot.modbus.serialport.service.DataAvailableListener;
import gnu.io.SerialPortEventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: jSerialComm 串口工具类
 * @projectName: iot-modbus
 * @package: com.takeoff.iot.modbus.serialport.utils
 * @className: SerialPortUtilNew
 * @author: yw
 * @date: 2024/8/18 16:38
 */
@Slf4j
public class SerialPortUtilNew {

    //私有化SerialTool类的构造方法，不允许其他类生成SerialTool对象
    private SerialPortUtilNew() {

    }

    /**
     * 查找所有可用端口
     *
     * @return {@link List}<{@link String}>
     */
    public static List<String> findPorts() {
        // 获得当前所有可用串口
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        List<String> portNameList = Lists.newLinkedList();
        // 将可用串口名添加到List并返回该List
        for (SerialPort serialPort : serialPorts) {
            portNameList.add(serialPort.getSystemPortName());
        }
        //去重
        portNameList = portNameList.stream().distinct().collect(Collectors.toList());
        return portNameList;
    }


    /**
     * 打开串口
     *
     * @param portName 端口名称
     * @param baudRate 波特率
     * @param databits 数据位
     * @param stopbits 停止位
     * @param parity   校验位（奇偶位）
     * @return 串口对象
     */
    public static final SerialPort openPort(String portName, int timeout, Integer baudRate, int databits, int stopbits, int parity) {
        SerialPort serialPort = SerialPort.getCommPort(portName);
        if (baudRate != null) {
            serialPort.setBaudRate(baudRate);
        }
        //开启串口
        if (!serialPort.isOpen()) {
            serialPort.openPort(1000);
        } else {
            return serialPort;
        }
        // 设置一下串口的波特率等参数
        // 数据位：8
        // 停止位：1
        // 校验位：None
        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        if (null != baudRate) {
            serialPort.setComPortParameters(baudRate, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        }
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 1000, 1000);
        return serialPort;
    }

    /**
     * 关闭串口
     *
     * @param serialPort 待关闭的串口对象
     */
    public static void closePort(SerialPort serialPort) {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }
    }

    /**
     * 往串口发送数据
     *
     * @param serialPort 串口对象
     * @param content    待发送数据
     */
    public static void sendToPort(SerialPort serialPort, byte[] content) {
        if (!serialPort.isOpen()) {
            return;
        }
        serialPort.writeBytes(content, content.length);
    }

    /**
     * 从串口读取数据
     *
     * @param serialPort 当前已建立连接的SerialPort对象
     * @return 读取到的数据
     */
    public static byte[] readFromPort(SerialPort serialPort) {
        log.info("开始发送消息====》 {}", serialPort.bytesAvailable());
        byte[] reslutData = null;
        try {
            if (!serialPort.isOpen()) {
                return null;
            }
            int i = 0;
            while (serialPort.bytesAvailable() > 0 && i++ < 5) Thread.sleep(20);
            byte[] readBuffer = new byte[serialPort.bytesAvailable()];
            int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
            if (numRead > 0) {
                reslutData = readBuffer;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return reslutData;
    }

    /**
     * 添加监听器
     * @param serialPort    串口对象
     * @param listener  串口存在有效数据监听
     */
    public static void addListener(SerialPort serialPort, DataAvailableListener listener) {
        try {
            // 给串口添加监听器
            serialPort.addDataListener(new SerialPortListener(listener));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
