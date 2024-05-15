package com.saite.lift.common.util;


import org.apache.tomcat.util.buf.HexUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author zzy
 * CRC校验
 */
public class VerifyUtil {

    /**
     * 南京/杭州/广州白云 电梯校验值判断
     *
     * @param bytes
     * @return
     */
    public static boolean check(byte[] bytes) {
        if(bytes.length < 8) {
            return false;
        }
        int len = bytes[2];
        int checksum = 0;
        for (int i = 2; i < len - 2; ++i) {
            checksum = checksum + bytes[i];
        }
        checksum = 0xff & (~checksum) + 1;
        //接收的校验值
        if(bytes.length > len -2){
            int chk = bytes[len-2];
            if (((byte) checksum) != chk) {
                return false;
            }
        }else {
            return false;
        }
        return true;
    }

//    public static void main(String[] args) {
//        //55 AA 0A 01 01 00 00 00 F5 DD
//        byte[] data = new byte[10];
//        data[0] = 0x55;
//        data[1] = (byte)0xAA;
//        data[2] = 0x0A;
//        data[3] = 0x01;
//        data[4] = 0x01;
//        data[5] = 0x00;
//        data[6] = 0x00;
//        data[7] = 0x00;
//        data[8] = (byte)0xF5;
//        data[9] = (byte)0xDD;
//        boolean result = VerifyUtil.check(data);
//    }
//    /**
//     * 通士达电梯校验生成
//     *
//     * @param protocol
//     * @return
//     */
//    public static byte[] checksumTSD(TSDProtocol protocol) {
//        byte[] cmd = protocol.getCmd();
//        byte[] serial = protocol.getSerialNum();
//        String projectId = String.valueOf(protocol.getProjectId());
//        byte[] projectId2 = charToAsc(projectId);
//        String elevatorId = String.valueOf(protocol.getElevatorId());
//        byte[] elevatorId2 = charToAsc(elevatorId);
//        String data = String.valueOf(protocol.getData());
//        byte[] data2 = charToAsc(data);
//
//        int sum = serial[0] + serial[1] + cmd[0] + cmd[1] + projectId2[0] + projectId2[1] + elevatorId2[0] + elevatorId2[1] + data2[0] + data2[1];
//        String hex = Integer.toHexString(sum);
//        int len = hex.length();
//        String ch = hex.substring(len - 2, len).toUpperCase();
//        char[] c = ch.toCharArray();
//        int c1 = c[0];
//        int c2 = c[1];
//        byte[] chk = {(byte) c1, (byte) c2};
//        return chk;
//    }

    public static short byte2short(byte[] data){
        short result = 0;
        for (int i = 0; i < 2; i++) {
            result<<=8; //<<=和我们的 +=是一样的，意思就是 l = l << 8
            result |= (data[i] & 0xff); //和上面也是一样的  l = l | (b[i]&0xff)
        }
        return result;
    }

    public static byte[] short2byte(short s){
        byte[] b = new byte[2];
        for(int i = 0; i < 2; i++){
            int offset = 16 - (i+1)*8; //因为byte占4个字节，所以要计算偏移量
            b[i] = (byte)((s >> offset)&0xff); //把16位分为2个8位进行分别存储
        }
        return b;
    }

    //byte 与 int 的相互转换
    public static byte intToByte(int x) {
        return (byte) x;
    }

    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }
    /**
     * 珠海电梯asc转字符
     *
     * @param c
     * @return
     */
    public static short ascToShort(byte[] c) {
        if(c[0] == 66){
            return Short.parseShort("-" + (char)c[1]);
        }
        String s = new String(c);
        byte[] bytes = HexUtils.fromHexString(s);
        return bytes[0];
    }
