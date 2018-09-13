package com.niles.mqtt_module;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.Utils;
import com.niles.mqtt.DefaultMqttLog;
import com.niles.mqtt.MqttClientManager;
import com.niles.mqtt.MqttConfig;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * Created by Niles
 * Date 2018/9/3 22:40
 * Email niulinguo@163.com
 */
public class MyApp extends Application {

    private MqttClientManager mMqttClientManager;

    public static byte[] debugInfo(String info) {
        byte[] debugInfo = info.getBytes();
        int size = 12 + 8 + 16 + 4 + debugInfo.length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putShort((short) (size & 0xffff))
                .put((byte) 0x01)
                .put((byte) 0x31)
                .put((byte) 0x03)
                .put((byte) 0x00)
                .put((byte) 0x01)
                .put((byte) 0x00)
                .putInt((int) (System.currentTimeMillis() / 1000));
        byteBuffer.put(chars2Bytes("T0001212".toCharArray()))
                .put(ConvertUtils.hexString2Bytes(UUID.randomUUID().toString().replace("-", "")))
                .putInt((int) (System.currentTimeMillis() / 1000))
                .put(debugInfo);
        return byteBuffer.array();
    }

    private static byte[] chars2Bytes(char[] chars) {
        int count = chars == null ? 0 : chars.length;
        byte[] bytes = new byte[count];
        for (int i = 0; i < count; i++) {
            bytes[i] = (byte) chars[i];
        }
        return bytes;
    }

    public MqttClientManager getMqttClientManager() {
        return mMqttClientManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);

        mMqttClientManager = new MqttClientManager(this, MqttConfig
                .newBuilder()
                .setAutomaticReconnect(true)
                .setBufferEnabled(true)
                .setBufferSize(10000)
                .setMaxInflight(10000)
                .setCleanSession(false)
                .setClientId("demoClient")
                .setDeleteOldestMessages(false)
                .setPersistBuffer(true)
                .setTraceEnable(true)
                .setServerUri(BuildConfig.SERVER_URI)
                .setUsername(BuildConfig.USERNAME)
                .setPassword(BuildConfig.PASSWORD)
                .setMqttLog(new DefaultMqttLog() {
                    @Override
                    public void log(String tag, Bundle bundle) {
                        if (BuildConfig.DEBUG) {
                            if (bundle == null) {
                                Log.i("MqttLog", "[" + tag + "]");
                            } else {
                                Log.i("MqttLog", "[" + tag + "]" + bundle.toString());
                            }
                        }
                    }
                })
                .build());

        mMqttClientManager.connect();
    }
}
