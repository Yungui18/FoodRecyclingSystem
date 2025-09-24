package com.hyeprion.foodrecyclingsystem.util;

import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.bean.TimesInlet;
import com.hyeprion.foodrecyclingsystem.bean.TroubleLog;
import com.hyeprion.foodrecyclingsystem.greendao.db.TimesInletDao;
import com.hyeprion.foodrecyclingsystem.greendao.db.TroubleLogDao;
import com.hyeprion.foodrecyclingsystem.util.port.PortControlUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class GreenDaoUtil {
    private static GreenDaoUtil greenDaoUtil;

    // 单例模式中获取唯一的GreenDaoUtil实例
    public static synchronized GreenDaoUtil getInstance() {
        if (null == greenDaoUtil) {
            greenDaoUtil = new GreenDaoUtil();
        }
        return greenDaoUtil;
    }

    /**
     * 插入本次投入重量记录
     *
     * @param timesInletWeight 本次投入重量
     * @param loginType        登录方式: 1:密码登录{@link com.hyeprion.foodrecyclingsystem.R.string#login_type_pw}
     *                         1:RFID登录{@link com.hyeprion.foodrecyclingsystem.R.string#login_type_RFID}
     *                         1:无{@link com.hyeprion.foodrecyclingsystem.R.string#login_type_none}
     */
    public void insertTimesInlet(float timesInletWeight, String loginType) {
        TimesInlet timesInlet = new TimesInlet();
        timesInletWeight = DecimalFormatUtil.DecimalFormatTwo(timesInletWeight);
        timesInlet.setTimesInlet(timesInletWeight);
        timesInlet.setTime(TimeUtil.getDateSToString());
        timesInlet.setInletId(MyApplication.loginId);
        timesInlet.setLoginType(loginType);
        timesInlet.setTimeStamp(TimeUtil.getDate());
        MyApplication.getInstance().getDaoSession().getTimesInletDao().insert(timesInlet);
    }

    /**
     * 获取24小时内的投入记录
     *
     * @return
     */
    public List<TimesInlet> getTimesInlets24H() {
        List<TimesInlet> timesInlets = new ArrayList<>();
        timesInlets = MyApplication.getInstance().getDaoSession().getTimesInletDao().queryBuilder().where(
                TimesInletDao.Properties.TimeStamp.gt(TimeUtil.getDate() - (24 * 60 * 60 * Constant.second)))
                .list();
        return timesInlets;
    }

    /**
     * 获取N天内的的投入记录
     * @param days 获取的天数
     * @return
     */
    public List<TimesInlet> getTimesInletsByDays(int days) {
        List<TimesInlet> timesInlets = new ArrayList<>();
        timesInlets = MyApplication.getInstance().getDaoSession().getTimesInletDao().queryBuilder().where(
                TimesInletDao.Properties.TimeStamp.gt(TimeUtil.getDate() - (days * 24 * 60 * 60 * Constant.second)))
                .list();
        return timesInlets;
    }

    /**
     * 获取当天0点至查询时间的投入记录
     *
     * @return
     */
    public List<TimesInlet> getTimesInletsToday() {
        List<TimesInlet> timesInlets = new ArrayList<>();
        timesInlets = MyApplication.getInstance().getDaoSession().getTimesInletDao().queryBuilder()
                .where(TimesInletDao.Properties.TimeStamp.gt(TimeUtil.getWeeOfToday()))
//                .orderDesc(TimesInletDao.Properties.Id)
                .list();
        return timesInlets;
    }

    /**
     * 删除所有投入记录
     */
    public void deleteAllInletLog(){
        MyApplication.getInstance().getDaoSession().getTimesInletDao().deleteAll();
        MyApplication.getInstance().getDaoSession().clear();
    }

    /**
     * 获取当月第一天0点至查询时间的投入记录
     *
     * @return
     */
    public List<TimesInlet> getTimesInletsMonth() {
        List<TimesInlet> timesInlets = new ArrayList<>();
        timesInlets = MyApplication.getInstance().getDaoSession().getTimesInletDao().queryBuilder()
                .where(TimesInletDao.Properties.TimeStamp.gt(TimeUtil.getWeeOfMonth()))
//                .orderDesc(TimesInletDao.Properties.Id)
                .list();
        return timesInlets;
    }

    /**
     * 获取24小时投入的重量
     *
     * @return String
     */
    public String get24HTotalInletWeight() {
        List<TimesInlet> timesInlets = getTimesInlets24H();
        float totalInletWeight = 0f;
        if (timesInlets == null || timesInlets.size() == 0) {
            return "0";
        }

        for (TimesInlet timesInlet : timesInlets) {
            totalInletWeight += timesInlet.getTimesInlet();
        }
        totalInletWeight = DecimalFormatUtil.DecimalFormatTwo(totalInletWeight);
        return String.valueOf(totalInletWeight);
    }

    /**
     * 获取当天0点至查询时间的重量
     *
     * @return String
     */
    public String getTodayInletWeight() {
        List<TimesInlet> timesInlets = getTimesInletsToday();
        float todayInletWeight = 0f;
        if (timesInlets == null || timesInlets.size() == 0) {
            return "0";
        }

        for (TimesInlet timesInlet : timesInlets) {
            todayInletWeight += timesInlet.getTimesInlet();
        }
        todayInletWeight = DecimalFormatUtil.DecimalFormatTwo(todayInletWeight);
        return String.valueOf(todayInletWeight);
    }

    /**
     * 获取当月第一天0点至查询时间的重量
     *
     * @return String
     */
    public String getMonthInletWeight() {
        List<TimesInlet> timesInlets = getTimesInletsMonth();
        float monthInletWeight = 0f;
        if (timesInlets == null || timesInlets.size() == 0) {
            return "0";
        }

        for (TimesInlet timesInlet : timesInlets) {
            monthInletWeight += timesInlet.getTimesInlet();
        }
        monthInletWeight = DecimalFormatUtil.DecimalFormatTwo(monthInletWeight);
        return String.valueOf(monthInletWeight);
    }

    /**
     * 插入故障信息到故障表
     *
     * @return String
     */
    public void insertTrouble(int type) {
        TroubleLog troubleLog = new TroubleLog();
        troubleLog.setTime(TimeUtil.getDateSToString());
        switch (type) {
            case 0:
                troubleLog.setTrouble(ActivityUtil.getInstance().getActivity().getString(R.string.trouble_remove));
                troubleLog.setTroubleType(Constant.TROUBLE_TYPE_REMOVE);
                break;
            case 1:
                troubleLog.setTrouble(ActivityUtil.getInstance().getActivity().getString(R.string.trouble_info_stir));
                troubleLog.setTroubleType(Constant.TROUBLE_TYPE_STIR);
                break;
            case 2:
                troubleLog.setTrouble(ActivityUtil.getInstance().getActivity().getString(R.string.trouble_info_outlet));
                troubleLog.setTroubleType(Constant.TROUBLE_TYPE_OUTLET);
                break;
            case 3:
//                troubleLog.setTrouble(ActivityUtil.getInstance().getActivity().getString(R.string.trouble_info_observe));
                troubleLog.setTrouble(ActivityUtil.getInstance().getActivity().getString(R.string.trouble_info_safety_door));
                troubleLog.setTroubleType(Constant.TROUBLE_TYPE_OBSERVE);
                break;
            case 4:
                troubleLog.setTrouble(ActivityUtil.getInstance().getActivity().getString(R.string.trouble_info_inlet));
                troubleLog.setTroubleType(Constant.TROUBLE_TYPE_INLET);
                break;
            case 110:
                troubleLog.setTrouble(ActivityUtil.getInstance().getActivity().getString(R.string.trouble_info_heating) + ":F" +
                        PortControlUtil.getInstance().getPortStatus().getHeaterTemperature1()
                        + " B" + PortControlUtil.getInstance().getPortStatus().getHeaterTemperature2());
                troubleLog.setTroubleType(Constant.TROUBLE_TYPE_HEATING_MAX);
                break;
            case 111:
                troubleLog.setTrouble(ActivityUtil.getInstance().getActivity().getString(R.string.trouble_info_heating) + ":F" +
                        PortControlUtil.getInstance().getPortStatus().getHeaterTemperature1()
                        + " B" + PortControlUtil.getInstance().getPortStatus().getHeaterTemperature2());
                troubleLog.setTroubleType(Constant.TROUBLE_TYPE_HEATING_MIN);
                break;
            case 6:
                troubleLog.setTrouble(ActivityUtil.getInstance().getActivity().getString(R.string.trouble_info_weigh));
                troubleLog.setTroubleType(Constant.TROUBLE_TYPE_WEIGH);
                break;
            case 7:
                troubleLog.setTrouble(ActivityUtil.getInstance().getActivity().getString(R.string.trouble_info_humidity));
                troubleLog.setTroubleType(Constant.TROUBLE_TYPE_HUMIDITY);
                break;
            case 8:
                troubleLog.setTrouble(ActivityUtil.getInstance().getActivity().getString(R.string.trouble_info_wind_pressure));
                troubleLog.setTroubleType(Constant.TROUBLE_TYPE_WIND_PRESSURE);
                break;
            case 9:
                troubleLog.setTrouble(ActivityUtil.getInstance().getActivity().getString(R.string.trouble_info_stir_error));
                troubleLog.setTroubleType(Constant.TROUBLE_TYPE_STIR_ERROR);
                break;
        }
//        if (type != 0) {
        EventBus.getDefault().post(troubleLog);
//        }
        MyApplication.getInstance().getDaoSession().getTroubleLogDao().insert(troubleLog);
        // 1. 获取故障代码和描述
        String[] errorInfo = HTTPServerUtil.getErrorInfoByType(troubleLog.getTroubleType());
        int errorCode = Integer.parseInt(errorInfo[0]);
        String errorMessage = errorInfo[1];
        // 2. 获取LED值
        int ledValue = HTTPServerUtil.getCurrentLedValue();
        // 3. 获取设备编号
        String systemNo = MyApplication.deviceId;
        // 4. 发送到新API
        HTTPServerUtil.sendErrorInfo(errorCode, errorMessage, ledValue, systemNo);
    }


    public List<TroubleLog> getTroubleList() {
        List<TroubleLog> troubleLogs = new ArrayList<>();
        troubleLogs = MyApplication.getInstance().getDaoSession().getTroubleLogDao().queryBuilder().
                orderDesc(TroubleLogDao.Properties.Id).list();
        return troubleLogs;
    }


}
