package com.takeoff.iot.modbus.test.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.takeoff.iot.modbus.common.entity.Printer;
import com.takeoff.iot.modbus.common.entity.PrinterData;
import com.takeoff.iot.modbus.common.entity.StockInDto;
import com.takeoff.iot.modbus.test.service.PrinterDataService;
import com.takeoff.iot.modbus.test.utils.PrinterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class PrinterDataServiceImpl implements PrinterDataService {

    //Y轴偏移量
    private final int offset = 30;

    //Y轴偏移量
    private final int offsetSecond_y = 35;

    //X轴偏移量
    private final int offsetSecond_x = 20;

    //垂直下移
    private final int offsetSecondConstant_Y = 50;

    //每行打印的内容长度
    private final BigDecimal rowOfNumber = BigDecimal.valueOf(13);

    /**
     * 根据打印机实际的PID VID进行打印,可以在设备管理器 打印支持 查看
     *
     * @param vid
     * @param pid
     * @return void
     * @author yw
     * @date 2021-07-15 19:31:27
     */
    @Override
    public void openDevice(String vid, String pid, String command) throws Exception {
        if (StrUtil.hasEmpty(vid, pid, command)) {
            throw new UtilException("参数为空");
        }
//        int handle = PrinterUtils.OpenDevice(vid, pid);
//        PrinterUtils.Write(handle, command.getBytes("GBK"), command.getBytes("GBK").length);
        download();
    }

    /**
     * 指定打印机型号打印
     *
     * @param modelName
     * @param command
     * @return void
     * @author yw
     * @date 2021-07-15 19:44:09
     */
    @Override
    public void printerByModelName(String modelName, String command) throws Exception {
        if (StrUtil.isEmpty(modelName)) {
            throw new UtilException("参数为空");
        }
        Printer printer = PrinterUtils.PrinterOpen(modelName);
        if (printer == null) {
            throw new UtilException("指定的打印机型号不存在");
        }
        PrinterUtils.PrinterWrite(printer, command.getBytes("GBK"), command.getBytes("GBK").length);
        checkPrinterStatus(printer);
    }

    /**
     * 获取一台打印机打印
     *
     * @param command
     * @return void
     * @author yw
     * @date 2021-07-15 20:12:49
     */
    @Override
    public void printerByRandom(String command) throws Exception {
        if (StrUtil.isEmpty(command)) {
            throw new UtilException("参数为空");
        }
        //扫描USB端口
        List<Printer> list = PrinterUtils.PrinterScan();
        if (list == null || list.isEmpty()) {
            throw new UtilException("请检查usb是否插入打印机");
        }

        Printer printer = PrinterUtils.PrinterOpen(list.get(0));
        PrinterUtils.PrinterWrite(printer, command.getBytes("GBK"), command.getBytes("GBK").length);
        checkPrinterStatus(printer);
    }

    @Override
    public PrinterData tracingBackToTheSource(PrinterData tbsData) throws Exception {

        if (tbsData == null) {
            /*TbsData tbsDataTmp = new TbsData();
            tbsDataTmp.setGoodsName("菠菜");
            tbsDataTmp.setWeight(BigDecimal.valueOf(9999));
            tbsDataTmp.setPrice(BigDecimal.valueOf(9999));
            tbsDataTmp.setPickUpTime(new Date());
            tbsDataTmp.setEnterprise("湖南绿源购电子商务有限公司");
            tbsDataTmp.setEnterpriseLogo("");
            tbsDataTmp.setQrCode("895574644f7a45ec90bc93e47da33954");
            tbsDataTmp.setProvenance("新疆维吾尔自治区克孜勒苏柯尔克孜自治州");
            tbsData = JSONUtil.toBean(JSONUtil.toJsonStr(tbsDataTmp), TbsData.class);*/
            throw new UtilException("参数为空");
        }

        log.error("称重标签打印：{}", JSON.toJSONString(tbsData));

        //写入打印机的model内容
        if (StrUtil.hasEmpty(tbsData.getGoodsName(), tbsData.getQrCode(), tbsData.getUserName(), tbsData.getPhone(), tbsData.getGoodsCode())) {
            throw new UtilException("参数为空");
        }
        if (tbsData.getWeight() == null || tbsData.getPrice() == null) {
            throw new UtilException("重量或价格为空");
        }
        if (tbsData.getPerWeight() == null) {
            throw new UtilException("每份重量为空");
        }
//        tbsData.setWeight(tbsData.getWeight().divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP));
        tbsData.setPrice(tbsData.getPrice().setScale(2, RoundingMode.HALF_UP));
        tbsData.setQrCode(tbsData.getUrl().concat(tbsData.getQrCode()));
        //展示为包装日期
        tbsData.setPickUpTime(new Date());
        if (StrUtil.isEmpty(tbsData.getProvenance())) {
            tbsData.setProvenance("");
        } else {
            if (tbsData.getProvenance().indexOf("市") > 0) {
                String provenance = tbsData.getProvenance().substring(0, tbsData.getProvenance().indexOf("市") + 1);
                tbsData.setProvenance(provenance);
            }
        }

        //电话脱敏
        tbsData.setPhone(DesensitizedUtil.mobilePhone(tbsData.getPhone()));

        File file = FileUtil.file("img/test.jpg");
        ImgUtil.gray(FileUtil.file("img/lvy-logo.jpg"), file);

        //扫描USB端口
        List<Printer> list = PrinterUtils.PrinterScan();
        if (list == null || list.isEmpty()) {
            throw new UtilException("usb未检测到打印机设备");
        }

        Printer printer = PrinterUtils.PrinterOpen(list.get(0));

//        versionFirst(tbsData, printer);
        versionSecond(tbsData, printer, file);
        return tbsData;
    }

    @Override
    @Deprecated
    public void printerImg() throws Exception {
        File file = FileUtil.file("D:/img/3.BMP");
        if (!file.exists()) {
            file.mkdirs();
        }
//        File targetFile = convertTo(FileUtil.file("img/LOGO.JPG"),file);
        File targetFile = convertSingleColorBMP(FileUtil.file("D:/img/LOGO.JPG"));
//        File targetFile = convertSingleColorBMPFile(FileUtil.file("img/LOGO.JPG"),file);
//        File targetFile = file;
        //扫描USB端口
        List<Printer> list = PrinterUtils.PrinterScan();
        if (list == null || list.isEmpty()) {
            throw new UtilException("usb未检测到打印机设备");
        }
        Printer printer = PrinterUtils.PrinterOpen(list.get(0));

//            String download = "DOWNLOAD \""+targetFile.getName()+"\","+targetFile.getAbsolutePath()+"\r\n";
//            System.out.println(download);
//            PrinterUtils.PrinterWrite(printer, download.getBytes("GBK"), download.getBytes("GBK").length);

        //纸张大小
        String size = "SIZE 50 mm,40 mm\r\n";
        PrinterUtils.PrinterWrite(printer, size.getBytes("GBK"), size.getBytes("GBK").length);
        System.out.println(size);

        PrinterUtils.PrinterWrite(printer, "CODEPAGE 936\r\n");
        System.out.println("CODEPAGE 936\r\n");

        //设置打印坐标（左上角为xy轴0点）
        String direction = "DIRECTION 1\r\n";
        PrinterUtils.PrinterWrite(printer, direction.getBytes("GBK"), direction.getBytes("GBK").length);
        System.out.println(direction);

        //清除打印机中的图像缓存
        PrinterUtils.PrinterWrite(printer, "CLS\r\n".getBytes("GBK"), "CLS\r\n".length());
        System.out.println("CLS\r\n");

       /* String move = "MOVE\r\n";
        PrinterUtils.PrinterWrite(printer, move.getBytes("GBK"), move.length());
        System.out.println(move);*/

        String img = "PUTBMP -130,130,\"UPLOGO.BMP\"\r\n";
        System.out.println(img);
        PrinterUtils.PrinterWrite(printer, img.getBytes("GBK"), img.getBytes("GBK").length);

        //将打印机中图像缓存印出
        String print = "PRINT 1\r\n";
        System.out.println(print);
        PrinterUtils.PrinterWrite(printer, print.getBytes("GBK"), print.getBytes("GBK").length);

        //将打印机中图像缓存印出
        String eop = "EOP\r\n";
//            System.out.println(eop);
//            PrinterUtils.PrinterWrite(printer, eop.getBytes("GBK"), eop.getBytes("GBK").length);
    }

    /**
     * 图片转换位BMP格式，用于logo热敏打印
     * <p>
     * 1.目前仅测试的jpg格式图片
     * 2./NEW开头的文件名称位按比例缩放后的图片
     * 3./UP开头的文件名为上传到打印工具的图片，注意图片的大小，最好为1-100kb
     * 4.暂不考虑原图片，/NEW，/UP合为一张图片去做修改，后期可优化
     * 5.对于图片的是否由我们进行优化问题，客户提供的图片，上下左右留的空白有很
     * 大概率会比较大（会影响到图片布局，四面边距空白最好10左右），以及客户logo
     * 最好横向展示，不然溯源标签可能存在变形情况，最好是由我们进行二次优化，基于
     * 此考虑，当前版本为需经过我们优化的方式
     * 6.弊端，需要对每台打印机进行图片上传，增加一个logo，需每台打印机上传一次，
     * 步骤：拿到用户logo，最好进行重新截图，保持思辨空白只有10左右，然后调用此
     * 方法，会生成UP***.BMP文件，然后打开当前操作台下的Diagnostic Tool工具
     * 档案管理tab,档案类型BMP,浏览选择UP***.BMP，然后点下载，下载完成后，档
     * 案信息点读取，会展示杠下载的UP***.BMP
     * 7.download命令下载，对接未成功，对方技术给的方案为Diagnostic Tool上
     * 传，download下载，无法检查图片大小，图片过大，下载超慢，影响效率，以及
     * 打印机可用内存大小，考虑风险性以及效率，以及目前时间着重在测试以及流程的
     * 正常运行,download方式，暂缓，此方式为最佳方式,后期可优化
     *
     * @param sourceFile
     * @return java.io.File
     * @author yw
     * @date 2021-07-27 19:57:57
     */
    @Override
    public File convertSingleColorBMP(File sourceFile) throws Exception {

        String fileName = sourceFile.getName().substring(0, sourceFile.getName().lastIndexOf(".")).toUpperCase().concat(".BMP");
        String path = sourceFile.getAbsolutePath().substring(0, sourceFile.getAbsolutePath().lastIndexOf(sourceFile.getName()));
        File targetFile = FileUtil.file(path + "/NEW" + fileName);
        File uploadFile = FileUtil.file(path + "/UP" + fileName);
        ImgUtil.scale(sourceFile, targetFile, 0.3f);
        BufferedImage sourceImg = ImageIO.read(targetFile);
        int h = sourceImg.getHeight();
        int w = sourceImg.getWidth();

        int[] pixels = new int[w * h];
        PixelGrabber pixelGrabber = new PixelGrabber(sourceImg, 0, 0, w, h, pixels, 0, w);
        pixelGrabber.grabPixels();

        int gray;
        // 由红，绿，蓝值得到灰度值
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                gray = (int) (((pixels[w * j + i] >> 16) & 0xff) * 0.8);
                gray += (int) (((pixels[w * j + i] >> 8) & 0xff) * 0.1);
                gray += (int) (((pixels[w * j + i]) & 0xff) * 0.1);
                pixels[w * j + i] = (255 << 24) | (gray << 16) | (gray << 8) | gray;
            }
        }
        MemoryImageSource imageSource = new MemoryImageSource(w, h, pixels, 0, w);
        Image image = Toolkit.getDefaultToolkit().createImage(imageSource);
        BufferedImage bufImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
        bufImage.createGraphics().drawImage(image, 0, 0, null);
        ImageIO.write(bufImage, "BMP", uploadFile);
        return uploadFile;
    }

    /**
     * 入库单打印
     *
     * @param json
     * @return void
     * @author yw
     * @date 2021-07-30 17:04:26
     */
    @Override
    public void printerStockInCode(String json) throws Exception {

        if (StrUtil.isEmpty(json)) {
            throw new UtilException("参数为空");
        }
        //扫描USB端口
        List<Printer> list = PrinterUtils.PrinterScan();
        if (list == null || list.isEmpty()) {
            throw new UtilException("usb未检测到打印机设备");
        }

        StockInDto stockInDto = JSONUtil.toBean(json, StockInDto.class);

        Printer printer = PrinterUtils.PrinterOpen(list.get(0));

        for (String stockInCode : stockInDto.getStockInCodes()) {
            //纸张大小
            String size = "SIZE 50 mm,40 mm\r\n";
            PrinterUtils.PrinterWrite(printer, size.getBytes("GBK"), size.getBytes("GBK").length);

            this.printerSet(printer);

            String img = "PUTBMP 10,20,\"UPLOGO.BMP\"\r\n";
            PrinterUtils.PrinterWrite(printer, img.getBytes("GBK"), img.getBytes("GBK").length);

            //二维码
            String qrCode = "QRCODE 300," + 300 + ",Q,3,A,0,M2,\"" + stockInCode + "\"\r\n";
            PrinterUtils.PrinterWrite(printer, qrCode.getBytes("GBK"), qrCode.getBytes("GBK").length);

            //将打印机中图像缓存印出
            String print = "PRINT 1\r\n";
            PrinterUtils.PrinterWrite(printer, print.getBytes("GBK"), print.getBytes("GBK").length);
        }
    }

    /**
     * 一行一列，左对齐的一列往下排 纸张大小：50*40MM 型号：Xprinter xp-420B
     * 品名：菠菜   重量：
     * 价格：19元   重量
     * 。。。
     *
     * @param tbsData
     * @param printer
     * @return com.lvy.scan.dto.TbsData
     * @author yw
     * @date 2021-07-20 09:10:23
     */
    public PrinterData versionSecond(PrinterData tbsData, Printer printer, File file) throws Exception {

        //纸张大小
        String size = "SIZE 50 mm,40 mm\r\n";
        PrinterUtils.PrinterWrite(printer, size.getBytes("GBK"), size.getBytes("GBK").length);


        this.printerSet(printer);

        //头部空白
//        String space = "TEXT " + 30 + "," + 30 + ",\"TSS24.BF2\",0,2,2,\"绿源购\"\r\n";
//        PrinterUtils.PrinterWrite(printer, space.getBytes("GBK"), space.getBytes("GBK").length);

        String img = "PUTBMP 120,20,\"UPLOGO.BMP\"\r\n";
        PrinterUtils.PrinterWrite(printer, img.getBytes("GBK"), img.getBytes("GBK").length);

        log.info("商品长度：{}", tbsData.getGoodsName().length());
        BigDecimal goodsLength = BigDecimal.valueOf(tbsData.getGoodsName().length());
        int num = goodsLength.divide(rowOfNumber, 0, RoundingMode.DOWN).intValue();
        log.info("num：{}", num);
        BigDecimal remainder = goodsLength.remainder(rowOfNumber);
        log.info("remainder：{}", remainder);
        if (remainder.compareTo(BigDecimal.ZERO) > 0) {
            num = num + 1;
        }
        log.info("new:{}", num);
        /*只能是两行的商品名称，再多的话，因为第一行会多出“品名：”，商行以上的话，换行不准确，暂时只保留两行，太长容不下**/
        if (goodsLength.compareTo(rowOfNumber) <= 0) {
            //商品
            String text = "TEXT " + offsetSecond_x + "," + (offsetSecond_y + offsetSecondConstant_Y) + ",\"TSS24.BF2\",0,1,1,\"品名：" + tbsData.getGoodsName() + "\"\r\n";
            PrinterUtils.PrinterWrite(printer, text.getBytes("GBK"), text.getBytes("GBK").length);
        } else {
            /*商品打印换行截取**/
            int index = rowOfNumber.intValue();
            for (int i = 0; i < num; i++) {
                String subStr = "";
                int numIndex = i + 1;
                if (i > 0) {
                    if (i > 1) {
                        break;
                    }
                    int nextIndex = BigDecimal.valueOf(numIndex).multiply(rowOfNumber).intValue();
                    if (goodsLength.compareTo(BigDecimal.valueOf(numIndex).multiply(rowOfNumber)) > 0) {
                        //下一个未结束，则继续
                        subStr = tbsData.getGoodsName().substring(index, nextIndex);
                    } else {
                        //下一个index已结束，则用商品长度截取
                        subStr = tbsData.getGoodsName().substring(index, goodsLength.intValue());
                    }
                    index = nextIndex;
                    //商品
                    String text = "TEXT " + offsetSecond_x + "," + (numIndex * offsetSecond_y + offsetSecondConstant_Y) + ",\"TSS24.BF2\",0,1,1,\"" + subStr + "\"\r\n";
                    PrinterUtils.PrinterWrite(printer, text.getBytes("GBK"), text.getBytes("GBK").length);
                } else {
                    subStr = tbsData.getGoodsName().substring(i, index);
                    //商品
                    String text = "TEXT " + offsetSecond_x + "," + (numIndex * offsetSecond_y + offsetSecondConstant_Y) + ",\"TSS24.BF2\",0,1,1,\"品名：" + subStr + "\"\r\n";
                    PrinterUtils.PrinterWrite(printer, text.getBytes("GBK"), text.getBytes("GBK").length);
                }

            }
        }

        //用户
        int nameLength = tbsData.getUserName().length();
        String dataStr = "";
        if (nameLength >= 13) {
            dataStr = tbsData.getUserName().substring(0, 14);
        } else {
            dataStr = tbsData.getUserName();
        }
        String date = "TEXT " + offsetSecond_x + "," + ((num + 1) * offsetSecond_y + offsetSecondConstant_Y) + ",\"TSS24.BF2\",0,1,1,\"用户：" + dataStr + "\"\r\n";
        PrinterUtils.PrinterWrite(printer, date.getBytes("GBK"), date.getBytes("GBK").length);

        //电话
        String provenance = "TEXT " + offsetSecond_x + "," + ((num + 2) * offsetSecond_y + offsetSecondConstant_Y) + ",\"TSS24.BF2\",0,1,1,\"电话：" + tbsData.getPhone() + "\"\r\n";
        PrinterUtils.PrinterWrite(printer, provenance.getBytes("GBK"), provenance.getBytes("GBK").length);

        //价格
        String price = "TEXT " + offsetSecond_x + "," + ((num + 3) * offsetSecond_y + offsetSecondConstant_Y) + ",\"TSS24.BF2\",0,1,1,\"价格：" + tbsData.getPrice() + " 元\"\r\n";
        PrinterUtils.PrinterWrite(printer, price.getBytes("GBK"), price.getBytes("GBK").length);

        //规格
        int quantity = tbsData.getWeight().divide(tbsData.getPerWeight(), 0, RoundingMode.HALF_UP).intValue();
        BigDecimal actWeight = tbsData.getWeight().divide(BigDecimal.valueOf(quantity), 0, RoundingMode.HALF_UP);
        String weightInfo = actWeight + " * " + quantity;
        String weight = "TEXT " + offsetSecond_x + "," + ((num + 4) * offsetSecond_y + offsetSecondConstant_Y) + ",\"TSS24.BF2\",0,1,1,\"重量：" + weightInfo + " /g\"\r\n";
        PrinterUtils.PrinterWrite(printer, weight.getBytes("GBK"), weight.getBytes("GBK").length);


        //企业名称
       /* String name = "TEXT " + offsetSecond_x + "," + ((num + 5) * offsetSecond_y + offsetSecondConstant_Y) + ",\"TSS24.BF2\",0,1,1,\"企业：" + tbsData.getEnterprise() + "\"\r\n";
        PrinterUtils.PrinterWrite(printer, name.getBytes("GBK"), name.getBytes("GBK").length);*/
        //企业logo

        //二维码
        //TODO 待确定URL再调整
       /* String qrCode = "QRCODE 650," + 180 + ",Q,4,A,0,M2,\"" + tbsData.getQrCode() + "\"\r\n";
        PrinterUtils.PrinterWrite(printer, qrCode.getBytes("GBK"), qrCode.getBytes("GBK").length);*/

        //扫码溯源
//        String source = "TEXT 620," + 15 + ",\"TSS24.BF2\",0,1,1,\"扫码溯源\"\r\n";
//        PrinterUtils.PrinterWrite(printer, source.getBytes("GBK"), source.getBytes("GBK").length);

        //将打印机中图像缓存印出
        String print = "PRINT 1\r\n";
        PrinterUtils.PrinterWrite(printer, print.getBytes("GBK"), print.getBytes("GBK").length);
        //        checkPrinterStatus(printer);
        return tbsData;
    }

    /**
     * 一行一列，左对齐的一列往下排 纸张大小：70*50MM/50*40MM 型号：Xprinter D3601B
     * 品名：
     * 重量：
     * 价格：
     * 。。。
     *
     * @param tbsData
     * @param printer
     * @return com.lvy.scan.dto.TbsData
     * @author yw
     * @date 2021-07-20 09:10:23
     */
    public PrinterData versionFirst(PrinterData tbsData, Printer printer) throws Exception {

        this.printerSet(printer);

        //头部空白
        String space = "TEXT 40," + 30 + ",\"TSS24.BF2\",0,1,1,\"\"\r\n";
        PrinterUtils.PrinterWrite(printer, space.getBytes("GBK"), space.getBytes("GBK").length);

        //商品
        String text = "TEXT 40," + (2 * offset) + ",\"TSS24.BF2\",0,1,1,\"品名：" + tbsData.getGoodsName() + "\"\r\n";
        PrinterUtils.PrinterWrite(printer, text.getBytes("GBK"), text.getBytes("GBK").length);

        //重量
        String weight = "TEXT 40," + (3 * offset) + ",\"TSS24.BF2\",0,1,1,\"重量：" + tbsData.getWeight() + "/kg\"\r\n";
        PrinterUtils.PrinterWrite(printer, weight.getBytes("GBK"), weight.getBytes("GBK").length);

        //价格
        String price = "TEXT 40," + (4 * offset) + ",\"TSS24.BF2\",0,1,1,\"价格：" + tbsData.getPrice() + "/元\"\r\n";
        PrinterUtils.PrinterWrite(printer, price.getBytes("GBK"), price.getBytes("GBK").length);

        //出库日期
        String dateStr = DateUtil.formatDate(tbsData.getPickUpTime());
        String date = "TEXT 40," + (5 * offset) + ",\"TSS24.BF2\",0,1,1,\"出库日期：" + dateStr + "\"\r\n";
        PrinterUtils.PrinterWrite(printer, date.getBytes("GBK"), date.getBytes("GBK").length);

        //企业名称
        String name = "TEXT 40," + (6 * offset) + ",\"TSS24.BF2\",0,1,1,\"企业名称：" + tbsData.getEnterprise() + "\"\r\n";
        PrinterUtils.PrinterWrite(printer, name.getBytes("GBK"), name.getBytes("GBK").length);
        //企业logo

       /* File file = new File("/img/lvy-logo.jpg");
        File bmpFile = new File("/img/lvy-logo.BMP");
        ImgUtil.convert(file, bmpFile);
        bmpFile.getName();
        String logo = "PUTBMP 30,210,\"SAMPLE.BMP\",8,80\r\n";
        PrinterUtils.PrinterWrite(printer, logo.getBytes("GBK"), logo.getBytes("GBK").length);*/

        //二维码
        String qrCode = "QRCODE 40," + (12 * offset) + ",H,4,A,0,M2,\"" + tbsData.getQrCode() + "\"\r\n";
        PrinterUtils.PrinterWrite(printer, qrCode.getBytes("GBK"), qrCode.getBytes("GBK").length);

        //扫码溯源
        String source = "TEXT 260," + (15 * offset) + ",\"TSS24.BF2\",0,1,1,\"扫码溯源\"\r\n";
        PrinterUtils.PrinterWrite(printer, source.getBytes("GBK"), source.getBytes("GBK").length);

        //将打印机中图像缓存印出
        String print = "PRINT 1\r\n";
        PrinterUtils.PrinterWrite(printer, print.getBytes("GBK"), print.getBytes("GBK").length);
//        checkPrinterStatus(printer);
        return tbsData;
    }

    private Printer printerSet(Printer printer) throws Exception {

//        if (!checkPrinterStatus(printer)) {
//            throw new UtilException("打印机状态异常");
//        }

        //设置国码编码，936：Simplified Chinese GBK
        PrinterUtils.PrinterWrite(printer, "CODEPAGE 936\r\n");

        //设置打印坐标（左上角为xy轴0点）
        String direction = "DIRECTION 1\r\n";
        PrinterUtils.PrinterWrite(printer, direction.getBytes("GBK"), direction.getBytes("GBK").length);

        //清除打印机中的图像缓存
        PrinterUtils.PrinterWrite(printer, "CLS\r\n".getBytes("GBK"), "CLS\r\n".length());

//        PrinterUtils.PrinterWrite(printer, "OFFSET 0\r\n".getBytes("GBK"), "OFFSET \r\n".length());
        return printer;
    }

    @Deprecated
    private void download() throws Exception {
        //扫描USB端口
        List<Printer> list = PrinterUtils.PrinterScan();
        if (list == null || list.isEmpty()) {
            throw new UtilException("请检查usb是否插入打印机");
        }

        Printer printer = PrinterUtils.PrinterOpen(list.get(0));
        File file = FileUtil.file("img/UP3.BMP");
        String fileDownload = "DOWNLOAD F,\"" + file.getName() + "\"," + file.getAbsolutePath();
        PrinterUtils.PrinterWrite(printer, fileDownload.getBytes("GBK"), fileDownload.getBytes("GBK").length);
        String print = "PRINT 1\r\n";
        PrinterUtils.PrinterWrite(printer, print.getBytes("GBK"), print.getBytes("GBK").length);
        String eop = "EOP\r\n";
        PrinterUtils.PrinterWrite(printer, eop.getBytes("GBK"), eop.getBytes("GBK").length);
    }

    /**
     * 检查打印状态
     *
     * @param printer
     * @return boolean
     * @author yw
     * @date 2021-07-15 19:56:04
     */
    public boolean checkPrinterStatus(Printer printer) throws Exception {
        boolean ret = PrinterUtils.PrinterCheckJob(printer);
//        PrinterUtils.PrinterClose(printer);
        System.out.println(ret);
        if (ret) {
            System.out.println("状态正常");
            return true;
        } else {
            int status = PrinterUtils.PrinterQueryStatus(printer);
            if (status == PrinterUtils.STATUS_TPH_OPEN) {
                throw new UtilException("打印失败: 打印头打开");
            } else if (status == PrinterUtils.STATUS_PAPER_ERROR) {
                throw new UtilException("打印失败:纸张错误");
            } else if (status == PrinterUtils.STATUS_OTHER_ERROR) {
                throw new UtilException("打印失败:其他错误");
            }
        }
        return false;
    }
}
