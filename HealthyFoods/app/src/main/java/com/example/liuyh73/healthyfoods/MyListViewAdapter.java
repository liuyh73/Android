package com.example.liuyh73.healthyfoods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MyListViewAdapter extends BaseAdapter {
        private List<Collection> foodList;
        private Context context;
        public MyListViewAdapter(List<Collection> collectList, Context context) {
            this.foodList = collectList;
            this.context = context;
        }

        @Override
        public int getCount() {
            if (foodList == null) {
                return 0;
            }
            return foodList.size();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public Object getItem(int i) {
            if (foodList == null) {
                return null;
            }
            return foodList.get(i);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            // 新声明一个View变量和ViewHoleder变量,ViewHolder类在下面定义。
            View convertView;
            ViewHolder viewHolder;
            // 当view为空时才加载布局，否则，直接修改内容
            if (view == null) {
                // 通过inflate的方法加载布局，context需要在使用这个Adapter的Activity中传入。
                convertView = LayoutInflater.from(context).inflate(R.layout.item, null);
                viewHolder = new ViewHolder();
                viewHolder.name = convertView.findViewById(R.id.name);
                viewHolder.abbr = convertView.findViewById(R.id.abbr);
                convertView.setTag(viewHolder); // 用setTag方法将处理好的viewHolder放入view中
            } else { // 否则，让convertView等于view，然后从中取出ViewHolder即可
                convertView = view;
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // 从viewHolder中取出对应的对象，然后赋值给他们
            viewHolder.name.setText(foodList.get(i).getName());
            viewHolder.abbr.setText(foodList.get(i).getAbbr());
            // 将这个处理好的view返回
            return convertView;
        }

        public void refresh(List<Collection>collectList){
            this.foodList = collectList;
            this.notifyDataSetChanged();
        }

        private class ViewHolder {
            public TextView name;
            public TextView abbr;
        }
    }