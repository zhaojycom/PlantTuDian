package com.zhaojy.planttudian.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.bean.WanTuBean;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zhaojy
 * @data:On 2018/9/24.
 */

public class WanTuGridViewAdapter extends BaseAdapter {
    private Context context;
    private List<WanTuBean> data;
    private GridView gridView;
    private Map<Integer, View> viewMap = new HashMap<>();

    public WanTuGridViewAdapter(Context context, List<WanTuBean> data, GridView gridView) {
        this.context = context;
        this.data = data;
        this.gridView = gridView;
    }

    private class Holder {
        ImageView img;

        public Holder(View view) {
            img = view.findViewById(R.id.img);
        }

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Holder holder;
        WanTuBean wanTuBean = data.get(position);

        if (!viewMap.containsKey(position) || viewMap.get(position) == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.wantu_item, viewGroup, false);
            holder = new Holder(view);
            WeakReference<ImageView> imageViewReference = new WeakReference<>(holder.img);

            Glide.with(context)
                    .load(wanTuBean.getsImgUrl())
                    .placeholder(R.mipmap.icon)
                    .centerCrop()
                    .into(imageViewReference.get());

            view.setTag(holder);
            viewMap.put(position, view);
        } else {
            view = viewMap.get(position);
            holder = (Holder) view.getTag();
        }

        //清理viewMap
        clearViewMap(view);
        return view;
    }

    /**
     * 清理viewMap
     */
    private void clearViewMap(View view) {
        if (viewMap.size() > 20) {
            synchronized (view) {
                for (int i = 1; i < gridView.getFirstVisiblePosition() - 3; i++) {
                    viewMap.remove(i);
                }
                for (int i = gridView.getLastVisiblePosition() + 3; i < getCount(); i++) {
                    viewMap.remove(i);
                }
            }
        }
    }

}