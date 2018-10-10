package com.niles.mqtt;

import android.text.TextUtils;

/**
 * Created by Niles
 * Date 2018/10/10 17:57
 * Email niulinguo@163.com
 */
public class MqttSubscribeInfo {

    private final String mTopic;
    private final int mQos;

    private MqttSubscribeInfo(String topic, int qos) {
        mTopic = topic;
        mQos = qos;
    }

    public int getQos() {
        return mQos;
    }

    public String getTopic() {
        return mTopic;
    }

    public static final class Builder {
        private String mTopic;
        private int mQos = 2;

        public String getTopic() {
            return mTopic;
        }

        public Builder setTopic(String topic) {
            mTopic = topic;
            return this;
        }

        public int getQos() {
            return mQos;
        }

        public Builder setQos(int qos) {
            mQos = qos;
            return this;
        }

        public MqttSubscribeInfo build() {
            if (TextUtils.isEmpty(mTopic)) {
                throw new RuntimeException("Topic Is Null");
            }
            return new MqttSubscribeInfo(
                    mTopic,
                    mQos
            );
        }
    }
}
