package com.zhaojy.planttudian.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.bean.Article;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author: zhaojy
 * @data:On 2018/9/20.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> implements
        View.OnClickListener {
    private List<Article> data;
    private Context context;

    private OnItemClickListener mOnItemClickListener = null;

    public ArticleAdapter(List<Article> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.article_item, parent,
                false);
        ViewHolder viewHolder = new ViewHolder(view);
        //设置监听事件
        view.setOnClickListener(this);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);

        Article article = data.get(position);
        holder.title.setText(article.getTitle());
        holder.articleSort.setText("- " + article.getName() + " -");
        WeakReference<ImageView> img = new WeakReference<>(holder.img);
        Glide.with(context)
                .load(article.getImgUrl())
                .placeholder(R.mipmap.icon)
                .into(img.get());
    }

    @Override
    public int getItemCount() {

        return data.size();
    }

    /**
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(view, (Integer) view.getTag());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView articleSort;
        ImageView img;
        TextView title;

        private ViewHolder(View view) {
            super(view);
            articleSort = view.findViewById(R.id.articleSort);
            img = view.findViewById(R.id.img);
            title = view.findViewById(R.id.title);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * item事件监听接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
