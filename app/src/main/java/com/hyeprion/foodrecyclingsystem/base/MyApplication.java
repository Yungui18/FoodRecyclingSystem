package com.hyeprion.foodrecyclingsystem.base;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.LanguageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.bean.AdminParameterBean;
import com.hyeprion.foodrecyclingsystem.greendao.db.DaoMaster;
import com.hyeprion.foodrecyclingsystem.greendao.db.DaoSession;
import com.hyeprion.foodrecyclingsystem.util.BarUtil;
import com.hyeprion.foodrecyclingsystem.util.Constant;
import com.hyeprion.foodrecyclingsystem.util.CrashHandler;
import com.hyeprion.foodrecyclingsystem.util.SharedPreFerUtil;
import com.hyeprion.foodrecyclingsystem.util.SoundPlayUtils;
import com.hyeprion.foodrecyclingsystem.util.SwitchLanguageUtil;
import com.hyeprion.foodrecyclingsystem.util.port.PortControlUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.multidex.MultiDexApplication;
import okhttp3.OkHttpClient;

public class MyApplication extends MultiDexApplication {
    private static MyApplication instance = null;
    public static BaseActivity showingActivity;
    public static AdminParameterBean adminParameterBean;
    public static SoundPlayUtils soundPlayUtils;

    public static String serverIp = "";
    public static String loginId = "";
    public static String loginPW = "";
    public static String deviceId = "05400007";
    public static String loginType = "";
    public static String cardNo = "";

    public static boolean firmwareVersionNew = false; // 下位机是否为2.0版本的新pub板  true：是

    /**
     * 初次打开程序，设置时间内不做最小温度判断
     */
    public static boolean isStartUp = true;
    /**
     * 开机时为急停状态，解除急停后启动所有电机，设置自动
     */
    public static boolean StartUp = true;

    /**
     * 忽略下位机重启 寄存器结果 标志位
     */
    public static boolean HWRestart = true;
    /**
     * 杀菌模式标识位  true：杀菌模式   false：未处于杀菌模式
     */
    public static boolean sterilizationFlag = false;


    public static String weighingUnit = "";

    private DaoSession daoSession;

    public DaoSession getDaoSession() {
        return daoSession;
    }

//    private DaoSession daoSession;
//    public DaoSession getDaoSession() {
//        return daoSession;
//    }


    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(getApplicationContext());
        instance = this;
        PortControlUtil.getInstance().openPort();
        initGreenDao();

        loginType = getString(R.string.login_type_pw);

        initAdminParameter();
        soundPlayUtils = new SoundPlayUtils(this);

        BarUtil.hideNavBar(getApplicationContext(), true);

        initOKGo();

