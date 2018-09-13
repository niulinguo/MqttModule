package com.niles.mqtt;

import android.os.Bundle;

import org.eclipse.paho.android.service.MqttTraceHandler;

/**
 * Created by Niles
 * Date 2018/8/30 14:55
 * Email niulinguo@163.com
 */
public class MqttTraceCallback implements MqttTraceHandler {

    private final MqttLog mMqttLog;

    MqttTraceCallback(MqttLog mqttLog) {
        mMqttLog = mqttLog;
    }

    @Override
    public void traceDebug(String tag, String message) {
        Bundle bundle = new Bundle();
        bundle.putString("Tag", tag);
        bundle.putString("Message", message);
        mMqttLog.log("traceDebug", bundle);
    }

    @Override
    public void traceError(String tag, String message) {
        Bundle bundle = new Bundle();
        bundle.putString("Tag", tag);
        bundle.putString("Message", message);
        mMqttLog.log("traceError", bundle);
    }

    @Override
    public void traceException(String tag, String message, Exception e) {
        Bundle bundle = new Bundle();
        bundle.putString("Tag", tag);
        bundle.putString("Message", message);
        bundle.putString("Exception", e == null ? "" : e.getMessage());
        mMqttLog.log("traceException", bundle);
    }
}
