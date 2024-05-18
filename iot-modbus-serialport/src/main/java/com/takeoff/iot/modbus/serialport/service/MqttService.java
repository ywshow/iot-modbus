package com.takeoff.iot.modbus.serialport.service;

import com.takeoff.iot.modbus.serialport.conf.MqttConfig;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@MessagingGateway(defaultRequestChannel = MqttConfig.CHANNEL_NAME_OUT)
public interface MqttService {

    /**
     * 发送信息到MQTT服务器
     *
     * @param payload 发送的文本
     * @return
     * @author yw
     * @date 2022-01-18 20:45:35
     */
    void sendToMqtt(String payload);

    /**
     * 发送信息到MQTT服务器
     *
     * @param topic   主题
     * @param payload 消息主体
     * @return
     * @author yw
     * @date 2022-01-18 20:45:02
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);

    /**
     * 发送信息到MQTT服务器
     *
     * @param topic   主题
     * @param qos     对消息处理的几种机制。
     *                0 表示的是订阅者没收到消息不会再次发送，消息会丢失。
     *                1 表示的是会尝试重试，一直到接收到消息，但这种情况可能导致订阅者收到多次重复消息。
     *                2 多了一次去重的动作，确保订阅者收到的消息有一次。
     * @param payload 消息主体
     * @return
     * @author yw
     * @date 2022-01-18 20:44:40
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, String payload);
}
