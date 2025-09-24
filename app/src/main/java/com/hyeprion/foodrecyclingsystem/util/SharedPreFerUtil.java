package com.hyeprion.foodrecyclingsystem.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.blankj.utilcode.util.LogUtils;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * Created by ljj on 2019/8/13.
 * Describe:
 */
public class SharedPreFerUtil {
    /**
     * 保存序列化的对象
     *
     * @param preferenceName
     * @param key
     * @param obj
     */
    public static void saveObj(String preferenceName, String key, Object obj) {
        if (obj instanceof Serializable) {
            SharedPreferences sharedPreferences = MyApplication.getInstance().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);//把对象写到流里
                String temp = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
                editor.putString(key, temp);
                editor.apply();
                baos.close();
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.e("saveObj fail\nIOException:" + e.getMessage());
            }
        } else {
            LogUtils.e("saveObj fail\nmust implements Serializable");
        }
    }

    public static void clear(String preferenceName) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(
                preferenceName, Context.MODE_PRIVATE);

        if (sp != null) {
            sp.edit().clear().apply();
        }
    }

    public static Object getObj(String preferenceName, String key) {
        SharedPreferences sharedPreferences = MyApplication.getInstance().getSharedPreferences(
                preferenceName, Context.MODE_PRIVATE);
        String temp = sharedPreferences.getString(key, "");
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
        Object readObject = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            readObject = ois.readObject();
        } catch (IOException e) {
        } catch (ClassNotFoundException e1) {

        }
        return readObject;
    }



    public static void put(String preferenceName, String key, int value) {
        MyApplication.getInstance().getSharedPreferences(preferenceName, Context.MODE_PRIVATE).edit().putInt(key, value).apply();
    }

    public static void put(String preferenceName, String key, float value) {
        MyApplication.getInstance().getSharedPreferences(preferenceName, Context.MODE_PRIVATE).edit().putFloat(key, value).apply();
    }

    public static void put(String preferenceName, String key, boolean value) {
        MyApplication.getInstance().getSharedPreferences(preferenceName, Context.MODE_PRIVATE).edit().putBoolean(key, value).apply();
    }

    public static void put(String preferenceName, String key, String value) {
        MyApplication.getInstance().getSharedPreferences(preferenceName, Context.MODE_PRIVATE).edit().putString(key, value).apply();
    }

    public static int get(String preferenceName, String key, int defValue) {
        return MyApplication.getInstance().getSharedPreferences(preferenceName, Context.MODE_PRIVATE).getInt(key, defValue);
    }

    public static float get(String preferenceName, String key, float defValue) {
        return MyApplication.getInstance().getSharedPreferences(preferenceName, Context.MODE_PRIVATE).getFloat(key, defValue);
    }

    public static boolean get(String preferenceName, String key, boolean defValue) {
        return MyApplication.getInstance().getSharedPreferences(preferenceName, Context.MODE_PRIVATE).getBoolean(key, defValue);
    }

    public static String get(String preferenceName, String key, String defValue) {
        return MyApplication.getInstance().getSharedPreferences(preferenceName, Context.MODE_PRIVATE).getString(key, defValue);
    }


    /**
     * 将字节数组转化为字符串
     *
     * @param bytes
     * @return
     */
    public static String byteToString(byte[] bytes) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            if (Integer.toHexString(0xFF & bytes[i]).length() == 1) {
                buffer.append("0").append(
                        Integer.toHexString(0xFF & bytes[i]));
            } else {
                buffer.append(Integer.toHexString(0xFF & bytes[i]));
            }
        }
        return buffer.toString();
    }
}
