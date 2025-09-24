package com.hyeprion.foodrecyclingsystem.util;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.bean.DeviceStatusResponse;
import com.hyeprion.foodrecyclingsystem.bean.PortStatus;
import com.hyeprion.foodrecyclingsystem.util.port.PortConstants;
import com.hyeprion.foodrecyclingsystem.util.port.PortControlUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

/**
 * 发送数据到服务器
 */
public class HTTPServerUtil {
    public static void sendHeartBeat() {
        sendHeartBeat(Constant.HTTP_PARAMS_IS_AUTO_ON);
    }

    public static void sendHeartBeat(int HTTP_PARAMS_IS_AUTO) {
        int stir = 1;
        int heater = 1;
        int fan = 1;
        int inlet = 1;
        if (PortControlUtil.getInstance().getPortStatus().getStirStatus() == 3 ||
                PortControlUtil.getInstance().getPortStatus().getStirStatus() == 5
        ) {
            stir = 0;
        }
        if (PortControlUtil.getInstance().getPortStatus().getHeater1() == 2) {
            heater = 0;
        }
        if (PortControlUtil.getInstance().getPortStatus().getFan1() == 0 ||
                PortControlUtil.getInstance().getPortStatus().getFan1() == 3) {
            fan = 0;
        }
        if (PortControlUtil.getInstance().getPortStatus().getInletStatus() == 5) {
            inlet = 0;
        }

        OkGo.<String>post(Constant.IP)
                /*.params(Constant.HTTP_PARAMS_TYPE, Constant.HTTP_PARAMS_TYPE_DEVICE_STATUS)
                .params(Constant.HTTP_PARAMS_SYSTEM_NO, "") //设备标号
                .params(Constant.HTTP_PARAMS_IS_ON, Constant.TRUE) //是否开启
                .params(Constant.HTTP_PARAMS_IS_AUTO, serialPortEvent.getIsAuto())//自动状态，手动状态
                .params(Constant.HTTP_PARAMS_STS_MOTOR, serialPortEvent.getStsMotor())//搅拌电机
                .params(Constant.HTTP_PARAMS_STS_WORM_LINE, serialPortEvent.getStsWormLine())//加热系统是否在工作
                .params(Constant.HTTP_PARAMS_STS_VENTILATOR, serialPortEvent.getStsVentilator())//fan
                .params(Constant.HTTP_PARAMS_STS_TEMPERATURE, serialPortEvent.getStsVentilator())//温度
                .params(Constant.HTTP_PARAMS_STS_HUMIDITY, serialPortEvent.getStsVentilator())//湿度
                .params(Constant.HTTP_PARAMS_STS_STOP, serialPortEvent.getStsVentilator())//急停
                .params(Constant.HTTP_PARAMS_STS_ERROR, serialPortEvent.getStsVentilator())//系统错误（之后定义）0x0000
                .params(Constant.HTTP_PARAMS_STS_DOOR, serialPortEvent.getStsDoor())//投入口*/

                .params(Constant.HTTP_PARAMS_TYPE, Constant.HTTP_PARAMS_TYPE_DEVICE_STATUS)
                .params(Constant.HTTP_PARAMS_SYSTEM_NO, MyApplication.deviceId) //设备标号
                .params(Constant.HTTP_PARAMS_IS_ON, Constant.TRUE) //是否开启
                .params(Constant.HTTP_PARAMS_IS_AUTO, HTTP_PARAMS_IS_AUTO)//自动状态，手动状态
                .params(Constant.HTTP_PARAMS_STS_MOTOR, stir)//搅拌电机
                .params(Constant.HTTP_PARAMS_STS_WORM_LINE, heater)//加热系统是否在工作
                .params(Constant.HTTP_PARAMS_STS_VENTILATOR, fan)//fan
                .params(Constant.HTTP_PARAMS_STS_DOOR, inlet)//fan


                .params(Constant.HTTP_PARAMS_STS_TIMER, "")
                .params(Constant.HTTP_PARAMS_ERROR_NOTI, "")
                .params(Constant.HTTP_PARAMS_CONTROLER_SETTINGS, "")
                .tag(ActivityUtil.getInstance().getActivity())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String res = response.body();
                        LogUtils.e(res);
                        try {
                            DeviceStatusResponse deviceStatusResponse = JSON.parseObject(res, DeviceStatusResponse.class);
                            if (deviceStatusResponse.isSuccess()) {
                                // TODO: 2022/11/17 上传成功
                            }
                        } catch (JSONException jsonException) {
                            ToastUtils.showShort(R.string.please_retry);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    public static void sendHeartBeat2() {


        int inlet = 1;
        int stir = 1;
        if (PortControlUtil.getInstance().getPortStatus().getStirStatus() == 3 ||
                PortControlUtil.getInstance().getPortStatus().getStirStatus() == 5
        ) {
            stir = 0;
        }

        String inletSensor = "0,0,0";
        // 开门传感器触发04；关门传感器被触发02；减速传感器被触发01；关门传感器和减速传感器同时触发03
        if (PortControlUtil.getInstance().getPortStatus().getInletSensorStatus() == 1) {
            inletSensor = "0,0,1";
        } else if (PortControlUtil.getInstance().getPortStatus().getInletSensorStatus() == 2) {
            inletSensor = "0,1,0";
            inletSensor = "0,1,1";
        } else if (PortControlUtil.getInstance().getPortStatus().getInletSensorStatus() == 4) {
            inletSensor = "1,0,0";
        }

        int fan = 0;
        // 风扇  01：手动（1档）；02：手动（2档）；03：手动（3档）；00：停止；11：自动（1档)；12：自动（2档)；13：自动（3档)；10：自动（停止中）；05：异常
        if (PortControlUtil.getInstance().getPortStatus().getFan1() == 1 ||
                PortControlUtil.getInstance().getPortStatus().getFan1() == 11) {
            fan = 1;
        } else if (PortControlUtil.getInstance().getPortStatus().getFan1() == 2 ||
                PortControlUtil.getInstance().getPortStatus().getFan1() == 12) {
            fan = 2;
        } else if (PortControlUtil.getInstance().getPortStatus().getFan1() == 3 ||
                PortControlUtil.getInstance().getPortStatus().getFan1() == 13) {
            fan = 3;
        }

        int led1 = 0;
        //  LED1   01：常亮；02：常灭；03：闪烁；04：呼吸
        if (PortControlUtil.getInstance().getPortStatus().getLed1RGB() == PortConstants.COLOR_GREEN) {
            led1 = 1;
        } else if (PortControlUtil.getInstance().getPortStatus().getLed1RGB() == PortConstants.COLOR_YELLOW) {
            led1 = 2;
        } else if (PortControlUtil.getInstance().getPortStatus().getLed1RGB() == PortConstants.COLOR_RED) {
            led1 = 3;
        }
        int led2 = 0;
        //  LED2   01：常亮；02：常灭；03：闪烁；04：呼吸
        if (PortControlUtil.getInstance().getPortStatus().getLed2RGB() == PortConstants.COLOR_GREEN) {
            led1 = 1;
        } else if (PortControlUtil.getInstance().getPortStatus().getLed2RGB() == PortConstants.COLOR_YELLOW) {
            led1 = 2;
        } else if (PortControlUtil.getInstance().getPortStatus().getLed2RGB() == PortConstants.COLOR_RED) {
            led1 = 3;
        }


        OkGo.<String>post(Constant.IP)
                /*.params(Constant.HTTP_PARAMS_TYPE, Constant.HTTP_PARAMS_TYPE_DEVICE_STATUS)
                .params(Constant.HTTP_PARAMS_SYSTEM_NO, "") //设备标号
                .params(Constant.HTTP_PARAMS_IS_ON, Constant.TRUE) //是否开启
                .params(Constant.HTTP_PARAMS_IS_AUTO, serialPortEvent.getIsAuto())//自动状态，手动状态
                .params(Constant.HTTP_PARAMS_STS_MOTOR, serialPortEvent.getStsMotor())//搅拌电机
                .params(Constant.HTTP_PARAMS_STS_WORM_LINE, serialPortEvent.getStsWormLine())//加热系统是否在工作
                .params(Constant.HTTP_PARAMS_STS_VENTILATOR, serialPortEvent.getStsVentilator())//fan
                .params(Constant.HTTP_PARAMS_STS_TEMPERATURE, serialPortEvent.getStsVentilator())//温度
                .params(Constant.HTTP_PARAMS_STS_HUMIDITY, serialPortEvent.getStsVentilator())//湿度
                .params(Constant.HTTP_PARAMS_STS_STOP, serialPortEvent.getStsVentilator())//急停
                .params(Constant.HTTP_PARAMS_STS_ERROR, serialPortEvent.getStsVentilator())//系统错误（之后定义）0x0000
                .params(Constant.HTTP_PARAMS_STS_DOOR, serialPortEvent.getStsDoor())//投入口*/

                .params(Constant.HTTP_PARAMS_TYPE, Constant.HTTP_PARAMS_TYPE_DEVICE_STATUS)
                .params(Constant.HTTP_PARAMS_SYSTEM_NO, MyApplication.deviceId) //设备标号
                .params(Constant.HTTP_PARAMS_IS_ON, Constant.TRUE) //是否开启
                .params(Constant.HTTP_PARAMS_IS_AUTO,
                        MyApplication.adminParameterBean.getDeviceMode() == 1 ? 1 : 0)//自动状态，手动状态
                .params(Constant.HTTP_PARAMS_STS_MOTOR, stir)//搅拌电机
                .params(Constant.HTTP_PARAMS_STS_MOTOR_ERROR,
                        (PortControlUtil.getInstance().getPortStatus().getStirStatus() != 5 &&
                                PortControlUtil.getInstance().getPortStatus().getStirStatus() != 4) ? 0 : 1)//搅拌电机错误 = 0, 1 (normal , error)
                .params(Constant.HTTP_PARAMS_IS_TEMP,
                        PortControlUtil.getInstance().getPortStatus().getTemperature())//空气温度
                .params(Constant.HTTP_PARAMS_IS_WARM_FRONT,
                        PortControlUtil.getInstance().getPortStatus().getHeater1() == 1 ||
                                PortControlUtil.getInstance().getPortStatus().getHeater1() == 11 ? 1 : 0)//加热1
                .params(Constant.HTTP_PARAMS_IS_WARM_FRONT_TEMP,
                        PortControlUtil.getInstance().getPortStatus().getHeaterTemperature1())//加热前温度(加热1)
                .params(Constant.HTTP_PARAMS_IS_WARM_BACK,
                        PortControlUtil.getInstance().getPortStatus().getHeater2() == 1 ||
                                PortControlUtil.getInstance().getPortStatus().getHeater2() == 11 ? 1 : 0)//加热2
                .params(Constant.HTTP_PARAMS_IS_WARM_BACK_TEMP,
                        PortControlUtil.getInstance().getPortStatus().getHeaterTemperature2())//加热后温度(加热2)
                .params(Constant.HTTP_PARAMS_IS_DRYER,
                        PortControlUtil.getInstance().getPortStatus().getStirStatus() == 6 ? 1 : 0)// 搅拌轴错误 = 0, 1 (normal , error)
                .params(Constant.HTTP_PARAMS_HUMIDITY,
                        PortControlUtil.getInstance().getPortStatus().getHumidity())//空气湿度 =   0~100 %
                .params(Constant.HTTP_PARAMS_IS_LIGHT,
                        PortControlUtil.getInstance().getPortStatus().getLighting() == 1 ? 1 : 0)//照明 =   0, 1 (off , on)
                .params(Constant.HTTP_PARAMS_IS_DOOR_SENSORS, inletSensor)//投入口感应器
                .params(Constant.HTTP_PARAMS_IS_DOOR_BUTTON,
                        PortControlUtil.getInstance().getPortStatus().getOpenDoorBtn() == 0 ? 0 : 1)//投入口按钮 = 0, 1 (off , on)
                .params(Constant.HTTP_PARAMS_IS_DETECT_DOOR,
                        PortControlUtil.getInstance().getPortStatus().getObserveDoorStatus() == 0? 0 : 1)//观察口 = 0, 1 (off , on)
                .params(Constant.HTTP_PARAMS_IS_OUT_DOOR,
                        PortControlUtil.getInstance().getPortStatus().getOutletStatus() == 0? 0 : 1)//排出口 = 0, 1 (off , on)
                .params(Constant.HTTP_PARAMS_IS_FAN, fan)//排气扇 =  0, 1, 2, 3 (off, 1, 2, 3)
                .params(Constant.HTTP_PARAMS_IS_LED_RIGHT, led1)//LED右 =   0, 1, 2, 3 (off, G, Y, R)
                .params(Constant.HTTP_PARAMS_IS_LED_LEFT,led2)//LED左 =   0, 1, 2, 3 (off, G, Y, R)
//                .params(Constant.HTTP_PARAMS_PA, PortControlUtil.getInstance().getPortStatus().getWindPressure())//风压
                .params(Constant.HTTP_PARAMS_PA, DecimalFormatUtil.DecimalFormatInt(PortControlUtil.getInstance().getPortStatus().getChooseUseWeighing()))//风压的值替换为保留整数位的当前重量
                .params(Constant.HTTP_PARAMS_ERROR, PortControlUtil.troubleTypeBean.getTroubleType())//fan
                .tag(ActivityUtil.getInstance().getActivity())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String res = response.body();
                        LogUtils.e(res);
                        try {
                            DeviceStatusResponse deviceStatusResponse = JSON.parseObject(res, DeviceStatusResponse.class);
                            if (deviceStatusResponse.isSuccess()) {
                                // TODO: 2022/11/17 上传成功
                            }
                        } catch (JSONException jsonException) {
                            ToastUtils.showShort(R.string.please_retry);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }
    /**
     * 往服务器发送故障异常信息
     * @param errorCode 故障代码（对应Constant中的TROUBLE_TYPE_*）
     * @param errorMessage 故障描述信息
     * @param ledValue LED状态值
     */
    public static void sendErrorInfo(int errorCode, String errorMessage, int ledValue, String systemNo) {
        if (TextUtils.isEmpty(systemNo)) {
            LogUtils.e("设备编号为空，无法上报故障");
            return;
        }
        // 构建GET请求（按URL格式要求）
        OkGo.<String>get(Constant.IP)
                .params("type", "5") // 固定type=5
                .params("systemNo", systemNo)
                .params("errorCode", errorCode)
                .params("errorMessage", errorMessage)
                .params("led", ledValue)
                .tag("ErrorReport")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogUtils.e("故障上报成功：" + response.body());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        LogUtils.e("故障上报失败：" + response.message());

                    }
                });
    }

