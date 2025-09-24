package com.hyeprion.foodrecyclingsystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.BaseActivity;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.bean.MonthlyTotalInlet;
import com.hyeprion.foodrecyclingsystem.bean.TimesInlet;
import com.hyeprion.foodrecyclingsystem.bean.TimesWeightBean;
import com.hyeprion.foodrecyclingsystem.databinding.ActivityWeighingDetailsShopBinding;
import com.hyeprion.foodrecyclingsystem.greendao.db.MonthlyTotalInletDao;
import com.hyeprion.foodrecyclingsystem.greendao.db.TimesInletDao;
import com.hyeprion.foodrecyclingsystem.util.Constant;
import com.hyeprion.foodrecyclingsystem.util.HTTPServerUtil;
import com.hyeprion.foodrecyclingsystem.util.TimeUtil;
import com.hyeprion.foodrecyclingsystem.util.port.PortConstants;
import com.hyeprion.foodrecyclingsystem.util.port.PortControlUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Apartment-None/Shop-Option
 * 外部称重
 * 获取外部连接称重装置的重量值
 */
public class WeighingDetailsShopActivity extends BaseActivity<ActivityWeighingDetailsShopBinding> {
    private ShowTimeWeightAdapter showTimeWeightAdapter;
    private MonthlyTotalInlet monthlyTotalInlet; // 月投总量
    private TimesInlet timesInlet; // 次投重量

    // 现在重量
    private String nowWeight;
    private float monthlyTotal; // 月投总量


    //1，首先创建一个Handler对象
//    private Handler handler = new Handler();
//    private Runnable runnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        myHandler =  new Handler();
        //每隔1s获取投入口和排除口的状态
        myRunnable =new Runnable(){
            @Override
            public void run() {
                //要做的事情，这里再次调用此Runnable对象，以实现每秒实现一次的定时器操作
                myHandler.postDelayed(this, 1000);

                //投入口或者排除口打开，则搅拌电机停止,上传心跳
                if (PortControlUtil.getInstance().getPortStatus().getInletStatus()==2 ||
                        PortControlUtil.getInstance().getPortStatus().getOutletStatus() == 0
                ){
                    // TODO: 2023/1/18 搅拌电机停止并报警
                    PortControlUtil.getInstance().sendCommands(PortConstants.STIR_STOP);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            PortControlUtil.getInstance().sendCommands(PortConstants.LED_RED_TOGGLE);
                        }
                    },200);
                    HTTPServerUtil.sendHeartBeat2();
                    return;
                }


            }
        };
        //3，使用PostDelayed方法，调用此Runnable对象
        myHandler.postDelayed(myRunnable, 1000);


        List<TimesWeightBean> timesWeightBeans = new ArrayList<>();
        nowWeight = PortControlUtil.getInstance().getPortStatus().getChooseUseWeighing() + "";
        float dailyTotalInlet = 0;
        viewBinding.tvNowKg.setText(String.format(getString(R.string.KG), nowWeight));

        // 获取月投总量
        List<MonthlyTotalInlet> monthlyTotalInlets = MyApplication.getInstance().getDaoSession().
                getMonthlyTotalInletDao().queryBuilder().where(MonthlyTotalInletDao.Properties.Time
                .eq(TimeUtil.getDateMonthToString())).list();
        // 月投总量为空
        if (monthlyTotalInlets == null || monthlyTotalInlets.size() == 0) {
            monthlyTotal = Float.parseFloat(nowWeight);
            viewBinding.tv2DailyTotalKg.setText(String.format(getString(R.string.KG), nowWeight));
            viewBinding.tv3DailyTimesTime.setText(String.format(getString(R.string.total_times), 1));
            timesWeightBeans.add(new TimesWeightBean(1, TimeUtil.getDateDayToString(), nowWeight));
            MonthlyTotalInlet monthlyTotalInlet = new MonthlyTotalInlet();
            monthlyTotalInlet.setTime(TimeUtil.getDateMonthToString());
            monthlyTotalInlet.setMonthlyTotalInlet(monthlyTotal);
            MyApplication.getInstance().getDaoSession().getMonthlyTotalInletDao().insert(monthlyTotalInlet);
            return;
        }
        monthlyTotalInlet = monthlyTotalInlets.get(0);
        // 获取日投所有数据，多次投放的数据
        List<TimesInlet> timesInlets = MyApplication.getInstance().getDaoSession().
                getTimesInletDao().queryBuilder().where(TimesInletDao.Properties.Time
                .eq(TimeUtil.getDateDayToString())).list();

        // 初始化列表数据
        for (int i = 0; i < timesInlets.size(); i++) {
            dailyTotalInlet += timesInlets.get(i).getTimesInlet();
            timesWeightBeans.add(new TimesWeightBean(i + 1, TimeUtil.getDateDayToString(),
                    timesInlets.get(i).getTimesInlet() + ""));
        }
        timesWeightBeans.add(new TimesWeightBean(timesInlets.size() + 1,
                TimeUtil.getDateDayToString(), nowWeight));
        dailyTotalInlet += Float.parseFloat(nowWeight);
//        viewBinding.tvNowKg.setText(String.format(getString(R.string.KG),
//                PortControlUtil.getInstance().getPortStatus().getNetWeight() + ""));
        monthlyTotal = monthlyTotalInlet.getMonthlyTotalInlet() + Float.parseFloat(nowWeight);
        viewBinding.tv1MonthlyTotalKg.setText(String.format(getString(R.string.KG), monthlyTotal + ""));
        viewBinding.tv2DailyTotalKg.setText(String.format(getString(R.string.KG), dailyTotalInlet + ""));

