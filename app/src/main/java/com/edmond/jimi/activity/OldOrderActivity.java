package com.edmond.jimi.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.edmond.jimi.Constants;
import com.edmond.jimi.entity.Customer;
import com.edmond.jimi.util.DBHelper;
import com.edmond.jimi.KangmeiApplication;
import com.edmond.jimi.util.PrefrenceTool;
import com.edmond.kangmei.R;
import com.edmond.jimi.entity.Order;
import com.edmond.jimi.entity.OrderItem;

public class OldOrderActivity extends Activity {
    GridView gridview;
    String imagePath;
    ArrayList<OrderItem> list;
    OrderProductItemAdapter adapter;
    String operate;
    GestureDetector gd;
    GestureDetector gd2;
    OnTouchListener touchListener;
    ImageView imgDetail;
    String imgName;
    Button btnOk;
    Button btnCancel;
    int imgWidth = 250;
    private AutoCompleteTextView txtCustomer;
    private EditText txtId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_order);

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


        WindowManager manage = getWindowManager();
        Display display = manage.getDefaultDisplay();
        int screenHeight = display.getHeight();
        int screenWidth = display.getWidth();
        imgWidth = screenWidth / 3 - 35;
        Intent intent = getIntent();
        operate = intent.getStringExtra(Constants.OPERATE);
        if (Constants.OPERATE_MODIFY.equals(operate)
                && intent.getStringExtra("orderid") != null) {
            list = DBHelper
                    .getInstance((KangmeiApplication) this.getApplication())
                    .genOrderItemListForModify(intent.getStringExtra("orderid"));
        } else if (Constants.OPERATE_NEW.equals(operate) || Constants.OPERATE_VIEW.equals(operate)) {
            list = DBHelper.getInstance(
                    (KangmeiApplication) this.getApplication())
                    .genOrderItemList();
        }
        imagePath = ((KangmeiApplication) getApplication()).imagepath;

        imgDetail = (ImageView) findViewById(R.id.imgDetail);
        imgDetail.setLongClickable(true);
        imgDetail.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gd2.onTouchEvent(event);
            }
        });

