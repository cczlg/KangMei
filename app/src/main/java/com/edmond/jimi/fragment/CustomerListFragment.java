package com.edmond.jimi.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.edmond.jimi.Constants;
import com.edmond.jimi.KangmeiApplication;
import com.edmond.jimi.activity.MainActivity;
import com.edmond.jimi.adapter.CustomerSortAdapter;
import com.edmond.jimi.component.ClearEditText;
import com.edmond.jimi.component.SideBar;
import com.edmond.jimi.entity.Customer;
import com.edmond.jimi.listener.DataReloadListener;
import com.edmond.jimi.util.CharacterParser;
import com.edmond.jimi.util.DBHelper;
import com.edmond.jimi.util.DensityUtils;
import com.edmond.jimi.util.PinyinComparator;
import com.edmond.kangmei.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by apple on 15/9/6.
 */
public class CustomerListFragment extends PlaceholderFragment implements DataReloadListener{

    /******************  华丽的分隔线   ***************************/
    private ListView sortListView;
    private SideBar sideBar;
    /**
     * 显示字母的TextView
     */
    private TextView dialog;
    private CustomerSortAdapter adapter;
    private ClearEditText mClearEditText;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<Customer> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    private void initViews() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) getActivity().findViewById(R.id.sidebar);
        dialog = (TextView) getActivity().findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) getActivity().findViewById(R.id.customerListView);
        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                Toast.makeText(getActivity().getApplication(), ((Customer)adapter.getItem(position)).name, Toast.LENGTH_SHORT).show();
            }
        });

        SourceDateList = filledData(getCustomeList(null));

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new CustomerSortAdapter(getActivity(), SourceDateList);
        sortListView.setAdapter(adapter);


        mClearEditText = (ClearEditText) getActivity().findViewById(R.id.filter_edit);

        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    /**
     * 为ListView填充数据
     * @param mSortList
     * @return
     */
    private List<Customer> filledData(List<Customer> mSortList){

        for(int i=0; i<mSortList.size(); i++){
            Customer sortModel = mSortList.get(i);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(sortModel.name);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                sortModel.setSortLetters(sortString.toUpperCase());
            }else{
                sortModel.setSortLetters("#");
            }
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<Customer> filterDateList = new ArrayList<Customer>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (Customer sortModel : SourceDateList) {
                String name = sortModel.name;
                if (name.toUpperCase().indexOf(
                        filterStr.toString().toUpperCase()) != -1
                        || characterParser.getSelling(name).toUpperCase()
                        .startsWith(filterStr.toString().toUpperCase())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    private List<Customer> getCustomeList(String orderField) {
        ArrayList<Customer> list = DBHelper.getInstance(
                (KangmeiApplication) getActivity().getApplication()).getCustomers(
                orderField);
        return list;
    }
    /******************  华丽的分隔线   ***************************/


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
        initViews();
        initSwipeMenus();
    }

    private void initSwipeMenus() {
        SwipeMenuListView customerListView= (SwipeMenuListView) getActivity().findViewById(R.id.customerListView);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // 创建一个Item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // 设置背景
                openItem.setBackground(R.color.swipeMenuBackground);
                // 设置宽度
                openItem.setWidth(DensityUtils.dp2px(getActivity(), 90));
                openItem.setIcon(R.drawable.icon_edit);
//                // 设置显示的文字
//                openItem.setTitle("Open");
//                // 设置文字大小
//                openItem.setTitleSize(18);
//                // 设置文字颜色
//                openItem.setTitleColor(Color.WHITE);
                // 添加到菜单里
                menu.addMenuItem(openItem);

                // 再创建一个Item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // 设置背景
                deleteItem.setBackground(R.color.swipeMenuBackground);
                // 设置宽度
                deleteItem.setWidth(DensityUtils.dp2px(getActivity(), 90));
                // 设置一个图标
                deleteItem.setIcon(R.drawable.icon_delete);
                // 添加到菜单
                menu.addMenuItem(deleteItem);
            }
        };

//      将菜单生成器设置给ListView即可
        customerListView.setMenuCreator(creator);

        customerListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Customer item=null;
                switch (index) {
                    case 0:
                        // 第一项被点击
                        item= (Customer) adapter.getItem(position);
                        CustomerDialogFragment customerDialog = CustomerDialogFragment.newInstance(Constants.OPERATE_MODIFY,item.name);
                        customerDialog.setTargetFragment(CustomerListFragment.this,1234);
                        customerDialog.show(getFragmentManager(),"Customer_dialog");
                        break;
                    case 1:
                        item= (Customer) adapter.getItem(position);
                        deleteDialog(item.name);
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


    @Override
    public void reloadData() {
        SourceDateList = filledData(getCustomeList(null));
        Collections.sort(SourceDateList, pinyinComparator);
        adapter.updateListView(SourceDateList);
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

                reloadData();
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
