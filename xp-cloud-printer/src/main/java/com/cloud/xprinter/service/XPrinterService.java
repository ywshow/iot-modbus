package com.cloud.xprinter.service;

import com.gitee.gsocode.opensdk.requestvo.*;
import com.gitee.gsocode.opensdk.responsevo.ObjectRestResponse;
import com.gitee.gsocode.opensdk.responsevo.PrinterResult;
import com.takeoff.iot.modbus.common.entity.PrinterData;

import java.util.List;

public interface XPrinterService {

    /**
     * @Description 票据打印
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:37
     * @Return
     **/
    ObjectRestResponse<String> print(PrintRequest restRequest, PrinterData printerData);

    /**
     * @Description 总仓票据打印
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:37
     * @Return
     **/
    ObjectRestResponse<String> printCenter(PrintRequest restRequest, PrinterData printerData);

    /**
     * @Description 标签打印
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:37
     * @Return
     **/
    ObjectRestResponse<String> printLabel(PrintRequest restRequest, PrinterData printerData);

    /**
     * @Description 批量删除打印机
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:17
     * @Return
     **/
    ObjectRestResponse<PrinterResult> delPrinters(DelPrinterRequest restRequest, PrinterData printerData);

    /**
     * @Description 清空待打印队列
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:17
     * @Return
     **/
    ObjectRestResponse<Boolean> delPrinterQueue(ClearPrintOrderRequest restRequest, PrinterData printerData);

    /**
     * @Description 批量获取指定打印机状态
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:17
     * @Return
     **/
    ObjectRestResponse<List<Integer>> queryPrintersStatus(PrintersRequest restRequest, PrinterData printerData);

    /**
     * @Description 设置打印机语音类型
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:17
     * @Return
     **/
    ObjectRestResponse<Boolean> setPrinterVoiceType(SetVoiceTypeRequest restRequest, PrinterData printerData);

    /**
     * @Description 添加打印机到开发者账户（可批量） 【必接】
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:17
     * @Return
     **/
    ObjectRestResponse<PrinterResult> addPrinters(AddPrinterRequest restRequest, PrinterData printerData);
}
