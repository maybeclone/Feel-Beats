package com.silent.feelbeat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.silent.feelbeat.R;
import com.silent.feelbeat.configs.ConfigServer;
import com.silent.feelbeat.models.account.User;
import com.silent.feelbeat.servers.account.LoginPostUserAsyncTask;

/**
 * Created by silent on 5/16/2018.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerText;
    private TextView forgotPasswordText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindView();
    }

    private void bindView(){
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerText = (TextView) findViewById(R.id.registerText);
        forgotPasswordText = (TextView) findViewById(R.id.forgotPasswordText);

        loginButton.setOnClickListener(this);
        forgotPasswordText.setOnClickListener(this);
        registerText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginButton:
                if(!emailEditText.getText().toString().isEmpty() && !passwordEditText.getText().toString().isEmpty()){
                    new LoginPostUserAsyncTask(this,
                            new User(emailEditText.getText().toString(), passwordEditText.getText().toString())).execute(ConfigServer.LOGIN_URL);
                } else {
                    Toast.makeText(this, "Please complete all information", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.registerText:
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.forgotPasswordText:
                Toast.makeText(this, "Thì thôi", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
