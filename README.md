# MqttModule

[Mqtt Android SDK v1.1.1][Mqtt Android SDK v1.1.1]封装

## 集成

1. 使用 jitpack 仓库

``` Gradle
allprojects {
    repositories {
        ...
        maven { url 'https://www.jitpack.io' }
    }
}
```

2. 引入 Library

``` Gradle
dependencies {
        implementation 'com.github.niulinguo:MqttModule:v1.2'
}
```

## 使用方式

1. 初始化

``` java
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
    .build());
```

2. mqtt 连接

``` java
mMqttClientManager.connect();
```

3. 发布消息

``` java
mMqttClientManager.publish(MqttMessage.newBuilder()
    .setQos(2)
    .setRetained(true)
    .setTopic("useiov/event")
    .setPayload(payload)
    .build());
```


[Mqtt Android SDK v1.1.1]: https://github.com/eclipse/paho.mqtt.android