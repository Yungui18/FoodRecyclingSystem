package com.hyeprion.foodrecyclingsystem.util;

public class Constant {
//    public static final String IP = "http://bmtssystem.com/api/api.php";
    public static final String IP = "http://14.63.221.64:28080/api/new_api.php";
    public static String SERVER_IP;


    public static final long second = 1000L;
    public static final long minute = 60 * second;

    /**
     * 存放管理员参数设置
     *
     * @see com.hyeprion.foodrecyclingsystem.bean.AdminParameterBean
     */
    public static final String ADMIN_PARAMETER_FILENAME = "admin_parameter_save";
    public static final String ADMIN_PARAMETER_KEY = "adminParameterData";

    /**
     * s
     */
    public static int SHOP_ALLOCATED_SHOP_LOCATION_CODE = 1;


    /**
     * 对应管理员参数管理中的各项选项
     */
    public static final String CHINESE = "zh";
    public static final String KOREAN = "ko";
    public static final String JAPANESE = "ja";
    public static final String ENGLISH = "en";
    public static final String APARTMENT = "apartment";
    public static final String SHOP = "shop";
    //    public static final String INSIDE = "inside";
//    public static final String OUTSIDE = "outside";
    public static final String HAVE = "have";
    public static final String NONE = "none";
    public static final String BINDING = "binding";
    public static final String AUTOMATION = "automation";
    public static final String MANUAL = "manual";


    /**
     * 设备参数
     */
    public static String HTTP_PARAMS_TYPE = "type";
    /**
     * 设备状态
     */
    public static String HTTP_PARAMS_TYPE_DEVICE_STATUS = "1";
    /**
     * 投入垃圾
     */
    public static String HTTP_PARAMS_TYPE_PUT_INTO_RUBBISH = "2";
    /**
     * 设备登录
     */
    public static String HTTP_PARAMS_TYPE_DEVICE_LOGIN = "3";
    /**
     * 卡片id绑定账号、密码
     */
    public static String HTTP_PARAMS_TYPE_CARD_BINGING = "4";
    /**
     * 故障信息上报
     */
    public static String HTTP_PARAMS_TYPE_ERROR_REPORT = "5";
    /**
     * 故障信息参数
     */
    public static String HTTP_PARAMS_ERROR_MESSAGE = "errorMessage";
    public static String HTTP_PARAMS_LED = "led";
    /**
     * 设备编码
     */
    public static String HTTP_PARAMS_SYSTEM_NO = "systemNo";
    /**
     * 用户类型 0=apartment; 1=shop；2=Manager
     */
    public static String HTTP_PARAMS_USER_TYPE = "userType";
    /**
     * 是否开启 0,1
     */
    public static String HTTP_PARAMS_IS_ON = "isOn";
    /**
     * 1:自动状态   0:手动状态
     */
    public static String HTTP_PARAMS_IS_AUTO = "isAuto";
    /**
     * 1:自动状态   0:手动状态
     */
    public static int HTTP_PARAMS_IS_AUTO_ON = 1;
    /**
     * 1:自动状态   0:手动状态
     */
    public static int HTTP_PARAMS_IS_AUTO_OFF = 0;

    /**
     * 登录类型 id登录
     */
    public static int HTTP_LOGIN_TYPE_ID = 0;
    /**
     * 登录类型 RFID登录
     */
    public static int HTTP_LOGIN_TYPE_RFID = 1;
    /**
     * 登录类型 admin 管理员登录
     */
    public static int HTTP_LOGIN_TYPE_ADMIN = 2;