//    public static short ascToShort(byte[] c) {
//        StringBuffer sub = new StringBuffer();
//        for (int i = 0; i < c.length; i++) {
//            if(c[i] == 66){
//                //收到的负楼层,将"B"转成"-"
//                sub.append("-");
//            }else {
//                sub.append((char) c[i]);
//            }
//        }
//        String str = sub.toString();
//        //收到的负楼层,将"B"转成"-"
//        String asc = "ABCDEF";
//        if (str.contains("A")) {
//            str = str.replace("A", "10");
//        } else if (str.contains("B")) {
//            str = str.replace("B", "11");
//        } else if (str.contains("C")) {
//            str = str.replace("C", "12");
//        } else if (str.contains("D")) {
//            str = str.replace("D", "13");
//        } else if (str.contains("E")) {
//            str = str.replace("E", "14");
//        } else if (str.contains("F")) {
//            str = str.replace("F", "15");
//        }
//        int val = 0;
//        try{
//            val = Integer.parseInt(str);
//        }catch (Exception e){
//            val = 0;
//            return 0;
//        }
//        short value;
//        if (val >= 16) {
//            int str1 = Integer.parseInt(str.substring(0, 1)) + 1;
//            int str2 = Integer.parseInt(str.substring(1));
//            value = (short) ((str1 * 16) + str2);
//        } else {
//            value = Short.parseShort(str);
//        }
//        return value;
//    }

    /**
     * 珠海电梯字符转ASC
     *
     * @param str
     * @return
     */
    public static byte[] charToAsc(String str) {
        if(str.startsWith("-")){
            byte[] bytes = new byte[2];
            bytes[0] = 0x42;
            bytes[1] = (byte) str.charAt(1);
            return bytes;
        }
        byte num = Byte.parseByte(str);
        byte[] bytes = new byte[]{num};
        String s = HexUtils.toHexString(bytes).toUpperCase();
        return s.getBytes();
    }
