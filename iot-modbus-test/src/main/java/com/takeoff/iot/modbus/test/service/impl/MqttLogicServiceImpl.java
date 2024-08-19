package com.takeoff.iot.modbus.test.service.impl;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.takeoff.iot.modbus.common.entity.Param;
import com.takeoff.iot.modbus.common.entity.PrinterData;
import com.takeoff.iot.modbus.serialport.service.MqttService;
import com.takeoff.iot.modbus.test.service.MqttLogicService;
import com.takeoff.iot.modbus.test.service.PrinterDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;

/**
 * @description: mqtt业务逻辑实现
 * @projectName: iot-modbus
 * @package: com.takeoff.iot.modbus.serialport.service.impl
 * @className: MqttLogicServiceImpl
 * @author: yw
 * @date: 2024/8/1 10:02
 */
@Service
@Slf4j
public class MqttLogicServiceImpl implements MqttLogicService {

    @Autowired
    private PrinterDataService printerDataService;

    @Autowired
    private MqttService mqttService;

    /**
     * 根据topic区分业务处理
     *
     * @param topic 主题
     * @param msg   内容
     * @return
     * @author yw
     * @date 2022-01-18 20:49:39
     */
    @Override
    public void doBusiness(String topic, String msg) throws Exception {
        switch (topic) {
            case "sort_weight_topic":
                sortWeightPrinter(topic, msg);
                break;
            case "topic_default":
                break;
            default:
                log.error("default：topic：{}；msg：{}", topic, msg);
        }
    }

    @Override
    public void sentToServer(Param paramInfo) throws Exception {
        if (paramInfo == null) {
            throw new UtilException("请传入参数");
        }
        if (StrUtil.hasEmpty(paramInfo.getTopic())) {
            throw new UtilException("参数为空！");
        }
        if (paramInfo.getData() == null) {
            throw new UtilException("数据信息为空");
        }
        PrinterData tbsData = JSON.parseObject(JSON.toJSONString(paramInfo.getData()), PrinterData.class);
        if (StrUtil.hasEmpty(tbsData.getGoodsName(), tbsData.getQrCode(), tbsData.getUserName(), tbsData.getPhone(), tbsData.getGoodsCode())) {
            throw new UtilException("参数为空");
        }
        if (tbsData.getWeight() == null || tbsData.getPrice() == null) {
            throw new UtilException("重量或价格为空");
        }
        if (tbsData.getPerWeight() == null) {
            throw new UtilException("每份重量为空");
        }
        String ipStr = NetUtil.getLocalhostStr();
        paramInfo.setTag(ipStr);
        mqttService.sendToMqtt(paramInfo.getTopic(), JSON.toJSONString(paramInfo));
    }

    /**
     * @Description 称重打印标签
     * @Param [topic, msg]
     * @Author yw
     * @Date 2024/8/1 10:19
     * @Return void
     **/
    public void sortWeightPrinter(String topic, String msg) throws Exception {
        String ipStr = NetUtil.getLocalhostStr();
        String ipOfLongStr = String.valueOf(NetUtil.ipv4ToLong(ipStr));
        log.error("本地IP：{}", ipStr);
        log.error("本地转换：{}", ipOfLongStr);

        Param param = JSON.parseObject(msg, Param.class);

        log.error("远程IP转换{}", NetUtil.ipv4ToLong(param.getTag()));
        //WMS传入IP，根据IP作为tag校验哪个电脑的称
        if (!StrUtil.isEmpty(param.getTag()) && ipOfLongStr.equals(String.valueOf(NetUtil.ipv4ToLong(param.getTag())))) {
            if (param.getData() != null) {
                PrinterData printerData = JSON.parseObject(JSON.toJSONString(param.getData()), PrinterData.class);
                printerDataService.tracingBackToTheSource(printerData);
            }
        }
    }
}
