package com.hyeprion.foodrecyclingsystem.util;

public interface PlaybackInfoListener {
    void onDurationChanged(int duration);//总时长

    void onPositionChanged(int position);//当前时长进度

    void onStateChanged(int state);//记录当前的状态

    void onPlaybackCompleted();//播放完成回调
}