//    public static byte[] charToAsc(String str) {
//        byte[] value = new byte[2];
//        int num = Integer.parseInt(str);
//        if (num < 0) {
//            value[0] = zhuHai_table[11];
//            value[1] = zhuHai_table[Math.abs(num)];
//        } else if (num <= 15 && num >= 0) {
//            value[0] = zhuHai_table[0];
//            value[1] = zhuHai_table[num];
//        } else {
//            int n1 = (num / 16) - 1;
//            int n2 = num % 16;
//            value[0] = zhuHai_table[n1];
//            value[1] = zhuHai_table[n2];
//        }
//        return value;
//    }

    /**
     * 珠海电梯数据表
     */
    private static final byte[] zhuHai_table = {
            0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,
            0x41, 0x42, 0x43, 0x44, 0x45, 0x46
    };

    /**
     * 用于OTA--crc查询数据
     */
    private static final int[] ccitt_table = {
            0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50A5, 0x60C6, 0x70E7,
            0x8108, 0x9129, 0xA14A, 0xB16B, 0xC18C, 0xD1AD, 0xE1CE, 0xF1EF,
            0x1231, 0x0210, 0x3273, 0x2252, 0x52B5, 0x4294, 0x72F7, 0x62D6,
            0x9339, 0x8318, 0xB37B, 0xA35A, 0xD3BD, 0xC39C, 0xF3FF, 0xE3DE,
            0x2462, 0x3443, 0x0420, 0x1401, 0x64E6, 0x74C7, 0x44A4, 0x5485,
            0xA56A, 0xB54B, 0x8528, 0x9509, 0xE5EE, 0xF5CF, 0xC5AC, 0xD58D,
            0x3653, 0x2672, 0x1611, 0x0630, 0x76D7, 0x66F6, 0x5695, 0x46B4,
            0xB75B, 0xA77A, 0x9719, 0x8738, 0xF7DF, 0xE7FE, 0xD79D, 0xC7BC,
            0x48C4, 0x58E5, 0x6886, 0x78A7, 0x0840, 0x1861, 0x2802, 0x3823,
            0xC9CC, 0xD9ED, 0xE98E, 0xF9AF, 0x8948, 0x9969, 0xA90A, 0xB92B,
            0x5AF5, 0x4AD4, 0x7AB7, 0x6A96, 0x1A71, 0x0A50, 0x3A33, 0x2A12,
            0xDBFD, 0xCBDC, 0xFBBF, 0xEB9E, 0x9B79, 0x8B58, 0xBB3B, 0xAB1A,
            0x6CA6, 0x7C87, 0x4CE4, 0x5CC5, 0x2C22, 0x3C03, 0x0C60, 0x1C41,
            0xEDAE, 0xFD8F, 0xCDEC, 0xDDCD, 0xAD2A, 0xBD0B, 0x8D68, 0x9D49,
            0x7E97, 0x6EB6, 0x5ED5, 0x4EF4, 0x3E13, 0x2E32, 0x1E51, 0x0E70,
            0xFF9F, 0xEFBE, 0xDFDD, 0xCFFC, 0xBF1B, 0xAF3A, 0x9F59, 0x8F78,
            0x9188, 0x81A9, 0xB1CA, 0xA1EB, 0xD10C, 0xC12D, 0xF14E, 0xE16F,
            0x1080, 0x00A1, 0x30C2, 0x20E3, 0x5004, 0x4025, 0x7046, 0x6067,
            0x83B9, 0x9398, 0xA3FB, 0xB3DA, 0xC33D, 0xD31C, 0xE37F, 0xF35E,
            0x02B1, 0x1290, 0x22F3, 0x32D2, 0x4235, 0x5214, 0x6277, 0x7256,
            0xB5EA, 0xA5CB, 0x95A8, 0x8589, 0xF56E, 0xE54F, 0xD52C, 0xC50D,
            0x34E2, 0x24C3, 0x14A0, 0x0481, 0x7466, 0x6447, 0x5424, 0x4405,
            0xA7DB, 0xB7FA, 0x8799, 0x97B8, 0xE75F, 0xF77E, 0xC71D, 0xD73C,
            0x26D3, 0x36F2, 0x0691, 0x16B0, 0x6657, 0x7676, 0x4615, 0x5634,
            0xD94C, 0xC96D, 0xF90E, 0xE92F, 0x99C8, 0x89E9, 0xB98A, 0xA9AB,
            0x5844, 0x4865, 0x7806, 0x6827, 0x18C0, 0x08E1, 0x3882, 0x28A3,
            0xCB7D, 0xDB5C, 0xEB3F, 0xFB1E, 0x8BF9, 0x9BD8, 0xABBB, 0xBB9A,
            0x4A75, 0x5A54, 0x6A37, 0x7A16, 0x0AF1, 0x1AD0, 0x2AB3, 0x3A92,
            0xFD2E, 0xED0F, 0xDD6C, 0xCD4D, 0xBDAA, 0xAD8B, 0x9DE8, 0x8DC9,
            0x7C26, 0x6C07, 0x5C64, 0x4C45, 0x3CA2, 0x2C83, 0x1CE0, 0x0CC1,
            0xEF1F, 0xFF3E, 0xCF5D, 0xDF7C, 0xAF9B, 0xBFBA, 0x8FD9, 0x9FF8,
            0x6E17, 0x7E36, 0x4E55, 0x5E74, 0x2E93, 0x3EB2, 0x0ED1, 0x1EF0
    };

    public static byte[] getCRC16(byte[] data, int oldCrc16) {
        int crc16 = oldCrc16 & 0xffff;
        for (int i = 0; i < data.length; ++i) {
            crc16 = ccitt_table[(crc16 >> 8 ^ data[i]) & 0xff] ^ (crc16 << 8);
        }
        int Hcrc = (crc16 >> 8) & 0xff;
        int Lcrc = crc16 & 0xff;
        byte[] crc16_bytes = new byte[2];
        crc16_bytes[0] = (byte) Lcrc;
        crc16_bytes[1] = (byte) Hcrc;
        return crc16_bytes;
    }

    /**
     * 开关机接收设备信息校验
     *
     * @param str
     * @return
     */
    public static boolean checkChk(String str) {
        int chk = 0;
        int len = str.length();
        String receiveChk = str.substring(len - 2, len);
        int chk2 = Integer.parseInt(receiveChk, 16);
        String s = str.substring(10, str.length() - 2);
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < b.length; i++) {
            chk = chk ^ b[i];
        }
        if (chk != chk2) {
            return false;
        }
        return true;
    }

//    /**
//     * 远程开关机chk异或值算法
//     *
//     * @param chargeProtocol
//     * @return
//     */
//    public static byte getChk(ChargeProtocol chargeProtocol) {
//        int value = 0;
//        if (chargeProtocol.getLen() == 7) {
//            //主动发送与接收客户端信息,校验加上内容字段
//            int len = chargeProtocol.getLen(), no = chargeProtocol.getNo(),
//                    dir = chargeProtocol.getDir(), type = chargeProtocol.getType(),
//                    serial = chargeProtocol.getSerial(), cmd = chargeProtocol.getCmd();
//            value = len ^ no ^ dir ^ type ^ serial ^ cmd;
//        } else if (chargeProtocol.getLen() == 3) {
//            //接收客户端应答,不需要内容字段
//            int len = chargeProtocol.getLen(), no = chargeProtocol.getNo(),
//                    dir = chargeProtocol.getDir();
//            value = len ^ no ^ dir;
//        }
//        byte chk = (byte) value;
//        return chk;
//    }

