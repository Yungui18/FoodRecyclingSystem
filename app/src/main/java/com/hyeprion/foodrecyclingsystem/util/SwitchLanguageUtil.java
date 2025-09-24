package com.hyeprion.foodrecyclingsystem.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.blankj.utilcode.util.LogUtils;

import java.util.Locale;


/**
 * Created by me on 2018/8/31.
 */
public class SwitchLanguageUtil {

    public static void switchLanguage(Context context, String language) {
        if (TextUtils.isEmpty(language)) {
            LogUtils.e("the language is null");
        }
        //设置应用语言类型
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (TextUtils.equals("en", language)) {
            config.locale = Locale.ENGLISH;
        } else if (TextUtils.equals("cn", language)) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if (TextUtils.equals("tw", language)) {
            config.locale = Locale.TRADITIONAL_CHINESE;
        } else if (TextUtils.equals("ja", language)) {
            config.locale = Locale.JAPANESE;
        } else if (TextUtils.equals("ko", language)) {
            config.locale = Locale.KOREA;
        } else {
            config.locale = Locale.getDefault();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(config.locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context.createConfigurationContext(config);
        }
        resources.updateConfiguration(config, dm);
    }

   /* public static void switchLanguageBySetting(Context context) {
        switchLanguage(context, context.getResources().getStringArray(R.array.country_code)[SharedPrefsUtil.get(MyConst.SHARE_KEY_LANGUAGE_INDEX, 0)]);
    }*/


    public static boolean checkLanguage(Context context, Locale locale) {
        if (context == null || locale == null) {
            return false;
        }

        return context.getResources().getConfiguration().locale == locale;
    }

    public static Locale getLocale(Context context) {
        return context.getResources().getConfiguration().locale;
    }

    public static Locale getLocale(String language,Context context) {
        Configuration config = context.getResources().getConfiguration();
        if (TextUtils.equals(language, Constant.CHINESE)) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if (TextUtils.equals(language, Constant.KOREAN)) {
            config.locale = Locale.KOREA;
        } else if (TextUtils.equals(language, Constant.JAPANESE)) {
            config.locale = Locale.JAPANESE;
        } else if (TextUtils.equals(language, Constant.ENGLISH)) {
            config.locale = Locale.ENGLISH;
        } else {
            config.locale = Locale.getDefault();
        }
        return config.locale;
    }
}
