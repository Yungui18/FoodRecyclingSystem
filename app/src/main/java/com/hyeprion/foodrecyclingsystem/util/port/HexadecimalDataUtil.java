package com.hyeprion.foodrecyclingsystem.util.port;

import java.math.BigInteger;

/**
 * 数据、进制格式处理、转换
 */
public class HexadecimalDataUtil {
    /**
     * @function 将十六进制的字符串转换成二进制的字符串
     * @param hexString 16进制字符串
     * @return  4位二进制字符串
     */
    public static String hexStrToBinaryStr2(String hexString) {

        if (hexString == null || hexString.equals("")) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        // 将每一个十六进制字符分别转换成一个四位的二进制字符
        for (int i = 0; i < hexString.length(); i++) {
            String indexStr = hexString.substring(i, i + 1);
            String binaryStr = Integer.toBinaryString(Integer.parseInt(indexStr, 16));
            while (binaryStr.length() < 4) {
                binaryStr = "0" + binaryStr;
            }
            sb.append(binaryStr);
        }
        return sb.toString();
    }

    /**
     * @function 二进制字符串转换为十六进制字符串
     * 二进制字符串位数必须满足是4的倍数
     * @param binaryStr 四位的二进制字符串
     * @return 一位的十六进制字符串
     */
    public static String binaryStrToHexStr(String binaryStr) {

        if (binaryStr == null || binaryStr.equals("") || binaryStr.length() % 4 != 0) {
            return null;
        }

        StringBuffer sbs = new StringBuffer();
        // 二进制字符串是4的倍数，所以四位二进制转换成一位十六进制
        for (int i = 0; i < binaryStr.length() / 4; i++) {
            String subStr = binaryStr.substring(i * 4, i * 4 + 4);
            String hexStr = Integer.toHexString(Integer.parseInt(subStr, 2));
            sbs.append(hexStr);
        }

        return sbs.toString();
    }



    /**
     * RGB转换成十六进制字符串
     * @param
     * @return 16进制颜色字符串
     * */
    public static String toHexFromRGB(int red, int green, int blue){
        String r,g,b;
        StringBuilder su = new StringBuilder();
        r = decimalToHex2(red);
        g = decimalToHex2(green);
        b =decimalToHex2(blue);
        r = r.length() == 1 ? "0" + r : r;
        g = g.length() ==1 ? "0" +g : g;
        b = b.length() == 1 ? "0" + b : b;
        r = r.toUpperCase();
        g = g.toUpperCase();
        b = b.toUpperCase();
        //su.append("0xFF");
        su.append(r);
        su.append(g);
        su.append(b);
        //0000FF
        return su.toString();
    }

    /**
     * 十进制转十六进制
     * @param decimalStr 十进制数字
     */
    /*public static String decimalToHex2(int decimalStr){
        decimalStr = Math.min(decimalStr, 256);
        decimalStr = Math.max(decimalStr, 0);
        String result = Integer.toHexString(decimalStr);
        return result;
    }*/

    /**
     * 十进制转十六进制,高位补零 4位16进制结果
     * @param decimalStr 十进制数字
     */
    public static String decimalToHex(int decimalStr){
        String result =  String.format("%04x", decimalStr).toUpperCase();//高位补0
//        String result = Integer.toHexString(decimalStr);
        return result;
    }

    /**
     * 十进制转十六进制,高位补零 2位16进制结果
     * @param decimalStr 十进制数字
     */
    public static String decimalToHex2(int decimalStr){
        String result =  String.format("%02x", decimalStr).toUpperCase();//高位补0
//        String result = Integer.toHexString(decimalStr);
        return result;
    }

    /**
     * 十六进制 转 十进制
     * @param hexStr 十六进制数字
     */
    public static int hexToDecimal(String hexStr){
        int result = Integer.valueOf(hexStr,16);
        return result;
    }

    /**
     * 十六进制 转 浮点数
     * @param hexStr 十六进制数字
     */
    public static Float hex2Float(String hexStr){
        BigInteger b = new BigInteger(hexStr, 16);
        float value = Float.intBitsToFloat(b.intValue());

//        Float value = Float.intBitsToFloat(Integer.valueOf(hexStr.trim(), 16));
        return value;
    }

    /**
     * 浮点数 转 十六进制
     * @param f 浮点数
     */
    public static String float2Hex(Float f){
        String result = Integer.toHexString(Float.floatToIntBits(f));
        return result;
    }

    /**
     * Hex字符串转byte
     * @param inHex 待转换的Hex字符串
     * @return  转换后的byte
     */
    public static byte hexToByte(String inHex){
        return (byte)Integer.parseInt(inHex,16);
    }

    /**
     * hex字符串转byte数组
     * @param inHex 待转换的Hex字符串
     * @return  转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex){
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        }else {
            //偶数
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]=hexToByte(inHex.substring(i,i+2));
            j++;
        }
        return result;
    }

    /**
     *  读取小端byte数组为short
     * @param b
     * @return
     */
    public static short byteToShortLittle(byte[] b) {
        return (short) (((b[1] << 8) | b[0] & 0xff));
    }
    /**
     *  读取大端byte数组为short
     * @param b
     * @return
     */
    public static short byteToShortBig(byte[] b) {
        return (short) (((b[0] << 8) | b[1] & 0xff));
    }

    /**
     *  读取大端byte数组为short
     * @param hex 16进制
     * @return
     */
    public static short byteToShortBig(String hex) {
        byte[] b = hexToByteArray(hex);
        return (short) (((b[0] << 8) | b[1] & 0xff));
    }




    /**
     * MODBUS CRC16
     * @param data
     * @return
     */
    public static String getCRC(String data) {
        data = data.replace(" ", "");
        int len = data.length();
        if (!(len % 2 == 0)) {
            return "0000";
        }
        int num = len / 2;
        byte[] para = new byte[num];
        for (int i = 0; i < num; i++) {
            int value = Integer.valueOf(data.substring(i * 2, 2 * (i + 1)), 16);
            para[i] = (byte) value;
        }
        return getCRC(para);
    }

    /**
     * 校验CRC是否正确
     * @param data
     * @return
     */
    public static boolean isRightCrc(String data){
        if (data.length()<4){
            return false;
        }
      String crc =   getCRC(data.substring(0,data.length()-4));
      return crc.equals(data.substring(data.length()-4));
    }

    /**
     * 计算CRC16校验码
     *
     * @param bytes
     *            字节数组
     * @return {@link String} 校验码
     */
    private static String getCRC(byte[] bytes) {
        // CRC寄存器全为1
        int CRC = 0x0000ffff;
        // 多项式校验值
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        // 结果转换为16进制
        String result = Integer.toHexString(CRC).toUpperCase();
        if (result.length() != 4) {
            StringBuffer sb = new StringBuffer("0000");
            result = sb.replace(4 - result.length(), 4, result).toString();
        }
        //高位在前地位在后
        //return result.substring(2, 4) + " " + result.substring(0, 2);
        // 交换高低位，低位在前高位在后
        return result.substring(2, 4) /*+ " "*/ + result.substring(0, 2);
    }
}
