package com.edmond.jimi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edmond.jimi.entity.Order;
import com.edmond.jimi.entity.OrderItem;
import com.edmond.kangmei.R;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by apple on 15/9/5.
 */
public class OrderListExpandableAdapter extends BaseExpandableListAdapter {
    private Context context;
    List<Order> groups;
    List<List<OrderItem>> childs;
    BigDecimal price;
    BigDecimal qty;
    private int[] colors = new int[] { 0x30FF0000, 0x300000FF };

    /*
     * 构造函数: 参数1:context对象 参数2:一级列表数据源 参数3:二级列表数据源
     */
    public OrderListExpandableAdapter(Context context, List<Order> groups,
                             List<List<OrderItem>> childs) {
        this.groups = groups;
        this.childs = childs;
        this.context = context;
    }

    // 获取一级列表View对象
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Order order = groups.get(groupPosition);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 获取一级列表布局文件,设置相应元素属性
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(
                R.layout.order_list_item, null);
        TextView textView = (TextView) linearLayout
                .findViewById(R.id.txtCustomer);
        textView.setText(order.customer);

        textView = (TextView) linearLayout.findViewById(R.id.txtTotal);
        textView.setText(order.getTotal());

        textView = (TextView) linearLayout.findViewById(R.id.txtOrderId);
        textView.setText(String.valueOf(order.id));

        int colorPos = groupPosition % colors.length;
        if (colorPos == 1)
            linearLayout.setBackgroundColor(Color.argb(250, 255, 255, 255));
        else
            linearLayout.setBackgroundColor(Color.argb(250, 154, 193, 210));
        return linearLayout;
    }

    // 获取二级列表的View对象
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        @SuppressWarnings("unchecked")
        OrderItem item = (OrderItem) getChild(groupPosition, childPosition);
        price = new BigDecimal(item.price);
        qty = new BigDecimal(item.quantity);

        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 获取二级列表对应的布局文件, 并将其各元素设置相应的属性
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(
                R.layout.order_item_list_item, null);
        TextView tv = (TextView) linearLayout.findViewById(R.id.txtProduct);
        tv.setText(item.product);
        tv = (TextView) linearLayout.findViewById(R.id.txtPrice);
        if (item.flag == 1||item.flag==3||item.flag==4) {
            // 换货不显示价格
            tv.setText("");
        } else {
            tv.setText(String.valueOf(item.price));
        }
        tv = (TextView) linearLayout.findViewById(R.id.txtQuantity);
        if (item.flag == 2) {
            tv.setTextColor(Color.RED);
            tv.setText("-" + String.valueOf(item.quantity));
        } else {
            tv.setText(String.valueOf(item.quantity));
        }
        tv = (TextView) linearLayout.findViewById(R.id.txtSubtotal);

        if (item.flag == 2) {
            tv.setTextColor(Color.RED);
            tv.setText("-"
                    + price.multiply(qty)
                    .setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toString());
        } else if (item.flag == 1||item.flag==3||item.flag==4) {
            tv.setText("");
        } else {
            tv.setText(price.multiply(qty)
                    .setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        }
        tv = (TextView) linearLayout.findViewById(R.id.txtId);
        tv.setText(String.valueOf(item.id));

        int colorPos = groupPosition % colors.length;
        if (colorPos == 1)
            linearLayout.setBackgroundColor(Color.argb(250, 255, 255, 255));
        else
            linearLayout.setBackgroundColor(Color.argb(250, 224, 243, 250));

        return linearLayout;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return childs.get(groupPosition).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        if (childs.get(groupPosition) == null)
            return 0;
        return childs.get(groupPosition).size();
    }

    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    public int getGroupCount() {
        return groups.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
