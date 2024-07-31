package com.takeoff.iot.modbus.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Printer {

    private String Vid;
    private String Pid;
    private String ModelName;
    private String SerialNum;
    private int Port;
    private int UsbHandle;

    public Printer() {
        UsbHandle = -1;
    }

    @Override
    public String toString() {
        return "Printer{" +
                "Vid='" + Vid + '\'' +
                ", Pid='" + Pid + '\'' +
                ", ModelName='" + ModelName + '\'' +
                ", SerialNum='" + SerialNum + '\'' +
                ", Port=" + Port +
                ", UsbHandle=" + UsbHandle +
                '}';
    }

}
