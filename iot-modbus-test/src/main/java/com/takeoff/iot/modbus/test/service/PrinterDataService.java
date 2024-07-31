package com.takeoff.iot.modbus.test.service;

import com.takeoff.iot.modbus.common.entity.PrinterData;

import java.io.File;

public interface PrinterDataService {


    /**
     * 根据打印机实际的PID VID进行打印,可以在设备管理器 打印支持 查看
     *
     * @param vid
     * @param pid
     * @return void
     * @author yw
     * @date 2021-07-15 19:31:27
     */
    void openDevice(String vid, String pid, String command) throws Exception;

    /**
     * 指定打印机型号打印
     *
     * @param modelName
     * @param command
     * @return void
     * @author yw
     * @date 2021-07-15 19:44:09
     */
    void printerByModelName(String modelName, String command) throws Exception;

    /**
     * 获取一台打印机打印
     *
     * @param command
     * @return void
     * @author yw
     * @date 2021-07-15 20:12:49
     */
    void printerByRandom(String command) throws Exception;

    PrinterData tracingBackToTheSource(PrinterData tbsData) throws Exception;

    void printerImg() throws Exception;

    /**
     * 图片转换位BMP格式，用于logo热敏打印
     *
     * @param sourceFile
     * @return
     * @author yw
     * @date 2021-07-30 17:01:31
     */
    File convertSingleColorBMP(File sourceFile) throws Exception;

    /**
     * 入库单打印
     *
     * @param json
     * @return
     * @author yw
     * @date 2021-07-30 17:02:21
     */
    void printerStockInCode(String json) throws Exception;
}
