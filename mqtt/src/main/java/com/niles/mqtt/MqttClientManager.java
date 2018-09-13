package com.niles.mqtt;

import android.app.Application;
import android.text.TextUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttPublish;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.File;
import java.util.List;
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
    private MyMqttFilePersistence mMyPersistence;
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
        mMyPersistence = new MyMqttFilePersistence(new File(app.getCacheDir(), "MyMqttPersistence").getAbsolutePath());
        try {
            mMyPersistence.open(mqttConfig.getClientId(), mqttConfig.getServerUri());
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
            throw new RuntimeException("MqttDefaultFilePersistence open failure");
        }
        MqttDefaultFilePersistence persistence = new MqttDefaultFilePersistence(new File(app.getCacheDir(), "MqttPersistence").getAbsolutePath());
        mMqttAndroidClient = new MqttAndroidClient(app, mqttConfig.getServerUri(), mqttConfig.getClientId(), persistence);
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

    /**
     * 上传保存的消息
     * 返回是否全部上传成功
     * 如果没有保存的消息，返回true
     */
    private boolean publishSavedMessage() {
        try {
            // 获取被保存的消息
            List<MqttPublish> allPublishMessage = mMyPersistence.getSavedPublishMessage();
            for (MqttPublish mqttPublish : allPublishMessage) {
                if (publishWithResult(mqttPublish)) {
                    // 上传成功则移除保存的消息
                    mMyPersistence.removeMessage(mqttPublish);
                } else {
                    // 上传失败则停止上传
                    return false;
                }
            }
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void onMqttConnected() {
        // 发布保存的消息
        publishSavedMessage();
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
        options.setMaxInflight(mMqttConfig.getMaxInflight());
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

    /**
     * 发送消息，如果没有发送成功则保存消息
     */
    public void publish(MqttMessage message) {
        if (!mIsMqttServiceStarted) {
            mMqttLog.log("Publish Failure, Mqtt Service UnStarted.");
            mMyPersistence.put(message);
            return;
        }

        // 首先上传保存的消息
        if (!publishSavedMessage()) {
            mMyPersistence.put(message);
            return;
        }

        if (!publishWithResult(message.getTopic(),
                message.getPayload(),
                message.getQos(),
                message.isRetained())) {
            mMyPersistence.put(message);
        }
    }

    /**
     * 发送消息，如果没有发送成功返回 false
     */
    private boolean publishWithResult(MqttPublish mqttPublish) {
        return publishWithResult(
                mqttPublish.getTopicName(),
                mqttPublish.getMessage().getPayload(),
                mqttPublish.getMessage().getQos(),
                mqttPublish.getMessage().isRetained());
    }

    /**
     * 发送消息，如果没有发送成功返回 false
     */
    private boolean publishWithResult(String topic, byte[] payload, int qos, boolean isRetained) {
        try {
            IMqttDeliveryToken deliveryToken = mMqttAndroidClient.publish(topic,
                    payload,
                    qos,
                    isRetained,
                    null,
                    new MqttActionListener(MqttAction.PUBLISH, mMqttLog, mMqttConnHandler));

            int messageId = deliveryToken.getMessageId();
            if (messageId == 0) {
                // Delegate Token is null
                mMqttLog.log("Publish Failure, Mqtt Unconnected.");
                return false;
            } else {
                mMqttLog.log(String.format(Locale.getDefault(), "Publish MessageId %s", messageId));
                return true;
            }
        } catch (MqttException e) {
            e.printStackTrace();
            mMqttLog.log(String.format(Locale.getDefault(), "Publish Exception %s", e.getMessage()));
            return false;
        }
    }
}
