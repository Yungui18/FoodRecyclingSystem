package com.hyeprion.foodrecyclingsystem.util;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.hyeprion.foodrecyclingsystem.util.TimeUtil.getDateMsToString;

/**
 * 保存文字信息到内存卡
 */
public class SaveToSdUtil {
    /**
     * 保存log信息到文件中
     *
     * @param str
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    public static String saveLog(String str) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH");
        String mqttStr = (String.format("\n时间：%s\n", getDateMsToString()))
                + str + System.getProperty("line.separator");
        try {
            String time = formatter.format(new Date());
            String fileName = "port-" + time + ".txt";

            String path = Environment.getExternalStorageDirectory().getPath()
                    + "/recycling"
                    + "/PortErrorLog";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(path + "/" + fileName, true);
//            mqttStr = (String.format(MyApplication.showingActivity.getString(R.string.mqtt_top_strs), TimeUtil.getDateToString()))
//                    + str + System.getProperty("line.separator");

            fos.write(mqttStr.getBytes());
            fos.close();
            return fileName;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 保存串口日志
     *
     * @param str
     * @param type 1 查询命令  2 其他命令
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    public static String savePortDataToSD(String str, int type) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH");
        String mqttStr = (String.format("\n时间：%s\n", getDateMsToString()))
                + str + System.getProperty("line.separator");
        try {
            String fileName = "";
            String time = formatter.format(new Date());
            if (type == 1) {
                fileName = "port-query-" + time + ".txt";
            } else if (type == 2) {
                fileName = "port-" + time + ".txt";
            }


            String path = Environment.getExternalStorageDirectory().getPath()
                    + "/recycling"
                    + "/PortLog";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(path + "/" + fileName, true);
//            mqttStr = (String.format(MyApplication.showingActivity.getString(R.string.mqtt_top_strs), TimeUtil.getDateToString()))
//                    + str + System.getProperty("line.separator");

            fos.write(mqttStr.getBytes());
            fos.close();
            return fileName;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 读取SD卡 /configure.txt 文件下的 内容
     */
    public static String readFromSD(String filename) {
        StringBuilder sb = new StringBuilder("");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                filename = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + filename;
                //打开文件输入流
                FileInputStream input = new FileInputStream(filename);
                byte[] temp = new byte[1024];

                int len = 0;
                //读取文件内容:
                while ((len = input.read(temp)) > 0) {
                    sb.append(new String(temp, 0, len));
                }
                //关闭输入流
                input.close();
            } catch (IOException e) {

            }
        }
        return sb.toString();
    }
}
