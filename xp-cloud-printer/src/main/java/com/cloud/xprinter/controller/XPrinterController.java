package com.cloud.xprinter.controller;

import cn.hutool.core.exceptions.UtilException;
import com.cloud.util.ParamInfo;
import com.cloud.util.R;
import com.cloud.xprinter.service.XPrinterService;
import com.gitee.gsocode.opensdk.IXpyunPrintService;
import com.gitee.gsocode.opensdk.requestvo.*;
import com.gitee.gsocode.opensdk.responsevo.ObjectRestResponse;
import com.gitee.gsocode.opensdk.responsevo.PrinterResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description:
 * @projectName: iot-modbus
 * @package: com.cloud.xprinter
 * @className: XprinterController
 * @author: yw
 * @date: 2024/8/24 16:04
 */
@RestController
@RequestMapping("/xp/pt")
@Slf4j
public class XPrinterController {

    @Autowired
    private XPrinterService xPrinterService;

    @RequestMapping("/test")
    public R test() {
        return R.ok();
    }

    /**
     * @Description 票据打印
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:17
     * @Return
     **/
    @RequestMapping("/receipt")
    public R printReceipt(@RequestBody ParamInfo paramInfo) {
        try {
            ObjectRestResponse<String> restResponse = xPrinterService.print(paramInfo.getPrintRequest(), paramInfo.getPrinterData());
            return R.ok(restResponse);
        } catch (Exception e) {
            log.error("printReceipt:", e);
            if (e instanceof UtilException) {
                return R.error(e.getMessage());
            } else {
                return R.error("系统异常");
            }
        }
    }

    /**
     * @Description 标签打印
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:17
     * @Return
     **/
    @RequestMapping("/label")
    public R printLabel(@RequestBody ParamInfo paramInfo) {
        try {
            ObjectRestResponse<String> restResponse = xPrinterService.printLabel(paramInfo.getPrintRequest(), paramInfo.getPrinterData());
            return R.ok(restResponse);
        } catch (Exception e) {
            log.error("printLabel:", e);
            if (e instanceof UtilException) {
                return R.error(e.getMessage());
            } else {
                return R.error("系统异常");
            }
        }
    }

    /**
     * @Description 批量删除打印机
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:17
     * @Return
     **/
    @RequestMapping("/delPrinters")
    public R delPrinters(@RequestBody ParamInfo paramInfo) {
        try {
            ObjectRestResponse<PrinterResult> restResponse = xPrinterService.delPrinters(paramInfo.getDelPrinterRequest(), paramInfo.getPrinterData());
            return R.ok(restResponse);
        } catch (Exception e) {
            log.error("delPrinters:", e);
            if (e instanceof UtilException) {
                return R.error(e.getMessage());
            } else {
                return R.error("系统异常");
            }
        }
    }

    /**
     * @Description 清空待打印队列
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:17
     * @Return
     **/
    @RequestMapping("/delPrinterQueue")
    public R delPrinterQueue(@RequestBody ParamInfo paramInfo) {
        try {
            ObjectRestResponse<Boolean> restResponse = xPrinterService.delPrinterQueue(paramInfo.getClearPrintOrderRequest(), paramInfo.getPrinterData());
            return R.ok(restResponse);
        } catch (Exception e) {
            log.error("delPrinterQueue:", e);
            if (e instanceof UtilException) {
                return R.error(e.getMessage());
            } else {
                return R.error("系统异常");
            }
        }
    }


    /**
     * @Description 批量获取指定打印机状态
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:17
     * @Return
     **/
    @RequestMapping("/queryPrintersStatus")
    public R queryPrintersStatus(@RequestBody ParamInfo paramInfo) {
        try {
            ObjectRestResponse<List<Integer>> restResponse = xPrinterService.queryPrintersStatus(paramInfo.getPrintersRequest(), paramInfo.getPrinterData());
            return R.ok(restResponse);
        } catch (Exception e) {
            log.error("queryPrintersStatus:", e);
            if (e instanceof UtilException) {
                return R.error(e.getMessage());
            } else {
                return R.error("系统异常");
            }
        }
    }

    /**
     * @Description 设置打印机语音类型
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:17
     * @Return
     **/
    @RequestMapping("/setVoiceType")
    public R setVoiceType(@RequestBody ParamInfo paramInfo) {
        try {
            ObjectRestResponse<Boolean> restResponse = xPrinterService.setPrinterVoiceType(paramInfo.getSetVoiceTypeRequest(), paramInfo.getPrinterData());
            return R.ok(restResponse);
        } catch (Exception e) {
            log.error("setVoiceType:", e);
            if (e instanceof UtilException) {
                return R.error(e.getMessage());
            } else {
                return R.error("系统异常");
            }
        }
    }

    /**
     * @Description 添加打印机到开发者账户（可批量） 【必接】
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:17
     * @Return
     **/
    @RequestMapping("/addPrinters")
    public R addPrinters(@RequestBody ParamInfo paramInfo) {
        try {
            ObjectRestResponse<PrinterResult> restResponse = xPrinterService.addPrinters(paramInfo.getAddPrinterRequest(), paramInfo.getPrinterData());
            return R.ok(restResponse);
        } catch (Exception e) {
            log.error("addPrinters:", e);
            if (e instanceof UtilException) {
                return R.error(e.getMessage());
            } else {
                return R.error("系统异常");
            }
        }
    }
}