    /**
     * 搅拌电机 =(0,1)
     */
    public static String HTTP_PARAMS_STS_MOTOR = "stsMotor";
    /**
     * 搅拌电机错误 = 0, 1 (normal , error)
     */
    public static String HTTP_PARAMS_STS_MOTOR_ERROR = "isInverterError";
    /**
     * 空气温度 零下30度到零上100度
     */
    public static String HTTP_PARAMS_IS_TEMP = "isTemp";
    /**
     * 加热前(加热1) =  0, 1 (off , on)
     */
    public static String HTTP_PARAMS_IS_WARM_FRONT = "isWarmFront";
    /**
     * 加热前温度(加热1) =  零下30度到零上200度
     */
    public static String HTTP_PARAMS_IS_WARM_FRONT_TEMP = "isWarmFrontTemp";
    /**
     * 加热后(加热2) =  0, 1 (off , on)
     */
    public static String HTTP_PARAMS_IS_WARM_BACK = "isWarmBack";
    /**
     * 加热后温度(加热2) =  零下30度到零上200度
     */
    public static String HTTP_PARAMS_IS_WARM_BACK_TEMP = "isWarmBackTemp";
    /**
     * 除湿器（干燥机） =  0, 1 (off , on)
     */
    public static String HTTP_PARAMS_IS_DRYER = "isDryer";
    /**
     * 空气湿度 =   0~100 %
     */
    public static String HTTP_PARAMS_HUMIDITY = "humidity";
    /**
     * 照明 =   0, 1 (off , on)
     */
    public static String HTTP_PARAMS_IS_LIGHT = "isLight";
    /**
     * 投入口感应器 = (0,1) ,(0,1) , (0,1) -> (off,on) , (off,on) , (off,on)
     */
    public static String HTTP_PARAMS_IS_DOOR_SENSORS = "isDoorSensors";
    /**
     * 投入口按钮 = 0, 1 (off , on)
     */
    public static String HTTP_PARAMS_IS_DOOR_BUTTON = "isDoorButton";
    /**
     * 观察口 = 0, 1 (off , on)
     */
    public static String HTTP_PARAMS_IS_DETECT_DOOR = "isDetectDoor";
    /**
     * 排出口 = 0, 1 (off , on)
     */
    public static String HTTP_PARAMS_IS_OUT_DOOR = "isOutDoor";
    /**
     * 排气扇 =  0, 1, 2, 3 (off, 1, 2, 3)
     */
    public static String HTTP_PARAMS_IS_FAN = "isFan";
    /**
     * LED右 =   0, 1, 2, 3 (off, G, Y, R)
     */
    public static String HTTP_PARAMS_IS_LED_RIGHT = "isLedRight";
    /**
     * LED左 =   0, 1, 2, 3 (off, G, Y, R)
     */
    public static String HTTP_PARAMS_IS_LED_LEFT = "isLedLeft";
    /**
     *风压
     */
    public static String HTTP_PARAMS_PA = "pa";
    /**
     *风压
     */
    public static String HTTP_PARAMS_ERROR = "error";



    /**
     * 错误 :000~200 （错误代码用Excel文件从000到200上传显示）
     */
    public static String HTTP_PARAMS_STS_WORM_LINE = "stsWormLine";
    /**
     * fan=(0,1)
     */
    public static String HTTP_PARAMS_STS_VENTILATOR = "stsVentilator";
    /**
     * 温度=float 70.00
     */
    public static String HTTP_PARAMS_STS_TEMPERATURE = "stsTemperature";
    /**
     * 湿度=float 50.00
     */
    public static String HTTP_PARAMS_STS_HUMIDITY = "stsHumidity";
    /**
     * 急停 0,1
     */
    public static String HTTP_PARAMS_STS_STOP = "stsEmergency";
    /**
     * 系统错误，之后定义
     */
    public static String HTTP_PARAMS_STS_ERROR = "stsError";
    /**
     * 投入口=(0,1
     */
    public static String HTTP_PARAMS_STS_DOOR = "stsDoor";
    /**
     * 现在不用
     */
    public static String HTTP_PARAMS_STS_TIMER = "stsTimer";
    /**
     * 现在不用
     */
    public static String HTTP_PARAMS_ERROR_NOTI = "errorNoti";
    /**
     * 现在不用
     */
    public static String HTTP_PARAMS_CONTROLER_SETTINGS = "controlerSettings";
    /**
     * 投入重量
     */
    public static String HTTP_PARAMS_WEIGHT = "weight";
    /**
     * 几幢几号（ID）
     */
    public static String HTTP_PARAMS_LOGIN_ID = "loginId";
    /**
     * 注册时写入的密码
     */
    public static String HTTP_PARAMS_LOGIN_PW = "loginPw";
    /**
     * 卡号
     */
    public static String HTTP_PARAMS_CARD_NO = "cardNo"; /**
     * 登录类型 0=ID登录, 1=RFID登录
     */
    public static String HTTP_PARAMS_LOGIN_TYPE = "loginType";

