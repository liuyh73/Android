package com.example.liuyh73.glory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;

public class SuperAwesomeCardFragment extends Fragment {
    private static final String ARG_AVATAR = "avatar";
    private static final String ARG_SKIN = "skin";
    private static Map<String, Drawable> skinBitmapMap = new HashMap<>();
    private static Map<String, Bitmap> avatarBitmapMap = new HashMap<>();
    private String avatar;
    private String skin;

    public static SuperAwesomeCardFragment newInstance(String _skin, String _avatar) {
        SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
        Bundle b = new Bundle();
        b.putString(ARG_SKIN, _skin);
        b.putString(ARG_AVATAR, _avatar);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        skin = bundle.getString(ARG_SKIN);
        avatar = bundle.getString(ARG_AVATAR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_page_item,container,false);
        ViewCompat.setElevation(rootView, 50);
        ImageView imageView = rootView.findViewById(R.id.detailSkinAvatar);
        if (avatarBitmapMap.containsKey(avatar)) {
            imageView.setImageBitmap(avatarBitmapMap.get(avatar));
        } else {
            DownloadImage.setViewImage(imageView, avatar);
        }
        RelativeLayout relativeLayout = rootView.findViewById(R.id.detailShowSkin);
        if (skinBitmapMap.containsKey(skin)) {
            relativeLayout.setBackground(skinBitmapMap.get(skin));
        } else {
            DownloadImage.setRelativeLayoutBackground(relativeLayout, skin, rootView.getContext());
        }
        return rootView;
    }
}
