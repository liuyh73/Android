package com.example.liuyh73.glory;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.ryan.rv_gallery.AnimManager;
import com.ryan.rv_gallery.GalleryRecyclerView;
import com.ryan.rv_gallery.util.DLog;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements GalleryRecyclerView.OnItemClickListener,RecyclerAdapter.OnItemPhotoChangedListener {
    private MyDB db;
    private GalleryRecyclerView mRecyclerView;
    private RecyclerAdapter adapter;
    private LinearLayout mContainer;
    private List<Hero> mDatas;
    private SeekBar mSeekbar;
    private ImageView loadImage;
    private static Hero clickedHero;
    private boolean wetherAddHero = false;
    public static Hero getClickedHero(){
        Hero temp = clickedHero;
        return temp;
    }
    //Handler
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    loadImage.setVisibility(View.GONE);
                    mContainer.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    //虚化背景的位置
    private int mLastDraPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DLog.setDebug(true);
        DLog.d("MainPage: ", "MainActivity onCreate()");
        db = new MyDB(MainActivity.this);
        loadImage = findViewById(R.id.mainPageLoad);
        mDatas = db.getAllPresentHeros();
        FloatingActionButton addBtn = findViewById(R.id.mainPageAddHero);
        mRecyclerView = findViewById(R.id.mainPageRecyclerView);
        mContainer = findViewById(R.id.mainPageAll);
        mSeekbar = findViewById(R.id.seekBar);
        mSeekbar.setMax(mDatas.size()-1);
        //延时展示
        delayDispaly(2000);
        //关闭seekbar点击效果
        mSeekbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        //初始化Adapter
        initRecycleViewAdapter();
        adapter.setOnItemPhotoChangedListener(this);
        //初始化RecycleView
        initRecycleView();
        //添加英雄点击事件
        adapter.notifyDataSetChanged();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wetherAddHero = true;
                Intent intent = new Intent(MainActivity.this,AddActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initRecycleViewAdapter() {
        adapter = new RecyclerAdapter(mDatas) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.hero_item;
            }
        };
    }

    private void initRecycleView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView
                // 设置滑动速度（像素/s）
                .initFlingSpeed(3000)
                // 设置页边距和左右图片的可见宽度，单位dp
                .initPageParams(0, 40)
                // 设置切换动画的参数因子
                .setAnimFactor(0.1f)
                // 设置切换动画类型，目前有AnimManager.ANIM_BOTTOM_TO_TOP和目前有AnimManager.ANIM_TOP_TO_BOTTOM
                .setAnimType(AnimManager.ANIM_BOTTOM_TO_TOP)
                // 设置点击事件
                .setOnItemClickListener(this)
                // 设置自动播放
                .autoPlay(false)
                // 设置自动播放间隔时间 ms
                .intervalTime(1000)
                // 设置初始化的位置
                .initPosition(0)
                // 在设置完成之后，必须调用setUp()方法
                .setUp();
        // 背景高斯模糊 & 淡入淡出
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    setBlurImage(false);
                    mSeekbar.setProgress(mRecyclerView.getScrolledPosition());
                }
            }
        });
        setBlurImage(false);
    }

    //设置背景高斯模糊
    public void setBlurImage(boolean forceUpdate) {
        final RecyclerAdapter adapter = (RecyclerAdapter) mRecyclerView.getAdapter();
        final int mCurViewPosition = mRecyclerView.getScrolledPosition();

        boolean isSamePosAndNotUpdate = (mCurViewPosition == mLastDraPosition) && !forceUpdate;

        if (adapter == null || mRecyclerView == null || isSamePosAndNotUpdate) {
            return;
        }
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                DLog.d("MainPage: ", "setBlurImage.-View Position: "+ mCurViewPosition);
                CardView cardView = (CardView) mRecyclerView.getLayoutManager().findViewByPosition(mCurViewPosition);
                if (cardView == null) {
                    return;
                }
                ImageView imageView = cardView.findViewById(R.id.hero_photo);
                Drawable drawable = imageView.getDrawable();
                //图片是否已经加载
                if(drawable == null){
                    DownloadImage.setLinnerLayoutViewImage(mContainer,mDatas.get(mCurViewPosition).getSkins().get(0).getImg_url(),mRecyclerView.getContext());
                } else {
                    BlurBitmapUtil.setBlur(((BitmapDrawable)drawable).getBitmap(),mContainer,mRecyclerView.getContext());
                }
                // 记录上一次高斯模糊的位置
                mLastDraPosition = mCurViewPosition;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(wetherAddHero){
            delayDispaly(1500);
            wetherAddHero = !wetherAddHero;
            mDatas = db.getAllPresentHeros();
            mSeekbar.setMax(mDatas.size()-1);
            adapter.notifyDataSetChanged();
        }
        DLog.d("MainPage: ", "MainActivity onResume()");
    }

    @Override
    protected void onStart() {
        super.onStart();

        DLog.d("MainPage: ", "MainActivity onStart()");
    }

    @Override
    protected void onPause() {
        super.onPause();

        DLog.d("MainPage: ", "MainActivity onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();

        DLog.d("MainPage: ", "MainActivity onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DLog.d("MainPage: ", "MainActivity onDestroy()");

        if (mRecyclerView != null) {
            // 释放资源
            mRecyclerView.release();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        clickedHero = mDatas.get(position);
        Intent intent = new Intent(MainActivity.this,DetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemPhotoChanged() {
        setBlurImage(true);
    }

    private void delayDispaly(int delay){
        mContainer.setVisibility(View.INVISIBLE);
        loadImage.setVisibility(View.VISIBLE);
        //随机展示图片
        Random random=new Random();
        int randomPic = random.nextInt(5);
        int resId = getResources().getIdentifier("loadpic"+ randomPic , "mipmap" , getPackageName());
        loadImage.setBackgroundResource(resId);
        //延时展示
        Timer mTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {//创建一个线程来执行run方法中的代码
            @Override
            public void run() {
                mHandler.obtainMessage(0).sendToTarget();
            }
        };
        mTimer.schedule(mTimerTask, delay);//延迟2秒执行
    }
}
