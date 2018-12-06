package com.example.liuyh73.bubbleview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.cpiz.android.bubbleview.BubblePopupWindow;
import com.cpiz.android.bubbleview.BubbleStyle;
import com.cpiz.android.bubbleview.BubbleTextView;

public class MainActivity extends AppCompatActivity {
    private Button click;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        click = findViewById(R.id.iv2);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View rootView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.simple_text_bubble, null);
                BubbleTextView bubbleView = (BubbleTextView) rootView.findViewById(R.id.popup_bubble);
                BubblePopupWindow window = new BubblePopupWindow(rootView, bubbleView);
                window.setCancelOnTouch(true);
                window.setCancelOnTouchOutside(true);
                window.setCancelOnLater(3000);
                window.showArrowTo(v, BubbleStyle.ArrowDirection.Down);
            }
        });
    }
}
