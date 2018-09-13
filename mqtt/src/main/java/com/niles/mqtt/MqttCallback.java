package com.niles.mqtt;

import android.os.Bundle;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by Niles
 * Date 2018/8/30 09:58
 * Email niulinguo@163.com
 */
public class MqttCallback implements MqttCallbackExtended {

    private final MqttLog mMqttLog;
    private final MqttConnHandler mMqttConnHandler;

    MqttCallback(MqttLog mqttLog, MqttConnHandler mqttConnHandler) {
        mMqttLog = mqttLog;
        mMqttConnHandler = mqttConnHandler;
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("Reconnect", reconnect);
        bundle.putString("ServerUri", serverURI);
        mMqttLog.log("connectComplete", bundle);
        if (reconnect) {
            mMqttConnHandler.changeConnStatus(MqttConnStatus.CONNECTED);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Bundle bundle = new Bundle();
        bundle.putString("Exception", cause == null ? "" : cause.getMessage());
        mMqttLog.log("connectionLost", bundle);
        mMqttConnHandler.changeConnStatus(MqttConnStatus.DISCONNECTED);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Bundle bundle = new Bundle();
        bundle.putString("Topic", topic);
        bundle.putString("Msg", String.valueOf(message));
        mMqttLog.log("messageArrived", bundle);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Bundle bundle = new Bundle();
        bundle.putInt("MsgId", token.getMessageId());
        mMqttLog.log("deliveryComplete", bundle);
    }
}
