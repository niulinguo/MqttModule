package com.niles.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistable;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.internal.ExceptionHelper;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttPublish;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttWireMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Niles
 * Date 2018/9/12 16:52
 * Email niulinguo@163.com
 */
public class MyMqttFilePersistence extends MqttDefaultFilePersistence {

    private static final String PERSISTENCE_PUBLISH_PREFIX = "pub-";

    private static final int MIN_MSG_ID = 1;        // Lowest possible MQTT message ID to use
    private static final int MAX_MSG_ID = 65535;    // Highest possible MQTT message ID to use
    private int nextMsgId = MIN_MSG_ID - 1;         // The next available message ID to use
    private Hashtable<Integer, Integer> inUseMsgIds;// Used to store a set of in-use message IDs

    MyMqttFilePersistence(String directory) {
        super(directory);
        init();
    }

    private void init() {
        inUseMsgIds = new Hashtable<>();
    }

    /**
     * Releases a message ID back into the pool of available message IDs.
     * If the supplied message ID is not in use, then nothing will happen.
     *
     * @param msgId A message ID that can be freed up for re-use.
     */
    private synchronized void releaseMessageId(int msgId) {
        inUseMsgIds.remove(msgId);
    }

    private void removeMessage(int msgId) {
        try {
            remove(createPublishKey(msgId));
            releaseMessageId(msgId);
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        }
    }

    public void removeMessage(MqttPublish mqttPublish) {
        removeMessage(mqttPublish.getMessageId());
    }

    /**
     * Get the next MQTT message ID that is not already in use, and marks
     * it as now being in use.
     *
     * @return the next MQTT message ID to use
     */
    private synchronized int getNextMessageId() throws MqttException {
        int startingMessageId = nextMsgId;
        // Allow two complete passes of the message ID range. This gives
        // any asynchronous releases a chance to occur
        int loopCount = 0;
        do {
            nextMsgId++;
            if (nextMsgId > MAX_MSG_ID) {
                nextMsgId = MIN_MSG_ID;
            }
            if (nextMsgId == startingMessageId) {
                loopCount++;
                if (loopCount == 2) {
                    throw ExceptionHelper.createMqttException(MqttException.REASON_CODE_NO_MESSAGE_IDS_AVAILABLE);
                }
            }
        } while (inUseMsgIds.containsKey(nextMsgId));
        Integer id = nextMsgId;
        inUseMsgIds.put(id, id);
        return nextMsgId;
    }

    /**
     * 保存Mqtt消息
     */
    public void put(MqttMessage message) {
        org.eclipse.paho.client.mqttv3.MqttMessage mqttMessage = new org.eclipse.paho.client.mqttv3.MqttMessage();
        mqttMessage.setPayload(message.getPayload());
        mqttMessage.setQos(message.getQos());
        mqttMessage.setRetained(message.isRetained());
        try {
            int msgId = getNextMessageId();
            MqttPublish mqttPublish = new MqttPublish(message.getTopic(), mqttMessage);
            mqttPublish.setMessageId(msgId);
            put(createPublishKey(msgId), mqttPublish);
        } catch (MqttException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String createPublishKey(int id) {
        return PERSISTENCE_PUBLISH_PREFIX + id;
    }

    private MqttWireMessage restoreMessage(String key, MqttPersistable persistable) throws MqttException {
        MqttWireMessage message = null;
        try {
            message = MqttWireMessage.createWireMessage(persistable);
        } catch (MqttException ex) {
            if (ex.getCause() instanceof EOFException) {
                // Premature end-of-file means that the message is corrupted
                if (key != null) {
                    remove(key);
                }
            } else {
                throw ex;
            }
        }
        return message;
    }

    @Override
    public synchronized void open(String clientId, String theConnection) throws MqttPersistenceException {
        super.open(clientId, theConnection);

        // 初始化被保存的消息
        Enumeration keys = keys();
        String key;
        MqttPersistable persistable;
        MqttWireMessage message;
        while (keys.hasMoreElements()) {
            key = (String) keys.nextElement();
            persistable = get(key);
            try {
                message = restoreMessage(key, persistable);
            } catch (MqttException e) {
                e.printStackTrace();
                message = null;
            }
            if (message != null) {
                if (key.startsWith(PERSISTENCE_PUBLISH_PREFIX)) {
                    int messageId = message.getMessageId();
                    inUseMsgIds.put(messageId, messageId);
                }
            }
        }
    }

    /**
     * 获取保存的未发送的消息
     */
    public List<MqttPublish> getSavedPublishMessage() throws MqttPersistenceException {
        List<MqttPublish> mqttPublishes = new ArrayList<>();
        Enumeration keys = keys();
        String key;
        MqttPersistable persistable;
        MqttWireMessage message;
        while (keys.hasMoreElements()) {
            key = (String) keys.nextElement();
            persistable = get(key);
            try {
                message = restoreMessage(key, persistable);
            } catch (MqttException e) {
                e.printStackTrace();
                message = null;
            }
            if (message != null) {
                if (key.startsWith(PERSISTENCE_PUBLISH_PREFIX)) {
                    mqttPublishes.add((MqttPublish) message);
                }
            }
        }
        return mqttPublishes;
    }
}
