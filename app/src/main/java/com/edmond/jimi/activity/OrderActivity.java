package com.edmond.jimi.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.edmond.jimi.Constants;
import com.edmond.jimi.adapter.OrderItemSortAdapter;
import com.edmond.jimi.component.ClearEditText;
import com.edmond.jimi.component.SideBar;
import com.edmond.jimi.entity.Customer;
import com.edmond.jimi.entity.Product;
import com.edmond.jimi.util.CharacterParser;
import com.edmond.jimi.util.DBHelper;
import com.edmond.jimi.KangmeiApplication;
import com.edmond.jimi.util.PinyinComparator;
import com.edmond.jimi.util.PrefrenceTool;
import com.edmond.kangmei.R;
import com.edmond.jimi.entity.Order;
import com.edmond.jimi.entity.OrderItem;

public class OrderActivity extends Activity {
    private String orderid = null;
    GridView gridview;
    String imagePath;
    String operate;
    OnTouchListener touchListener;
    ImageView imgDetail;
    String imgName;
    Button btnOk;
    Button btnCancel;
    int imgWidth = 250;
    private AutoCompleteTextView txtCustomer;
    private EditText txtId;
    
    
    /******************  华丽的分隔线   ***************************/
    private ListView sortListView;
    private SideBar sideBar;
    /**
     * 显示字母的TextView
     */
    private TextView dialog;
    private OrderItemSortAdapter adapter;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<OrderItem> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    private void initViews() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar)  findViewById(R.id.sidebar);
        dialog = (TextView)  findViewById(R.id.dialog);
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

        sortListView = (ListView)  findViewById(R.id.orderItemList);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                Toast.makeText( getApplication(), ((Customer) adapter.getItem(position)).name, Toast.LENGTH_SHORT).show();
            }
        });

        SourceDateList = filledData(getOrderItems());

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new OrderItemSortAdapter(OrderActivity.this, SourceDateList);
        sortListView.setAdapter(adapter);

    }


    /**
     * 为ListView填充数据
     * @param mSortList
     * @return
     */
    private List<OrderItem> filledData(List<OrderItem> mSortList){

        for(int i=0; i<mSortList.size(); i++){
            OrderItem sortModel = mSortList.get(i);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(sortModel.product);
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

    /******************  华丽的分隔线   ***************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_order);

        //init parameters
        Intent intent = getIntent();
        operate = intent.getStringExtra(Constants.OPERATE);
        orderid=intent.getStringExtra("orderid");

        //init customer infomation
        initCustomer();

        initViews();
        //init button
        initButton();

        imagePath = ((KangmeiApplication) getApplication()).imagepath;

        imgDetail = (ImageView) findViewById(R.id.imgDetail);
        imgDetail.setLongClickable(true);

    }

    private void initButton() {
        btnOk = (Button) findViewById(R.id.btnOk);
        if (Constants.OPERATE_VIEW.equals(operate))
            btnOk.setVisibility(View.GONE);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtCustomer.getText()==null || txtCustomer.getText().length()<=0){
                    Toast.makeText(OrderActivity.this, R.string.require_customer_name,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                boolean needSave = false;
                for (OrderItem item : adapter.getList()) {
                    if (item.quantity > 0) {
                        needSave = true;
                        break;
                    }
                }
                if (needSave) {
                    save();
                    Intent intent = getIntent();
                    // 返回intent
                    setResult(RESULT_OK, intent);
                    OrderActivity.this.finish();
                    return;
                }
            }
        });

        btnCancel = (Button) findViewById(R.id.btnCancel);
        if ("view".equals(operate))
            btnCancel.setVisibility(View.GONE);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                canceldialog();
            }
        });
    }

    private void initCustomer() {
        txtId=(EditText)findViewById(R.id.txtId);
        String customers[] = DBHelper.getInstance(
                (KangmeiApplication) getApplication()).getCustomerNames();
        txtCustomer = (AutoCompleteTextView) findViewById(R.id.txtCustomer);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, customers);
        txtCustomer.setAdapter(aa);
        txtCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Customer c = DBHelper.getInstance(
                        (KangmeiApplication) getApplication()).getCustomer(
                        txtCustomer.getText().toString());
                txtId.setText(String.valueOf(c.id));
            }
        });
    }

    private List<OrderItem> getOrderItems() {
        List<OrderItem> list=null;
        if (Constants.OPERATE_MODIFY.equals(operate)
                && orderid != null) {
            list = DBHelper
                    .getInstance((KangmeiApplication) this.getApplication())
                    .genOrderItemListForModify(orderid);
        } else if (Constants.OPERATE_NEW.equals(operate) || Constants.OPERATE_VIEW.equals(operate)) {
            list = DBHelper.getInstance(
                    (KangmeiApplication) this.getApplication())
                    .genOrderItemList();
        }
        return list;
    }


    @Override
    public void onBackPressed() {
        if (!"view".equals(operate)) {
            boolean needSave = false;
            for (OrderItem item : adapter.getList()) {
                if (item.quantity > 0) {
                    needSave = true;
                    break;
                }
            }
            if (needSave) {
                dialog();
                return;
            }
        }
        super.onBackPressed();
    }

    protected void canceldialog() {

        AlertDialog.Builder builder = new Builder(OrderActivity.this);
        builder.setMessage("确定要取消订单？");
        builder.setTitle("提示");

        builder.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                OrderActivity.this.finish();
            }
        });
        builder.setPositiveButton("不取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    protected void dialog() {

        AlertDialog.Builder builder = new Builder(OrderActivity.this);
        builder.setMessage("退出前是否要保存订单？");
        builder.setTitle("提示");

        builder.setNegativeButton("保存", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                save();
                dialog.dismiss();
                Intent intent = getIntent();
                // 返回intent
                setResult(RESULT_OK, intent);
                OrderActivity.this.finish();
            }
        });
        builder.setPositiveButton("不保存", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Intent intent = getIntent();
                // 返回intent
                setResult(RESULT_CANCELED, intent);
                OrderActivity.this.finish();
            }
        });
        builder.create().show();

    }

    private boolean save() {
        Order order = new Order();
        Intent intent = getIntent();
        operate = intent.getStringExtra(Constants.OPERATE);
        if (Constants.OPERATE_MODIFY.equals(operate)
                && orderid != null) {
            order = DBHelper.getInstance((KangmeiApplication) getApplication())
                    .getOrderHead(orderid);
        } else {
            order.customer = (txtId.getText() == null || txtId.getText().length() == 0) ? "0"
                    : txtId.getText().toString();
            order.orderTime = new Date();
            order.code = "No."
                    + (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date(
                    System.currentTimeMillis()));
            order.salesman = PrefrenceTool.getStringValue("kangmei",
                    "salesman", getApplicationContext());
            Customer c=DBHelper.getInstance((KangmeiApplication) getApplication())
                    .getCustomer(txtCustomer.getText().toString());

            order.address = c.address;
            order.customerphone = c.phone;
        }
        for (OrderItem item : adapter.getList()) {
            if (item.quantity > 0)
                order.items.add(item);
        }

        if (order.items.size() > 0) {

            if (Constants.OPERATE_MODIFY.equals(operate)) {
                DBHelper.getInstance((KangmeiApplication) getApplication())
                        .updateOrders(order);
            } else {
                DBHelper.getInstance((KangmeiApplication) getApplication())
                        .saveOrder(order);
            }
        }
        return true;
    }

    public class GestureListener extends
            GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (imgName != null) {
                imgDetail.setImageBitmap(BitmapFactory.decodeFile(imgName));
                imgDetail.setVisibility(View.VISIBLE);
            }
            return false;
        }
    }

    public class DetailGestureListener extends
            GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            imgName = null;
            imgDetail.setVisibility(View.GONE);
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            imgName = null;
            imgDetail.setVisibility(View.GONE);
            return false;
        }

    }
}
