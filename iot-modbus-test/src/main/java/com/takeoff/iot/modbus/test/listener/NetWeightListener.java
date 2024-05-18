package com.takeoff.iot.modbus.test.listener;

import com.alibaba.fastjson.JSON;
import com.takeoff.iot.modbus.common.data.MiiNetWeightData;
import com.takeoff.iot.modbus.common.message.MiiMessage;
import com.takeoff.iot.modbus.netty.channel.MiiChannel;
import com.takeoff.iot.modbus.netty.listener.MiiListener;
import com.takeoff.iot.modbus.serialport.data.NetWeightData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @description: 电子秤称重
 * @projectName: iot-modbus
 * @package: com.takeoff.iot.modbus.test.listener
 * @className: NetWeight
 * @author: yw
 * @date: 2024/5/14 16:23
 */
@Slf4j
@Component
public class NetWeightListener implements MiiListener {
    @Override
    public void receive(MiiChannel channel, MiiMessage message) {
        if (message.command() == MiiMessage.ESCALE) {
            MiiNetWeightData weightData = (MiiNetWeightData) message.data();
            log.info("Net weight received: {}", JSON.toJSONString(weightData));
        }
    }

    @EventListener
    public void handleReceiveDataEvent(NetWeightData data) {
        log.info("称重指令: {}", data.getCommand());
        log.info("称重设备号: {}", data.getDevice());
        log.info("称重完整信息: {}", JSON.toJSONString(data));
    }
}
