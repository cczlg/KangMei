package com.edmond.jimi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.edmond.jimi.util.DBHelper;
import com.edmond.jimi.KangmeiApplication;
import com.edmond.jimi.util.PrefrenceTool;
import com.edmond.kangmei.R;
import com.edmond.jimi.entity.Customer;

public class CustomerActivity extends Activity {
    SimpleAdapter mySimpleAdapter;
    ArrayList<HashMap<String, String>> myArrayList;
    ListView customerList;
    int REQUEST_CODE_NEW = 2012;
    int REQUEST_CODE_MODIFY = 2013;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        String title = PrefrenceTool.getStringValue("kangmei", "apptitle",
                getApplication());
        if (title == null || title.length() == 0) {
            title = getResources().getString(R.string.title_activity_customer);
        } else {
            title = title + "--客户";
        }
        setTitle(title);

        customerList = (ListView) findViewById(R.id.customerListView);

        myArrayList = new ArrayList<HashMap<String, String>>();

        initArrayList(null);

        mySimpleAdapter = new CustomerAdapter(this, myArrayList,// 数据源
                R.layout.customer_list_item, new String[] { "txtCustomer",
                "txtPhone", "txtScore", "txtAddress" }, new int[] {
                R.id.txtCustomer, R.id.txtPhone, R.id.txtScore,
                R.id.txtAddress, });

        customerList.setAdapter(mySimpleAdapter);
        customerList.setOnCreateContextMenuListener(CustomerActivity.this);
    }

    private void initArrayList(String orderField) {
        ArrayList<Customer> list = DBHelper.getInstance(
                (KangmeiApplication) this.getApplication()).getCustomers(
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

    protected void deleteDialog(final String customer) {

        AlertDialog.Builder builder = new Builder(CustomerActivity.this);
        builder.setMessage("确定要删除这条记录？");
        builder.setTitle("警告");

        builder.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBHelper.getInstance((KangmeiApplication) getApplication())
                        .deleteCustomer(customer);

                for(int i=myArrayList.size()-1;i>0;i--){
                    HashMap<String, String> map=myArrayList.get(i);
                    if (customer.equals(map.get("txtCustomer"))) {
                        myArrayList.remove(i);
                        break;
                    }
                }
                mySimpleAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        TextView txtCustomer = (TextView) info.targetView
                .findViewById(R.id.txtCustomer);
        switch (item.getItemId()) {
            case Menu.FIRST:
                Intent intent = new Intent(CustomerActivity.this,
                        CustomerDetailActivity.class);
                intent.putExtra("operate", "new");
                startActivityForResult(intent, REQUEST_CODE_NEW);
                break;
            case Menu.FIRST + 1:
                intent = new Intent(CustomerActivity.this,
                        CustomerDetailActivity.class);
                intent.putExtra("operate", "modify");
                intent.putExtra("customer", txtCustomer.getText().toString());
                startActivityForResult(intent, REQUEST_CODE_MODIFY);
                break;
            case Menu.FIRST + 2:
                // DBHelper.getInstance((KangmeiApplication) getApplication())
                // .deleteCustomer(txtCustomer.getText().toString());
                deleteDialog(txtCustomer.getText().toString());
                break;
            case Menu.FIRST + 3:
                initArrayList("name");
                mySimpleAdapter.notifyDataSetChanged();
                break;
            case Menu.FIRST + 4:
                initArrayList("score");
                mySimpleAdapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("操作");
        // 添加菜单项
        menu.add(0, Menu.FIRST, 0, "新建");
        menu.add(0, Menu.FIRST + 1, 0, "修改");
        menu.add(0, Menu.FIRST + 2, 0, "删除");
        menu.add(1, Menu.FIRST + 3, 0, "名称排序");
        menu.add(1, Menu.FIRST + 4, 0, "积分排序");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_new) {
            Intent intent = new Intent(CustomerActivity.this,
                    CustomerDetailActivity.class);
            intent.putExtra("operate", "new");
            startActivityForResult(intent, REQUEST_CODE_NEW);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_NEW) {
            if (resultCode == RESULT_OK) {
                HashMap<String, String> itemMap = new HashMap<String, String>();
                itemMap.put("txtId", data.getExtras().getString("id"));
                itemMap.put("txtCustomer", data.getExtras().getString("customer"));
                itemMap.put("txtPhone", data.getExtras().getString("phone"));
                itemMap.put("txtScore", data.getExtras().getString("score"));
                itemMap.put("txtAddress", data.getExtras().getString("address"));
//				myArrayList.add(itemMap);

                mySimpleAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == REQUEST_CODE_MODIFY) {
            if (resultCode == RESULT_OK) {
                for (HashMap<String, String> item : myArrayList) {
                    if (data.getExtras().getString("name") != null
                            && data.getExtras().getString("name")
                            .equals(item.get("txtCustomer"))) {
                        item.put("txtPhone", data.getExtras()
                                .getString("phone"));
                        item.put("txtScore", data.getExtras()
                                .getString("score"));
                        item.put("txtAddress",
                                data.getExtras().getString("address"));
                    }
                }

                mySimpleAdapter.notifyDataSetChanged();
            }
        }
    }

    public class CustomerAdapter extends SimpleAdapter {
        private int[] colors = new int[] { 0x30FF0000, 0x300000FF };

        public CustomerAdapter(Context context,
                               List<? extends Map<String, ?>> data, int resource,
                               String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            int colorPos = position % colors.length;
            if (colorPos == 1)
                view.setBackgroundColor(Color.argb(250, 255, 255, 255));
            else
                view.setBackgroundColor(Color.argb(250, 224, 243, 250));
            return view;
        }

    }
}
