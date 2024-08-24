package com.takeoff.iot.modbus.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description: 称重打印/溯源二维码标签，分解完成后贴在销售单下的每一个商品，每个商品对应一个标签
 * @projectName: iot-modbus
 * @package: com.takeoff.iot.modbus.common.data
 * @className: PrinterData
 * @author: yw
 * @date: 2024/7/30 17:19
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PrinterData<T> implements Serializable {


    //TODO 临时方案，之后放数据字典及redis
    private String url = "http://manager.lvyuangou.com/CloudShopCenter-manager/static/index.html";

    /**
     * 用户
     */
    private String userName;

    /**
     * 电话
     */
    private String phone;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品编码
     */
    private String goodsCode;

    /**
     * 称重重量
     */
    private BigDecimal weight;

    /**
     * 每份重量
     */
    private BigDecimal perWeight;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 取货时间/出库时间
     */
    private Date pickUpTime;

    /**
     * 企业
     */
    private String enterprise;

    /**
     * 企业logo
     */
    private String enterpriseLogo;

    /**
     * 二维码code
     */
    private String qrCode;

    /**
     * 原产地
     */
    private String provenance;

    /**
     * 是否标品，0：否；1：是
     */
    private Integer standard;

    /**
     * @Description 是否购物清单（用户购买商品分拣完成的商品列表，不包含缺货），0：否，1：是
     * @Param
     * @Author yw
     * @Date 2024/8/20 9:09
     * @Return
     **/
    private boolean shoppingList;

    /**
     * @Description 购物清单列表
     * @Param
     * @Author yw
     * @Date 2024/8/20 9:14
     * @Return
     **/
    private T data;

    private List<ShoppingList> list;

    /**
     * 打印机编号，放到wms的redis里，然后传过来
     */
    private String printerNo;

    /**
     * 订单编号，对应平台的业务单号
     */
    private String orderNo;

    /**
     * @Description 购物清单是否第一张标签打印
     * @Param
     * @Author yw
     * @Date 2024/8/20 11:19
     * @Return
     **/
    private boolean nextPagePrinter;
}