//        gridview = (GridView) findViewById(R.id.orderGridView);
        // gridview.setColumnWidth(screenWidth/3-30);
        adapter = new OrderProductItemAdapter(OldOrderActivity.this, list);
        // 添加Item到网格中
        gridview.setAdapter(adapter);

        gd = new GestureDetector(this, new GestureListener());
        touchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//				 imgName = ((KMImageView) v).getBitMapName();
                v.findViewById(R.id.txtQuantity).requestFocus();
                return gd.onTouchEvent(event);
            }
        };
        gd2 = new GestureDetector(this, new DetailGestureListener());

        btnOk = (Button) findViewById(R.id.btnOk);
        if (Constants.OPERATE_VIEW.equals(operate))
            btnOk.setVisibility(View.GONE);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtCustomer.getText()==null || txtCustomer.getText().length()<=0){
                    Toast.makeText(OldOrderActivity.this, R.string.require_customer_name,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                boolean needSave = false;
                for (OrderItem item : adapter.list) {
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
                    OldOrderActivity.this.finish();
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gd.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!"view".equals(operate)) {
            boolean needSave = false;
            for (OrderItem item : adapter.list) {
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

        AlertDialog.Builder builder = new Builder(OldOrderActivity.this);
        builder.setMessage("确定要取消订单？");
        builder.setTitle("提示");

        builder.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                OldOrderActivity.this.finish();
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

        AlertDialog.Builder builder = new Builder(OldOrderActivity.this);
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
                OldOrderActivity.this.finish();
            }
        });
        builder.setPositiveButton("不保存", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Intent intent = getIntent();
                // 返回intent
                setResult(RESULT_CANCELED, intent);
                OldOrderActivity.this.finish();
            }
        });
        builder.create().show();

    }

    private boolean save() {
        Order order = new Order();
        Intent intent = getIntent();
        operate = intent.getStringExtra(Constants.OPERATE);
        if (Constants.OPERATE_MODIFY.equals(operate)
                && intent.getStringExtra("orderid") != null) {
            order = DBHelper.getInstance((KangmeiApplication) getApplication())
                    .getOrderHead(intent.getStringExtra("orderid"));
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
        for (OrderItem item : adapter.list) {
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

    public class OrderProductItemAdapter extends BaseAdapter {
        private Activity context;
        private List<OrderItem> list;
        private int proposition;

        // 这个用来保存 imageview 的引用
        private ArrayList<ImageView> viewList = new ArrayList<ImageView>();
        // 这个用来 保存 bitmap
        private ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();

        public OrderProductItemAdapter(Activity context,
                                       ArrayList<OrderItem> list2) {
            this.context = context;
            this.list = list2;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.order_product_item, null);

            if (viewList.size() > 20)
                recycleMemory(proposition, position);

            OrderItem info = list.get(position);

            // 用try catch 块包围住
            try {
                setImage(convertView, info, position);
            } catch (OutOfMemoryError e) {
                // 这里就是当内存泄露时 需要做的事情
                e.printStackTrace();

                // 释放内存资源
                recycleMemory(proposition, position);

                // 将刚才 发生异常没有执行的 代码 再重新执行一次
                setImage(convertView, info, position);

            }
            proposition = position;
            convertView.setOnTouchListener(touchListener);//无图代码
            return convertView;
        }

        // 这里是关键
        private void recycleMemory(int proPos, int curPos) {
            // 一屏显示多少行 这里就设置为多少。不设也行 主要是用户体验好 不会将用户看到的图片设为默认图片
            int showCount = 20;

            //
            for (int i = 0; i < viewList.size() - showCount; i++) {
                ImageView iv = (ImageView) viewList.get(i);
                /***
                 * 这里是关键！ 将 imageview 设置一张默认的图片 ， 用于解决当释放bitmap的时候 还有其他 控件对他保持引用
                 * 就不会发生trying to use a recycled bitmap异常了
                 */
                iv.setImageResource(R.drawable.ic_launcher);
                // 从list中去除
                viewList.remove(i);
            }

            if (proPos < curPos) {
                while (bitmapList.size() > 20) {
                    Bitmap bitmap = (Bitmap) bitmapList.get(0);
                    // 这里就开始释放bitmap 所占的内存了
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    // 从list中去除
                    bitmapList.remove(0);
                }
            } else {
                for (int i = bitmapList.size() - 1; i > 20; i--) {
                    Bitmap bitmap = (Bitmap) bitmapList.get(i);
                    // 这里就开始释放bitmap 所占的内存了
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    // 从list中去除
                    bitmapList.remove(i);
                }
            }
        }

        private void setImage(View convertView, OrderItem info,
                              final int position) {

            final TextView txtProduct = (TextView) convertView
                    .findViewById(R.id.txtProduct);
            txtProduct.setText(info.product);

            TextView txtPrice = (TextView) convertView
                    .findViewById(R.id.txtPrice);
            txtPrice.setText(String.valueOf(info.price));

            final EditText txtQty = (EditText) convertView
                    .findViewById(R.id.txtQuantity);
            if ("view".equals(operate))
                txtQty.setVisibility(View.GONE);
            txtQty.setSelectAllOnFocus(true);
            txtQty.setText(String.valueOf(info.quantity));
            if (info.quantity > 0) {
                txtQty.setTextColor(Color.RED);
                txtProduct.setTextColor(Color.RED);
            }
            txtQty.setImeOptions(EditorInfo.IME_ACTION_DONE);
            txtQty.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus == false && ((EditText) v).getText() != null
                            && ((EditText) v).getText().length() > 0) {
                        list.get(position).quantity = Integer
                                .parseInt(((EditText) v).getText().toString());
                        if (list.get(position).quantity > 0) {
                            txtQty.setTextColor(Color.RED);
                            txtProduct.setTextColor(Color.RED);
                        }
                    }
                }
            });
            txtQty.setOnEditorActionListener(new OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId,
                                              KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE
                            || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        if (((EditText) v).getText() != null
                                && ((EditText) v).getText().length() > 0)
                            list.get(position).quantity = Integer.parseInt(v
                                    .getText().toString());
                        if (list.get(position).quantity > 0) {
                            txtQty.setTextColor(Color.RED);
                            txtProduct.setTextColor(Color.RED);
                        }
                        return true;
                    }
                    return false;
                }
            });
            txtQty.addTextChangedListener(new TextWatcher() {

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
                    if (txtQty.getText() == null
                            || txtQty.getText().length() == 0) {
                        txtQty.setText("0");
                    }
                    list.get(position).quantity = Integer.parseInt(txtQty
                            .getText().toString());
                    if (list.get(position).quantity > 0) {
                        txtQty.setTextColor(Color.RED);
                        txtProduct.setTextColor(Color.RED);
                    }
                }
            });

            final Spinner spFlag = (Spinner) convertView
                    .findViewById(R.id.spanFlag);
            if ("view".equals(operate))
                spFlag.setVisibility(View.GONE);
            spFlag.setSelection(info.flag);
            spFlag.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    list.get(position).flag = spFlag.getSelectedItemPosition();
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });
            //有图代码开始
//			 KMImageView imageView = (KMImageView) convertView
//			 .findViewById(R.id.ItemImage);
//			 imageView.setBitMapName(imagePath + info.image);
//			 imageView.setLongClickable(true);
//			 imageView.setOnTouchListener(touchListener);
//			 imageView.setEditText(txtQty);
//			 BitmapFactory.Options opt = new BitmapFactory.Options();
//			 opt.outWidth = imgWidth;
//			 opt.inJustDecodeBounds = true;
//			 Bitmap bitmap = BitmapFactory.decodeFile(imagePath + info.image,
//			 opt);
//			 opt.inJustDecodeBounds = false;
//			 int be = opt.outWidth / (imgWidth / 10);
//			 if (be % 10 != 0) {
//			 be += 10;
//			 }
//			 be /= 10;
//			 if (be <= 0) {
//			 be = 1;
//			 }
//			 opt.inSampleSize = be;
//			 bitmap = BitmapFactory.decodeFile(imagePath + info.image, opt);
//			 if (bitmap != null) {
//			 int w = bitmap.getWidth();
//			 int h = bitmap.getHeight();
//			 h = h * imgWidth / w;
//			 }
//			
//			 imageView.setImageBitmap(bitmap);
//			
//			 // 将这个控件 添加到 list里
//			 viewList.add(imageView);
//			 // 将要 释放的 bitmap也添加到list里
//			 bitmapList.add(bitmap);//有图代码结束

            if ("view".equals(operate)) {
                txtQty.setVisibility(View.GONE);
                spFlag.setVisibility(View.GONE);
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

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
