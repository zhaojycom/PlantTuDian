package com.zhaojy.planttudian.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.bean.Plant;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zhaojy
 * @data:On 2018/10/9.
 */

public class SearchPlantAdapter extends BaseAdapter {
    private Context context;
    private List<Plant> data;
    private GridView gridView;
    private Map<Integer, View> viewMap = new HashMap<>();
    /**
     * 关键词
     */
    private String keyword;

    public SearchPlantAdapter(Context context, List<Plant> data, GridView gridView) {
        this.context = context;
        this.data = data;
        this.gridView = gridView;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    private class Holder {
        ImageView img;
        TextView plantName;

        public Holder(View view) {
            img = view.findViewById(R.id.img);
            plantName = view.findViewById(R.id.plantName);
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
        SearchPlantAdapter.Holder holder;
        Plant plant = data.get(position);

        if (!viewMap.containsKey(position) || viewMap.get(position) == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.search_plant_item, viewGroup, false);
            holder = new SearchPlantAdapter.Holder(view);
            final WeakReference<ImageView> imageViewReference = new WeakReference<>(holder.img);

            String plantName = plant.getPlantName();
            String str = "<font color='#5fe6b3'>" + keyword + "</font>";
            plantName = plantName.replace(keyword, str);
            holder.plantName.setText(Html.fromHtml(plantName));

            Glide.with(context)
                    .load(plant.getImgUrl())
                    .asBitmap()
                    .placeholder(R.mipmap.icon)
                    .centerCrop()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            imageViewReference.get().setImageBitmap(resource);
                        }
                    });

            view.setTag(holder);
            viewMap.put(position, view);
        } else {
            view = viewMap.get(position);
            holder = (SearchPlantAdapter.Holder) view.getTag();
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