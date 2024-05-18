package com.takeoff.iot.modbus.serialport.conf;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Configuration
@IntegrationComponentScan
@ConfigurationProperties(prefix = "mqtt")
@Data
public class MqttConfig {

    public static final String CHANNEL_NAME_OUT = "mqttOutboundChannel";

    /**
     * 订阅的bean名称
     */
    public static final String CHANNEL_NAME_IN = "mqttInboundChannel";

    // 客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息
    private static final byte[] WILL_DATA;

    static {
        WILL_DATA = "offline".getBytes();
    }

    private String username;

    private String password;

    private String serverURIs;

    @Value("${mqtt.client.id}")
    private String clientId;

    private String topic;

    private String qos;

    private List<String> subList;

    @PostConstruct
    public void init() {
        log.debug("username:{} password:{} serverURIs:{} clientId :{} topic:{} qos:{} subList:{}",
                this.username, this.password, this.serverURIs, this.clientId, this.topic, this.qos, this.subList);
    }

    /**
     * MQTT连接器选项
     *
     * @param
     * @return org.springframework.integration.mqtt.core.MqttPahoClientFactory
     * @author yw
     * @date 2022-01-18 20:47:13
     */
    @Bean
    public MqttPahoClientFactory clientFactory() {

        MqttConnectOptions options = new MqttConnectOptions();
        // 设置连接的用户名
        if (!username.trim().equals("")) {
            options.setUserName(username);
        }
        // 设置连接的密码
        options.setPassword(password.toCharArray());
        // 设置连接的地址
        options.setServerURIs(new String[]{serverURIs});
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线
        // 但这个方法并没有重连的机制
        options.setKeepAliveInterval(20);
        options.setCleanSession(false);
        options.setMaxInflight(5000);
        // 设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息。
        options.setWill("willTopic", WILL_DATA, 2, false);
        final DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(options);
        return factory;
    }

    /**
     * MQTT信息通道（生产者）
     *
     * @param
     * @return org.springframework.messaging.MessageChannel
     * @author yw
     * @date 2022-01-18 20:47:03
     */
    @Bean(name = CHANNEL_NAME_OUT)
    public MessageChannel mqttOutboundChannel() {

        return new DirectChannel();
    }

    /**
     * MQTT消息处理器（生产者）
     *
     * @param
     * @return org.springframework.messaging.MessageHandler
     * @author yw
     * @date 2022-01-18 20:46:50
     */
    @Bean
    @ServiceActivator(inputChannel = CHANNEL_NAME_OUT)
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                clientId,
                clientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(topic);
        return messageHandler;
    }


    /**
     * MQTT信息通道（消费者）
     *
     * @param
     * @return org.springframework.messaging.MessageChannel
     * @author yw
     * @date 2022-01-18 20:46:39
     */
    @Bean(name = CHANNEL_NAME_IN)
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    /**
     * MQTT消息订阅绑定（消费者）
     *
     * @param
     * @return org.springframework.integration.core.MessageProducer
     * @author yw
     * @date 2022-01-18 20:46:23
     */
    @Bean
    public MessageProducer inbound() {

        // 可以同时消费（订阅）多个Topic
        String[] strings = subList.toArray(new String[subList.size()]);
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId + "_wms_front", clientFactory(), strings);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(Integer.valueOf(qos));
        // 设置订阅通道
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }

    /**
     * MQTT消息处理器（消费者）
     *
     * @param
     * @return org.springframework.messaging.MessageHandler
     * @author yw
     * @date 2022-01-18 20:46:06
     */
    @Bean
    @ServiceActivator(inputChannel = CHANNEL_NAME_IN)
    public MessageHandler handler() {

        return message -> {
            String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
            String msg = message.getPayload().toString();
            for (String t : subList) {
                if (t.equals(topic)) {
                    try {
//                        mqttLogicService.doBusiness(topic, msg);
                        //todo 处理相关的业务逻辑
                    /*log.info("\n--------------------START-------------------\n" +
                            "接收到订阅消息:\ntopic:" + topic + "\nmessage:" + msg +
                            "\n---------------------END--------------------");*/
                    } catch (Exception e) {
                        log.error("doBusiness异常：", e);
                    }

                }
            }
        };
    }
}
