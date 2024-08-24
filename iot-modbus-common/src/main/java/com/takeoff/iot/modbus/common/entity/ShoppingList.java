package com.takeoff.iot.modbus.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description: 购物清单
 * @projectName: iot-modbus
 * @package: com.takeoff.iot.modbus.common.entity
 * @className: ShoppingList
 * @author: yw
 * @date: 2024/8/20 9:20
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShoppingList {

    /**
     * @Description 商品名称
     * @Param
     * @Author yw
     * @Date 2024/8/20 9:21
     * @Return
     **/
    private String name;

    /**
     * @Description 每份重量
     * @Param
     * @Author yw
     * @Date 2024/8/20 9:21
     * @Return
     **/
    private BigDecimal perWeight;

    /**
     * @Description 购买数量
     * @Param
     * @Author yw
     * @Date 2024/8/20 9:21
     * @Return
     **/
    private Integer num;
}