    public static int TRUE = 1;
    public static int FALSE = 0;

    /**
     * 进入杀菌模式密码
     */
    public static String STERILIZATION_PW = "0429";

    /**
     * 温度、湿度手动
     */
    public static int TEM_HUMIDITY_MANUAL = 1;
    /**
     * 温度、湿度自动
     */
    public static int TEM_HUMIDITY_AUTOMATIC = 2;
    /**
     * 温度、湿度停止
     */
    public static int TEM_HUMIDITY_STOP = 3;

    /**
     * 设备模式：自动
     */
    public static int DEVICE_MODE_AUTO = 1;
    /**
     * 设备模式：手动
     */
    public static int DEVICE_MODE_MANUAL = 2;
    /**
     * 设备模式：停止
     */
    public static int DEVICE_MODE_STOP = 3;

    /**
     * 称重源：485
     */
    public static int WEIGHING_SOURCE_485 = 1;
    /**
     * 称重源：直连1  loadcell1
     */
    public static int WEIGHING_SOURCE_LOADCELL1 = 2;
    /**
     * 称重源：直连2  loadcell2
     */
    public static int WEIGHING_SOURCE_LOADCELL2 = 3;


    /**
     * 称重单位：kg
     */
    public static int WEIGHING_UNIT_KG = 1;
    /**
     * 称重单位：磅 lb
     */
    public static int WEIGHING_UNIT_POUND = 2;
    /**
     * 异常解除
     */
    public static int TROUBLE_TYPE_REMOVE = 0;
    /**
     * 搅拌电机异常 stir 状态 = 4
     */
    public static final int TROUBLE_TYPE_STIR = 1;
    /**
     * 排料口未关闭
     */
    public static final int TROUBLE_TYPE_OUTLET = 2;
    /**
     * 观察口未关闭
     */
    public static final int TROUBLE_TYPE_OBSERVE = 3;
    /**
     * 电动门（投入口）异常
     */
    public static final int TROUBLE_TYPE_INLET = 4;
    /**
     * 加热异常,温度太高
     */
    public static final int TROUBLE_TYPE_HEATING_MAX = 110;
    /**
     * 加热异常,温度太低
     */
    public static final int TROUBLE_TYPE_HEATING_MIN = 111;
    /**
     * 称重异常
     */
    public static final int TROUBLE_TYPE_WEIGH = 6;
    /**
     * 湿度异常
     */
    public static final int TROUBLE_TYPE_HUMIDITY = 7;
    /**
     * "风压异常,
     * 请确认过滤网"
     */
    public static final int TROUBLE_TYPE_WIND_PRESSURE = 8;
    /**
     * 搅拌异常  stir 状态 = 5
     */
    public static final int TROUBLE_TYPE_STIR_ERROR = 9;

    public static float POUND = 2.205f;





    public static final String music_1_preparing = "music_1_preparing";
    public static final String music_2_press_open = "music_2_press_open";
    public static final String music_3_inlet_press_close = "music_3_inlet_press_close";
    public static final String music_4_weighing = "music_4_weighing";
    public static final String music_5_weigh_finish = "music_5_weigh_finish";

}
