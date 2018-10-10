package com.niles.mqtt;

import android.text.TextUtils;

/**
 * Created by Niles
 * Date 2018/8/30 16:35
 * Email niulinguo@163.com
 */
public class MqttMessage {

    private final String mTopic;
    private final int mQos;
    private final byte[] mPayload;
    private final boolean mRetained;

    private MqttMessage(String topic,
                        int qos,
                        byte[] payload,
                        boolean retained) {
        mTopic = topic;
        mQos = qos;
        mPayload = payload;
        mRetained = retained;
    }

    public static Build newBuilder() {
        return new Build();
    }

    public String getTopic() {
        return mTopic;
    }

    public boolean isRetained() {
        return mRetained;
    }

    public byte[] getPayload() {
        return mPayload;
    }

    public int getQos() {
        return mQos;
    }

    public static class Build {

        private String mTopic;
        private int mQos = 2;
        private byte[] mPayload;
        private boolean mRetained = true;

        public MqttMessage build() {
            check();
            return new MqttMessage(mTopic,
                    mQos,
                    mPayload,
                    mRetained);
        }

        public String getTopic() {
            return mTopic;
        }

        public Build setTopic(String topic) {
            mTopic = topic;
            return this;
        }

        public int getQos() {
            return mQos;
        }

        public Build setQos(int qos) {
            mQos = qos;
            return this;
        }

        public byte[] getPayload() {
            return mPayload;
        }

        public Build setPayload(byte[] payload) {
            mPayload = payload;
            return this;
        }

        public boolean isRetained() {
            return mRetained;
        }

        public Build setRetained(boolean retained) {
            mRetained = retained;
            return this;
        }

        private void check() {
            if (TextUtils.isEmpty(mTopic)) {
                throw new RuntimeException("topic is null");
            }
            if (mPayload == null) {
                throw new RuntimeException("payload is null");
            }
        }
    }

}
