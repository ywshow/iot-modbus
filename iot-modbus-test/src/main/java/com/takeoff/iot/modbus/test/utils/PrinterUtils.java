package com.takeoff.iot.modbus.test.utils;


import com.takeoff.iot.modbus.common.entity.Printer;
import com.takeoff.iot.modbus.test.service.PrinterDllService;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 打印工具类
 *
 * @author yw
 * @date 2021-07-15 17:45:34
 **/
@Slf4j
public class PrinterUtils {

    private static final int INFO_SIZE = 18 + 4 + 120;
    private static final int VID_PID_SIZE = 4;
    private static final int VID_POS = 4;
    private static final int PID_POS = 13;
    private static final int PORT_POS = 18;
    private static final int PORT_SIZE = 3;
    private static final int MODEL_NAME_POS = 22;
    private static final int MODEL_NAME_SIZE = 60;
    private static final int SERIAL_NUM_POS = 82;
    private static final int SERIAL_NUM_SIZE = 20;

    public static final int STATUS_READY = 1;
    public static final int STATUS_PRINTING = 2;
    public static final int STATUS_PAPER_ERROR = 3;
    public static final int STATUS_TPH_OPEN = 4;
    public static final int STATUS_OTHER_ERROR = 5;

    public static int OpenDevice(String vid, String pid) {
        return PrinterDllService.comm.OpenUsb(vid, pid);
    }

    public static int OpenDevice(String vid, String pid, int usb_num) {
        return PrinterDllService.comm.OpenUsbW(vid, pid, usb_num);
    }

    public static int Write(int handle, byte[] data, int len) {

        if (handle < 0) {
            return -1;
        }
        return PrinterDllService.comm.WriteUsb(handle, data, len);
    }

    public static int Write(int handle, String data) {
        if (handle < 0) {
            return -1;
        }
        return PrinterDllService.comm.WriteUsb(handle, data.getBytes(), data.getBytes().length);
    }

    public static int Read(int handle, byte[] data, int len) {
        if (handle < 0) {
            return -1;
        }
        return PrinterDllService.comm.ReadUsb(handle, data, len);
    }

    public static boolean CloseDevice(int handle) {
        if (handle < 0) {
            return false;
        }
        return PrinterDllService.comm.CloseUsb(handle);
    }

    public static List<Printer> PrinterScan() {
        byte[] buffer = new byte[1024 * 10];
        List<Printer> printerList = new LinkedList<>();
        log.error("称重标签打印：{}", 4.0);
        PrinterDllService.comm.UsbScan(buffer, 1024);
        log.error("称重标签打印：{}", 4.1);
        for (int index = 0; index < 1024 * 10; index += INFO_SIZE) {
            StringBuilder sb = new StringBuilder();
            Printer printer = new Printer();
            if (buffer[index] == 0) {
                break;
            }

            //读取VID
            for (int inner = 0; inner < VID_PID_SIZE; inner++) {
                sb.append(String.format("%c", buffer[VID_POS + inner + index]));
            }
            printer.setVid(sb.toString());

            //读取PID
            sb = new StringBuilder();
            for (int inner = 0; inner < VID_PID_SIZE; inner++) {
                sb.append(String.format("%c", buffer[PID_POS + inner + index]));
            }
            printer.setPid(sb.toString());

            //读取端口
            int port = 0;
            for (int inner = PORT_POS + PORT_SIZE + index; inner >= PORT_POS + index; inner--) {
                port = port * 10;
                port = port + buffer[inner];
            }
            printer.setPort(port);

            //读取型号
            sb = new StringBuilder();
            for (int inner = 0; inner < MODEL_NAME_SIZE; inner++) {
                if (buffer[MODEL_NAME_POS + inner + index] != 0) {
                    sb.append(String.format("%c", buffer[MODEL_NAME_POS + inner + index]));
                }
            }
            printer.setModelName(sb.toString());

            //读取序列号
            sb = new StringBuilder();
            for (int inner = 0; inner < SERIAL_NUM_SIZE; inner++) {
                if (buffer[SERIAL_NUM_POS + inner + index] != 0) {
                    sb.append(String.format("%c", buffer[SERIAL_NUM_POS + inner + index]));
                }
            }
            printer.setSerialNum(sb.toString());
            printerList.add(printer);
        }
        log.error("称重标签打印：{}", 4.2);
        return printerList;
    }

