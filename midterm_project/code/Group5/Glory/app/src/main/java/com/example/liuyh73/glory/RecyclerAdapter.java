package com.example.liuyh73.glory;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public abstract class RecyclerAdapter extends RecyclerView.Adapter<MyReccleViewHolder> {
    private List<Hero> mDatas;
    private OnItemPhotoChangedListener mOnItemPhotoChangedListener;

    public void convert(MyReccleViewHolder holder, Hero data, int position){
        DownloadImage.setViewImageCut((ImageView) holder.getView(R.id.hero_photo), data.getSkins().get(0).getImg_url());
        TextView textView = holder.getView(R.id.hero_name);
        textView.setText(data.getName());
    }

    RecyclerAdapter(List<Hero> mDatas) {
        this.mDatas = mDatas;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        DLog.d("RecyclerAdapter", "RecyclerAdapter onAttachedToRecyclerView");
        super.onAttachedToRecyclerView(recyclerView);
    }

    public abstract int getLayoutId(int viewType);
    @Override
    public MyReccleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DLog.d("RecyclerAdapter", "RecyclerAdapter onCreateViewHolder" + " width = " + parent.getWidth());
        return MyReccleViewHolder.get(parent,getLayoutId(viewType));
    }

    @Override
    public void onBindViewHolder(final MyReccleViewHolder holder, final int position) {
        DLog.d("RecyclerAdapter", "RecyclerAdapter onBindViewHolder" + "--> position = " + position);
        //holder.setImage(mDatas.get(holder.getAdapterPosition()));
        convert(holder, mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setOnItemPhotoChangedListener(OnItemPhotoChangedListener mOnItemPhotoChangedListener) {
        this.mOnItemPhotoChangedListener = mOnItemPhotoChangedListener;
    }

    public interface OnItemPhotoChangedListener {
        /**
         * 局部更新后需要替换背景图片
         */
        void onItemPhotoChanged();
    }
}