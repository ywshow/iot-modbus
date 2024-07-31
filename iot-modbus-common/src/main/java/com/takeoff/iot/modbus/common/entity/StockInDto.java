package com.takeoff.iot.modbus.common.entity;

import lombok.Data;

import java.util.List;

@Data
public class StockInDto {

    private List<String> stockInCodes;
}
