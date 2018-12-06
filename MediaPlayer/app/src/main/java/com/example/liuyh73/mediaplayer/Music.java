package com.example.liuyh73.mediaplayer;

import java.io.Serializable;

public class Music implements Serializable {
    private String title;
    private String album;
    private String artist;
    private String bitrate;
    private String duration;
    private String mimetype;
    private byte[] data;
    public Music(String title, String album, String artist, String bitrate, String duration, String mimetype, byte[] data) {
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.bitrate = bitrate;
        this.duration = duration;
        this.mimetype = mimetype;
        this.data = data.clone();
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getBitrate() {
        return bitrate;
    }

    public String getDuration() {
        return duration;
    }

    public String getMimetype() {
        return mimetype;
    }

    public byte[] getData() {
        return data;
    }
}
