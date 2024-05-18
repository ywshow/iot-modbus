package com.takeoff.iot.modbus.serialport.data;

import com.takeoff.iot.modbus.common.data.MiiLockData;
import com.takeoff.iot.modbus.common.data.MiiNetWeightData;
import lombok.Getter;
import lombok.Setter;

/**
 * @description: 串口电子秤称重
 * @projectName: iot-modbus
 * @package: com.takeoff.iot.modbus.serialport.data
 * @className: NetWeightData
 * @author: yw
 * @date: 2024/5/14 16:30
 */
@Getter
@Setter
public class NetWeightData extends ReceiveDataEvent {

    /*
    返回内容
    **/
    private String content;

    public NetWeightData(Object source, int command, MiiNetWeightData data) {
        super(source, command, data.device(), data.shelf(), data.slot());
        this.content = data.content();
    }
}
