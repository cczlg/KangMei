package com.edmond.jimi.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.edmond.jimi.Constants;
import com.edmond.jimi.KangmeiApplication;
import com.edmond.jimi.activity.MainActivity;
import com.edmond.jimi.adapter.CustomerListAdapter;
import com.edmond.jimi.entity.Customer;
import com.edmond.jimi.listener.DataReloadListener;
import com.edmond.jimi.util.DBHelper;
import com.edmond.jimi.util.DensityUtils;
import com.edmond.kangmei.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by apple on 15/9/6.
 */
public class CustomerListFragment extends PlaceholderFragment implements DataReloadListener{
    SimpleAdapter myCustomerAdapter;
    ArrayList<HashMap<String, String>> myArrayList;
    ListView customerList;
    int REQUEST_CODE_NEW = 2012;
    int REQUEST_CODE_MODIFY = 2013;

    public static CustomerListFragment newInstance(int sectionNumber) {
        CustomerListFragment fragment = new CustomerListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_list, container, false);



        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        customerList = (ListView) getActivity().findViewById(R.id.customerListView);

        myArrayList = new ArrayList<HashMap<String, String>>();

        initArrayList(null);

        myCustomerAdapter = new CustomerListAdapter(getActivity(), myArrayList,// 数据源
                R.layout.customer_list_item, new String[] { "txtCustomer",
                "txtPhone", "txtScore", "txtAddress" }, new int[] {
                R.id.txtCustomer, R.id.txtPhone, R.id.txtScore,
                R.id.txtAddress, });

        customerList.setAdapter(myCustomerAdapter);
//        customerList.setOnCreateContextMenuListener(CustomerActivity.this);

        SwipeMenuListView customerListView= (SwipeMenuListView) getActivity().findViewById(R.id.customerListView);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // 创建一个Item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // 设置背景
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // 设置宽度
                openItem.setWidth(DensityUtils.dp2px(getActivity(), 90));
                // 设置显示的文字
                openItem.setTitle("Open");
                // 设置文字大小
                openItem.setTitleSize(18);
                // 设置文字颜色
                openItem.setTitleColor(Color.WHITE);
                // 添加到菜单里
                menu.addMenuItem(openItem);

                // 再创建一个Item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // 设置背景
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // 设置宽度
                deleteItem.setWidth(DensityUtils.dp2px(getActivity(), 90));
                // 设置一个图标
                deleteItem.setIcon(R.drawable.ic_action_search);
                // 添加到菜单
                menu.addMenuItem(deleteItem);
            }
        };

//      将菜单生成器设置给ListView即可
        customerListView.setMenuCreator(creator);

        customerListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Map<String,String> item=null;
                switch (index) {
                    case 0:
                        // 第一项被点击
                        item= (Map<String, String>) myCustomerAdapter.getItem(position);
                        CustomerDialogFragment customerDialog = CustomerDialogFragment.newInstance(Constants.OPERATE_MODIFY,item.get("txtCustomer"));
                        customerDialog.setTargetFragment(CustomerListFragment.this,1234);
                        customerDialog.show(getFragmentManager(),"Customer_dialog");
                        break;
                    case 1:
                        item= (Map<String, String>) myCustomerAdapter.getItem(position);
                        deleteDialog(item.get("txtCustomer"));
                        break;
                }
                return true;
            }
        });

        // Close Interpolator
        customerListView.setCloseInterpolator(new BounceInterpolator());
        // Open Interpolator
        customerListView.setOpenInterpolator(new BounceInterpolator());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }


    private void initArrayList(String orderField) {
        ArrayList<Customer> list = DBHelper.getInstance(
                (KangmeiApplication) getActivity().getApplication()).getCustomers(
                orderField);
        myArrayList.clear();
        for (Customer customer : list) {
            HashMap<String, String> itemMap = new HashMap<String, String>();
            itemMap.put("txtCustomer", customer.name);
            itemMap.put("txtPhone", customer.phone);
            itemMap.put("txtScore", String.valueOf(customer.score));
            itemMap.put("txtAddress", customer.address);
            myArrayList.add(itemMap);
        }
    }

    @Override
    public void reloadData() {
        this.initArrayList(null);
        customerList.setAdapter(myCustomerAdapter);
    }

    protected void deleteDialog(final String customer) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确定要删除这条记录？");
        builder.setTitle("警告");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBHelper.getInstance((KangmeiApplication) getActivity().getApplication())
                        .deleteCustomer(customer);

                for(int i=myArrayList.size()-1;i>0;i--){
                    HashMap<String, String> map=myArrayList.get(i);
                    if (customer.equals(map.get("txtCustomer"))) {
                        myArrayList.remove(i);
                        break;
                    }
                }
                myCustomerAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }
}
