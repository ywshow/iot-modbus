package com.takeoff.iot.modbus.test.controller;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.takeoff.iot.modbus.common.entity.Param;
import com.takeoff.iot.modbus.common.entity.PrinterData;
import com.takeoff.iot.modbus.common.entity.ShoppingList;
import com.takeoff.iot.modbus.test.service.MqttLogicService;
import com.takeoff.iot.modbus.test.service.PrinterDataService;
import com.takeoff.iot.modbus.test.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @description: 称重打印
 * @projectName: iot-modbus
 * @package: com.takeoff.iot.modbus.test.controller
 * @className: PrinterDataController
 * @author: yw
 * @date: 2024/7/31 10:41
 */
@RestController
@RequestMapping({"/api/pt"})
@Slf4j
public class PrinterDataController {

    @Autowired
    private PrinterDataService printerDataService;

    @Autowired
    private MqttLogicService mqttLogicService;

    /**
     * @Description 打开驱动
     * @Param
     * @Author yw
     * @Date 2024/7/31 11:00
     * @Return
     **/
    @RequestMapping("/openDevice/{vid}/{pid}")
    public R openDevice(@PathVariable String vid, @PathVariable String pid) {
        try {
            printerDataService.openDevice(vid, pid, "123");
            return R.ok();
        } catch (Exception e) {
            log.error("openDevice", e);
            if (e instanceof UtilException) {
                return R.error(e.getMessage());
            } else {
                return R.error("服务异常");
            }
        }
    }

    /**
     * @Description 溯源码打印
     * @Param code 二维码code
     * @Author yw
     * @Date 2024/7/31 11:00
     * @Return
     **/
    @RequestMapping("/printerOfTbs")
    public R tracingBackToTheSource(@RequestBody Map<String, Object> params) {
        try {
            if (params != null && !params.isEmpty()) {
                PrinterData tbsData = JSONUtil.toBean(JSON.toJSONString(params), PrinterData.class);
                printerDataService.tracingBackToTheSource(tbsData);
                return R.ok();
            } else {
                return R.error("参数异常");
            }
        } catch (Exception e) {
            log.error("tracingBackToTheSource", e);
            if (e instanceof UtilException) {
                return R.error(e.getMessage());
            } else {
                return R.error("服务异常");
            }
        }
    }

    /**
     * @Description 溯源码打印
     * @Param code 二维码code
     * @Author yw
     * @Date 2024/7/31 11:00
     * @Return
     **/
    @RequestMapping("/printerImg")
    public R printerImg() {
        try {
            printerDataService.printerImg();
            return R.ok();
        } catch (Exception e) {
            log.error("printerImg", e);
            if (e instanceof UtilException) {
                return R.error(e.getMessage());
            } else {
                return R.error("服务异常");
            }
        }
    }

    /**
     * @Description 图片转换位BMP格式，用于logo热敏打印
     * @Param path 文件路径
     * @Author yw
     * @Date 2024/7/31 11:00
     * @Return
     **/
    @RequestMapping("/convertSingleColorBMP")
    public R convertSingleColorBMP(String path) {
        try {
            if (StrUtil.isEmpty(path)) {
                return R.error("参数为空");
            }
            printerDataService.convertSingleColorBMP(FileUtil.file(path));
            return R.ok();
        } catch (Exception e) {
            log.error("convertSingleColorBMP", e);
            if (e instanceof UtilException) {
                return R.error(e.getMessage());
            } else {
                return R.error("服务异常");
            }
        }
    }


    /**
     * @Description 入库单号列表
     * @Param stockInList 入库单号列表
     * @Author yw
     * @Date 2024/7/31 11:00
     * @Return
     **/
    @RequestMapping("/printerStockInCode")
    public R printerStockInCode(@RequestBody String stockInList) {
        try {
            printerDataService.printerStockInCode(stockInList);
            return R.ok();
        } catch (Exception e) {
            log.error("convertSingleColorBMP", e);
            if (e instanceof UtilException) {
                return R.error(e.getMessage());
            } else {
                return R.error("服务异常");
            }
        }
    }

    /**
     * @Description 称重mqtt推送
     * @Param map 参数
     * @Author yw
     * @Date 2024/7/31 11:00
     * @Return
     **/
    @RequestMapping("/mq/printer")
    public R mqPrinter(@RequestBody Param<PrinterData> param) {
        try {
            mqttLogicService.sentToServer(param);
            return R.ok();
        } catch (Exception e) {
            log.error("mqPrinter", e);
            if (e instanceof UtilException) {
                return R.error(e.getMessage());
            } else {
                return R.error("服务异常");
            }
        }
    }

    /**
     * @Description 称重mqtt推送
     * @Param map 参数
     * @Author yw
     * @Date 2024/7/31 11:00
     * @Return
     **/
    @RequestMapping("/mq/printer/tag")
    public R tracingBackToTheSource(@RequestBody PrinterData<ShoppingList> param) {
        try {
            printerDataService.tracingBackToTheSource(param);
            return R.ok();
        } catch (Exception e) {
            log.error("mqPrinter", e);
            if (e instanceof UtilException) {
                return R.error(e.getMessage());
            } else {
                return R.error("服务异常");
            }
        }
    }
}
