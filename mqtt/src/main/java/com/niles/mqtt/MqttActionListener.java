package com.niles.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import java.util.Locale;

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
        mMqttLog.log("onConnectSuccess");
        mMqttConnHandler.changeConnStatus(MqttConnStatus.CONNECTED);
    }

    private void onConnectFailure(IMqttToken asyncActionToken, Throwable exception) {
        mMqttLog.log(String.format(Locale.getDefault(), "onConnectFailure Exception:%s", exception));
        mMqttConnHandler.changeConnStatus(MqttConnStatus.ERROR);
    }

    private void onPublishSuccess(IMqttToken asyncActionToken) {
        mMqttLog.log(String.format(Locale.getDefault(), "onPublishSuccess %s", asyncActionToken.getMessageId()));
    }

    private void onPublishFailure(IMqttToken asyncActionToken, Throwable exception) {
        mMqttLog.log(String.format(Locale.getDefault(), "onPublishFailure Token:%s Exception:%s", asyncActionToken, exception));
    }

    private void onSubscribeSuccess(IMqttToken asyncActionToken) {
        mMqttLog.log(String.format(Locale.getDefault(), "onSubscribeSuccess Token:%s", asyncActionToken));
    }

    private void onSubscribeFailure(IMqttToken asyncActionToken, Throwable exception) {
        mMqttLog.log(String.format(Locale.getDefault(), "onSubscribeFailure Token:%s Exception:%s", asyncActionToken, exception));
    }

    private void onDisconnectSuccess(IMqttToken asyncActionToken) {
        mMqttLog.log(String.format(Locale.getDefault(), "onDisconnectSuccess Token:%s", asyncActionToken));
        mMqttConnHandler.changeConnStatus(MqttConnStatus.DISCONNECTED);
    }

    private void onDisconnectFailure(IMqttToken asyncActionToken, Throwable exception) {
        mMqttLog.log(String.format(Locale.getDefault(), "onDisconnectFailure Token:%s Exception:%s", asyncActionToken, exception));
        mMqttConnHandler.changeConnStatus(MqttConnStatus.DISCONNECTED);
    }

    private void onUnsubscribeSuccess(IMqttToken asyncActionToken) {
        mMqttLog.log(String.format(Locale.getDefault(), "onUnsubscribeSuccess Token:%s", asyncActionToken));
    }

    private void onUnsubscribeFailure(IMqttToken asyncActionToken, Throwable exception) {
        mMqttLog.log(String.format(Locale.getDefault(), "onUnsubscribeFailure Token:%s Exception:%s", asyncActionToken, exception));
    }
}
