package com.niles.mqtt;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niles
 * Date 2018/8/30 13:39
 * Email niulinguo@163.com
 */
public class MqttConfig {

    private final boolean mTraceEnable;
    private final String mServerUri;
    private final String mClientId;
    private final String mUsername;
    private final String mPassword;
    private final boolean mCleanSession;
    private final int mConnectionTimeout;
    private final int mKeepAliveInterval;
    private final boolean mAutomaticReconnect;
    private final MqttLog mMqttLog;
    private final boolean mBufferEnabled;
    private final int mBufferSize;
    private final boolean mPersistBuffer;
    private final boolean mDeleteOldestMessages;
    private final int mMaxInflight;
    private final List<MqttSubscribeInfo> mMqttSubscribeInfos;

    MqttConfig(boolean traceEnable,
               String serverUri,
               String clientId,
               String username,
               String password,
               boolean cleanSession,
               int connectionTimeout,
               int keepAliveInterval,
               boolean automaticReconnect,
               MqttLog mqttLog,
               boolean bufferEnabled,
               int bufferSize,
               boolean persistBuffer,
               boolean deleteOldestMessages,
               int maxInflight,
               List<MqttSubscribeInfo> mqttSubscribeInfos) {
        mTraceEnable = traceEnable;
        mServerUri = serverUri;
        mClientId = clientId;
        mUsername = username;
        mPassword = password;
        mCleanSession = cleanSession;
        mConnectionTimeout = connectionTimeout;
        mKeepAliveInterval = keepAliveInterval;
        mAutomaticReconnect = automaticReconnect;
        mMqttLog = mqttLog;
        mBufferEnabled = bufferEnabled;
        mBufferSize = bufferSize;
        mPersistBuffer = persistBuffer;
        mDeleteOldestMessages = deleteOldestMessages;
        mMaxInflight = maxInflight;
        mMqttSubscribeInfos = mqttSubscribeInfos;
    }

    public static Build newBuilder() {
        return new Build();
    }

    public List<MqttSubscribeInfo> getMqttSubscribeInfos() {
        return mMqttSubscribeInfos;
    }

    public boolean isDeleteOldestMessages() {
        return mDeleteOldestMessages;
    }

    public int getConnectionTimeout() {
        return mConnectionTimeout;
    }

    public boolean isPersistBuffer() {
        return mPersistBuffer;
    }

    public int getBufferSize() {
        return mBufferSize;
    }

    public int getMaxInflight() {
        return mMaxInflight;
    }

    public boolean isBufferEnabled() {
        return mBufferEnabled;
    }

    public boolean isAutomaticReconnect() {
        return mAutomaticReconnect;
    }

    public boolean isCleanSession() {
        return mCleanSession;
    }

    public boolean isTraceEnable() {
        return mTraceEnable;
    }

    public int getKeepAliveInterval() {
        return mKeepAliveInterval;
    }

    public MqttLog getMqttLog() {
        return mMqttLog;
    }

    public String getClientId() {
        return mClientId;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getServerUri() {
        return mServerUri;
    }

    public String getUsername() {
        return mUsername;
    }

    public static class Build {

        private boolean mTraceEnable;
        private String mServerUri;
        private String mClientId;
        private String mUsername;
        private String mPassword;
        private boolean mCleanSession;
        private int mConnectionTimeout = 30;
        private int mKeepAliveInterval = 60;
        private boolean mAutomaticReconnect;
        private MqttLog mMqttLog;
        private boolean mBufferEnabled;
        private int mBufferSize = 10000;
        private int mMaxInflight = 10000;
        private boolean mPersistBuffer;
        private boolean mDeleteOldestMessages;
        private List<MqttSubscribeInfo> mMqttSubscribeInfos = new ArrayList<>();

        public MqttConfig build() {
            check();

            if (mMqttLog == null) {
                mMqttLog = new DefaultMqttLog();
            }

            return new MqttConfig(mTraceEnable,
                    mServerUri,
                    mClientId,
                    mUsername,
                    mPassword,
                    mCleanSession,
                    mConnectionTimeout,
                    mKeepAliveInterval,
                    mAutomaticReconnect,
                    mMqttLog,
                    mBufferEnabled,
                    mBufferSize,
                    mPersistBuffer,
                    mDeleteOldestMessages,
                    mMaxInflight,
                    mMqttSubscribeInfos);
        }

        public boolean isTraceEnable() {
            return mTraceEnable;
        }

        public Build setTraceEnable(boolean traceEnable) {
            mTraceEnable = traceEnable;
            return this;
        }

        public String getServerUri() {
            return mServerUri;
        }

        public Build setServerUri(String serverUri) {
            mServerUri = serverUri;
            return this;
        }

        public String getClientId() {
            return mClientId;
        }

        public Build setClientId(String clientId) {
            mClientId = clientId;
            return this;
        }

        public String getUsername() {
            return mUsername;
        }

        public Build setUsername(String username) {
            mUsername = username;
            return this;
        }

        public String getPassword() {
            return mPassword;
        }

        public Build setPassword(String password) {
            mPassword = password;
            return this;
        }

        public boolean isCleanSession() {
            return mCleanSession;
        }

        public Build setCleanSession(boolean cleanSession) {
            mCleanSession = cleanSession;
            return this;
        }

        public int getConnectionTimeout() {
            return mConnectionTimeout;
        }

        public Build setConnectionTimeout(int connectionTimeout) {
            mConnectionTimeout = connectionTimeout;
            return this;
        }

        public int getKeepAliveInterval() {
            return mKeepAliveInterval;
        }

        public Build setKeepAliveInterval(int keepAliveInterval) {
            mKeepAliveInterval = keepAliveInterval;
            return this;
        }

        public boolean isAutomaticReconnect() {
            return mAutomaticReconnect;
        }

        public Build setAutomaticReconnect(boolean automaticReconnect) {
            mAutomaticReconnect = automaticReconnect;
            return this;
        }

        public MqttLog getMqttLog() {
            return mMqttLog;
        }

        public Build setMqttLog(MqttLog mqttLog) {
            mMqttLog = mqttLog;
            return this;
        }

        public Build addSubscribeInfo(MqttSubscribeInfo mqttSubscribeInfo) {
            mMqttSubscribeInfos.add(mqttSubscribeInfo);
            return this;
        }

        public boolean isBufferEnabled() {
            return mBufferEnabled;
        }

        public Build setBufferEnabled(boolean bufferEnabled) {
            mBufferEnabled = bufferEnabled;
            return this;
        }

        public int getBufferSize() {
            return mBufferSize;
        }

        public Build setBufferSize(int bufferSize) {
            mBufferSize = bufferSize;
            return this;
        }

        public int getMaxInflight() {
            return mMaxInflight;
        }

        public Build setMaxInflight(int maxInflight) {
            mMaxInflight = maxInflight;
            return this;
        }

        public boolean isPersistBuffer() {
            return mPersistBuffer;
        }

        public Build setPersistBuffer(boolean persistBuffer) {
            mPersistBuffer = persistBuffer;
            return this;
        }

        public boolean isDeleteOldestMessages() {
            return mDeleteOldestMessages;
        }

        public Build setDeleteOldestMessages(boolean deleteOldestMessages) {
            mDeleteOldestMessages = deleteOldestMessages;
            return this;
        }

        void check() {
            if (TextUtils.isEmpty(mServerUri)) {
                throw new RuntimeException("serverUri is null");
            }
            if (TextUtils.isEmpty(mClientId)) {
                throw new RuntimeException("clientId is null");
            }
        }
    }
}
