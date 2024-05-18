package com.takeoff.iot.modbus.serialport.utils;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * 发布消息回调
 *
 * @author yw
 * @date 2022-01-17 20:09:12
 **/
@Slf4j
public class PushCallback implements MqttCallback {

    /**
     * 在断开连接时调用,视业务做对于的逻辑处理
     *
     * @param throwable
     * @return void
     * @author yw
     * @date 2022-01-17 20:08:00
     */
    @Override
    public void connectionLost(Throwable throwable) {
        //连接丢失后，一般在这里面进行重连
        log.warn(throwable.getMessage());
        System.out.println("连接断开，可以做重连");
    }

    /**
     * 接收已经预订的发布
     *
     * @param s
     * @param mqttMessage
     * @return void
     * @author yw
     * @date 2022-01-17 20:07:48
     */
    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        // subscribe后得到的消息会执行到这里面
        System.out.println("接收消息主题 : " + s);
        System.out.println("接收消息Qos : " + mqttMessage.getQos());
        System.out.println("接收消息内容 : " + new String(mqttMessage.getPayload()));
    }

    /**
     * 接收到已经发布的 QoS 1 或 QoS 2 消息的传递令牌时调用
     *
     * @param iMqttDeliveryToken
     * @return void
     * @author yw
     * @date 2022-01-17 20:08:39
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("deliveryComplete---------" + iMqttDeliveryToken.isComplete());
    }
}
