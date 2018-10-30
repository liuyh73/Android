package com.example.liuyh73.healthyfoods;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;


public class MainActivity extends AppCompatActivity {
    /**
     * 食品数据：foodList
     * 收藏夹数据：collectList
     * 食品列表：recycleView
     * 收藏夹列表：listView
     * 食品列表适配器：recyclerViewAdapter
     * 收藏夹列表适配器：listViewAdapter
     * 收藏夹图标：collectBtn
     * 请求码：REQUEST_CODE
     * 静态广播名称：STATICACTION
     */
    private List<Collection> foodList = new ArrayList<>();
    private List<Collection> collectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ListView listView;
    private MyRecyclerViewAdapter recyclerViewAdapter;
    private MyListViewAdapter listViewAdapter;
    private FloatingActionButton collectBtn;
    private static int REQUEST_CODE = 1;
    private static final String STATICACTION = "com.example.liuyh73.healthyfoods.MyStaticFilter";
    private static final String WIDGETSTATICACTION = "com.example.liuyh73.healthyfoods.MyWidgetStaticFilter";
    private static int randomIndex;
    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey("Collections")){
            recyclerView.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 注册订阅者
        EventBus.getDefault().register(this);
        // 初始化listView
        listView = findViewById(R.id.listView);
        // 初始化foodList
        foodList.add(new Collection("大豆", "粮", "粮食", "蛋白质", "#BB4C3B"));
        foodList.add(new Collection("十字花科蔬菜", "蔬", "肉食", "维生素C", "#C48D30"));
        foodList.add(new Collection("牛奶", "饮", "饮品", "钙", "#4469B0"));
        foodList.add(new Collection("海鱼", "肉", "肉食", "蛋白质", "#20A17B"));
        foodList.add(new Collection("菌菇类", "蔬", "蔬菜", "微量元素", "#BB4C3B"));
        foodList.add(new Collection("番茄", "蔬", "蔬菜", "番茄红素", "#4469B0"));
        foodList.add(new Collection("胡萝卜", "蔬", "蔬菜", "胡萝卜素", "#20A17B"));
        foodList.add(new Collection("芥麦", "粮", "粮食", "膳食纤维", "#BB4C3B"));
        foodList.add(new Collection("鸡蛋", "杂", "杂", "几乎所有营养物质", "#C48D30"));

        // 初始化recyclerView
        recyclerView = findViewById(R.id.recyclerView);
        // 初始化MyRecyclerViewAdapter
        recyclerViewAdapter = new MyRecyclerViewAdapter<Collection>(MainActivity.this, R.layout.item, this.foodList) {
            @Override
            public void convert(MyViewHolder holder, Collection s) {
                // Colloction是自定义的一个类，封装了数据信息，也可以直接将数据做成一个Map，那么这里就是Map<String, Object>
                TextView name = holder.getView(R.id.name);
                name.setText(s.getName());
                TextView abbr = holder.getView(R.id.abbr);
                abbr.setText(s.getAbbr());
            }
        };
        // 设置LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        // 设置Animation
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(recyclerViewAdapter);
        scaleInAnimationAdapter.setDuration(1000);
        recyclerView.setAdapter(scaleInAnimationAdapter);
        recyclerView.setItemAnimator(new OvershootInLeftAnimator());

        // 初始化collectList
        collectList.add(new Collection("收藏夹", "*", "", "", ""));
        // 初始化listView
        listView = findViewById(R.id.listView);
        // 初始化listViewAdapter
        listViewAdapter = new MyListViewAdapter(collectList, MainActivity.this);
        // 设置 listViewAdapter
        listView.setAdapter(listViewAdapter);
        // 初始化visible
        recyclerView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);
        // 设定食品点击监听事件
        recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Collection food = foodList.get(position);
                Intent intent = new Intent(MainActivity.this, Detail.class);
                intent.putExtra("Current food", food);
                startActivityForResult(intent, REQUEST_CODE);
            }
            @Override
            public void onLongClick(int position) {
                Collection food = foodList.get(position);
                foodList.remove(position);
                recyclerViewAdapter.notifyItemRemoved(position);
                Toast.makeText(MainActivity.this, "删除"+food.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        // 设置购物栏点击事件
        collectBtn = findViewById(R.id.collectBtn);
        collectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    collectBtn.setImageResource(R.mipmap.mainpage);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                    collectBtn.setImageResource(R.mipmap.collections);
                }
            }
        });

        // 设置收藏夹列表项点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) return;
                Collection food = collectList.get(position);
                Intent intent = new Intent(MainActivity.this, Detail.class);
                intent.putExtra("Current food", food);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        // 设置收藏夹列表项长点击事件
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(position == 0) return true;
                final Collection food = collectList.get(position);
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setIcon(R.mipmap.ic_launcher).setTitle("删除").setMessage("确定删除"+food.getName()).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        collectList.remove(food);
                        for(int i=0;i<foodList.size();i++){
                            if(food.getName().equals(foodList.get(i).getName())){
                                food.setIsCollected(false);
                                foodList.set(i, food);
                                break;
                            }
                        }
                        listViewAdapter.refresh(collectList);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.show();
                return true;
            }
        });
        // 发送静态广播
        Random random = new Random();
        randomIndex = random.nextInt(foodList.size());
        Intent intentBroadCast = new Intent(STATICACTION);
        Bundle bundles = new Bundle();
        bundles.putSerializable("Food notified", foodList.get(randomIndex));
        bundles.putSerializable("Icon", R.mipmap.empty_star);
        intentBroadCast.putExtras(bundles);
        sendBroadcast(intentBroadCast);

        // widget广播
         widgetBroadcast(randomIndex);
    }
    @Override
    public void onRestart() {
        super.onRestart();
        Random random = new Random();
        randomIndex = random.nextInt(foodList.size());
        widgetBroadcast(randomIndex);
    }

    public void widgetBroadcast(int index){
        Intent widgetIntentBroadcast = new Intent();
        widgetIntentBroadcast.setAction(WIDGETSTATICACTION);
        Bundle extras = new Bundle();
        extras.putSerializable("Recommend Food", foodList.get(index));
        // bundles.putSerializable("Icon", R.mipmap.empty_star);
        widgetIntentBroadcast.putExtras(extras);
        sendBroadcast(widgetIntentBroadcast);
    }
//    此函数被EventBus所替代
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//        if(requestCode == REQUEST_CODE) {
//            if (resultCode == 2) {
//                Bundle extras = intent.getExtras();
//                Collection food = (Collection) extras.get("Collecting food");
//                collectList.add(food);
//                for(int i=0;i<foodList.size();i++){
//                    if(food.getName().equals(foodList.get(i).getName())){
//                        foodList.set(i, food);
//                        break;
//                    }
//                }
//                listViewAdapter.refresh(collectList);
//            }
//        }
//    }

    @Subscribe
    public void onMessageEvent (MessageEvent event) {
        for(int i=0;i<foodList.size();i++){
            Collection food = foodList.get(i);
            if(food.getName().equals(event.getName()) && food.getIsCollected()==false){
                food.setIsCollected(true);
                collectList.add(food);
                foodList.set(i, food);
                break;
            }
        }
        listViewAdapter.refresh(collectList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销订阅者
        EventBus.getDefault().unregister(this);
    }
}
