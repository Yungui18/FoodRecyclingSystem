package com.hyeprion.foodrecyclingsystem.util.port;

public class PortConstants {
    /**
     * 查询所有，暂时有问题 不使用
     */
    public static String QUERY_STATUS = "010300200022C419";

    /**
     * 查询到除湿器
     *//*
    public static String QUERY_STATUS1 = "01030020000F0404";
    *//**
     * 查询紧急开关-加热温度
     *//*
    public static String QUERY_STATUS2 = "0103002F000EF5C7";
*/

    /**
     * 查询到FAN1 10
     */
    public static String QUERY_STATUS1 = "01030020000AC407";
    /**
     * 查询FAN2-毛重 10
     */
    static String QUERY_STATUS2 = "0103002A000AE405";
    /**
     * 查询净重-油温2（加热温度2） 10
     */
    static String QUERY_STATUS3 = "01030034000A8403";

    /**
     * 查询照明-风压 4
     */
//    public static String QUERY_STATUS4 = "0103003E000425C5";

    /**
     * 查询照明-判断下位机是否重启  12
     */
    public static String QUERY_STATUS4 = "0103003E000C2403";


    /**
     * 投入口打开开门
     */
    public static String INLET_OPEN = "010600000001480A";
    /**
     * 投入口机器上的开门按钮上锁，此时无法打开
     */
    public static String INLET_LOCK = "010600020000280A";
    /**
     * 投入口解锁，解锁后才可在实体机点击开门
     */
    public static String INLET_UNLOCK = "010600020001E9CA";
    /**
     * 投入口关闭 关门
     */
    public static String INLET_CLOSE = "010600000002080B";
    /**
     * 投入口停止
     */
    public static String INLET_STOP = "010600000003C9CB";
    /**
     * 投入口超时和低速电压设置 AA=超时时间+03~18：3V~24V慢速电压 在添加CRC
     * 例：010600010118D990  表示超时时间为1S，慢速电压为24V
     */
    public static String INLET_TIMEOUT_VOLTAGE_HEAD = "01060001";
    /**
     * 投入口开关电压设置 01060050AABBCCCC AA = 开门电压  BB = 关门电压，CCCC=CRC
     * 例：010600500102098A  表示开门电压1v，关门电压为2V
     */
    public static String INLET_OPEN_CLOSE_VOLTAGE_HEAD = "01060050";


    /**
     * 搅拌电机正转
     */
    public static String STIR_FORWARD = "010600030001B80A";
    /**
     * 搅拌电机反转
     */
    public static String STIR_REVERSES = "010600030002F80B";
    /**
     * 搅拌电机停止
     */
    public static String STIR_STOP = "01060003000339CB";
//    public static String STIR_error = "010600030005B9C9";


    /**
     * 灯1 绿色 常亮
     */
    public static String LED1_ON_GREEN = "0110000400020400010010A250";
    /**
     * 灯1 黄色 常亮
     */
    public static String LED1_ON_YELLOW = "0110000400020400010110A3C0";
    /**
     * 灯1 黄色 闪烁
     */
    public static String LED1_TOGGLE_YELLOW = "01100004000204000301100200";
    /**
     * 灯1 红色 常亮
     */
    public static String LED1_ON_RED = "0110000400020400010100A20C";
    /**
     * 灯1 常灭
     */
    public static String LED1_OFF = "01100004000204000201119200";

    /**
     * 绿色
     */
    public static int COLOR_GREEN = 10;
    /**
     * 黄色
     */
    public static int COLOR_YELLOW = 110;
    /**
     * 红色
     */
    public static int COLOR_RED = 100;


    /**
     * 灯2 绿色 常亮
     */
    public static String LED2_ON_GREEN = "01100006000204000100102389";
    /**
     * 灯2 黄色 常亮
     */
    public static String LED2_ON_YELLOW = "01100006000204000101102219";
    /**
     * 灯2 黄色 闪烁
     */
    public static String LED2_TOGGLE_YELLOW = "011000060002040003011083D9";
    /**
     * 灯2 红色 常亮
     */
    public static String LED2_ON_RED = "011000060002040001010023D5";
    /**
     * 灯2 常灭
     */
    public static String LED2_OFF = "011000060002040002011113D9";