    /**
     * 通过机型型号打开打印机,当出现多台型号相同打印机时，优先打开List<Printer>排列顺序较高的打印机
     *
     * @param modelName
     * @return com.lvy.scan.dto.Printer
     * @author yw
     * @date 2021-07-15 19:20:41
     */
    public static Printer PrinterOpen(String modelName) {
        List<Printer> Printers = PrinterScan();

        if (Printers != null) {
            Iterator<Printer> it = Printers.iterator();
            while (it.hasNext()) {
                Printer printer = it.next();
                if (printer.getModelName().equals(modelName)) {
                    int handle = OpenDevice(printer.getVid(), printer.getPid(), printer.getPort());
                    if (handle != -1) {
                        printer.setUsbHandle(handle);
                        return printer;
                    }
                }
            }
        }
        return null;
    }


    public static Printer PrinterOpen(Printer printer) {
        if (printer == null) {
            return null;
        }

        if (printer.getUsbHandle() > 0) {
            return printer;
        }

        int handle = OpenDevice(printer.getVid(), printer.getPid(), printer.getPort());
        if (handle != -1) {
            printer.setUsbHandle(handle);
            return printer;
        }
        return null;
    }


    public static int PrinterWrite(Printer printer, byte[] data, int len) {
        if (printer == null || printer.getUsbHandle() < 0) {
            return -1;
        }
        return Write(printer.getUsbHandle(), data, len);
    }

    public static int PrinterWrite(Printer printer, String string) {
        if (printer == null || printer.getUsbHandle() < 0) {
            return -1;
        }

        return Write(printer.getUsbHandle(), string);
    }

    public static int PrinterRead(Printer printer, byte[] data, int len) {
        if (printer == null || printer.getUsbHandle() < 0) {
            return -1;
        }
        return Read(printer.getUsbHandle(), data, len);
    }

    public static boolean PrinterClose(Printer printer) {
        if (printer == null) {
            return false;
        }
        return CloseDevice(printer.getUsbHandle());
    }

    /**
     * 查询打印机状态
     *
     * @return
     */
    public static int PrinterQueryStatus(Printer printer) {

        byte[] rec = new byte[1];

        rec[0] = (byte) 0xFF;
        PrinterWrite(printer, new byte[]{0x1B, 0x21, 0x3F}, 3);
        PrinterRead(printer, rec, 10);

        if (rec[0] == 0x00) {
            return STATUS_READY;
        } else if ((rec[0] & 0x01) == 0x01) {
            return STATUS_TPH_OPEN;
        } else if ((rec[0] & 0x02) == 0x02) {
            return STATUS_PAPER_ERROR;
        } else if ((rec[0] & 0x04) == 0x04) {
            return STATUS_PAPER_ERROR;
        } else if ((rec[0] & 0x20) == 0x20) {
            return STATUS_PRINTING;
        }
        return STATUS_OTHER_ERROR;
    }

    public static boolean PrinterCheckJob(Printer printer) {

        //先延时一会，确保任务开始
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {
            int status = PrinterQueryStatus(printer);
            if (status == STATUS_READY) {
                return true;
            } else if (status == STATUS_PRINTING) {
                continue;
            } else {
                return false;
            }
        }
    }

    public static List<String> PrinterGetFileList(Printer printer) {

        byte[] buffer = new byte[1024];
        List<String> fileList = new LinkedList<>();

        PrinterWrite(printer, "DIGNOSTIC DIGINTERFACE USB\r\n");
        PrinterWrite(printer, "DIGNOSTIC DIGREPORT REPFFILELIST\r\n");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PrinterRead(printer, buffer, 1024);

        String ret = new String(buffer);
        int cnt = 0;

        if (ret.indexOf("(0:)") != 0) {
            return null;
        }
        ret = ret.substring("(0:)".length());

        while (cnt < ret.length()) {
            int startIndex = ret.indexOf(":", cnt) + 1;
            if (startIndex == -1) {
                break;
            }
            int endIndex = ret.indexOf(")", startIndex);
            if (endIndex == -1) {
                break;
            }
            cnt = endIndex;

            if (endIndex - startIndex <= 1) {
                break;
            }

            fileList.add(ret.substring(startIndex, endIndex));
        }

        if (fileList.size() == 0) {
            return null;
        } else {
            return fileList;
        }
    }
}