    /**
     * 根据故障类型获取故障代码和描述
     * @param troubleType 故障类型（如Constant.TROUBLE_TYPE_STIR）
     * @return 数组[errorCode, errorMessage]
     */
    public static String[] getErrorInfoByType(int troubleType) {
        switch (troubleType) {
            case Constant.TROUBLE_TYPE_STIR:
                return new String[]{"1", "搅拌电机异常"};
            case Constant.TROUBLE_TYPE_OUTLET:
                return new String[]{"2", "排料口未关闭"};
            case Constant.TROUBLE_TYPE_OBSERVE:
                return new String[]{"3", "观察口未关闭"};
            case Constant.TROUBLE_TYPE_INLET:
                return new String[]{"4", "投入口异常"};
            case Constant.TROUBLE_TYPE_WEIGH:
                return new String[]{"6", "称重异常"};
            case Constant.TROUBLE_TYPE_HUMIDITY:
                return new String[]{"7", "湿度异常"};
            case Constant.TROUBLE_TYPE_HEATING_MAX:
                return new String[]{"110","加热异常,温度太高"};
            case Constant.TROUBLE_TYPE_HEATING_MIN:
                return new String[]{"111","加热异常,温度太低"};
            case Constant.TROUBLE_TYPE_WIND_PRESSURE:
                return new String[]{"8","风压异常,请确认过滤网"};
            case Constant.TROUBLE_TYPE_STIR_ERROR:
                return new String[]{"9","搅拌异常"};
            // 其他故障类型依次补充
            default:
                return new String[]{"0", "异常解除"};
        }
    }

    /**
     * 获取当前LED状态值（映射为1-3：绿、黄、红，0为默认）
     */
    public static int getCurrentLedValue() {
        PortStatus portStatus = PortControlUtil.getInstance().getPortStatus();
        if (portStatus == null) return 0;
        // 假设LED1为主状态灯
        int ledRgb = portStatus.getLed1RGB();
        if (ledRgb == PortConstants.COLOR_GREEN) {
            return 1; // 绿色
        } else if (ledRgb == PortConstants.COLOR_YELLOW) {
            return 2; // 黄色
        } else if (ledRgb == PortConstants.COLOR_RED) {
            return 3; // 红色
        } else {
            return 0; // 未定义
        }
}
}