    /**
     * 风扇1 手动1档运行
     */
    public static String  FAN1_MANUAL_GEAR_1 = "010600080011C804";
    /**
     * 风扇1 手动2档运行
     */
    public static String FAN1_MANUAL_GEAR_2 = "010600080021C810";
    /**
     * 风扇1 手动3档运行
     */
    public static String FAN1_MANUAL_GEAR_3 = "010600080031C9DC";
    /**
     * 风扇1 关闭，停止
     */
    public static String FAN1_STOP = "01060008000289C9";
    /**
     * 风扇1 自动
     */
    public static String FAN1_AUTOMATIC = "0106000800034809";

    /**
     * 风扇1 设置1挡电压、湿度 01060009+AAXX+CRC AA=电压3V~24V  XX=湿度 在添加CRC
     * 例：010600091832D21D 代表电压=24，湿度=50
     */
    public static String FAN1_SETTING_VOLTAGE_HUMIDITY_GEAR_HEAD_1 = "01060009";
    /**
     * 风扇1 设置2挡电压、湿度 0106000A+BBYY+CRC BB=电压3V~24V  YY=湿度 在添加CRC
     * 例：0106000A1832221D 代表电压=24，湿度=50
     */
    public static String FAN1_SETTING_VOLTAGE_HUMIDITY_GEAR_HEAD_2 = "0106000A";
    /**
     * 风扇1 设置3挡电压、湿度 0106000B+CCZZ+CRC CC=电压3V~24V  ZZ=湿度 在添加CRC
     * 例：0106000B183273DD 代表电压=24，湿度=50
     */
    public static String FAN1_SETTING_VOLTAGE_HUMIDITY_GEAR_HEAD_3 = "0106000B";


    /**
     * 风扇2 手动运行
     */
    public static String FAN2_MANUAL = "0106000C00018809";
    /**
     * 风扇2 关闭，停止
     */
    public static String FAN2_STOP = "0106000C0002C808";
    /**
     * 风扇2 自动
     */
    public static String FAN2_AUTOMATIC = "0106000C000309C8";


    /**
     * 灯 红色  闪烁
     */
//    public static String LED_RED_TOGGLE = "01100004000408000300FF0000000060A1";

    /**
     * 灯 黄色  闪烁
     */
//    public static String LED_YELLOW_TOGGLE = "01100004000408000300FF00FF00005091";
    /**
     * 灯 头部 拼接 R G B crc
     */
//    public static String LED_HEAD = "01100004000408";
    /**
     * 红色 255 0 0
     */
//    public static String COLOR_RED = "0100";
    /**
     * 绿色 0 255 0
     */
//    public static String COLOR_GREEN = "0010";
    /**
     * 蓝色 0 0 255
     */
//    public static String COLOR_BLUE = "0000000000FF";
    /**
     * 黄色 255 255 0
     */
//    public static String COLOR_YELLOW = "0110";
    /**
     * 青色（薄荷色） 0 255 255
     */
//    public static String COLOR_CYAN= "000000FF00FF";
    /**
     * 紫色 160 32 240
     */
//    public static String COLOR_PURPLE= "00A0002000F0";
    /**
     * 白色 255 255 255
     */
//    public static String COLOR_WHITE= "00FF00FF00FF";


    /**
     * heater1加热模式-手动
     */
    public static String HEATER1_MANUAL = "0110000D0003060001000000004ABA";

    /**
     * heater1加热模式-停止
     */
    public static String HEATER1_STOP = "0110000D0003060002000000000EBA";
    /**
     * heater1加热模式-自动 加热温度60 停止温度 80
     */
//    public static String HEATER1_AUTOMATIC = "";
    /**
     * heater1加热头部 拼接 加热温度 停止温度 crc
     * 例：0110000D0003060003003C0050F34A  代表加热温度=60 停止温度=80
     */
    public static String HEATER1_AUTOMATIC_HEAD = "0110000D0003060003";


    /**
     * heater2加热模式-手动
     */
    public static String HEATER2_MANUAL = "01100010000306000100000000DAD5";

