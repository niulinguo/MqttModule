package com.niles.mqtt;

import android.os.Bundle;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

/**
 * Created by Niles
 * Date 2018/8/30 09:31
 * Email niulinguo@163.com
 */
public class MqttActionListener implements IMqttActionListener {

    private final MqttAction mMqttAction;
    private final MqttLog mMqttLog;
    private final MqttConnHandler mMqttConnHandler;

    MqttActionListener(MqttAction mqttAction, MqttLog mqttLog, MqttConnHandler mqttConnHandler) {
        mMqttAction = mqttAction;
        mMqttLog = mqttLog;
        mMqttConnHandler = mqttConnHandler;
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        switch (mMqttAction) {
            case CONNECT: {
                onConnectSuccess(asyncActionToken);
                break;
            }
            case PUBLISH: {
                onPublishSuccess(asyncActionToken);
                break;
            }
            case SUBSCRIBE: {
                onSubscribeSuccess(asyncActionToken);
                break;
            }
            case DISCONNECT: {
                onDisconnectSuccess(asyncActionToken);
                break;
            }
            case UNSUBSCRIBE: {
                onUnsubscribeSuccess(asyncActionToken);
                break;
            }
        }
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        switch (mMqttAction) {
            case CONNECT: {
                onConnectFailure(asyncActionToken, exception);
                break;
            }
            case PUBLISH: {
                onPublishFailure(asyncActionToken, exception);
                break;
            }
            case SUBSCRIBE: {
                onSubscribeFailure(asyncActionToken, exception);
                break;
            }
            case DISCONNECT: {
                onDisconnectFailure(asyncActionToken, exception);
                break;
            }
            case UNSUBSCRIBE: {
                onUnsubscribeFailure(asyncActionToken, exception);
                break;
            }
        }
    }

    private void onConnectSuccess(IMqttToken asyncActionToken) {
        mMqttLog.log("onConnectSuccess", null);
        mMqttConnHandler.changeConnStatus(MqttConnStatus.CONNECTED);
    }

    private void onConnectFailure(IMqttToken asyncActionToken, Throwable exception) {
        Bundle bundle = new Bundle();
        bundle.putString("Exception", exception == null ? "" : exception.getMessage());
        mMqttLog.log("onConnectFailure", bundle);
        mMqttConnHandler.changeConnStatus(MqttConnStatus.ERROR);
    }

    private void onPublishSuccess(IMqttToken asyncActionToken) {
        Bundle bundle = new Bundle();
        bundle.putInt("MsgId", asyncActionToken.getMessageId());
        mMqttLog.log("onPublishSuccess", bundle);
    }

    private void onPublishFailure(IMqttToken asyncActionToken, Throwable exception) {
        Bundle bundle = new Bundle();
        bundle.putString("Exception", exception == null ? "" : exception.getMessage());
        mMqttLog.log("onPublishFailure", bundle);
    }

    private void onSubscribeSuccess(IMqttToken asyncActionToken) {
        mMqttLog.log("onSubscribeSuccess", null);
    }

    private void onSubscribeFailure(IMqttToken asyncActionToken, Throwable exception) {
        Bundle bundle = new Bundle();
        bundle.putString("Exception", exception == null ? "" : exception.getMessage());
        mMqttLog.log("onSubscribeFailure", bundle);
    }

    private void onDisconnectSuccess(IMqttToken asyncActionToken) {
        mMqttLog.log("onDisconnectSuccess", null);
        mMqttConnHandler.changeConnStatus(MqttConnStatus.DISCONNECTED);
    }

    private void onDisconnectFailure(IMqttToken asyncActionToken, Throwable exception) {
        Bundle bundle = new Bundle();
        bundle.putString("Exception", exception == null ? "" : exception.getMessage());
        mMqttLog.log("onDisconnectFailure", bundle);
        mMqttConnHandler.changeConnStatus(MqttConnStatus.DISCONNECTED);
    }

    private void onUnsubscribeSuccess(IMqttToken asyncActionToken) {
        mMqttLog.log("onUnsubscribeSuccess", null);
    }

    private void onUnsubscribeFailure(IMqttToken asyncActionToken, Throwable exception) {
        Bundle bundle = new Bundle();
        bundle.putString("Exception", exception == null ? "" : exception.getMessage());
        mMqttLog.log("onUnsubscribeFailure", bundle);
    }
}
