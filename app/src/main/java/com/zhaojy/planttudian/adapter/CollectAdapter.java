package com.zhaojy.planttudian.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.bean.Article;
import com.zhaojy.planttudian.bean.Collect;
import com.zhaojy.planttudian.bean.Plant;
import com.zhaojy.planttudian.constant.SiteInfo;
import com.zhaojy.planttudian.constant.Strings;
import com.zhaojy.planttudian.data.Update;
import com.zhaojy.planttudian.data.plant.GetArticleByIdPresenter;
import com.zhaojy.planttudian.data.plant.GetPlantByIdPresenter;
import com.zhaojy.planttudian.ui.activity.ArticleDetailActivity;
import com.zhaojy.planttudian.ui.activity.PlantActivity;

import java.util.List;

/**
 * @author: zhaojy
 * @data:On 2018/10/11.
 */

public class CollectAdapter extends
        RecyclerView.Adapter<CollectAdapter.ViewHolder> {
    private List<Collect> data;
    private Context context;

    private CollectAdapter.OnItemClickListener mOnItemClickListener = null;

    public CollectAdapter(List<Collect> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public CollectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item, parent,
                false);
        CollectAdapter.ViewHolder viewHolder = new CollectAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CollectAdapter.ViewHolder holder, int position) {
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);

        Collect collect = data.get(position);
        switch (collect.getCollectSort()) {
            case Strings.ARTICLE_SORT:
                loadArticle(holder, collect.getCollectId());
                break;
            case Strings.PLANT_SORT:
                loadPlant(holder, collect.getCollectId());
                break;
            default:
                break;
        }

    }

    @Override
    public int getItemCount() {

        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title;
        TextView sort;

        private ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.img);
            title = view.findViewById(R.id.title);
            sort = view.findViewById(R.id.sort);
        }
    }

    public void setOnItemClickListener(CollectAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * item事件监听接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * 加载文章
     *
     * @param holder
     * @param id     文章id
     */
    private void loadArticle(final CollectAdapter.ViewHolder holder, int id) {
        GetArticleByIdPresenter articlePresenter = new GetArticleByIdPresenter(context);
        articlePresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.PLANT);
        articlePresenter.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                final Article article = (Article) object;
                if (null != article) {
                    holder.title.setText(article.getTitle());
                    holder.sort.setText("[" + Strings.ARTICLE + "]");
                    Glide.with(context)
                            .load(article.getImgUrl())
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    holder.img.setImageBitmap(resource);
                                }
                            });

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ArticleDetailActivity.class);
                            intent.putExtra(ArticleDetailActivity.TITLE,
                                    article.getTitle());
                            intent.putExtra(ArticleDetailActivity.ARTICLE_URL,
                                    article.getArticleUrl());
                            intent.putExtra(ArticleDetailActivity.ID,
                                    article.getId());
                            context.startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onError(String result) {

            }
        });
        articlePresenter.onCreate();
        articlePresenter.getArticleById(id);
    }

    /**
     * 加载植物
     *
     * @param holder
     * @param id     植物id
     */
    private void loadPlant(final CollectAdapter.ViewHolder holder, int id) {
        GetPlantByIdPresenter plantPresenter = new GetPlantByIdPresenter(context);
        plantPresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.PLANT);
        plantPresenter.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                final Plant plant = (Plant) object;
                if (null != plant) {
                    holder.title.setText(plant.getPlantName());
                    holder.sort.setText("[" + Strings.PLANT + "]");
                    Glide.with(context)
                            .load(plant.getImgUrl())
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    holder.img.setImageBitmap(resource);
                                }
                            });

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, PlantActivity.class);
                            intent.putExtra(PlantActivity.PLANT_NAME,
                                    plant.getPlantName());
                            intent.putExtra(PlantActivity.PLANT_URL,
                                    plant.getArticleUrl());
                            intent.putExtra(PlantActivity.ID,
                                    plant.getId());
                            context.startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onError(String result) {

            }
        });
        plantPresenter.onCreate();
        plantPresenter.getPlantById(id);
    }

}