    /**
     * heater2加热模式-停止
     */
    public static String HEATER2_STOP = "011000100003060002000000009ED5";
    /**
     * heater2加热模式-自动 加热温度60 停止温度 80
     */
//    public static String HEATER2_AUTOMATIC = "";
    /**
     * heater2加热头部 拼接 加热温度 停止温度 crc
     * 例：011000100003060003003C00506325  代表加热温度=60 停止温度=80
     */
    public static String HEATER2_AUTOMATIC_HEAD = "011000100003060003";


    /**
     * 除湿模式-手动
     */
    public static String DEHUMIDIFICATION_MANUAL = "011000130003060001000000002ADA";

    /**
     * 除湿模式-停止
     */
    public static String DEHUMIDIFICATION_STOP = "011000130003060002000000006EDA";
    /**
     * 除湿模式-自动 除湿开始湿度80 停止湿度 60
     */
//    public static String DEHUMIDIFICATION_AUTOMATIC = "0110000D000306000303200258326E";
    /**
     * 除湿模式 头部 拼接 加热温度 停止温度 crc
     * 例：0110001300030600030050003C531A  代表除湿开始湿度=80 停止湿度=60
     */
    public static String DEHUMIDIFICATION_AUTOMATIC_HEAD = "011000130003060003";


    /**
     * 称重模式 485置零
     */
    public static String WEIGHING_SETTING_ZERO_485 = "010600160001A9CE";

    /**
     * 称重模式 去皮,净重
     */
    public static String WEIGHING_SETTING_NETWEIGHT = "010600160002E9CF";


    /**
     * 照明 常亮
     */
    public static String LIGHTING_ON = "010600170001F80E";
    /**
     * 照明 常灭
     */
    public static String LIGHTING_OFF = "010600170002B80F";
    /**
     * 照明 自动
     */
    public static String LIGHTING_AUTO = "01060017000379CF";


    /**
     * 解除急停
     */
    public static String STOP_RELEASE = "010600180001C80D";
    /**
     * 未解除急停
     */
    public static String STOP_NON_RELEASE = "01060018000009CD";


    /**
     * 直连称重1 置零
     */
    public static String LOAD_CELL1_ZERO = "01060019000199CD";

    /**
     * 重量标定 直连1 loadcell1重量标定 01060019+01XX+CRC  XX-》DEC：以XXKG进行标定
     * 例：01060019 0114 5992 0114代表20KG  5992是CRC
     */
    public static String LOAD_CELL1_WEIGHT_CALIBRATION = "0106001901";

    /**
     * 直连称重2 置零
     */
    public static String LOAD_CELL2_ZERO = "0106001A000169CD";


    /**
     * 重量标定 直连2 loadcell2重量标定 0106001A+01XX+CRC  XX-》DEC：以XXKG进行标定
     * 例：0106001A 0114 A992 0114代表20KG  A992是CRC
     */
    public static String LOAD_CELL2_WEIGHT_CALIBRATION = "0106001A01";

    /**
     * 投入口开关电压设置 0106001BAABBCCCC AA = 开门电压  BB = 关门电压，CCCC=CRC
     * 例：0106001B0102098A  表示开门电压1v，关门电压为2V
     */
    public static String INLET_OPEN_CLOSE_VOLTAGE_HEAD2 = "0106001B";


    /**
     * lock1控制 解锁
     */
    public static String LOCK1_CONTROL_UNLOCK = "0106001C000189CC";
    /**
     * lock1控制 上锁
     */
    public static String LOCK1_CONTROL_LOCK = "0106001C0002C9CD";


    /**
     * lock2控制 解锁
     */
    public static String LOCK2_CONTROL_UNLOCK = "0106001D0001D80C";
    /**
     * lock2控制 上锁
     */
    public static String LOCK2_CONTROL_LOCK = "0106001D0002980D";


    /**
     * 关门高速时间设置  0106001E 0-600 -> HEX 最小单位为0.1 发送时需要乘以10后转换为16进制
     * 例：0106001E00326819 其中0032代表设置的关门时间为5S，乘以10转换成16进制后为32
     */
    public static String INLET_CLOSE_QUICK_TIME = "0106001E";

    /**
     * 设置下位机状态为1标志位，当查询到此寄存器地址为0时，代表下位机初始化或是重启
     */
    public static String JUDGE_RESTART = "01060049000199DC";


}
