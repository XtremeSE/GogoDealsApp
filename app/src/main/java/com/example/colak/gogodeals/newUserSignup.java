package com.example.colak.gogodeals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class newUserSignup extends AppCompatActivity{

    //xml gui elements
    EditText regUsername;
    EditText regEmail;
    EditText regEmailConfirmation;
    EditText regPassword;
    EditText regPasswordConfirmation;

    TextView signupTips;

    Button gogosignup;


    static ConnectionMqtt connection1;
    @Override

    //Connect class with xml file
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            //opens newuser_signup layout screen
            setContentView(R.layout.newuser_signup);

            //setting an mqtt connection from ConnectionMqtt class
            connection1 = new ConnectionMqtt(this);

            //newuser_signup xml fields-buttons-text
            regUsername = (EditText) findViewById(R.id.regUsername); //username field
            regEmail = (EditText) findViewById(R.id.regEmail); //email field
            regEmailConfirmation = (EditText) findViewById(R.id.regEmailConfirmation); //email confirmation field
            regPassword = (EditText) findViewById(R.id.regPassword); //password field
            regPasswordConfirmation = (EditText) findViewById(R.id.regPasswordConfirmation); //password confirmation field
            gogosignup = (Button) findViewById(R.id.gogosignup); //signup button
            signupTips = (TextView) findViewById(R.id.signupTips); //textview on bottom of screen



            //signup tips on bottom of user screen
            signupTips.setText("Tips:");

            //define new line by append android system line separator
            signupTips.append(System.getProperty("line.separator"));
            signupTips.append("1. Username, email and password must not have same values");
            signupTips.append(System.getProperty("line.separator"));
            signupTips.append("2. Username cannot be less than 5 characters");
            signupTips.append(System.getProperty("line.separator"));
            signupTips.append("3. Password cannot be less than 8 characters");
            signupTips.append(System.getProperty("line.separator"));
            signupTips.append("4. No empty fields allowed");

            //signup button
            gogosignup.setOnClickListener(new View.OnClickListener() {
                public void onClick(View V) {
                    registerGogouser();
                }
            });
        }

    //Register gogodeals user (without facebook)
    private void registerGogouser() {

        //sign up text fields variables to avoid long-writing check statements
        String regUser = regUsername.getText().toString();
        String regMail = regEmail.getText().toString();
        String regPass = regPassword.getText().toString();
        String regMailConf = regEmailConfirmation.getText().toString();
        String regPassConf = regPasswordConfirmation.getText().toString();


        //topic and payload which will add user to database
        String topic = "deal/gogodeals/user/new";
        String payload = "{\"id\":\"12345678-1011-M012-N210-112233445566\",\"data\":{\"name\":\""
                + regUser + "\",\"password\": \"" + regPass + "\",\"email\": \"" + regMail + "\"},}";


        //if not every credential field is filled out, user must check tips on his screen
        if (regUser.isEmpty() || regMail.isEmpty()
                || regPass.isEmpty() || regMailConf.isEmpty() || regPassConf.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Check tips", Toast.LENGTH_SHORT).show();
        }

        //if username, password and/or email have same values, user must check tips on his screen
        else if (regUser.equals(regPass) || regUser.equals(regMail)
                || regMail.equals(regPass) || regUser.length() < 1 || regPass.length() < 1) {
            Toast.makeText(getApplicationContext(), "Check tips", Toast.LENGTH_SHORT).show();
        }

        //confirmation fields are not properly filled out
        else if (!regMail.equals(regMailConf) || !regPass.equals(regPassConf)) {
            Toast.makeText(getApplicationContext(), "Confirm email or password", Toast.LENGTH_SHORT).show();
        }

        //If credential fields are not empty, username, password, email are different between them
        //and if username has more than 4 characters, password has more than 7 characters
        //credential fields are filled out properly and user signs up
        else if ((!regUser.isEmpty() && !regPass.isEmpty() &&
                !regMail.isEmpty() && !regMailConf.isEmpty() && !regPassConf.isEmpty())) {
            if (regUser.length() >= 1 && regPass.length() >= 1) {
                if ((!regUser.equals(regPass) && !regMail.equals(regPass) &&
                        !regUser.equals(regMail) && regMail.equals(regMailConf) && regPass.equals(regPassConf))) {


                    Toast.makeText(getApplicationContext(), "Welcome:" + regUser, Toast.LENGTH_SHORT).show();

                    //opens mqtt connection and sends data to database
                    connection1.sendMqtt(topic, payload);
                    //Parsers.checkEmail();

                    Log.i("topic payload: ", topic + " " + payload);

                    //clear fields
                    regUsername.getText().clear();
                    regPassword.getText().clear();
                    regEmail.getText().clear();
                    regEmailConfirmation.getText().clear();
                    regPasswordConfirmation.getText().clear();

                   Intent backToLogIn= new Intent(newUserSignup.this, MainActivity.class);
                    startActivity(backToLogIn);

                }
            }
        }
    }


    }






