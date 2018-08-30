package com.niles.mqtt;

import android.util.Log;

/**
 * Created by Niles
 * Date 2018/8/30 15:18
 * Email niulinguo@163.com
 */
public class DefaultMqttLog implements MqttLog {
    @Override
    public void log(String msg) {
        if (BuildConfig.DEBUG) {
            Log.i("MqttModule", msg);
        }
    }
}
