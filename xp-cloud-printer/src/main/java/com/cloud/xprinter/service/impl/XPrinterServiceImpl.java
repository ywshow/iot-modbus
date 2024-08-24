package com.cloud.xprinter.service.impl;

import com.cloud.xprinter.service.XPrinterService;
import com.gitee.gsocode.opensdk.IXpyunPrintService;
import com.gitee.gsocode.opensdk.requestvo.*;
import com.gitee.gsocode.opensdk.responsevo.ObjectRestResponse;
import com.gitee.gsocode.opensdk.responsevo.PrinterResult;
import com.takeoff.iot.modbus.common.entity.PrinterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @projectName: iot-modbus
 * @package: com.cloud.xprinter.service.impl
 * @className: XPrinterServiceImpl
 * @author: yw
 * @date: 2024/8/24 16:36
 */
@Service
public class XPrinterServiceImpl implements XPrinterService {

    @Autowired
    private IXpyunPrintService iXpyunPrintService;

    /**
     * @Description 票据打印
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:37
     * @Return
     **/
    @Override
    public ObjectRestResponse<String> print(PrintRequest restRequest, PrinterData printerData) {
        if (restRequest == null) {
            restRequest = new PrintRequest();
        }
        if (restRequest.getVoice() == null) {
            //静音
            restRequest.setVoice(1);
        }
        restRequest.setIdempotent(printerData.getOrderNo());
        //不检查打印机是否在线，直接生成打印订单，并返回打印订单号。如果打印机不在线，订单将缓存在打印队列中，打印机正常在线时会自动打印
        restRequest.setMode(1);
        restRequest.setExpiresIn(7200);
        StringBuffer content = new StringBuffer();
        content.append("<CB>绿源购购物清单<BR><BR><BR></CB>");
        content.append("<L>");
//        content.append("<LINE p=\"20,26\" />商品<HT>数量<HT>重量(g)<HT>单价<BR>");
        content.append("<LINE p=\"20,26\" />商品<HT>数量<HT>单价<BR>");
        content.append("--------------------------------<BR>");
        content.append("可乐鸡翅<HT>2<HT>9.99<BR>");
        content.append("水煮鱼特辣<HT>1<HT>108.00<BR>");
        content.append("豪华版超级无敌龙虾炒饭<BR>");
        content.append("<HT>1<HT>99.90<BR>");
        content.append("炭烤鳕鱼<HT>5<HT>19.99<BR>");
        content.append("--------------------------------<BR>");
        content.append("</L>");
        content.append("<R>合计：327.83元<BR></R><BR>");
        content.append("<L>");
        content.append("用户：卡萨收费看来是<BR>");
        content.append("电话：1363*****88<BR>");
        content.append("地址：珠海市香洲区xx路xx号<BR>");
        content.append("</L>");
        content.append("<QRCODE s=8 e=L l=center>http://www.xpyun.net</QRCODE><BR>");
        return null;
    }

    /**
     * @Description 标签打印
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:37
     * @Return
     **/
    @Override
    public ObjectRestResponse<String> printLabel(PrintRequest restRequest, PrinterData printerData) {
        if (restRequest == null) {
            restRequest = new PrintRequest();
        }
        if (restRequest.getVoice() == null) {
            //静音
            restRequest.setVoice(1);
        }
        restRequest.setIdempotent(printerData.getOrderNo());
        //不检查打印机是否在线，直接生成打印订单，并返回打印订单号。如果打印机不在线，订单将缓存在打印队列中，打印机正常在线时会自动打印
        restRequest.setMode(1);
        restRequest.setExpiresIn(7200);

        return null;
    }

    /**
     * @Description 批量删除打印机
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:17
     * @Return
     **/
    @Override
    public ObjectRestResponse<PrinterResult> delPrinters(DelPrinterRequest restRequest, PrinterData printerData) {
        if (restRequest == null) {
            restRequest = new DelPrinterRequest();
        }
        return null;
    }

    /**
     * @Description 清空待打印队列
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:17
     * @Return
     **/
    @Override
    public ObjectRestResponse<Boolean> delPrinterQueue(ClearPrintOrderRequest restRequest, PrinterData printerData) {
        if (restRequest == null) {
            restRequest = new ClearPrintOrderRequest();
        }
        return null;
    }

    /**
     * @Description 批量获取指定打印机状态
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:17
     * @Return
     **/
    @Override
    public ObjectRestResponse<List<Integer>> queryPrintersStatus(PrintersRequest restRequest, PrinterData printerData) {
        if (restRequest == null) {
            restRequest = new PrintersRequest();
        }
        return null;
    }

    /**
     * @Description 设置打印机语音类型
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:17
     * @Return
     **/
    @Override
    public ObjectRestResponse<Boolean> setPrinterVoiceType(SetVoiceTypeRequest restRequest, PrinterData printerData) {
        if (restRequest == null) {
            restRequest = new SetVoiceTypeRequest();
        }
        return null;
    }

    /**
     * @Description 添加打印机到开发者账户（可批量） 【必接】
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:17
     * @Return
     **/
    @Override
    public ObjectRestResponse<PrinterResult> addPrinters(AddPrinterRequest restRequest, PrinterData printerData) {
        if (restRequest == null) {
            restRequest = new AddPrinterRequest();
        }
        return null;
    }
}
