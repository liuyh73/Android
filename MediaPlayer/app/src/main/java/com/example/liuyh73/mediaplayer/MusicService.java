package com.example.liuyh73.mediaplayer;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import java.io.IOException;
import java.net.URI;

public class MusicService extends Service {
    private Music music;
    private IBinder binder;
    public MediaPlayer mediaPlayer;
    private String defaultMusicPath = Environment.getExternalStorageDirectory() + "/data/山高水长.mp3";
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("MusicService: ", "MusicService onBind()");
        return binder;
    }

    @Override
    public void onCreate(){
        Log.i("MusicService: ", "MusicService onCreate()");
        binder = new MyBinder();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(defaultMusicPath);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            setMusic(null);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyBinder extends Binder {
        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
            switch (code) {
                // 初始化歌曲信息
                case 0:
                    try {
                        Log.i("MusicService code=0: ", music.getDuration());
                        reply.writeSerializable(music);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                // stopBtn点击
                case 1:
                    mediaPlayer.stop();
                    try {
                        mediaPlayer.prepare();
                        mediaPlayer.seekTo(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                // playBtn点击
                case 2:
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        reply.writeString("pause");
                    } else {
                        mediaPlayer.start();
                        reply.writeString("play");
                    }
                    break;
                // 获取当前播放时间
                case 3:
                    try {
                        reply.writeInt(mediaPlayer.getCurrentPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                // 更改音乐播放进度
                case 4:
                    try {
                        mediaPlayer.seekTo(data.readInt());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                // 切换音乐
                case 5:
                    mediaPlayer.stop();
                    mediaPlayer.seekTo(0);
                    Uri uri = data.readParcelable(getClass().getClassLoader());
                    try{
                        mediaPlayer.setDataSource(MusicService.this, uri);
                        mediaPlayer.prepare();
                        setMusic(uri);
                        reply.writeSerializable(music);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return super.onTransact(code, data, reply, flags);
        }
    }

    public void setMusic(Uri uri) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        if (uri != null) {
            mmr.setDataSource(this, uri);
        }
        else {
            mmr.setDataSource(defaultMusicPath);
        }
        music = new Music(
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE),
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION),
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE),
                mmr.getEmbeddedPicture()
        );
        Log.i("Music: ", "parseMp3File名称: " + music.getTitle());
        Log.i("Music: ", "parseMp3File专辑: " + music.getAlbum());
        Log.i("Music: ", "parseMp3File歌手: " + music.getArtist());
        Log.i("Music: ", "parseMp3File码率: " + music.getBitrate());
        Log.i("Music: ", "parseMp3File时长: " + music.getDuration());
        Log.i("Music: ", "parseMp3File类型: " + music.getMimetype());
        mmr.release();
    }
}
