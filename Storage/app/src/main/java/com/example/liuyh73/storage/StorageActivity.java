package com.example.liuyh73.storage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class StorageActivity extends AppCompatActivity {
    private Button saveBtn;
    private Button loadBtn;
    private Button storageClearBtn;
    private EditText fileContentText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        // saveBtn
        saveBtn = findViewById(R.id.save);
        saveBtn.setOnClickListener(new SaveBtnListener());

        // loadBtn
        loadBtn = findViewById(R.id.load);
        loadBtn.setOnClickListener(new LoadBtnListener());

        // clearBtn
        storageClearBtn = findViewById(R.id.storageClear);
        storageClearBtn.setOnClickListener(new ClearBtnListener());

        // fileContentText
        fileContentText = findViewById(R.id.fileContent);
    }

    class SaveBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            try (FileOutputStream fileOutputStream = openFileOutput("File.txt", MODE_PRIVATE)) {
                String str = fileContentText.getText().toString();
                fileOutputStream.write(str.getBytes());
                Log.i("TAG", "Successfully saved file.");
                Toast.makeText(StorageActivity.this, "Save Successfully", Toast.LENGTH_SHORT).show();
                fileOutputStream.flush();
            } catch (IOException ex) {
                Log.e("TAG", "Fail to save file.");
                Toast.makeText(StorageActivity.this, "Fail to save, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class LoadBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            try (FileInputStream fileInputStream = openFileInput("File.txt")) {
                byte[] contents = new byte[fileInputStream.available()];
                fileInputStream.read(contents);
                Log.i("contents", new String(contents));
                fileContentText.setText(new String(contents));
                Toast.makeText(StorageActivity.this, "Load Successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException ex) {
                Log.e("TAG", "Fail to read file.");
                Toast.makeText(StorageActivity.this, "Fail to load, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class ClearBtnListener implements  View.OnClickListener{
        @Override
        public void onClick(View v) {
            fileContentText.setText(null);
        }
    }
}
