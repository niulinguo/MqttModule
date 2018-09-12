package com.niles.mqtt_module;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;
import com.niles.mqtt.MqttMessage;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                count += 1;
                publish(null);
            }
        }, 10000, 10000);
    }

    public void publish(View view) {
        byte[] payload = MyApp.debugInfo(String.valueOf(count));
        Log.e("payload", ConvertUtils.bytes2HexString(payload));
        ((MyApp) getApplication()).getMqttClientManager().
                publish(MqttMessage.newBuilder()
                        .setQos(2)
                        .setRetained(true)
                        .setTopic("useiov/event")
                        .setPayload(payload)
                        .build());
    }
}
