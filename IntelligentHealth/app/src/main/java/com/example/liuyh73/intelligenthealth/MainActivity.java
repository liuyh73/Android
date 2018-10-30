package com.example.liuyh73.intelligenthealth;

import android.content.DialogInterface;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button searchBtn;
    private RadioGroup radioGroup;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 搜索框
        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new SearchBtnListener());
        // 单选按钮
        radioGroup = findViewById(R.id.radioBtns);
        radioGroup.setOnCheckedChangeListener(new CheckedChangeListener());
        // 输入框
        editText = findViewById(R.id.searchBox);
    }

    class SearchBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v){
            // 判断搜索内容是否为空
            String context = editText.getText().toString();
            if(TextUtils.isEmpty(context)) {
                Toast.makeText(MainActivity.this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
            } else {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                String message = new String();
                RadioButton checkedBtn = findViewById(radioGroup.getCheckedRadioButtonId());
                message = context.equals("Health") ? checkedBtn.getText()+"搜索成功" : "搜索失败";
                alertDialog.setTitle("提示").setMessage(message).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"对话框\"确认\"按钮被点击",Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"对话框\"取消\"按钮被点击",Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.show();
            }
        }
    }

    class CheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton checkedBtn = findViewById(checkedId);
            Toast.makeText(MainActivity. this,checkedBtn.getText()+"被选中", Toast.LENGTH_SHORT).show();
        }
    }
}
