package com.example.liuyh73.healthyfoods;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.Image;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class Detail extends AppCompatActivity {
    /**
     * back 返回图片
     * star 星星图片
     * detailCollectBtn 收藏图片
     * food 当前显示的食品
     * Request_Code 请求码
     * operationListView 操作列表
     * operations 操作数组
     * DYNAMICACTION 动态广播名称
     */
    private ImageView back;
    private ImageView star;
    private ImageView detailCollectBtn;
    private Collection food;
    private static int REQUEST_CODE = 2;
    private boolean alreadyCollected = true;
    private ListView operationListView;
    private String[] operations={"分享信息", "不感兴趣", "查看更多信息", "出错反馈"};
    private static final String DYNAMICACTION = "com.example.liuyh73.healthyfoods.MyDynamicFilter";
    private static final String WIDGETDYNAMICACTION = "com.example.liuyh73.healthyfoods.MyWidgetDynamicFilter";
    private DynamicReceiver dynamicReceiver;
    private DynamicReceiver widgetDynamicReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        fillFoodInfo();
        // 注册广播
        IntentFilter dynamicFilter = new IntentFilter();
        dynamicFilter.addAction(DYNAMICACTION); //添加动态广播的Action
        dynamicReceiver = new DynamicReceiver();
        registerReceiver(dynamicReceiver, dynamicFilter);
        // 注册widget广播
        IntentFilter widgetDynamicFilter = new IntentFilter();
        widgetDynamicFilter.addAction(WIDGETDYNAMICACTION);
        widgetDynamicReceiver = new DynamicReceiver();
        registerReceiver(widgetDynamicReceiver, widgetDynamicFilter);

        back = findViewById(R.id.back);
        star = findViewById(R.id.star);
        detailCollectBtn = findViewById(R.id.detailCollectBtn);
        operationListView = findViewById(R.id.listView);
        ArrayAdapter<String>operationListViewAdapter = new ArrayAdapter<>(this, R.layout.operation, operations);
        operationListView.setAdapter(operationListViewAdapter);
        star.setTag(0);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((int)star.getTag() == 0) {
                    star.setImageResource(R.mipmap.full_star);
                    star.setTag(1);
                } else {
                    star.setImageResource(R.mipmap.empty_star);
                    star.setTag(0);
                }
            }
        });
        detailCollectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                food.setIsCollected(true);
                // 发送广播
                Intent intentBroadcast = new Intent();
                intentBroadcast.setAction(DYNAMICACTION);
                Bundle bundles = new Bundle();
                bundles.putSerializable("Collecting food", food);
                bundles.putSerializable("Icon", R.mipmap.empty_star);
                intentBroadcast.putExtras(bundles);
                sendBroadcast(intentBroadcast);
                // 发送MessageEvent
                EventBus.getDefault().post(new MessageEvent(food.getName()));
                Toast.makeText(Detail.this, "已收藏", Toast.LENGTH_SHORT).show();

                // 发送widget广播
                Intent widgetBroadcast = new Intent();
                widgetBroadcast.setAction(WIDGETDYNAMICACTION);
                widgetBroadcast.putExtras(bundles);
                sendBroadcast(widgetBroadcast);
            }
        });
    }

    private void fillFoodInfo() {
        Bundle extras = getIntent().getExtras();
        food = (Collection) extras.get("Current food");
        if (food == null) {
            food = (Collection) extras.get("Recommend Food");
        }
        if (food == null) {
            food = (Collection) extras.get("Food notified");
        }
        TextView foodName = findViewById(R.id.foodName);
        TextView category = findViewById(R.id.category);
        TextView nutritive = findViewById(R.id.nutritive);
        RelativeLayout top = findViewById(R.id.top);
        foodName.setText(food.getName());
        category.setText(food.getCategory());
        nutritive.setText(food.getNutritive());
        top.setBackgroundColor(Color.parseColor(food.getBgColor()));
    }

    @Override
    public void onRestart(){
        super.onRestart();
        food = null;
        fillFoodInfo();
    }

    @Override
    public void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销dynamicReceiver
        unregisterReceiver(dynamicReceiver);
        unregisterReceiver(widgetDynamicReceiver);
    }
}
