package com.niles.mqtt_module;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;
import com.niles.mqtt.MqttMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void publish(View view) {
        count += 1;

        byte[] payload = MyApp.debugInfo("push lazy point");
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
