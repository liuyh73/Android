package com.example.liuyh73.healthyfoods;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class MyRecyclerViewAdapter<Collection> extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private int layoutId;
    private List<Collection> foodList;
    private OnItemClickListener onItemClickListener;
    public MyRecyclerViewAdapter(Context _context, int _layoutId, List _foodList) {
        this.context = _context;
        this.layoutId = _layoutId;
        this.foodList = _foodList;
    }

    /**
     * 根据不同ViewType创建与之相应的Item-Layout
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = MyViewHolder.get(this.context, parent, this.layoutId);
        return holder;
    }

    /**
     * 访问数据集合并将数据绑定到正确的View上。
     */
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        convert(holder, this.foodList.get(position));

        if(onItemClickListener != null) {
            holder.itemView.setOnClickListener((new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(holder.getAdapterPosition());
                }
            }));

            holder.itemView.setOnLongClickListener((new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view) {
                    onItemClickListener.onLongClick(holder.getAdapterPosition());
                    return true;
                }
            }));
        }
    }

    public abstract void convert(MyViewHolder holder, Collection t);

    public void setOnItemClickListener(OnItemClickListener _onItemClickListener) {
        this.onItemClickListener = _onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }
}
