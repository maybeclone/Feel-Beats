package com.silent.feelbeat.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.silent.feelbeat.R;
import com.silent.feelbeat.configs.ConfigServer;
import com.silent.feelbeat.servers.account.ChangePasswordPostAsyncTask;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private EditText newPasswordAgainEditText;
    private Button changeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        bindView();
    }

    private void bindView(){
        oldPasswordEditText = (EditText) findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = (EditText) findViewById(R.id.newPasswordEditText);
        newPasswordAgainEditText = (EditText) findViewById(R.id.newPasswordAgainEditText);
        changeButton = (Button) findViewById(R.id.changeButton);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        changeButton.setOnClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.changeButton:
                if(!oldPasswordEditText.getText().toString().isEmpty() && !newPasswordEditText.getText().toString().isEmpty()
                        && !newPasswordAgainEditText.getText().toString().isEmpty()){
                    if(newPasswordEditText.getText().toString().equals(newPasswordAgainEditText.getText().toString())){
                        new ChangePasswordPostAsyncTask(this, oldPasswordEditText.getText().toString(), newPasswordEditText.getText().toString()).execute(ConfigServer.CHANGE_PASSWORD_URL);
                    } else{
                        Toast.makeText(this, "Password is not the same", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Please complete all information", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
