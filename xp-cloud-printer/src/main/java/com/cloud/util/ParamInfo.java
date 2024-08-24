package com.cloud.util;

import com.gitee.gsocode.opensdk.requestvo.*;
import com.takeoff.iot.modbus.common.entity.PrinterData;
import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @projectName: iot-modbus
 * @package: com.cloud.util
 * @className: ParamInfo
 * @author: yw
 * @date: 2024/8/24 17:02
 */
@Data
public class ParamInfo implements Serializable {

    /**
     * 打印内容
     */
    private PrinterData printerData;

    /**
     * 票据/标签打印参数
     */
    private PrintRequest printRequest;

    /**
     * 批量删除打印机参数
     */
    private DelPrinterRequest delPrinterRequest;

    /**
     * 清空待打印队列
     */
    private ClearPrintOrderRequest clearPrintOrderRequest;

    /**
     * 批量获取指定打印机状态
     */
    private PrintersRequest printersRequest;

    /**
     * 设置打印机语音类型
     */
    private SetVoiceTypeRequest setVoiceTypeRequest;

    /**
     * 添加打印机到开发者账户（可批量） 【必接】
     */
    private AddPrinterRequest addPrinterRequest;
}
