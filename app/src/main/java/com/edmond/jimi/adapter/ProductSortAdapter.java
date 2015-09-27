package com.edmond.jimi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.edmond.jimi.entity.Product;
import com.edmond.kangmei.R;

import java.util.List;

/**
 * Created by apple on 15/9/25.
 */
public class ProductSortAdapter extends BaseAdapter implements SectionIndexer {
    private List<Product> list = null;
    private Context mContext;

    public ProductSortAdapter(Context mContext, List<Product> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<Product> list) {
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
        final Product mContent = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.product_list_item, null);
            viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.product_catalog);
            viewHolder.tvId = (TextView) convertView.findViewById(R.id.txtId);
            viewHolder.tvProduct = (TextView) convertView.findViewById(R.id.txtProduct);
            viewHolder.tvPurchasePrice = (TextView) convertView.findViewById(R.id.txtPurchasePrice);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.txtPrice);
            viewHolder.tvMemo = (TextView) convertView.findViewById(R.id.txtMemo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //根据position获取分类的首字母的char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }

        viewHolder.tvId.setText(String.valueOf(this.list.get(position).id));
        viewHolder.tvProduct.setText(this.list.get(position).product);
        viewHolder.tvPurchasePrice.setText(Double.toString(this.list.get(position).purchasePrice));
        viewHolder.tvPrice.setText(String.valueOf(this.list.get(position).price));
        viewHolder.tvMemo.setText(String.valueOf(this.list.get(position).memo));

        return convertView;
    }

    final static class ViewHolder {
        TextView tvLetter;
        TextView tvId;
        TextView tvProduct;
        TextView tvPurchasePrice;
        TextView tvPrice;
        TextView tvMemo;

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