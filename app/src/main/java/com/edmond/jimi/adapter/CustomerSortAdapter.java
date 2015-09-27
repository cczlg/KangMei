package com.edmond.jimi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.edmond.jimi.entity.Customer;
import com.edmond.kangmei.R;

import java.util.List;

/**
 * Created by apple on 15/9/20.
 */
public class CustomerSortAdapter extends BaseAdapter implements SectionIndexer {
    private List<Customer> list = null;
    private Context mContext;

    public CustomerSortAdapter(Context mContext, List<Customer> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<Customer> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final Customer mContent = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.customer_list_item, null);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.txtCustomer);
            viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.customer_catalog);
            viewHolder.tvId = (TextView) convertView.findViewById(R.id.txtId);
            viewHolder.tvPhone = (TextView) convertView.findViewById(R.id.txtPhone);
            viewHolder.tvScore = (TextView) convertView.findViewById(R.id.txtScore);
            viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.txtAddress);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //根据position获取分类的首字母的char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(section)){
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
        }else{
            viewHolder.tvLetter.setVisibility(View.GONE);
        }

        viewHolder.tvTitle.setText(this.list.get(position).name);
        viewHolder.tvId.setText(String.valueOf(this.list.get(position).id));
        viewHolder.tvPhone.setText(this.list.get(position).phone);
        viewHolder.tvScore.setText(Double.toString(this.list.get(position).score));
        viewHolder.tvAddress.setText(this.list.get(position).address);

        return convertView;
    }

    final static class ViewHolder {
        TextView tvLetter;
        TextView tvTitle;
        TextView tvId;
        TextView tvPhone;
        TextView tvScore;
        TextView tvAddress;

    }
    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }
}
