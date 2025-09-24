package com.hyeprion.foodrecyclingsystem.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;

import org.greenrobot.greendao.annotation.NotNull;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SoundPlayUtils implements PlayerAdapterListener {
    public static int PLAYSTATUS0 = 0;//正在播放
    public static int PLAYSTATUS1 = 1;//暂停播放
    public static int PLAYSTATUS2 = 2;//重置
    public static int PLAYSTATUS3 = 3;//播放完成
    public static int PLAYSTATUS4 = 4;//媒体流装载完成
    public static int PLAYSTATUS5 = 5;//媒体流加载中
    public static int PLAYSTATUS6 = 6;//停止
    public static int PLAYSTATUSD1 = -1;//错误

    public int PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000;
    private String TAG = SoundPlayUtils.class.getSimpleName();
    private MediaPlayer mMediaPlayer;
    private ScheduledExecutorService mExecutor;//开启线程
    private PlaybackInfoListener mPlaybackInfoListener;
    private Runnable mSeekbarPositionUpdateTask;
    private String musiUrl;//音乐地址，可以是本地的音乐，可以是网络的音乐
    private Context mContext;
    private AudioManager am;
    private int max;
    private int day = 1;
    private String volumeUrl = "";

    public void setmPlaybackInfoListener(PlaybackInfoListener mPlaybackInfoListener) {
        this.mPlaybackInfoListener = mPlaybackInfoListener;
    }

    public SoundPlayUtils(Context mContext) {
        this.mContext = mContext;
        am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        max = am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
    }

    /**
     * @description:初始化MediaPlayer
     **/
    private void initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            //注册，播放完成后的监听
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopUpdatingCallbackWithPosition(true);
                    if (mPlaybackInfoListener != null) {
                        mPlaybackInfoListener.onStateChanged(PLAYSTATUS3);
                        mPlaybackInfoListener.onPlaybackCompleted();
                    }
                }
            });

            //监听媒体流是否装载完成
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    /*float volume = 0;
                    if (TextUtils.equals(volumeUrl, Constans.R_16_SOUND_CHECK)) {
                        if (day == 0) {
                            volume = Float.parseFloat((String) SharedpreferesUtil.getObj
                                    (Constans.VOLUME_FILENAME, Constans.VOLUME_NIGHT_KEY)) / 100;
                        } else {
                            volume = Float.parseFloat((String) SharedpreferesUtil.getObj
                                    (Constans.VOLUME_FILENAME, Constans.VOLUME_DAY_KEY)) / 100;
                        }
                    } else {
                        volume = Float.parseFloat(VolumeUtil.getVolume()) / 100;
                    }*/

                    mp.setVolume(80, 80);

                    medisaPreparedCompled();
                }
            });

            /**
             * @description:监听媒体错误信息
             **/
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    if (mPlaybackInfoListener != null) {
                        mPlaybackInfoListener.onStateChanged(PLAYSTATUSD1);
                    }
                    Log.d(TAG, "OnError - Error code: " + what + " Extra code: " + extra);
                    switch (what) {
                        case -1004:
                            Log.d(TAG, "MEDIA_ERROR_IO");
                            break;
                        case -1007:
                            Log.d(TAG, "MEDIA_ERROR_MALFORMED");
                            break;
                        case 200:
                            Log.d(TAG, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
                            break;
                        case 100:
                            Log.d(TAG, "MEDIA_ERROR_SERVER_DIED");
                            break;
                        case -110:
                            Log.d(TAG, "MEDIA_ERROR_TIMED_OUT");
                            break;
                        case 1:
                            Log.d(TAG, "MEDIA_ERROR_UNKNOWN");
                            break;
                        case -1010:
                            Log.d(TAG, "MEDIA_ERROR_UNSUPPORTED");
                            break;
                        default:
                            break;
                    }
                    switch (extra) {
                        case 800:
                            Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING");
                            break;
                        case 702:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_END");
                            break;
                        case 701:
                            Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                            break;
                        case 802:
                            Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                            break;
                        case 801:
                            Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE");
                            break;
                        case 1:
                            Log.d(TAG, "MEDIA_INFO_UNKNOWN");
                            break;
                        case 3:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                            break;
                        case 700:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }
    }

    /**
     * @description: 加载媒体资源
     **/
    @Override
    public void loadMedia(String musiUrl) {


        if (TextUtils.isEmpty(musiUrl)) {
            Log.i(TAG, "地址为空");
            return;
        }

//        int dayV = Integer.parseInt(VolumeUtil.getVolume());
//        int dV = dayV * max / 100;
//        am.setStreamVolume(AudioManager.STREAM_SYSTEM, dV, AudioManager.FLAG_PLAY_SOUND);

        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onStateChanged(PLAYSTATUS5);
        }
        if (musiUrl.contains("https://")) {
            this.musiUrl = musiUrl;
        } else {
            this.musiUrl = getLoacal(musiUrl);
        }
        initializeMediaPlayer();
        try {
            mMediaPlayer.reset();//防止再次添加进来出现崩溃信息
            mMediaPlayer.setDataSource(mContext, Uri.parse(this.musiUrl));
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @description: 加载媒体资源
     * day 0 夜间 1 白天
     **/
    @Override
    public void loadMedia(String musiUrl, int day) {
        volumeUrl = musiUrl;
        this.day = day;

        if (TextUtils.isEmpty(musiUrl)) {
            Log.i(TAG, "地址为空");
            return;
        }

//        int dayV = Integer.parseInt(VolumeUtil.getVolume());
//        int dV = dayV * max / 100;
//        am.setStreamVolume(AudioManager.STREAM_SYSTEM, dV, AudioManager.FLAG_PLAY_SOUND);

        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onStateChanged(PLAYSTATUS5);
        }
        if (musiUrl.contains("https://")) {
            this.musiUrl = musiUrl;
        } else {
            this.musiUrl = getLoacal(musiUrl);
        }
        initializeMediaPlayer();
        try {
            mMediaPlayer.reset();//防止再次添加进来出现崩溃信息
            mMediaPlayer.setDataSource(mContext, Uri.parse(this.musiUrl));
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @description:释放媒体资源
     **/
    @Override
    public void release() {
        if (mMediaPlayer != null) {
            stopUpdatingCallbackWithPosition(false);
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * @description:判断是否正在播放
     **/
    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * @description:播放开始
     **/
    @Override
    public void play() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(PLAYSTATUS0);
            }
            startUpdatingCallbackWithPosition();
        }
    }

    /**
     * @description:开启线程，获取当前播放的进度
     **/
    private void startUpdatingCallbackWithPosition() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (mSeekbarPositionUpdateTask == null) {
            mSeekbarPositionUpdateTask = new Runnable() {
                @Override
                public void run() {
                    updateProgressCallbackTask();
                }
            };
        }

        mExecutor.scheduleAtFixedRate(
                mSeekbarPositionUpdateTask,
                0,
                PLAYBACK_POSITION_REFRESH_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public void reset() {
        if (mMediaPlayer != null) {
            loadMedia(musiUrl);
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(PLAYSTATUS2);
            }
            stopUpdatingCallbackWithPosition(true);
        }
    }

    /**
     * @description:暂停
     **/
    @Override
    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(PLAYSTATUS1);
            }
        }
    }

    @Override
    public void stop() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(PLAYSTATUS6);
            }
        }
    }

    /**
     * @description:更新当前的进度
     **/
    private void updateProgressCallbackTask() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            int currentPosition = mMediaPlayer.getCurrentPosition();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onPositionChanged(currentPosition);
            }
        }
    }

    /**
     * @description:加载完成回调
     **/
    @Override
    public void medisaPreparedCompled() {
        int duration = mMediaPlayer.getDuration();//获取总时长
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onDurationChanged(duration);
            mPlaybackInfoListener.onPositionChanged(0);
            mPlaybackInfoListener.onStateChanged(PLAYSTATUS4);
        }
    }

    /**
     * @description:滑动播放到某个位置
     **/
    @Override
    public void seekTo(int position) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(position);
        }
    }


    /**
     * @description:播放完成回调监听
     **/
    private void stopUpdatingCallbackWithPosition(boolean resetUIPlaybackPosition) {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
            mSeekbarPositionUpdateTask = null;
            if (resetUIPlaybackPosition && mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onPositionChanged(0);
            }
        }
    }

    private String getLoacal(@NotNull String musiUrl) {
        String myMusiUrl = "android.resource://" + mContext.getPackageName() + "/";
        String language = MyApplication.adminParameterBean.getLanguage();
        if (language.equals(Constant.CHINESE)) {
            switch (musiUrl) {
                case Constant.music_1_preparing:
                    myMusiUrl = myMusiUrl + R.raw.r_01_preparing_cn;
                    break;
                case Constant.music_2_press_open:
                    myMusiUrl = myMusiUrl + R.raw.r_02_press_open_cn;
                    break;
                case Constant.music_3_inlet_press_close:
                    myMusiUrl = myMusiUrl + R.raw.r_03_inlet_press_close_cn;
                    break;
                case Constant.music_4_weighing:
                    myMusiUrl = myMusiUrl + R.raw.r_04_weighing_cn;
                    break;
                case Constant.music_5_weigh_finish:
                    myMusiUrl = myMusiUrl + R.raw.r_05_weigh_finish_cn;
                    break;
            }
        } else if (language.equals(Constant.KOREAN)) {
            switch (musiUrl) {
                case Constant.music_1_preparing:
                    myMusiUrl = myMusiUrl + R.raw.r_01_preparing_ko;
                    break;
                case Constant.music_2_press_open:
                    myMusiUrl = myMusiUrl + R.raw.r_02_press_open_ko;
                    break;
                case Constant.music_3_inlet_press_close:
                    myMusiUrl = myMusiUrl + R.raw.r_03_inlet_press_close_ko;
                    break;
                case Constant.music_4_weighing:
                    myMusiUrl = myMusiUrl + R.raw.r_04_weighing_ko;
                    break;
                case Constant.music_5_weigh_finish:
                    myMusiUrl = myMusiUrl + R.raw.r_05_weigh_finish_ko;
                    break;
            }
        } else if (language.equals(Constant.JAPANESE) ) {
            switch (musiUrl) {
                case Constant.music_1_preparing:
                    myMusiUrl = myMusiUrl + R.raw.r_01_preparing_ja;
                    break;
                case Constant.music_2_press_open:
                    myMusiUrl = myMusiUrl + R.raw.r_02_press_open_ja;
                    break;
                case Constant.music_3_inlet_press_close:
                    myMusiUrl = myMusiUrl + R.raw.r_03_inlet_press_close_ja;
                    break;
                case Constant.music_4_weighing:
                    myMusiUrl = myMusiUrl + R.raw.r_04_weighing_ja;
                    break;
                case Constant.music_5_weigh_finish:
                    myMusiUrl = myMusiUrl + R.raw.r_05_weigh_finish_ja;
                    break;
            }
        } else if (language.equals(Constant.ENGLISH)) {
            switch (musiUrl) {
                case Constant.music_1_preparing:
                    myMusiUrl = myMusiUrl + R.raw.r_01_preparing_en;
                    break;
                case Constant.music_2_press_open:
                    myMusiUrl = myMusiUrl + R.raw.r_02_press_open_en;
                    break;
                case Constant.music_3_inlet_press_close:
                    myMusiUrl = myMusiUrl + R.raw.r_03_inlet_press_close_en;
                    break;
                case Constant.music_4_weighing:
                    myMusiUrl = myMusiUrl + R.raw.r_04_weighing_en;
                    break;
                case Constant.music_5_weigh_finish:
                    myMusiUrl = myMusiUrl + R.raw.r_05_weigh_finish_en;
                    break;
            }
        }

        return myMusiUrl;
    }

}
