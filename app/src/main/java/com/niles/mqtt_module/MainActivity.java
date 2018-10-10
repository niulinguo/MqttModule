package com.niles.mqtt_module;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;
import com.niles.mqtt.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private static final String PUBLISH_TOPIC = "v1/devices/me/telemetry";
    private int count = 0;
//    private static final String PUBLISH_TOPIC = "useiov/event";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                publish(null);
//            }
//        }, 10000, 10000);
    }

    public void publish(View view) {
        byte[] payload = MyApp.debugInfo(count++);
        Log.e("payload", ConvertUtils.bytes2HexString(payload));
        ((MyApp) getApplication()).getMqttClientManager().
                publish(MqttMessage.newBuilder()
                        .setQos(2)
                        .setRetained(true)
                        .setTopic(PUBLISH_TOPIC)
                        .setPayload(payload)
                        .build());
    }
}
