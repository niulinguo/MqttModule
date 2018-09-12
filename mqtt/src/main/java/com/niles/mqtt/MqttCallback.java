package com.niles.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Locale;

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
        mMqttLog.log(String.format(Locale.getDefault(), "connectComplete reconnect:%s serverURI:%s", reconnect, serverURI));
        if (reconnect) {
            mMqttConnHandler.changeConnStatus(MqttConnStatus.CONNECTED);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        mMqttLog.log(String.format(Locale.getDefault(), "connectionLost %s", cause.getMessage()));
        mMqttConnHandler.changeConnStatus(MqttConnStatus.DISCONNECTED);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        mMqttLog.log(String.format(Locale.getDefault(), "messageArrived topic:%s message:%s", topic, message));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        mMqttLog.log(String.format(Locale.getDefault(), "deliveryComplete %s", token.getMessageId()));
    }
}
