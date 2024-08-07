package com.takeoff.iot.modbus.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: mq数据监听
 * @projectName: iot-modbus
 * @package: com.takeoff.iot.modbus.common.entity
 * @className: Param
 * @author: yw
 * @date: 2024/8/1 9:56
 */
@Data
public class Param<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主题
     */
    private String topic;

    /**
     * 标签
     */
    private String tag;

    /**
     * 数据信息
     */
    private T data;
}
