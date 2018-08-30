package com.niles.mqtt;

/**
 * Created by Niles
 * Date 2018/8/30 09:25
 * Email niulinguo@163.com
 */
public interface MqttConnHandler {

    void changeConnStatus(MqttConnStatus status);

    MqttConnStatus getConnStatus();

}
