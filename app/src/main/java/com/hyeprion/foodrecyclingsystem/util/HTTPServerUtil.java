package com.hyeprion.foodrecyclingsystem.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.bean.AdminParameterBean;
import com.hyeprion.foodrecyclingsystem.bean.DeviceStatusResponse;
import com.hyeprion.foodrecyclingsystem.bean.PortStatus;
import com.hyeprion.foodrecyclingsystem.bean.TroubleTypeBean;
import com.hyeprion.foodrecyclingsystem.util.port.PortConstants;
import com.hyeprion.foodrecyclingsystem.util.port.PortControlUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

/**
 * 发送数据到服务器
 */
public class HTTPServerUtil {
    // 心跳请求状态标记：避免请求堆积
    private static boolean isHeartBeatSending = false;
    private static boolean isHeartBeat2Sending = false;
    // 主线程Handler，用于UI线程操作
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    public static void sendHeartBeat() {
        sendHeartBeat(Constant.HTTP_PARAMS_IS_AUTO_ON);
    }

    public static void sendHeartBeat(int HTTP_PARAMS_IS_AUTO) {
        // 避免重复发送
        if (isHeartBeatSending) {
            LogUtils.e("sendHeartBeat: 已有请求在执行，跳过本次");
            return;
        }

        try {
            isHeartBeatSending = true;

            // 1. 核心对象判空
            PortControlUtil portControl = PortControlUtil.getInstance();
            if (portControl == null) {
                LogUtils.e("sendHeartBeat: PortControlUtil 未初始化");
                return;
            }
            PortStatus portStatus = portControl.getPortStatus();
            if (portStatus == null) {
                LogUtils.e("sendHeartBeat: portStatus 为null");
                return;
            }
            MyApplication app = MyApplication.getInstance();
            if (app == null || TextUtils.isEmpty(app.deviceId)) {
                LogUtils.e("sendHeartBeat: deviceId 未初始化");
                return;
            }

            // 2. 变量计算（带空指针防护）
            int stir = 1;
            if (portStatus.getStirStatus() == 3 || portStatus.getStirStatus() == 5) {
                stir = 0;
            }

            int heater = 1;
            if (portStatus.getHeater1() == 2) {
                heater = 0;
            }

            int fan = 1;
            if (portStatus.getFan1() == 0 || portStatus.getFan1() == 3) {
                fan = 0;
            }

            int inlet = 1;
            if (portStatus.getInletStatus() == 5) {
                inlet = 0;
            }

            // 3. 发送请求（用Application作为tag，避免内存泄漏）
            OkGo.<String>post(Constant.IP)
                    .params(Constant.HTTP_PARAMS_TYPE, Constant.HTTP_PARAMS_TYPE_DEVICE_STATUS)
                    .params(Constant.HTTP_PARAMS_SYSTEM_NO, app.deviceId)
                    .params(Constant.HTTP_PARAMS_IS_ON, Constant.TRUE)
                    .params(Constant.HTTP_PARAMS_IS_AUTO, HTTP_PARAMS_IS_AUTO)
                    .params(Constant.HTTP_PARAMS_STS_MOTOR, stir)
                    .params(Constant.HTTP_PARAMS_STS_WORM_LINE, heater)
                    .params(Constant.HTTP_PARAMS_STS_VENTILATOR, fan)
                    .params(Constant.HTTP_PARAMS_STS_DOOR, inlet)
                    .params(Constant.HTTP_PARAMS_STS_TIMER, "")
                    .params(Constant.HTTP_PARAMS_ERROR_NOTI, "")
                    .params(Constant.HTTP_PARAMS_CONTROLER_SETTINGS, "")
                    .tag(app) // 用Application作为tag，避免Activity泄漏
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            try {
                                String res = response.body();
                                LogUtils.e("sendHeartBeat 响应: " + res);
                                if (!TextUtils.isEmpty(res)) {
                                    DeviceStatusResponse deviceStatusResponse = JSON.parseObject(res, DeviceStatusResponse.class);
                                    if (deviceStatusResponse != null && deviceStatusResponse.isSuccess()) {
                                        LogUtils.e("sendHeartBeat 上传成功");
                                    }
                                }
                            } catch (JSONException e) {
                                LogUtils.e("sendHeartBeat JSON解析失败: " + e.getMessage());
                                showToastOnUiThread(R.string.please_retry);
                            } catch (Exception e) {
                                LogUtils.e("sendHeartBeat 回调异常: " + e.getMessage());
                            } finally {
                                isHeartBeatSending = false;
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            LogUtils.e("sendHeartBeat 请求失败: " + (response == null ? "未知错误" : response.message()));
                            isHeartBeatSending = false;
                        }
                    });

        } catch (Exception e) {
            LogUtils.e("sendHeartBeat 执行异常: " + e.getMessage());
            e.printStackTrace();
            isHeartBeatSending = false;
        }
    }

    public static void sendHeartBeat2() {
        // 避免重复发送
        if (isHeartBeat2Sending) {
            LogUtils.e("sendHeartBeat2: 已有请求在执行，跳过本次");
            return;
        }

        try {
            isHeartBeat2Sending = true;

            // 1. 核心对象判空（逐层校验，避免空指针）
            PortControlUtil portControl = PortControlUtil.getInstance();
            if (portControl == null) {
                LogUtils.e("sendHeartBeat2: PortControlUtil 未初始化");
                return;
            }
            PortStatus portStatus = portControl.getPortStatus();
            if (portStatus == null) {
                LogUtils.e("sendHeartBeat2: portStatus 为null");
                return;
            }
            MyApplication app = MyApplication.getInstance();
            if (app == null) {
                LogUtils.e("sendHeartBeat2: MyApplication 未初始化");
                return;
            }
            AdminParameterBean adminBean = app.adminParameterBean;
            if (adminBean == null) {
                LogUtils.e("sendHeartBeat2: adminParameterBean 为null");
                return;
            }
            if (TextUtils.isEmpty(app.deviceId)) {
                LogUtils.e("sendHeartBeat2: deviceId 未初始化");
                return;
            }

            // 2. 变量计算（基于非空对象）
            int stir = 1;
            if (portStatus.getStirStatus() == 3 || portStatus.getStirStatus() == 5) {
                stir = 0;
            }

            String inletSensor = "0,0,0";
            int inletSensorStatus = portStatus.getInletSensorStatus();
            if (inletSensorStatus == 1) {
                inletSensor = "0,0,1";
            } else if (inletSensorStatus == 2) {
                inletSensor = "0,1,0";
            } else if (inletSensorStatus == 3) {
                inletSensor = "0,1,1";
            } else if (inletSensorStatus == 4) {
                inletSensor = "1,0,0";
            }

            int fan = 0;
            int fan1Status = portStatus.getFan1();
            if (fan1Status == 0 || fan1Status == 10) {
                fan = 0;
            } else if (fan1Status == 1 || fan1Status == 11) {
                fan = 1;
            } else if (fan1Status == 2 || fan1Status == 12) {
                fan = 2;
            } else if (fan1Status == 3 || fan1Status == 13) {
                fan = 3;
            }

            int led1 = 0;
            int led1Rgb = portStatus.getLed1RGB();
            if (led1Rgb == PortConstants.COLOR_GREEN) {
                led1 = 1;
            } else if (led1Rgb == PortConstants.COLOR_YELLOW) {
                led1 = 2;
            } else if (led1Rgb == PortConstants.COLOR_RED) {
                led1 = 3;
            }

            int led2 = 0;
            int led2Rgb = portStatus.getLed2RGB();
            if (led2Rgb == PortConstants.COLOR_GREEN) {
                led2 = 1;
            } else if (led2Rgb == PortConstants.COLOR_YELLOW) {
                led2 = 2;
            } else if (led2Rgb == PortConstants.COLOR_RED) {
                led2 = 3;
            }

            // 搅拌电机错误状态
            int stirError = (portStatus.getStirStatus() == 4 || portStatus.getStirStatus() == 5 || portStatus.getStirStatus() == 6) ? 1 : 0;

            // 加热1状态
            int heater1Status = (portStatus.getHeater1() == 1 || portStatus.getHeater1() == 11) ? 1 : 0;

            // 加热2状态
            int heater2Status = (portStatus.getHeater2() == 1 || portStatus.getHeater2() == 11) ? 1 : 0;

            // 搅拌轴错误状态
            int dryerError = (portStatus.getStirStatus() == 6) ? 1 : 0;

            // 照明状态
            int lightingStatus = (portStatus.getLighting() == 1) ? 1 : 0;

            // 投入口按钮状态
            int doorBtnStatus = (portStatus.getOpenDoorBtn() == 0) ? 0 : 1;

            // 观察口状态
            int detectDoorStatus = (portStatus.getObserveDoorStatus() == 0) ? 0 : 1;

            // 排出口状态
            int outDoorStatus = (portStatus.getOutletStatus() == 0) ? 0 : 1;

            // 风压（重量）格式化（修复：float类型匹配）
            String paValue = "0";
            float weighing = portStatus.getChooseUseWeighing(); // 改为float，匹配getChooseUseWeighing()返回值
            paValue = String.valueOf(DecimalFormatUtil.DecimalFormatInt(weighing)); // 现在参数类型匹配

            // 故障类型（避免null）
            int troubleType = 0;
            TroubleTypeBean troubleBean = PortControlUtil.troubleTypeBean;
            if (troubleBean != null) {
                troubleType = troubleBean.getTroubleType();
            }

            // 3. 发送请求（用Application作为tag）
            OkGo.<String>post(Constant.IP)
                    .params(Constant.HTTP_PARAMS_TYPE, Constant.HTTP_PARAMS_TYPE_DEVICE_STATUS)
                    .params(Constant.HTTP_PARAMS_SYSTEM_NO, app.deviceId)
                    .params(Constant.HTTP_PARAMS_IS_ON, Constant.TRUE)
                    .params(Constant.HTTP_PARAMS_IS_AUTO, adminBean.getDeviceMode() == 1 ? 1 : 0)
                    .params(Constant.HTTP_PARAMS_STS_MOTOR, stir)
                    .params(Constant.HTTP_PARAMS_STS_MOTOR_ERROR, stirError)
                    .params(Constant.HTTP_PARAMS_IS_TEMP, portStatus.getTemperature())
                    .params(Constant.HTTP_PARAMS_IS_WARM_FRONT, heater1Status)
                    .params(Constant.HTTP_PARAMS_IS_WARM_FRONT_TEMP, portStatus.getHeaterTemperature1())
                    .params(Constant.HTTP_PARAMS_IS_WARM_BACK, heater2Status)
                    .params(Constant.HTTP_PARAMS_IS_WARM_BACK_TEMP, portStatus.getHeaterTemperature2())
                    .params(Constant.HTTP_PARAMS_IS_DRYER, dryerError)
                    .params(Constant.HTTP_PARAMS_HUMIDITY, portStatus.getHumidity())
                    .params(Constant.HTTP_PARAMS_IS_LIGHT, lightingStatus)
                    .params(Constant.HTTP_PARAMS_IS_DOOR_SENSORS, inletSensor)
                    .params(Constant.HTTP_PARAMS_IS_DOOR_BUTTON, doorBtnStatus)
                    .params(Constant.HTTP_PARAMS_IS_DETECT_DOOR, detectDoorStatus)
                    .params(Constant.HTTP_PARAMS_IS_OUT_DOOR, outDoorStatus)
                    .params(Constant.HTTP_PARAMS_IS_FAN, fan)
                    .params(Constant.HTTP_PARAMS_IS_LED_RIGHT, led1)
                    .params(Constant.HTTP_PARAMS_IS_LED_LEFT, led2)
                    .params(Constant.HTTP_PARAMS_PA, paValue)
                    .params(Constant.HTTP_PARAMS_ERROR, troubleType)
                    .tag(app) // 用Application作为tag，避免内存泄漏
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            try {
                                String res = response.body();
                                LogUtils.e("sendHeartBeat2 响应: " + res);
                                if (!TextUtils.isEmpty(res)) {
                                    DeviceStatusResponse deviceStatusResponse = JSON.parseObject(res, DeviceStatusResponse.class);
                                    if (deviceStatusResponse != null && deviceStatusResponse.isSuccess()) {
                                        LogUtils.e("sendHeartBeat2 上传成功");
                                    }
                                }
                            } catch (JSONException e) {
                                LogUtils.e("sendHeartBeat2 JSON解析失败: " + e.getMessage());
                                showToastOnUiThread(R.string.please_retry);
                            } catch (Exception e) {
                                LogUtils.e("sendHeartBeat2 回调异常: " + e.getMessage());
                            } finally {
                                isHeartBeat2Sending = false;
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            LogUtils.e("sendHeartBeat2 请求失败: " + (response == null ? "未知错误" : response.message()));
                            isHeartBeat2Sending = false;
                        }
                    });

        } catch (Exception e) {
            LogUtils.e("sendHeartBeat2 执行异常: " + e.getMessage());
            e.printStackTrace();
            isHeartBeat2Sending = false;
        }
    }

    /**
     * 往服务器发送故障异常信息
     *
     * @param errorCode    故障代码（对应Constant中的TROUBLE_TYPE_*）
     * @param errorMessage 故障描述信息
     * @param ledValue     LED状态值
     */
    public static void sendErrorInfo(int errorCode, String errorMessage, int ledValue, String systemNo) {
        try {
            MyApplication app = MyApplication.getInstance();
            if (app == null) {
                LogUtils.e("sendErrorInfo: MyApplication 未初始化");
                return;
            }
            if (TextUtils.isEmpty(systemNo)) {
                LogUtils.e("sendErrorInfo: systemNo 为空");
                return;
            }

            OkGo.<String>post(Constant.IP)
                    .params("type", Constant.HTTP_PARAMS_TYPE_ERROR_REPORT)
                    .params(Constant.HTTP_PARAMS_SYSTEM_NO, systemNo)
                    .params("errorCode", errorCode)
                    .params(Constant.HTTP_PARAMS_ERROR_MESSAGE, errorMessage)
                    .params(Constant.HTTP_PARAMS_LED, ledValue)
                    .tag(app)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            LogUtils.e("错误信息发送成功: " + response.body());
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            LogUtils.e("错误信息发送失败: " + (response == null ? "未知错误" : response.message()));
                        }
                    });
        } catch (Exception e) {
            LogUtils.e("sendErrorInfo 异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 根据故障类型获取故障代码和描述
     *
     * @param troubleType 故障类型（如Constant.TROUBLE_TYPE_STIR）
     * @return 数组[errorCode, errorMessage]
     */
    public static String[] getErrorInfoByType(int troubleType) {
        try {
            Context context = MyApplication.getInstance();
            if (context == null) {
                LogUtils.e("getErrorInfoByType: 上下文为null");
                return new String[]{"", ""};
            }

            String errorCode = String.valueOf(troubleType);
            String errorMessage = "";

            switch (troubleType) {
                case Constant.TROUBLE_TYPE_STIR:
                    errorMessage = context.getString(R.string.trouble_info_stir);
                    break;
                case Constant.TROUBLE_TYPE_OUTLET:
                    errorMessage = context.getString(R.string.trouble_info_outlet);
                    break;
                case Constant.TROUBLE_TYPE_OBSERVE:
                    errorMessage = context.getString(R.string.trouble_info_observe);
                    break;
                case Constant.TROUBLE_TYPE_INLET:
                    errorMessage = context.getString(R.string.trouble_info_inlet);
                    break;
                case Constant.TROUBLE_TYPE_HEATING_MAX:
                case Constant.TROUBLE_TYPE_HEATING_MIN:
                    errorMessage = context.getString(R.string.trouble_info_heating);
                    break;
                case Constant.TROUBLE_TYPE_WEIGH:
                    errorMessage = context.getString(R.string.trouble_info_weigh);
                    break;
                case Constant.TROUBLE_TYPE_HUMIDITY:
                    errorMessage = context.getString(R.string.trouble_info_humidity);
                    break;
                case Constant.TROUBLE_TYPE_WIND_PRESSURE:
                    errorMessage = context.getString(R.string.trouble_info_wind_pressure);
                    break;
                case Constant.TROUBLE_TYPE_STIR_ERROR:
                    errorMessage = context.getString(R.string.trouble_info_stir_error);
                    break;
                default:
                    errorMessage = context.getString(R.string.have_trouble);
            }
            return new String[]{errorCode, errorMessage};
        } catch (Exception e) {
            LogUtils.e("getErrorInfoByType 异常: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    /**
     * 获取当前LED状态值（映射为1-3：绿、黄、红，0为默认）
     */
    public static int getCurrentLedValue() {
        try {
            PortControlUtil portControl = PortControlUtil.getInstance();
            if (portControl == null) {
                LogUtils.e("getCurrentLedValue: PortControlUtil 未初始化");
                return 0;
            }
            PortStatus portStatus = portControl.getPortStatus();
            if (portStatus == null) {
                LogUtils.e("getCurrentLedValue: portStatus 为null");
                return 0;
            }

            int ledRgb = portStatus.getLed1RGB();
            if (ledRgb == PortConstants.COLOR_GREEN) {
                return 1;
            } else if (ledRgb == PortConstants.COLOR_YELLOW) {
                return 2;
            } else if (ledRgb == PortConstants.COLOR_RED) {
                return 3;
            } else {
                return 0;
            }
        } catch (Exception e) {
            LogUtils.e("getCurrentLedValue 异常: " + e.getMessage());
            return 0;
        }
    }

    /**
     * 确保Toast在UI线程显示（修复：用主线程Handler）
     */
    private static void showToastOnUiThread(int resId) {
        mainHandler.post(() -> ToastUtils.showShort(resId));
    }
}
