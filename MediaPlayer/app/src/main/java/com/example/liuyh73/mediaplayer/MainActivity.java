package com.example.liuyh73.mediaplayer;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import rx.Observable;

import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    private ImageView albumImage;
    private TextView title;
    private TextView artist;
    private TextView currentTime;
    private SeekBar seekBar;
    private TextView duration;
    private ImageView file;
    private ImageView play;
    private ImageView stop;
    private ImageView back;
    private ServiceConnection serviceConnection;
    private IBinder mBinder;
    private Music music;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
    private ObjectAnimator animator;
    private boolean musicEnd = true;
    // 观察者
    Observer<Integer> observer;
    // 被观察者
    Observable observable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("MainPage: ", "onCreate()");
        albumImage = findViewById(R.id.albumImage);
        title = findViewById(R.id.title);
        artist = findViewById(R.id.artist);
        currentTime = findViewById(R.id.currentTime);
        seekBar = findViewById(R.id.seekBar);
        duration = findViewById(R.id.duration);
        file = findViewById(R.id.file);
        play = findViewById(R.id.play);
        stop = findViewById(R.id.stop);
        back = findViewById(R.id.back);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {
                Log.i("MainPage: ", "musicService onServiceConnected()");
                mBinder = iBinder;
                Parcel reply = Parcel.obtain();
                // 0 - 初始化歌曲信息
                try {
                    mBinder.transact(0,null,reply,0);
                    Music music = (Music)reply.readSerializable();
                    Log.i("MainPage SvcConnected: ", music.getTitle());
                    seekBar.setMax(Integer.parseInt(music.getDuration()));
                    duration.setText(timeFormat.format(Integer.parseInt(music.getDuration())));
                    albumImage.setImageBitmap(BitmapFactory.decodeByteArray(music.getData(), 0, music.getData().length));
                    title.setText(music.getTitle());
                    artist.setText(music.getArtist());
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("MainPage: ", "musicService onServiceDisconnected()");
                serviceConnection = null;
            }
        };
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        file.setOnClickListener(new FileImageClickListener());
        play.setOnClickListener(new PlayBtnClickListener());
        stop.setOnClickListener(new StopBtnClickListener());
        back.setOnClickListener(new BackBtnClickListener());
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());

        animator = ObjectAnimator.ofFloat(albumImage, "rotation", 0f, 360f);//添加旋转动画，旋转中心默认为控件中点
        animator.setDuration(30*1000);//设置动画时间
        animator.setInterpolator(new LinearInterpolator());//动画时间线性渐变
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.RESTART);

        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onNext(Integer s) {
                currentTime.setText(timeFormat.format(s));
                seekBar.setProgress(Integer.valueOf(s));
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        };
        observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                while (true) {
                    try {
                        Parcel reply = Parcel.obtain();
                        mBinder.transact(3, null, reply, 0);
                        int curr = reply.readInt();
                        subscriber.onNext(curr);
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // 事件订阅
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }
    // execute after onCreate or onRestart
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("MainPage: ", "MainActivity onStart()");
    }
    // Another activity comes in front of the activity and this activity can also be visible
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MainPage: ", "MainActivity onPause()");
    }

    // The activity is no longer visible
    @Override
    protected void onStop(){
        super.onStop();
        Log.i("MainPage: ", "MainActivity onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MainPage: ", "MainActivity onDestroy()");
    }

    // The activity comes to the foreground form invisible
    @Override
    protected void onRestart() {
        super.onRestart();
//        if(musicService.mediaPlayer.isPlaying()) {
//            System.out.println("djfalkdjkalfjd");
//            play.setImageResource(R.mipmap.pause);
//            seekBar.setProgress(musicService.mediaPlayer.getCurrentPosition());
//        }
        Log.i("MainPage: ", "MainActivity onRestart()");
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }
//    @Override
//    public void onBackPressed() {
//        moveTaskToBack(false);
//    }

    class FileImageClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            //intent.setType(“image/*”);//选择图片
            intent.setType("audio/*"); //选择音频
            //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
            //intent.setType(“video/*;image/*”);//同时选择视频和图片
            // intent.setType("*/*");  // 无类型限制
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 1);
        }
    }
    // 2 - playBtn
    class PlayBtnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Parcel reply = Parcel.obtain();
            try {
                mBinder.transact(2, null, reply, 0);
                if(reply.readString().equals("pause")) {
                    play.setImageResource(R.mipmap.play);
                    animator.pause();
                } else {
                    play.setImageResource(R.mipmap.pause);
                    if (musicEnd){
                        animator.start();
                    } else {
                        animator.resume();
                    }
                    musicEnd = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            reply.recycle();
//            if(musicService.mediaPlayer.isPlaying()){
//                musicService.mediaPlayer.pause();
//                animator.pause();
//            } else {
//                musicService.mediaPlayer.start();
//                play.setImageResource(R.mipmap.pause);
//                if (musicEnd){
//                    animator.start();
//                } else {
//                    animator.resume();
//                }
//            }
        }
    }
    // 1 - stopBtn
    class StopBtnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            stop();
            Parcel data = Parcel.obtain();
            try {
                mBinder.transact(1, data, null, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            data.recycle();
        }
    }

    public void stop(){
        musicEnd = true;
        animator.end();
        play.setImageResource(R.mipmap.play);
        seekBar.setProgress(0);
        currentTime.setText(timeFormat.format(0));
    }
    class BackBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Parcel data = Parcel.obtain();
            try {
                mBinder.transact(1, data, null, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            data.recycle();
//            if (musicService.mediaPlayer != null) {
//                musicService.mediaPlayer.stop();
//                try {
//                    musicService.mediaPlayer.prepare();
//                    musicService.mediaPlayer.seekTo(0);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
            mHandler.removeCallbacks(mRunnable);
            unbindService(serviceConnection);
            try {
                MainActivity.this.finish();
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // 4 - 更改音乐播放进度
    class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.i("拖动过程中的值：", String.valueOf(progress) + ", " + String.valueOf(fromUser));
            currentTime.setText(timeFormat.format(progress));
//            if(musicService.mediaPlayer.isPlaying()) {
//                currentTime.setText(timeFormat.format(musicService.mediaPlayer.getCurrentPosition()));
//            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i("开始拖动中的值：", String.valueOf(seekBar.getProgress()));
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // musicService.mediaPlayer.seekTo(seekBar.getProgress());
            Parcel data = Parcel.obtain();
            data.writeInt(seekBar.getProgress());
            try{
                mBinder.transact(4,data,null,0);
            }catch (Exception e) {
                e.printStackTrace();
            }
            Log.v("停止滑动时的值：", String.valueOf(seekBar.getProgress()));
        }
    }
    // 5 change music
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = intent.getData();
//            if ("file".equalsIgnoreCase(uri.getScheme())) {
//                filePath = uri.getPath();
//                Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
//            }
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//                filePath = Utils.getInstance().getPath(this, uri);
//                Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
//            } else { //4.4以下下系统调用方法
//                filePath = Utils.getInstance().getRealPathFromURI(this, uri);
//                Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
//            }
            if(uri != null) {
                stop();
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeParcelable(uri, 0);
                try {
                    mBinder.transact(5,data,reply, 0);
                    music = (Music)reply.readSerializable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(music.getData() != null) {
                    albumImage.setImageBitmap(BitmapFactory.decodeByteArray(music.getData(), 0, music.getData().length));
                }
                title.setText(music.getTitle());
                artist.setText(music.getArtist());
                duration.setText(timeFormat.format(Integer.parseInt(music.getDuration())));
                currentTime.setText(timeFormat.format(0));
                seekBar.setMax(Integer.parseInt(music.getDuration()));
                data.recycle();
                reply.recycle();
            }
//            setMusic(uri);
//            try {
//                if(uri != null) {
//                    stop();
//                    musicService.mediaPlayer.stop();
//                    musicService.mediaPlayer.seekTo(0);
//                    musicService.mediaPlayer.setDataSource(this, uri);
//                    musicService.mediaPlayer.prepare();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }
}
