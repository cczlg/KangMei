package com.edmond.jimi.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.edmond.jimi.KangmeiApplication;
import com.edmond.jimi.activity.MainActivity;
import com.edmond.jimi.adapter.OrderListExpandableAdapter;
import com.edmond.jimi.entity.Order;
import com.edmond.jimi.entity.OrderItem;
import com.edmond.jimi.listener.DataReloadListener;
import com.edmond.jimi.util.DBHelper;
import com.edmond.kangmei.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 15/9/5.
 */
public class OrderListFragment extends PlaceholderFragment implements DataReloadListener{
    ArrayList<Order> groups;
    List<List<OrderItem>> children;
    OrderListExpandableAdapter viewAdapter;
    ExpandableListView elv;

    public static OrderListFragment newInstance(int sectionNumber) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_list, container, false);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initOrderList();
        elv.setOnCreateContextMenuListener(getActivity());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void initOrderList() {
        double total=0;
        double totalProfit=0;
        elv = (ExpandableListView) getActivity().findViewById(R.id.orderList);

        // 准备一级列表中显示的数据
        groups = DBHelper.getInstance(
                (KangmeiApplication) getActivity().getApplication()).getOrders();

        // 用一个list对象保存所有的二级列表数据
        children = new ArrayList<List<OrderItem>>();
        for (Order order : groups) {
            children.add(order.items);
            total+=Double.valueOf(order.getTotal());
            totalProfit+=Double.valueOf(order.getTotalProfit());
        }


        viewAdapter = new OrderListExpandableAdapter(getActivity(), groups, children);
        elv.setAdapter(viewAdapter);

        //set value
        TextView txtOrderCount=(TextView)getActivity().findViewById(R.id.txtOrderCount);
        TextView txtTotal=(TextView)getActivity().findViewById(R.id.txtTotal);
        TextView txtTotalProfit=(TextView)getActivity().findViewById(R.id.txtTotalProfit);

        txtOrderCount.setText(String.valueOf(groups.size()));
        txtTotal.setText(String.valueOf(total));
        txtTotalProfit.setText(String.valueOf(totalProfit));


    }

    @Override
    public void reloadData() {
        initOrderList();
    }
}