        Intent intent = new Intent(this, SocketService.class);
        startService(intent);
    }

    private void initAdminParameter() {
        String adminParameterStr = (String) SharedPreFerUtil.getObj(
                Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY);
        if (adminParameterStr == null || adminParameterStr.equals("")) {
            LogUtils.e(LanguageUtils.getSystemLanguage().getLanguage());
            adminParameterBean = new AdminParameterBean();
//            adminParameterBean.setLanguage(LanguageUtils.getSystemLanguage().getLanguage().toLowerCase()); // 语言
            adminParameterBean.setLanguage(Constant.KOREAN); // 语言 默认 韩语
            LanguageUtils.applyLanguage(SwitchLanguageUtil.getLocale(adminParameterBean.getLanguage(),this), true);
            adminParameterBean.setAppMode(Constant.APARTMENT); // app模式，暂时取消
            adminParameterBean.setWeighingMode(Constant.HAVE); // 称重模式 默认 有
            adminParameterBean.setInletLimitedAlarmTotalMax("900"); // 设备报警总重量最大值
            adminParameterBean.setInletLimitedAlarmTotalMin("400"); // 设备报警总重量最小值
            adminParameterBean.setInletLimitedTotal("1000"); // 总限定投入量 默认1000
            adminParameterBean.setInletLimitedTotalAccumulation("0"); // 每次投入重量累积
            adminParameterBean.setInletLimited24H("30"); // 日（24H）限定投入量 默认30
            adminParameterBean.setWeighAlarmTime("5"); // 称重报警时间 min
            adminParameterBean.setWeighAlarmWeight("5"); // 称重报警重量 kg
            adminParameterBean.setLoginMode(Constant.NONE); // 登录模块  默认无
            adminParameterBean.setInletMode(Constant.MANUAL); // 投入口模式 自动、手动  默认手动
            // 自动加热设置 目标温度、浮动温度、报警温度
            adminParameterBean.setHeartingTemperature(new AdminParameterBean.HeartingTemperature(
                    "50", "1", "80", "15",
                    "120","50","1"));
            // 自动除湿设置  目标湿度、浮动湿度
            adminParameterBean.setHumiditySetting(new AdminParameterBean.HumiditySetting(
                    "70", "10"));
            // 投入口开启或是关闭无响应超时时间  S
            adminParameterBean.setInletTimeoutTime("10");
            // 投入口开启或是关闭 低速电压 V
            adminParameterBean.setInletLowSpeedVoltage("2");

            List<AdminParameterBean.FanHumiditySettingBean> fanHumiditySettingBeans = new ArrayList<>();
            // 风扇湿度档位设置，1,2，3档，对应湿度数字及电压
            fanHumiditySettingBeans.add(new AdminParameterBean.FanHumiditySettingBean(1, 30, 24));
            fanHumiditySettingBeans.add(new AdminParameterBean.FanHumiditySettingBean(2, 30, 24));
            fanHumiditySettingBeans.add(new AdminParameterBean.FanHumiditySettingBean(3, 30, 24));
            adminParameterBean.setFanHumiditySettingBeanList(fanHumiditySettingBeans);

            // 搅拌正转运行时间 normal
            adminParameterBean.setStirRunTimeNormal("50");
            // 搅拌正转间隔时间  normal
            adminParameterBean.setStirIntervalTimeNormal("10");
            // 搅拌反转运行时间 normal
            adminParameterBean.setStirReverseRunTimeNormal("0");
            // 搅拌反转间隔时间  normal
            adminParameterBean.setStirReverseIntervalTimeNormal("1");
            // 搅拌正转运行时间 节电模式 power saving
            adminParameterBean.setStirRunTimePowerSaving("10");
            // 搅拌正转间隔时间  节电模式 power saving
            adminParameterBean.setStirIntervalPowerSaving("10");
            // 搅拌反转运行时间 节电模式 power saving
            adminParameterBean.setStirReverseRunTimePowerSaving("0");
            // 搅拌反转间隔时间  节电模式 power saving
            adminParameterBean.setStirReverseIntervalPowerSaving("1");

            // 自动加热设置-节电模式 power saving 前 目标温度、浮动温度， 后 目标温度、浮动温度
            adminParameterBean.setHeartingTemperaturePowerSaving(new AdminParameterBean.
                    HeartingTemperaturePowerSaving("40", "1","40", "1"));

            //加热模式
            adminParameterBean.setHeaterMode(Constant.TEM_HUMIDITY_AUTOMATIC);
            // 除湿模式
            adminParameterBean.setDehumidificationMode(Constant.TEM_HUMIDITY_AUTOMATIC);
            // 设备信息，设备id
            adminParameterBean.setDeviceId("05400007");
            // 设备运转模式，自动、手动、停止
            adminParameterBean.setDeviceMode(Constant.DEVICE_MODE_AUTO);
            adminParameterBean.setEnterPowerSavingTime("48");

            // 投料口打开关闭称重时间
            adminParameterBean.setFeedingBeforeWeightTime("5");
            adminParameterBean.setFeedingAfterWeightTime("5");

            // 进入屏保时间
            adminParameterBean.setScreensaverTime("30");

            adminParameterBean.setFanRunTimePowerSaving("10");
            adminParameterBean.setFanIntervalPowerSaving("10");
            adminParameterBean.setObservePortAlarm(false);
            adminParameterBean.setInletOpenVoltage("5");
            adminParameterBean.setInletCloseVoltage("5");
            adminParameterBean.setInletQuicklyCloseTime("1000");
            adminParameterBean.setWeighingSourceSetting(Constant.WEIGHING_SOURCE_LOADCELL1);
            adminParameterBean.setWeightCalibrationNum("10");
            adminParameterBean.setWeighingUnit(Constant.WEIGHING_UNIT_KG);
            adminParameterBean.setHumidityAlarmTime("360"); // 湿度报警时间 min
            adminParameterBean.setHumidityAlarmHumidity(100); // 湿度报警对应的湿度 %
            adminParameterBean.setHumidityAlarmSwitch(false); // 湿度报警对应的湿度 %
            adminParameterBean.setSterilizationModeTemp("70"); // 杀菌模式温度 ℃
            adminParameterBean.setSterilizationModeTime("60"); // 杀菌模式时间 min
            adminParameterBean.setWeightDecimalShow(true); // 重量小数显示
            SharedPreFerUtil.saveObj(
                    Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY, JSON.toJSONString(adminParameterBean));

        } else {
            adminParameterBean = JSON.parseObject(adminParameterStr, AdminParameterBean.class);
            if (adminParameterBean.getEnterPowerSavingTime() == null || adminParameterBean.getEnterPowerSavingTime().equals("")) {
                adminParameterBean.setEnterPowerSavingTime("48");
                SharedPreFerUtil.saveObj(
                        Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY, JSON.toJSONString(adminParameterBean));
            }
            if (adminParameterBean.getFeedingBeforeWeightTime() == null || adminParameterBean.getFeedingBeforeWeightTime().equals("")) {
                adminParameterBean.setFeedingBeforeWeightTime("2");
                adminParameterBean.setFeedingAfterWeightTime("5");
                SharedPreFerUtil.saveObj(
                        Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY, JSON.toJSONString(adminParameterBean));
            }

            if (adminParameterBean.getScreensaverTime() == null || adminParameterBean.getScreensaverTime().equals("")) {
                adminParameterBean.setScreensaverTime("0");
                SharedPreFerUtil.saveObj(
                        Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY, JSON.toJSONString(adminParameterBean));
            }
            if (adminParameterBean.getHeartingTemperature().getTargetTem2() == null ||
                    adminParameterBean.getHeartingTemperature().getTargetTem2().equals("")){
                // 自动加热设置 目标温度、浮动温度、报警温度
                adminParameterBean.setHeartingTemperature(new AdminParameterBean.HeartingTemperature(
                        "60", "10", "80", "40",
                        "20","60","10"));
                SharedPreFerUtil.saveObj(
                        Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY, JSON.toJSONString(adminParameterBean));
            }
            if (adminParameterBean.getHeartingTemperaturePowerSaving().getTargetTem2() == null ||
                    adminParameterBean.getHeartingTemperaturePowerSaving().getTargetTem2().equals("")){
                // 自动加热设置-节电模式 power saving 前 目标温度、浮动温度， 后 目标温度、浮动温度
                adminParameterBean.setHeartingTemperaturePowerSaving(new AdminParameterBean.
                        HeartingTemperaturePowerSaving("60", "10","60", "10"));
                SharedPreFerUtil.saveObj(
                        Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY, JSON.toJSONString(adminParameterBean));
            }
            if (adminParameterBean.getHumidityAlarmTime() == null ||
                    adminParameterBean.getHumidityAlarmTime().equals("")){
                // 湿度报警时间 min
                adminParameterBean.setHumidityAlarmTime("10");
                SharedPreFerUtil.saveObj(
                        Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY, JSON.toJSONString(adminParameterBean));
            }
            deviceId = adminParameterBean.getDeviceId();
        }

        if (MyApplication.adminParameterBean.getWeighingUnit() == Constant.WEIGHING_UNIT_KG){
            weighingUnit = getString(R.string.kg_2);
        }else if (MyApplication.adminParameterBean.getWeighingUnit() == Constant.WEIGHING_UNIT_POUND){
            weighingUnit = getString(R.string.LB);
        }
    }

    /**
     * 初始化OKGo
     */
    private void initOKGo() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //全局的读取超时时间
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);

        HttpHeaders headers = new HttpHeaders();
        headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文，不允许有特殊字符
        headers.put("commonHeaderKey2", "commonHeaderValue2");
        HttpParams params = new HttpParams();
        params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
        params.put("commonParamsKey2", "这里支持中文参数");

        OkGo.getInstance().init(this)                       //必须调用初始化
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3);                             //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
//                .addCommonHeaders(headers)                      //全局公共头
//                .addCommonParams(params);                       //全局公共参数
    }


    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "inlet.db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }



    /*public class DBHelper extends DaoMaster.OpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            super.onUpgrade(db, oldVersion, newVersion);
            */

    /**
     * com.github.yuweiguocn:GreenDaoUpgradeHelper:v2.1.0
     * 安全更新数据库
     * UserDao.class
     * 这些是安全更新的表的dao
     * 需要安全更新的数据表都dao要放到这里
     *//*
            MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {w
                @Override
                public void onCreateAllTables(Database db, boolean ifNotExists) {
                    DaoMaster.createAllTables(db, ifNotExists);
                }
                @Override
                public void onDropAllTables(Database db, boolean ifExists) {
                    DaoMaster.dropAllTables(db, ifExists);
                }
            },UserDao.class);
        }}*/
    public static boolean getFirmwareVersionNew() {
        // 如果版本号前面大于等于2，说明是新pcb板
        if (PortControlUtil.getInstance().
                getPortStatus().getFirmwareVersion() != null && PortControlUtil.getInstance().
                getPortStatus().getFirmwareVersion().contains(".")) {
            firmwareVersionNew = Integer.parseInt(PortControlUtil.getInstance().
                    getPortStatus().getFirmwareVersion().split("\\.")[0]) >= 2;
        } else {
            firmwareVersionNew = false;
        }

        return firmwareVersionNew;
    }
}
