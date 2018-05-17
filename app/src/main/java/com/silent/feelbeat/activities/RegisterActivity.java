package com.silent.feelbeat.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Paint;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.silent.feelbeat.R;
import com.silent.feelbeat.configs.ConfigServer;
import com.silent.feelbeat.models.account.User;
import com.silent.feelbeat.servers.account.RegisterPostUserAsyncTask;

import java.util.Calendar;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener{

    private Toolbar toolbar;
    private EditText emailEditText;
    private EditText passwordEditText, passwordAgainEditText;
    private EditText birthdayEditText;
    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton, femaleRadioButton;
    private Button createButton;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bindView();
    }

    private void bindView(){
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordAgainEditText = (EditText) findViewById(R.id.passwordAgainText);
        birthdayEditText = (EditText) findViewById(R.id.birthdayEditText);
        genderRadioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);
        maleRadioButton = (RadioButton) findViewById(R.id.maleRadioButton);
        femaleRadioButton = (RadioButton) findViewById(R.id.femaleRadioButton);
        createButton = (Button) findViewById(R.id.createButton);

        createButton.setOnClickListener(this);
        birthdayEditText.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(
                this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

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
            case R.id.createButton:
                if(!emailEditText.getText().toString().isEmpty() && !passwordEditText.getText().toString().isEmpty()
                        && !passwordAgainEditText.getText().toString().isEmpty() && genderRadioGroup.getCheckedRadioButtonId() != -1){

                    if(passwordEditText.getText().toString().equals(passwordAgainEditText.getText().toString())){
                        User user = new User();
                        user.email = emailEditText.getText().toString();
                        user.password = passwordEditText.getText().toString();
                        user.birthday = new Date(birthdayEditText.getText().toString());
                        user.gender = (femaleRadioButton.isChecked()) ? 0 : 1;
                        new RegisterPostUserAsyncTask(this, user).execute(ConfigServer.REGISTER_URL);
                    } else {
                        Toast.makeText(this, "Password is not the same", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Please complete all information", Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.birthdayEditText:
                datePickerDialog.show();
                break;
        }
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        birthdayEditText.setText(year+"/"+(month+1)+"/"+dayOfMonth);
    }
}
