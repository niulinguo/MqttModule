package com.niles.mqtt;

import org.eclipse.paho.android.service.MqttTraceHandler;

import java.util.Locale;

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
        mMqttLog.log(String.format(Locale.getDefault(), "traceDebug tag:%s message:%s", tag, message));
    }

    @Override
    public void traceError(String tag, String message) {
        mMqttLog.log(String.format(Locale.getDefault(), "traceError tag:%s message:%s", tag, message));
    }

    @Override
    public void traceException(String tag, String message, Exception e) {
        mMqttLog.log(String.format(Locale.getDefault(), "traceException tag:%s message:%s exception:%s", tag, message, e.getMessage()));
    }
}
