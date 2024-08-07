package com.takeoff.iot.modbus.test.config;

import com.takeoff.iot.modbus.serialport.conf.MqttConfig;
import com.takeoff.iot.modbus.test.service.MqttLogicService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;

import java.util.List;

/**
 * @description: mqtt 主题监听
 * @projectName: iot-modbus
 * @package: com.takeoff.iot.modbus.test.config
 * @className: MqttlistenInConfig
 * @author: yw
 * @date: 2024/8/1 14:42
 */
@Slf4j
@Configuration
@IntegrationComponentScan
@ConfigurationProperties(prefix = "mqtt-info")
@Data
public class MqttListenInConfig {

    @Autowired
    private MqttLogicService mqttLogicService;

    private List<String> subList;

    /**
     * MQTT消息处理器（消费者）
     *
     * @param
     * @return org.springframework.messaging.MessageHandler
     * @author yw
     * @date 2022-01-18 20:46:06
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInboundChannelIot")
    public MessageHandler handler() {
        return message -> {
            String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
            String msg = message.getPayload().toString();
            for (String t : subList) {
                if (t.equals(topic)) {
                    try {
                        log.info("\n--------------------START-------------------\n" +
                                "接收到订阅消息:\ntopic:" + topic + "\nmessage:" + msg +
                                "\n---------------------END--------------------");
                        mqttLogicService.doBusiness(topic, msg);
                    } catch (Exception e) {
                        log.error("doBusiness异常：", e);
                    }

                }
            }
        };
    }
}
