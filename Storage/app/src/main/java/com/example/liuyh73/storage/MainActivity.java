package com.example.liuyh73.storage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private EditText newPassText;
    private EditText confirmPassText;
    private static EditText passText;
    private Button okBtn;
    private Button clearBtn;
    private static final String PREFERENCE_PACKAGE = "com.example.liuyh73.storage";
    private static final int MODE = MODE_PRIVATE;
    private static final String PREFERENCE_NAME = "SavePassWord";
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // newPassText
        newPassText = findViewById(R.id.newPass);
        // confirmPassText
        confirmPassText = findViewById(R.id.confirmPass);
        // passText
        passText = findViewById(R.id.pass);

        // okBtn
        okBtn = findViewById(R.id.ok);
        okBtn.setOnClickListener(new OkBtnListener());
        // clearBtn
        clearBtn = findViewById(R.id.clear);
        clearBtn.setOnClickListener(new ClearBtnListener());
        Context context = null;
        try {
            context = this.createPackageContext(PREFERENCE_PACKAGE, Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // sharedPreferences
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE);
        if(!TextUtils.isEmpty(sharedPreferences.getString("Password", null))) {
            newPassText.setVisibility(View.GONE);
            confirmPassText.setVisibility(View.GONE);
            passText.setVisibility(View.VISIBLE);
        }
    }

    class OkBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(passText.getVisibility() == View.VISIBLE) {
                String password =  sharedPreferences.getString("Password", null);
                if(TextUtils.equals(passText.getText(), password)) {
                    Toast.makeText(MainActivity.this, "Correct Password", Toast.LENGTH_SHORT).show();
                    // 跳转到StorageActivity
                    Intent intent = new Intent(MainActivity.this, StorageActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                }
            } else {
                String newPass = newPassText.getText().toString();
                String confirmPass = confirmPassText.getText().toString();
                Log.i("newPass", newPass);
                Log.i("confirmPass", confirmPass);
                if(!TextUtils.equals(newPass, confirmPass)) {
                    Toast.makeText(MainActivity.this, "Password Mismatch", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)){
                    Toast.makeText(MainActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Set password successfully", Toast.LENGTH_SHORT).show();
                    // 保存密码
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Password", newPass);
                    editor.commit();

                    newPassText.setVisibility(View.GONE);
                    confirmPassText.setVisibility(View.GONE);
                    passText.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    class ClearBtnListener implements  View.OnClickListener{
        @Override
        public void onClick(View v) {
            newPassText.setText("");
            confirmPassText.setText(null);
            passText.setText(null);
        }
    }
}
