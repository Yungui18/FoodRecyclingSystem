package com.hyeprion.foodrecyclingsystem.util;

public interface PlayerAdapterListener {
    void loadMedia(String musiUrl);//加载媒体资源
    void loadMedia(String musiUrl, int day);//加载媒体资源

    void release();//释放资源

    boolean isPlaying();//判断是否在播放

    void play();//开始播放

    void reset();//重置

    void pause();//暂停
    void stop();//停止

    void medisaPreparedCompled();//完成媒体流的装载

    void seekTo(int position);//滑动到某个位置
}
