package com.cloud.xprinter.service;

import com.takeoff.iot.modbus.common.entity.Param;

public interface MqttLogicService {

    /**
     * 根据topic区分业务处理
     *
     * @param topic 主题
     * @param msg   内容
     * @return
     * @author yw
     * @date 2022-01-18 20:49:39
     */
    void doBusiness(String topic, String msg) throws Exception;

    void sentToServer(Param param) throws Exception;
}
