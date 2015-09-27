package com.edmond.jimi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.SectionIndexer;
import android.widget.Spinner;
import android.widget.TextView;

import com.edmond.jimi.entity.OrderItem;
import com.edmond.kangmei.R;

import java.util.List;

/**
 * Created by apple on 15/9/26.
 */
public class OrderItemSortAdapter extends BaseAdapter implements SectionIndexer {
    private List<OrderItem> list = null;
    private Context mContext;

    public OrderItemSortAdapter(Context mContext, List<OrderItem> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<OrderItem> list) {
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
        final OrderItem mContent = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.order_product_item, null);
            viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.orderitem_catalog);
            viewHolder.tvProduct = (TextView) convertView.findViewById(R.id.txtProduct);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.txtPrice);
            viewHolder.spanFlag = (Spinner) convertView.findViewById(R.id.spanFlag);
            viewHolder.tvQuantity = (EditText) convertView.findViewById(R.id.txtQuantity);
            //init quantity
            final ViewHolder finalViewHolder = viewHolder;
            viewHolder.tvQuantity.setSelectAllOnFocus(true);
            viewHolder.tvQuantity.setImeOptions(EditorInfo.IME_ACTION_DONE);
            viewHolder.tvQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus == false && ((EditText) v).getText() != null
                            && ((EditText) v).getText().length() > 0) {
                        mContent.quantity = Integer
                                .parseInt(((EditText) v).getText().toString());
                        if (mContent.quantity > 0) {
                            finalViewHolder.tvQuantity.setTextColor(Color.RED);
                            finalViewHolder.tvProduct.setTextColor(Color.RED);
                        }
                    }
                }
            });
            viewHolder.tvQuantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId,
                                              KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE
                            || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        if (((EditText) v).getText() != null
                                && ((EditText) v).getText().length() > 0)
                            mContent.quantity = Integer.parseInt(v
                                    .getText().toString());
                        if (mContent.quantity > 0) {
                            finalViewHolder.tvQuantity.setTextColor(Color.RED);
                            finalViewHolder.tvProduct.setTextColor(Color.RED);
                        }
                        return true;
                    }
                    return false;
                }
            });
            viewHolder.tvQuantity.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (finalViewHolder.tvQuantity.getText() == null
                            || finalViewHolder.tvQuantity.getText().length() == 0) {
                        finalViewHolder.tvQuantity.setText("0");
                    }
                    mContent.quantity = Integer.parseInt(finalViewHolder.tvQuantity
                            .getText().toString());
                    if (mContent.quantity > 0) {
                        finalViewHolder.tvQuantity.setTextColor(Color.RED);
                        finalViewHolder.tvProduct.setTextColor(Color.RED);
                    }
                }
            });
            
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

        viewHolder.tvProduct.setText(mContent.product);
        viewHolder.tvPrice.setText(String.valueOf(mContent.price));
        viewHolder.spanFlag.setSelection(mContent.flag);
        viewHolder.tvQuantity.setText(String.valueOf(mContent.quantity));
        if (mContent.quantity > 0) {
            viewHolder.tvQuantity.setTextColor(Color.RED);
            viewHolder.tvProduct.setTextColor(Color.RED);
        }

        return convertView;
    }

    final static class ViewHolder {
        TextView tvLetter;
        TextView tvProduct;
        TextView tvPrice;
        Spinner spanFlag;
        EditText tvQuantity;

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

    public List<OrderItem> getList(){
        return this.list;
    }
}