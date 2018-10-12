package com.zhaojy.planttudian.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.bean.ClassifyBean;

import java.util.List;

/**
 * @author: zhaojy
 * @data:On 2018/9/22.
 */

public class LeftClassifyAdapter extends RecyclerView.Adapter<LeftClassifyAdapter.ViewHolder>
        implements View.OnClickListener {
    private List<ClassifyBean> data;
    private Context context;

    private LeftClassifyAdapter.OnItemClickListener mOnItemClickListener = null;

    public LeftClassifyAdapter(List<ClassifyBean> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public LeftClassifyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.left_classify_item, parent,
                false);
        LeftClassifyAdapter.ViewHolder viewHolder = new LeftClassifyAdapter.ViewHolder(view);
        //设置监听事件
        view.setOnClickListener(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LeftClassifyAdapter.ViewHolder holder, int position) {
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);

        ClassifyBean cb = data.get(position);

        holder.classifyName.setText(cb.getName());
        if (position == 0) {
            holder.leftBorder.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundResource(R.color.white);
        }
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
        TextView classifyName;
        View leftBorder;

        private ViewHolder(View view) {
            super(view);
            classifyName = view.findViewById(R.id.classifyName);
            leftBorder = view.findViewById(R.id.leftBorder);
        }
    }

    public void setOnItemClickListener(LeftClassifyAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * item事件监听接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
