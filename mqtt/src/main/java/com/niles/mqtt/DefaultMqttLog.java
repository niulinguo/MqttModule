package com.niles.mqtt;

import android.os.Bundle;
import android.util.Log;

/**
 * Created by Niles
 * Date 2018/8/30 15:18
 * Email niulinguo@163.com
 */
public class DefaultMqttLog implements MqttLog {
    @Override
    public void log(String tag, Bundle bundle) {
        if (BuildConfig.DEBUG) {
            if (bundle == null) {
                Log.i("MqttLog", "[" + tag + "]");
            } else {
                Log.i("MqttLog", "[" + tag + "]" + bundle.toString());
            }
        }
    }
}
