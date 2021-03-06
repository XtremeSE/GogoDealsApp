
package com.example.colak.gogodeals.Objects;

import android.app.Activity;
import android.util.Log;

import com.example.colak.gogodeals.Controllers.Parsers;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

/*
Connectionmqqt handles the communication with the broker.

/**
 * @author colak on 06/10/16.
 */

public class ConnectionMqtt extends Activity implements MqttCallback {
    // Variables used in the class
    private static final String TAG = "ConnectionMqtt";
    MqttAndroidClient client;
    Activity parent;
    Parsers parsers;
    String payload;
    String sendTopic;
    String receiveTopic;
    int qot;

    /**
    This is the constructor for connectionmqtt, it takes an activity as input.
    it also instanciates a new parsers class.
     @param activity
     */
    public ConnectionMqtt(Activity activity){
        this.parent = activity;
        parsers = new Parsers();
        payload = "";
        sendTopic= "";
        receiveTopic ="";
    }
    /**
   The method sentmqtt is called from other classe and takes a payload and a topic and starts the connection
    and publishing to the broker. This method only publish and dont subscribe.
    @param payload
     @param topic
     */
    public void sendMqtt(String payload, String topic){
        this.payload = payload;
        this.sendTopic = topic;
        this.receiveTopic ="";
        this.qot = 0;
        open();
    }

    /**
    This method is the same as the previous one with the exception of the recievetopic and the qot.
    This method subsrcibes to the topic given.
     @param payload
     @param qot
     @param receiveTopic
     @param sendTopic
     */
    public void sendMqtt(String payload, String sendTopic, String receiveTopic, int qot){
        this.payload = payload;
        this.sendTopic = sendTopic;
        this.receiveTopic = receiveTopic;
        this.qot = qot;
        open();
    }

    /*
    Create and establish an MQTT-ConnectionMqtt
     */
    public void open() {
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(parent.getApplicationContext(), "tcp://54.154.153.243:1883",
        //client = new MqttAndroidClient(parent.getApplicationContext(), "tcp://176.10.136.208:1883",
                clientId);
        client.setCallback(this);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    if (receiveTopic.equals("")){
                        publish(payload,sendTopic);
                        close();
                    }else{
                        subscribe(receiveTopic,qot);
                    }
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. ConnectionMqtt timeout or firewall problems
                    Log.d(TAG, "onFailure");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /*
    This method publishes the payload to the given topic.
     */
    public void publish(String payload, String topic){
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
    /*
     This method subsribes to the topic given with the qos given.
     */
    public void subscribe(final String topic, int qos){
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published;
                    publish(payload,sendTopic);
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void connectionLost(Throwable cause){
        Log.d(TAG, "connection lost");
    }

    /*
    This method closes the connection.
     */
    public void close(){
        try {
            client.disconnect();
            Log.i("disconnected","");
        } catch (MqttException e) {
            Log.i("disconnect failed",e.toString());
            e.printStackTrace();
        }
    }

    /**When a message arrive from a subsribed topic this method calls the parsers class method parse.
     * @param message
     * @param topic */
    public void messageArrived(String topic, MqttMessage message) throws MqttException {
        try {
            parsers.parse(topic,message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        client.unsubscribe(topic);
        //close();

    }

    /**
     * Called when publish has been completed and accepted by broker.
     * @param token */
    public void deliveryComplete(IMqttDeliveryToken token){
    }
}





