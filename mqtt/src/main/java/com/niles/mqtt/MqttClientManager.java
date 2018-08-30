package com.niles.mqtt;

import android.app.Application;
import android.text.TextUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.util.Locale;

/**
 * Created by Niles
 * Date 2018/8/30 11:48
 * Email niulinguo@163.com
 */
public class MqttClientManager {

    private final MqttConfig mMqttConfig;
    private final MqttLog mMqttLog;
    private final MqttAndroidClient mMqttAndroidClient;
    private boolean mIsMqttServiceStarted = false;
    private MqttConnStatus mMqttConnStatus = MqttConnStatus.NONE;
    private final MqttConnHandler mMqttConnHandler = new MqttConnHandler() {
        @Override
        public void changeConnStatus(MqttConnStatus status) {
            mMqttConnStatus = status;

            if (status == MqttConnStatus.CONNECTED || status == MqttConnStatus.ERROR) {
                if (!mIsMqttServiceStarted) {
                    mIsMqttServiceStarted = true;
                    onMqttServiceStarted();
                }
            }

            if (status == MqttConnStatus.CONNECTED) {
                onMqttConnected();
            }
        }

        @Override
        public MqttConnStatus getConnStatus() {
            return mMqttConnStatus;
        }
    };

    public MqttClientManager(Application app, MqttConfig mqttConfig) {
        mMqttConfig = mqttConfig;
        mMqttLog = mqttConfig.getMqttLog();
        mMqttAndroidClient = new MqttAndroidClient(app, mqttConfig.getServerUri(), mqttConfig.getClientId(), new MqttDefaultFilePersistence());
        mMqttAndroidClient.setCallback(new MqttCallback(mMqttLog, mMqttConnHandler));
        mMqttLog.log(String.format(Locale.getDefault(), "Create Client(%s) ServerUri:%s", mqttConfig.getClientId(), mqttConfig.getServerUri()));
    }

    private void onMqttServiceStarted() {
        final DisconnectedBufferOptions options = new DisconnectedBufferOptions();
        options.setBufferEnabled(mMqttConfig.isBufferEnabled());
        options.setBufferSize(mMqttConfig.getBufferSize());
        options.setPersistBuffer(mMqttConfig.isPersistBuffer());
        options.setDeleteOldestMessages(mMqttConfig.isDeleteOldestMessages());

        mMqttAndroidClient.setBufferOpts(options);
        mMqttAndroidClient.setTraceEnabled(mMqttConfig.isTraceEnable());
        if (mMqttConfig.isTraceEnable()) {
            mMqttAndroidClient.setTraceCallback(new MqttTraceCallback(mMqttLog));
        }
        mMqttLog.log(String.format(Locale.getDefault(), "Buffered Message Count %d", mMqttAndroidClient.getBufferedMessageCount()));
    }

    private void onMqttConnected() {

    }

    public void connect() {
        if (mMqttConnStatus == MqttConnStatus.CONNECTED) {
            return;
        }

        mMqttConnHandler.changeConnStatus(MqttConnStatus.CONNECTING);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(mMqttConfig.isCleanSession());
        options.setConnectionTimeout(mMqttConfig.getConnectionTimeout());
        options.setKeepAliveInterval(mMqttConfig.getKeepAliveInterval());
        options.setAutomaticReconnect(mMqttConfig.isAutomaticReconnect());
        if (!TextUtils.isEmpty(mMqttConfig.getUsername())) {
            options.setUserName(mMqttConfig.getUsername());
        }
        if (!TextUtils.isEmpty(mMqttConfig.getPassword())) {
            options.setPassword(mMqttConfig.getPassword().toCharArray());
        }

        try {
            IMqttToken mqttToken = mMqttAndroidClient.connect(options, null, new MqttActionListener(MqttAction.CONNECT, mMqttLog, mMqttConnHandler));
            mMqttLog.log(String.format(Locale.getDefault(), "Connect Token:%s Username:%s Password:%s", mqttToken, mMqttConfig.getUsername(), mMqttConfig.getPassword()));
        } catch (MqttException e) {
            e.printStackTrace();
            mMqttLog.log(String.format(Locale.getDefault(), "Connect Exception %s", e.getMessage()));
        }
    }

    public void disconnect() {
        if (!mIsMqttServiceStarted) {
            return;
        }

        mMqttConnHandler.changeConnStatus(MqttConnStatus.DISCONNECTING);
        try {
            IMqttToken mqttToken = mMqttAndroidClient.disconnect(null, new MqttActionListener(MqttAction.DISCONNECT, mMqttLog, mMqttConnHandler));
            mMqttLog.log(String.format(Locale.getDefault(), "Disconnect Token:%s", mqttToken));
        } catch (MqttException e) {
            e.printStackTrace();
            mMqttLog.log(String.format(Locale.getDefault(), "Disconnect Exception %s", e.getMessage()));
        }
    }

    public void close() {
        mMqttAndroidClient.close();
    }

    private void publish(MqttMessage message) {
        if (!mIsMqttServiceStarted) {
            return;
        }

        try {
            IMqttDeliveryToken deliveryToken = mMqttAndroidClient.publish(message.getTopic(),
                    message.getPayload(),
                    message.getQos(),
                    message.isRetained(),
                    null,
                    new MqttActionListener(MqttAction.PUBLISH, mMqttLog, mMqttConnHandler));
            mMqttLog.log(String.format(Locale.getDefault(), "Publish Token:%s", deliveryToken));

            if (!mMqttAndroidClient.isConnected()) {
                mMqttLog.log(String.format(Locale.getDefault(), "Buffered Message Count %d", mMqttAndroidClient.getBufferedMessageCount()));
            }
        } catch (MqttException e) {
            e.printStackTrace();
            mMqttLog.log(String.format(Locale.getDefault(), "Publish Exception %s", e.getMessage()));
        }
    }
}
