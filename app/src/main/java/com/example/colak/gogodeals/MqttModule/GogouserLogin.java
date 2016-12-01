package com.example.colak.gogodeals.MqttModule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.colak.gogodeals.R;

/**
 * Created by Nikos on 01/12/2016.
 */

public class GogouserLogin extends AppCompatActivity {

    EditText loginEmail;
    EditText loginPassword;

    Button loginBtn;

    static ConnectionMqtt gogoUserMqtt;
    Parsers logincheck;

    public static ProgressDialog mProgressDlg;
    public static boolean loginResult;

    public static String email;
    public static String password;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gogoUserMqtt = new ConnectionMqtt(this);
        setContentView(R.layout.gogo_profile_login);

        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);

        loginBtn = (Button) findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View V) {
                gogoLogin();
            }
        });
    }

    private void gogoLogin() {

        email = loginEmail.getText().toString();
        password = loginPassword.getText().toString();

        String topic = "deal/gogodeals/user/info";
        String payload = "{\"id\":\"12345678-1011-M012-N210-112233445566\",\"data\":{\"email\":\""
                + email + "\",\"password\": \"" + password + "\"},}";

        String userSubscribe = "deal/gogodeals/database/users";
        gogoUserMqtt.sendMqtt(payload, topic, userSubscribe, 2);

        Log.i("loginfielads: ", email + password);

        //gogoUserMqtt.sendMqtt(topic, payload);
        Parsers.gogouserLogin=this;
        mProgressDlg = new ProgressDialog(this);
        mProgressDlg.setMessage("Validating");
        mProgressDlg.setCancelable(false);
        mProgressDlg.show();




      /*  if (logincheck){
            Intent login = new Intent (GogouserLogin.this, MapsActivity.class);
            startActivity(login);

        }
        else {
            Toast.makeText(getApplicationContext(), "Wrong credentials", Toast.LENGTH_LONG).show();
        }*/

    }

    public void loginResultReceived(){

        Log.i("8 :", String.valueOf(loginResult));
        if (loginResult){

            Toast.makeText(getApplicationContext(), "Login succesfull", Toast.LENGTH_LONG).show();
            Intent login = new Intent (GogouserLogin.this, MapsActivity.class);
            startActivity(login);


        }
        else {
            Toast.makeText(getApplicationContext(), "Wrong credentials", Toast.LENGTH_LONG).show();
        }
    }


}