//    public static byte getChk(WanglongLANProtocol protocol) {
//        int value = 0;
//        //主动发送与接收客户端信息,校验加上内容字段
//        int len = protocol.getLen();
//        byte no = protocol.getNo();
//        byte dir = protocol.getDir();
//        byte[] cmd = protocol.getCmd();
//        value = len ^ no ^ dir  ^ cmd[0] ^ cmd[1];
//        String info = protocol.getInfo();
//        if(info.length() > 0){
//            byte[] infoBytes = info.getBytes();
//            for(byte data : infoBytes){
//                value ^= data;
//            }
//        }
//        byte chk = (byte) value;
//        return chk;
//    }

    /**************************************************广医五电梯********************************************************/

    /**
     * 数据加密算法
     * @param Data  需要加密的数据
     * @param Crypt true:加密 false：解密
     */
    public static byte[] DataCrypt(byte[] Data, boolean Crypt) {
        int DataNum = Data.length;
        int KeyLEN = 8;
        //密匙定义
        byte[] KeyNum = new byte[KeyLEN];
        byte[] Key = new byte[KeyLEN];           //加密密匙数据
        byte tempdata = 0;
        int i, j;
        KeyNum[0] = (byte)0x46;
        KeyNum[1] = (byte)0xE9;
        KeyNum[2] = (byte)0x1A;
        KeyNum[3] = (byte)0xAF;
        KeyNum[4] = (byte)0x72;
        KeyNum[5] = (byte)0xC3;
        KeyNum[6] = (byte)0x7B;
        KeyNum[7] = (byte)0x75;
        if (!Crypt) {
            for (i = 0; i < DataNum; i++) {
                Data[i] = (byte)(~Data[i]);
            }
        }
        for (i = 0; i < KeyLEN; i++) {
            Key[i] = (byte)(tempdata ^ (KeyNum[i]));
        }

        for (i = 0, j = 0; i < DataNum; i++) {
            Data[i] = (byte)((Data[i] ^Key[j]));
            if (j < KeyLEN - 1) {
                j++;
            } else {
                j = 0;
            }
        }
        if (Crypt) {
            for (i = 0; i < DataNum; i++) {
                Data[i] =(byte) (~Data[i]);
            }
        }
        return Data;
    }

    /**
     * 广医五电梯
     * 生成验证码
     *
     * @param ip
     * @param SJData
     * @return
     */
    public static int getVerificationCode(String ip, byte[] SJData) {
        //ip拆分,存入int[]
        int[] IPData = new int[4];
        String[] str = ip.split("\\.");
        for (int a = 0; a < str.length; a++) {
            IPData[a] = Integer.parseInt(str[a]);
        }
        //生成算法
        int i;
        int PasswordTmp=0,Password=0;
        int[] keyNum = new int[4];
        keyNum[0] = IPData[2];
        keyNum[1] = IPData[1];
        keyNum[2] = IPData[0];
        keyNum[3] = IPData[3];
        for (i = 0; i < 4; i++) {
            PasswordTmp = keyNum[i] ^ SJData[i % 2];
            Password |= (PasswordTmp << (8 * i));
        }
        Password = ~(Password);
        return Password;
    }

   /*
    public static short createCRC(int[] data){
        int uchCRCHi = 0xFF;
        int uchCRCLo = 0xFF;
        int uIndex;
        int i;
        for (i = 0; i < data.length; i++) {
            uIndex = uchCRCLo ^ data[i];
            uchCRCLo = uchCRCHi ^ auchCRCLo[uIndex];
            uchCRCHi = auchCRCHi[uIndex] ;
        }
        short crc = (short)(uchCRCHi << 8 | uchCRCLo);
        return crc;
    }*/

    /**
     * 广医五电梯
     * 生成CRC校验值
     * @param srcData
     * @return
     */
    public static byte[] createCRCByChar(byte[] srcData){
        int uchCRCHi = 0xFF;
        int uchCRCLo = 0xFF;
        int uIndex;
        int i;
        for (i = 0; i < srcData.length; i++) {
            uIndex = uchCRCLo ^ (srcData[i] & 0xff);
            uchCRCLo = uchCRCHi ^ auchCRCLo[uIndex];
            uchCRCHi = (auchCRCHi[uIndex]) ;
        }
        byte[] crc = {(byte)uchCRCHi,(byte)uchCRCLo};
        //int crc = (uchCRCHi << 8 | uchCRCLo) & 0xff;
        return crc;
    }

    /**
     * 用于电梯---CRC校验查询数据1
     */
    private static final int[] auchCRCLo = {
            0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
            0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
            0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
            0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
            0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
            0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41,
            0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
            0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
            0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
            0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40,
            0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
            0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
            0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
            0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40,
            0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
            0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
            0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
            0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
            0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
            0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
            0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
            0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40,
            0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
            0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
            0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
            0x80, 0x41, 0x00, 0xC1, 0x81, 0x40
    };

    /**
     * 用于电梯---CRC校验查询数据2
     */
    private static final int[] auchCRCHi = {
            0x00, 0xC0, 0xC1, 0x01, 0xC3, 0x03, 0x02, 0xC2, 0xC6, 0x06,
            0x07, 0xC7, 0x05, 0xC5, 0xC4, 0x04, 0xCC, 0x0C, 0x0D, 0xCD,
            0x0F, 0xCF, 0xCE, 0x0E, 0x0A, 0xCA, 0xCB, 0x0B, 0xC9, 0x09,
            0x08, 0xC8, 0xD8, 0x18, 0x19, 0xD9, 0x1B, 0xDB, 0xDA, 0x1A,
            0x1E, 0xDE, 0xDF, 0x1F, 0xDD, 0x1D, 0x1C, 0xDC, 0x14, 0xD4,
            0xD5, 0x15, 0xD7, 0x17, 0x16, 0xD6, 0xD2, 0x12, 0x13, 0xD3,
            0x11, 0xD1, 0xD0, 0x10, 0xF0, 0x30, 0x31, 0xF1, 0x33, 0xF3,
            0xF2, 0x32, 0x36, 0xF6, 0xF7, 0x37, 0xF5, 0x35, 0x34, 0xF4,
            0x3C, 0xFC, 0xFD, 0x3D, 0xFF, 0x3F, 0x3E, 0xFE, 0xFA, 0x3A,
            0x3B, 0xFB, 0x39, 0xF9, 0xF8, 0x38, 0x28, 0xE8, 0xE9, 0x29,
            0xEB, 0x2B, 0x2A, 0xEA, 0xEE, 0x2E, 0x2F, 0xEF, 0x2D, 0xED,
            0xEC, 0x2C, 0xE4, 0x24, 0x25, 0xE5, 0x27, 0xE7, 0xE6, 0x26,
            0x22, 0xE2, 0xE3, 0x23, 0xE1, 0x21, 0x20, 0xE0, 0xA0, 0x60,
            0x61, 0xA1, 0x63, 0xA3, 0xA2, 0x62, 0x66, 0xA6, 0xA7, 0x67,
            0xA5, 0x65, 0x64, 0xA4, 0x6C, 0xAC, 0xAD, 0x6D, 0xAF, 0x6F,
            0x6E, 0xAE, 0xAA, 0x6A, 0x6B, 0xAB, 0x69, 0xA9, 0xA8, 0x68,
            0x78, 0xB8, 0xB9, 0x79, 0xBB, 0x7B, 0x7A, 0xBA, 0xBE, 0x7E,
            0x7F, 0xBF, 0x7D, 0xBD, 0xBC, 0x7C, 0xB4, 0x74, 0x75, 0xB5,
            0x77, 0xB7, 0xB6, 0x76, 0x72, 0xB2, 0xB3, 0x73, 0xB1, 0x71,
            0x70, 0xB0, 0x50, 0x90, 0x91, 0x51, 0x93, 0x53, 0x52, 0x92,
            0x96, 0x56, 0x57, 0x97, 0x55, 0x95, 0x94, 0x54, 0x9C, 0x5C,
            0x5D, 0x9D, 0x5F, 0x9F, 0x9E, 0x5E, 0x5A, 0x9A, 0x9B, 0x5B,
            0x99, 0x59, 0x58, 0x98, 0x88, 0x48, 0x49, 0x89, 0x4B, 0x8B,
            0x8A, 0x4A, 0x4E, 0x8E, 0x8F, 0x4F, 0x8D, 0x4D, 0x4C, 0x8C,
            0x44, 0x84, 0x85, 0x45, 0x87, 0x47, 0x46, 0x86, 0x82, 0x42,
            0x43, 0x83, 0x41, 0x81, 0x80, 0x40
    };
}