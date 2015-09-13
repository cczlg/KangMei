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
import android.widget.EditText;
import android.widget.ListView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.edmond.jimi.Constants;
import com.edmond.jimi.KangmeiApplication;
import com.edmond.jimi.activity.MainActivity;
import com.edmond.jimi.adapter.ProductListAdapter;
import com.edmond.jimi.entity.Product;
import com.edmond.jimi.listener.DataReloadListener;
import com.edmond.jimi.util.DBHelper;
import com.edmond.jimi.util.DensityUtils;
import com.edmond.jimi.util.PrefrenceTool;
import com.edmond.kangmei.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by apple on 15/9/6.
 */
public class ProductListFragment extends PlaceholderFragment implements DataReloadListener{
    int REQUEST_CODE = 2013;
    ArrayList<Product> list;
    String imagePath;
    ProductListAdapter adapter;
    ListView productList;

    private int REQUEST_CODE_MODIFY;

    public static ProductListFragment newInstance(int sectionNumber) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_list, container, false);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imagePath = ((KangmeiApplication) getActivity().getApplication()).imagepath;

        productList = (ListView) getActivity().findViewById(R.id.productListView);

        initListView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }


    protected void deleteDialog(final String product) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.confirm_delete));
        builder.setTitle(getString(R.string.warning));

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBHelper.getInstance((KangmeiApplication) getActivity().getApplication())
                        .deleteProduct(product);
                reloadData();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    private void initListView(){
        list = DBHelper.getInstance((KangmeiApplication) getActivity().getApplication())
                .getProducts();

        adapter = new ProductListAdapter(getActivity(), list);

        productList.setAdapter(adapter);
//        productList.setOnCreateContextMenuListener(getActivity());

        SwipeMenuListView productListView= (SwipeMenuListView) getActivity().findViewById(R.id.productListView);
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
        productListView.setMenuCreator(creator);

        productListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Product item = null;
                switch (index) {
                    case 0:
                        // 第一项被点击
                        item = (Product) adapter.getItem(position);
                        ProductDialogFragment productDialog = ProductDialogFragment.newInstance(Constants.OPERATE_MODIFY, item.product);
                        productDialog.setTargetFragment(ProductListFragment.this, 5678);
                        productDialog.show(getFragmentManager(), "Product_dialog");
                        break;
                    case 1:
                        item = (Product) adapter.getItem(position);
                        deleteDialog(item.product);
                        break;
                }
                return true;
            }
        });

        // Close Interpolator
        productListView.setCloseInterpolator(new BounceInterpolator());
        // Open Interpolator
        productListView.setOpenInterpolator(new BounceInterpolator());
    }
    @Override
    public void reloadData() {
        initListView();
    }
}
