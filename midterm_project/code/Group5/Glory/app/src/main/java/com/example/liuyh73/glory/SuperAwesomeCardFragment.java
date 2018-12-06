package com.example.liuyh73.glory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.Transition;

public class SuperAwesomeCardFragment extends Fragment {
    private static final String ARG_AVATAR = "avatar";
    private static final String ARG_SKIN = "skin";
    private String avatar;
    private String skin;
    private static SetBitmapAtCardFragment setBitmapAtCardFragment;

    public static SuperAwesomeCardFragment newInstance(String _skin, String _avatar, SetBitmapAtCardFragment _setBitmapAtCardFragment) {
        SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
        Bundle b = new Bundle();
        b.putString(ARG_SKIN, _skin);
        b.putString(ARG_AVATAR, _avatar);
        f.setArguments(b);
        setBitmapAtCardFragment = _setBitmapAtCardFragment;
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
        final View rootView = inflater.inflate(R.layout.view_page_item,container,false);
        ViewCompat.setElevation(rootView, 50);
        ImageView imageView = rootView.findViewById(R.id.detailSkinAvatar);
        Glide.with(this).load(avatar).into(imageView);
        final RelativeLayout relativeLayout = rootView.findViewById(R.id.detailShowSkin);
        Glide.with(this).load(skin).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                relativeLayout.setBackground(resource);
                Bitmap bitmap = BlurBitmapUtil.cropBitmap(((BitmapDrawable) resource).getBitmap());
                bitmap = BlurBitmapUtil.blurBitmap(rootView.getContext(), bitmap, 15.0f);
                setBitmapAtCardFragment.set(bitmap);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
        return rootView;
    }

    public interface SetBitmapAtCardFragment {
        void set(Bitmap bitmap);
    }

}
