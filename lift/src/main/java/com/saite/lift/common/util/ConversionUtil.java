package com.saite.lift.common.util;

import com.alibaba.fastjson.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * 16进制转换工具类
 */
public class ConversionUtil {
    /**
     * 将16进制字符串转换为byte数组
     * @param str 16进制字符串
     * @return 结果
     */
    public static byte[] hexStringToBytes(String str) {
        if (str == null || "".equals(str.trim())) {
            return new byte[0];
        }
        int count = str.length() / 2;
        byte[] result = new byte[count];
        for (int i = 0; i < count; i++) {
            String substring = str.substring(i * 2, i * 2 + 2);
            result[i] = (byte) (Integer.parseInt(substring, 16));
        }
        return result;
    }

    /**
     * 字节数组转16进制
     * @param bytes 字节数组
     * @return 结果
     */
    public static String bytesToHexString(byte[] bytes) {
        if (Objects.isNull(bytes)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if (hexString.length() < 2) {
                result.append(0);
            }
            result.append(hexString);
        }
        return result.toString();
    }

    /***
     * 单个字节转成16进制字符串
     * @param b 字节
     * @return 结果
     */
    public static String byteToHexString(byte b) {
        byte[] bytes = {b};
        return bytesToHexString(bytes);
    }

    /** 获取uchar值 */
    public static int getValueByUnsignedChar(byte b) {
        return b & 0xFF;
    }

    /** uchar数值转换为byte值 */
    public static byte getUnsignedCharByValue(int b) {
        return (byte) (b & 0xFF);
    }

    /**
     * int转换为四个字节数组
     * @param i 数字
     * @return 结果
     */
    public static byte[] intToFourByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    public static byte[] longToFourByteArray(Long i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    /**
     * int转换为2个字节数组 高位在低位下标，低位在高位下标
     * @param i 数字
     * @return 结果
     */
    public static byte[] intToTwoByteArray(int i) {
        byte[] result = new byte[2];
        result[0] = (byte)((i >> 8) & 0xFF);
        result[1] = (byte)(i & 0xFF);
        return result;
    }

    /**
     * short转换为2个字节数组
     * @param s 数字
     * @return 结果
     */
    public static byte[] shortToTwoByteArray(short s) {
        byte[] result = new byte[2];
        result[0] = (byte)((s >> 8) & 0xFF);
        result[1] = (byte)(s & 0xFF);
        return result;
    }

    /**
     * 数组转成int
     * @param bytes 数组
     * @return 结果
     */
    public static int bytesToInt(byte[] bytes) {
        if (bytes == null || bytes.length > 4) {
            return -1;
        }
        int num1 = bytes[0] & 0xFF;
        int num2 = (bytes[1] & 0xFF) << 8;
        int num3 = 0, num4 = 0;
        if (bytes.length > 2) {
            num3 = (bytes[2] & 0xFF) << 16;
            num4 = (bytes[3] & 0xFF) << 24;
        }
        return num1|num2|num3|num4;
    }

    /**
     * 截取数组
     * @param source 原始数组
     * @param offest 偏移量
     * @param length 数据长度
     * @return 结果
     */
    public static byte[] subByte(byte[] source, int offest, int length) {
        byte[] result = new byte[length];
        System.arraycopy(source, offest, result, 0, length);
        return result;
    }

    /**
     * 翻转低位与高位字节
     * @param source 原始数组
     * @return 结果
     */
    public static byte[] reverseByte(byte[] source) {
        int length = source.length;
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = source[length - 1 - i];
        }
        return result;
    }

    /**
     * 字符串转变为16进制字符串
     * @param str 字符
     * @return 结果
     */
    public static String stringToHexString(String str) {
        char[] hexChar = "0123456789ABCDEF".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        int bit;
        for (byte b : bytes) {
            bit = (b & 0xF0) >> 4;
            stringBuilder.append(hexChar[bit]);
            bit = b & 0x0F;
            stringBuilder.append(hexChar[bit]);
        }
        return stringBuilder.toString().trim();
    }

    /**
     * 十六进制字符串转换为字符串
     * @param hexString 16进制字符串
     * @return 结果
     */
    public static String hexStringToUTF8String(String hexString) {
        String hexStr = "0123456789ABCDEF";
        char[] hexChars = hexString.toCharArray();
        int length = hexChars.length / 2;
        byte[] bytes = new byte[length];
        int num;
        for (int i = 0; i < length; i++) {
            int position = i * 2;
            num = hexStr.indexOf(hexChars[position]) * 16;
            num += hexStr.indexOf(hexChars[position+1]);
            bytes[i] = (byte) (num & 0xFF);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    static void printBit(Long a) {
        for (int i = 0; i < 32; i++) {
            System.out.print(a >> i & 1);
            if (i != 0 && (i+1) % 8 == 0 ) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }

//    public static void main(String[] args) {
//       int a = 16;
//        for (int i = 0; i < 32; i++) {
//
//            System.out.print(a >> i & 1);
//            if (i != 0 && (i+1) % 8 == 0 ) {
//                System.out.print(" ");
//            }
//        }
//        int i = a >> 5 & 1;
//        System.out.println();
//        System.out.println(i);
//
//        int b = 0;
//        printBit((long) b);
//        int c = 1 << 6 | b;
//        printBit((long) c);
//        int d = 1 << 3 | c;
//        printBit((long) d);
//        Long bit = getFloorDetectionMarkBit();
//        printBit(bit);
//        byte[] bytes = longToFourByteArray(bit);
//        System.out.println(bytesToHexString(bytes));
//
//    }
    private static Long getFloorDetectionMarkBit() {
//        String s = "[-2,-1,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30]";
        String s = "[-2,-1,1,2,7,8,9,10,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30]";

        List list = JSONObject.parseObject(s, List.class);
        long mark = 0;
        for (int i = 0; i < list.size(); i++) {
            for (int j = 1; j <= 32; j++) {
                int temp;
                if (j < 3) {
                    temp = j - 3;
                } else {
                    temp = j - 2;
                }
                if (Objects.equals(temp, list.get(i))) {
                    mark = (1L << (j - 1)) | mark;
                }
            }
        }
        return mark;
    }

    public static void main(String[] args) {
        String s = "82797b22636d64223a22676574486561" +
        "727462656174222c2274696d65223a22" +
        "323032322d31322d31392031333a3339" +
        "3a3234222c226964223a223832303337" +
        "37343630303664346638646232623130" +
        "6462323033336663666633222c226465" +
        "7374223a22726f626f74222c226f7269" +
        "223a22646576737973227d";
        System.out.println(hexStringToUTF8String(s));
    }

}
