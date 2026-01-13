package com.cloud.xprinter.service.impl;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.cloud.xprinter.service.XPrinterService;
import com.gitee.gsocode.opensdk.IXpyunPrintService;
import com.gitee.gsocode.opensdk.requestvo.*;
import com.gitee.gsocode.opensdk.responsevo.ObjectRestResponse;
import com.gitee.gsocode.opensdk.responsevo.PrinterResult;
import com.takeoff.iot.modbus.common.entity.PrinterData;
import com.takeoff.iot.modbus.common.entity.ShoppingList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
@Slf4j
public class XPrinterServiceImpl implements XPrinterService {

    @Autowired
    private IXpyunPrintService iXpyunPrintService;

    /**
     * 小票每行的商品名称长度，小于等于16
     */
    private final int lengthOfLine = 11;

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
        restRequest.setSn(printerData.getPrinterNo());
        restRequest.setIdempotent(printerData.getOrderNo());
        //不检查打印机是否在线，直接生成打印订单，并返回打印订单号。如果打印机不在线，订单将缓存在打印队列中，打印机正常在线时会自动打印
        restRequest.setMode(1);
        restRequest.setExpiresIn(7200);
        StringBuffer content = new StringBuffer();
        content.append("<IMG200></IMG><BR>");
        content.append("<CB>购物清单<BR><BR><BR></CB>");
        content.append("<L>");
        content.append("用户：").append(printerData.getUserName()).append("<BR>");
        content.append("电话：").append(DesensitizedUtil.mobilePhone(printerData.getPhone())).append("<BR>");
//        content.append("地址：珠海市香洲区xx路xx号<BR>");
        content.append("</L>");
        content.append("<L>");
//        content.append("<LINE p=\"20,26\" />商品<HT>数量<HT>重量(g)<HT>单价<BR>");
        content.append("<LINE p=\"18,25\" />商品<HT>数量<HT>总重(g)<BR>");
        content.append("--------------------------------<BR>");
        if (printerData.getList() != null && !printerData.getList().isEmpty()) {
            for (Object shoppingList : printerData.getList()) {
                ShoppingList shopping = JSON.parseObject(JSON.toJSONString(shoppingList), ShoppingList.class);
                System.out.println(">>>" + JSON.toJSONString(shopping));
                int length = shopping.getName().length();
//                int totalWeight = BigDecimal.valueOf(shopping.getNum()).multiply(shopping.getPerWeight()).intValue();
                if (length <= lengthOfLine) {
                    String txt = "";
                    if (length > 8) {
                        txt = "<BR>";
                    }
                    content.append(shopping.getName()).append(txt).append("<HT>").append(shopping.getNum()).append("<HT>").append(shopping.getTotalWeight().intValue()).append("<BR>");
                } else {
                    //多行，则每行显示16个，lengthOfLine小于等于16
                    int newLengthOfLine = lengthOfLine + (new BigDecimal(16 - lengthOfLine).intValue());
                    /*剪切商品名称，多行显示**/
                    int printLine = BigDecimal.valueOf(length).divide(BigDecimal.valueOf(newLengthOfLine), 0, RoundingMode.HALF_UP).intValue();
                    BigDecimal remainder = BigDecimal.valueOf(length).remainder(BigDecimal.valueOf(newLengthOfLine));
                    if (remainder.compareTo(BigDecimal.ZERO) > 0) {
                        printLine = printLine + 1;
                    }
                    for (int i = 0; i < printLine; i++) {
                        int perIndex = i * newLengthOfLine;
                        int lineNum;
                        if (i != 0) {
                            perIndex = perIndex + 1;
                            lineNum = (i + 1) * perIndex;
                        } else {
                            lineNum = newLengthOfLine;
                        }
                        if (lineNum > length) {
                            lineNum = length;
                        }
                        String subName = shopping.getName().substring(perIndex, lineNum);
                        content.append(subName);
                        if (lineNum == length) {
                            String txt = "";
                            //最后一行，小于8就能容下数量跟总重量，则不换行，不然换行打印数量跟总重量
                            txt = "<BR>";
                            content.append(txt).append("<HT>").append(shopping.getNum()).append("<HT>").append(shopping.getTotalWeight().intValue()).append("<BR>");
                            break;
                        }
                    }
                }
            }
            /*content.append("可乐鸡翅<HT>2<HT>9.99<BR>");
            content.append("水煮鱼特辣<HT>1<HT>108.00<BR>");
            content.append("豪华版超级无敌龙虾炒饭<BR>");
            content.append("<HT>1<HT>99.90<BR>");
            content.append("炭烤鳕鱼<HT>5<HT>19.99<BR>");*/
            content.append("--------------------------------<BR>");
            content.append("</L>");
//            content.append("<R>合计：327.83元<BR></R><BR>");
//        content.append("<QRCODE s=8 e=L l=center>http://www.xpyun.net</QRCODE><BR>");
            restRequest.setContent(content.toString());
            return iXpyunPrintService.print(restRequest);
        } else {
            ObjectRestResponse<String> restResponse = new ObjectRestResponse();
            restResponse.setCode(10000);
            restResponse.setMsg("购物清单为空");
            return restResponse;
        }

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
        initLabelContentMiddlePaper(restRequest, printerData);
        log.info("打印标签内容：{}", JSON.toJSONString(restRequest));
        return iXpyunPrintService.printLabel(restRequest);
    }

    /**
     * @Description 标签打印，数据内容初始化
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:37
     * @Return
     **/
    public void initLabelContent(PrintRequest restRequest, PrinterData printerData) {
        String content = "";
        content += "<PAGE>";
        content += "<SIZE>40,30</SIZE>";
        content += "<TEXT x=\"8\" y=\"8\" w=\"1\" h=\"1\" r=\"0\">商品：" + printerData.getGoodsName() + "</TEXT>";
        content += "<TEXT x=\"8\" y=\"38\" w=\"1\" h=\"1\" r=\"0\">重量：" + printerData.getWeight() + " /g</TEXT>";
        //溯源码
        content += "<QRC x=\"10\" y=\"70\" s=\"5\" e=\"L\">" + printerData.getTraceCode() + "</QRC>";
        //PT-324-32423-23-234-S324
        content += "<QRC x=\"165\" y=\"70\" s=\"5\" e=\"L\">" + printerData.getQrCode() + "</QRC>";
        content += "<TEXT x=\"38\" y=\"210\" w=\"1\" h=\"1\" r=\"0\">溯源码</TEXT>";
        content += "<TEXT x=\"188\" y=\"210\" w=\"1\" h=\"1\" r=\"0\">商品码</TEXT>";
        content += "</PAGE>";
        restRequest.setContent(content);
    }

    /**
     * @Description 标签打印，数据内容初始化
     * @Param
     * @Author yw
     * @Date 2024/8/24 16:37
     * @Return
     **/
    public void initLabelContentMiddlePaper(PrintRequest restRequest, PrinterData printerData) {
        String content = "";
        content += "<PAGE>";
        content += "<SIZE>70,60</SIZE>";
        content += "<TEXT x=\"8\" y=\"8\" w=\"1\" h=\"1\" r=\"0\">订单号：" + printerData.getOrderNo() + "</TEXT>";
        content += "<TEXT x=\"8\" y=\"48\" w=\"1\" h=\"1\" r=\"0\">客户：" + printerData.getUserName() + " </TEXT>";
        content += "<TEXT x=\"8\" y=\"88\" w=\"1\" h=\"1\" r=\"0\">商品：" + printerData.getGoodsName() + "</TEXT>";
        content += "<TEXT x=\"8\" y=\"128\" w=\"1\" h=\"1\" r=\"0\">重量：" + printerData.getWeight() + " /g</TEXT>";
        if (!StrUtil.isEmpty(printerData.getShelfLocation())) {
            content += "<TEXT x=\"8\" y=\"168\" w=\"1\" h=\"1\" r=\"0\">取货位：" + printerData.getShelfLocation() + " </TEXT>";
        }

        //溯源码
        content += "<QRC x=\"250\" y=\"300\" s=\"5\" e=\"L\">" + printerData.getTraceCode() + "</QRC>";
        //PT-324-32423-23-234-S324
        //条形码
        content += "<BC128 x=\"520\" y=\"48\" h=\"100\" s=\"1\" n=\"2\" w=\"2\" r=\"90\">" + printerData.getQrCode() + "</BC128>";
        //订单
        content += "<QRC x=\"20\" y=\"300\" s=\"5\" e=\"L\">" + printerData.getOrderNo() + "</QRC>";
        content += "<TEXT x=\"270\" y=\"418\" w=\"1\" h=\"1\" r=\"0\">溯源码</TEXT>";
        content += "<TEXT x=\"38\" y=\"418\" w=\"1\" h=\"1\" r=\"0\">订单号</TEXT>";
        content += "</PAGE>";
        restRequest.setContent(content);
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
        return iXpyunPrintService.delPrinters(restRequest);
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
        return iXpyunPrintService.delPrinterQueue(restRequest);
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
        return iXpyunPrintService.queryPrintersStatus(restRequest);
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
        return iXpyunPrintService.setPrinterVoiceType(restRequest);
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
        return iXpyunPrintService.addPrinters(restRequest);
    }
}
