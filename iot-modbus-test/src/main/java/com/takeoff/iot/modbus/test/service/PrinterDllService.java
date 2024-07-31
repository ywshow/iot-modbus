package com.takeoff.iot.modbus.test.service;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

public interface PrinterDllService extends StdCallLibrary {

    /**
     * 64位JDK请选择JsPrinterDllX64.dll
     * 32位JDK请选择JsPrinterDll.dll
     */
    PrinterDllService comm = Native.load("dll/JsPrinterDllx64.dll", PrinterDllService.class);

    int OpenUsb(String vid, String pid);

    int OpenUsbW(String vid, String pid, int num);

    int WriteUsb(int fs, byte[] data, int len);

    int ReadUsb(int fs, byte[] data, int len);

    boolean CloseUsb(int fs);

    int UsbScan(byte[] buffer, int len);
}