//        timesWeightBeans.add(new TimesWeightBean(1, "2022-12-1", "3"));
//        timesWeightBeans.add(new TimesWeightBean(2, "2022-12-1", "4"));
//        timesWeightBeans.add(new TimesWeightBean(3, "2022-12-1", "6"));
//        timesWeightBeans.add(new TimesWeightBean(4, "2022-12-1", "2.3"));
//        timesWeightBeans.add(new TimesWeightBean(5, "2022-12-1", "3.5"));
        viewBinding.tv3DailyTimesTime.setText(String.format(getString(R.string.total_times), timesInlets.size() + 1));

        showTimeWeightAdapter = new ShowTimeWeightAdapter(timesWeightBeans);
        // 设置空布局
//        showTimeWeightAdapter.setEmptyView(MyLayoutManager.getEmptyView(mActivity, mActivity.getResources().getString(R.string.empty_data), R.layout.view_general_empty, null));

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        viewBinding.rlTimeWeitht.setLayoutManager(layoutManager);
        viewBinding.rlTimeWeitht.setAdapter(showTimeWeightAdapter);
    }

    @Override
    protected void initListener() {
        viewBinding.btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_confirm) {
            // 插入此次重量数据
            timesInlet = new TimesInlet();
            timesInlet.setTime(TimeUtil.getDateDayToString());
            timesInlet.setTimesInlet(Float.parseFloat(nowWeight));
            MyApplication.getInstance().getDaoSession().getTimesInletDao().insert(timesInlet);

            // 更新本月投入总量数据
            monthlyTotalInlet = MyApplication.getInstance().getDaoSession().
                    getMonthlyTotalInletDao().queryBuilder().where(MonthlyTotalInletDao.Properties.Time
                    .eq(TimeUtil.getDateMonthToString())).list().get(0);
            monthlyTotalInlet.setMonthlyTotalInlet(monthlyTotal);
            MyApplication.getInstance().getDaoSession().
                    getMonthlyTotalInletDao().updateInTx(monthlyTotalInlet);

            // 存在投入口模式
            if (MyApplication.adminParameterBean.getInletMode().equals(Constant.AUTOMATION)) {
                Intent intent = new Intent(mActivity, InletControlActivity.class);
                startActivity(intent);
            }

            // 不存在投入口模式，直接跳转回登录界面
            this.finish();
        }
    }


    static class ShowTimeWeightAdapter extends BaseQuickAdapter<TimesWeightBean, BaseViewHolder> {

        public ShowTimeWeightAdapter(@Nullable List<TimesWeightBean> data) {
            super(R.layout.item_rl_time_weight, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, TimesWeightBean item) {
            helper.setText(R.id.item_tv_time, String.format(ActivityUtils.getTopActivity().getString(R.string.how_many_times), item.getTimes()));
            helper.setText(R.id.item_tv_kg, String.format(ActivityUtils.getTopActivity().getString(R.string.kg), item.getWeight()));
        }
    }


//    public void executeByFixedAtFixRate() throws Exception {
//        asyncTest(10, new TestRunnable<String>() {
//            @Override
//            public void run(final int index, CountDownLatch latch) {
//                final TestScheduledTask<String> task = new TestScheduledTask<String>(latch, 3) {
//                    @Override
//                    public String doInBackground() throws Throwable {
//                        return
//                    }
//
//                    @Override
//                    void onTestSuccess(String result) {
//                        System.out.println(result);
//                    }
//                };
//                ThreadUtils.executeByFixedAtFixRate(3, task, 1000, TimeUnit.MILLISECONDS);
//            }
//        });
//    }
//
//    private <T> void asyncTest(int threadCount, TestRunnable<T> runnable) throws Exception {
//        CountDownLatch latch = new CountDownLatch(threadCount);
//        for (int i = 0; i < threadCount; i++) {
//            runnable.run(i, latch);
//        }
//        latch.await();
//    }
//
//    interface TestRunnable<T> {
//        void run(final int index, CountDownLatch latch);
//    }
//
//    abstract static class TestScheduledTask<T> extends ThreadUtils.Task<T> {
//
//        private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();
//        private              int           mTimes;
//        CountDownLatch mLatch;
//
//        TestScheduledTask(final CountDownLatch latch, final int times) {
//            mLatch = latch;
//            mTimes = times;
//        }
//
//        abstract void onTestSuccess(T result);
//
//        @Override
//        public void onSuccess(T result) {
//            onTestSuccess(result);
//            if (ATOMIC_INTEGER.addAndGet(1) % mTimes == 0) {
//                mLatch.countDown();
//            }
//        }
//
//        @Override
//        public void onCancel() {
//            System.out.println(Thread.currentThread() + " onCancel: ");
//            mLatch.countDown();
//        }
//
//        @Override
//        public void onFail(Throwable t) {
//            System.out.println(Thread.currentThread() + " onFail: " + t);
//            mLatch.countDown();
//        }
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

//4，关闭此定时器，可以这样操作
        if (myHandler != null){
            myHandler.removeCallbacks(myRunnable);
            myHandler.removeCallbacksAndMessages(null);
        }

//移除所有的消息
//handler.removeCallbacksAndMessages(null);
    }
}
