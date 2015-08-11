package com.edmond.jimi.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edmond.jimi.util.DBHelper;
import com.edmond.jimi.util.ExportUtil;
import com.edmond.jimi.KangmeiApplication;
import com.edmond.jimi.util.PrefrenceTool;
import com.edmond.kangmei.R;
import com.edmond.jimi.entity.Order;
import com.edmond.jimi.entity.OrderItem;

public class MainActivity extends Activity {
    int REQUEST_CODE = 2000;
    int REQUEST_ORDER = 2222;
    int REQUEST_MODIFY = 4444;
    ArrayList<Order> groups;
    List<List<OrderItem>> children;
    ExpandableAdapter viewAdapter;
    ExpandableListView elv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String title = PrefrenceTool.getStringValue("kangmei", "apptitle",
                getApplication());
        if (title == null || title.length() == 0) {
            dialog();
        }
        setTitle(title);

        Button btnOrder = (Button) findViewById(R.id.btnOrder);
        btnOrder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String salesman = PrefrenceTool.getStringValue("kangmei",
                        "salesman", getApplicationContext());
                if (salesman == null || salesman.length() <= 0) {
                    Toast.makeText(MainActivity.this, getString(R.string.salesman_first),
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this,
                            ConfigActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this,
                            CustomerDetailActivity.class);
                    intent.putExtra("operate", "order");
                    startActivityForResult(intent, REQUEST_ORDER);
                }
            }
        });
        Button btnExport = (Button) findViewById(R.id.btnExport);
        btnExport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new Builder(MainActivity.this);
                builder.setMessage("确定要导出excel？ ");
                builder.setTitle("提示");

                builder.setNegativeButton("导出",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                if (ExportUtil
                                        .exportExcel((KangmeiApplication) getApplication())) {
                                    groups.removeAll(groups);
                                    children.removeAll(children);
                                    viewAdapter.notifyDataSetChanged();
                                }
                                dialog.dismiss();
                            }
                        });
                builder.setPositiveButton("不导出",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        });

        Button btnView = (Button) findViewById(R.id.btnView);
        btnView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        OrderActivity.class);
                intent.putExtra("operate", "view");
                startActivity(intent);
            }
        });
        initOrderList();
        elv.setOnCreateContextMenuListener(MainActivity.this);
    }

    protected void dialog() {
        AlertDialog.Builder builder = new Builder(MainActivity.this);
        builder.setMessage("设置公司名称");

        builder.setTitle("提示");
        final EditText txtTitle = new EditText(this);
        builder.setView(txtTitle);
        builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                PrefrenceTool.saveValue("kangmei", "apptitle", txtTitle
                        .getText().toString(), getApplication());
                setTitle(txtTitle.getText());
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ORDER) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(MainActivity.this,
                        OrderActivity.class);
                intent.putExtra("customerid", data.getExtras().getString("id"));
                intent.putExtra("customer",
                        data.getExtras().getString("customer"));
                intent.putExtra("customerphone",
                        data.getExtras().getString("phone"));
                intent.putExtra("address", data.getExtras()
                        .getString("address"));
                intent.putExtra("operate", "new");
                startActivityForResult(intent, REQUEST_CODE);
            }
        } else if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                initOrderList();
            }
        } else if (requestCode == REQUEST_MODIFY) {
            if (resultCode == RESULT_OK) {
                initOrderList();
            }
        }
    }

    private void initOrderList() {
        double total=0;
        double totalProfit=0;
        elv = (ExpandableListView) findViewById(R.id.orderList);

        // 准备一级列表中显示的数据
        groups = DBHelper.getInstance(
                (KangmeiApplication) this.getApplication()).getOrders();

        // 用一个list对象保存所有的二级列表数据
        children = new ArrayList<List<OrderItem>>();
        for (Order order : groups) {
            children.add(order.items);
            total+=Double.valueOf(order.getTotal());
            totalProfit+=Double.valueOf(order.getTotalProfit());
        }


        viewAdapter = new ExpandableAdapter(this, groups, children);
        elv.setAdapter(viewAdapter);

        //set value
        TextView txtOrderCount=(TextView)findViewById(R.id.txtOrderCount);
        TextView txtTotal=(TextView)findViewById(R.id.txtTotal);
        TextView txtTotalProfit=(TextView)findViewById(R.id.txtTotalProfit);

        txtOrderCount.setText(String.valueOf(groups.size()));
        txtTotal.setText(String.valueOf(total));
        txtTotalProfit.setText(String.valueOf(totalProfit));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_config) {
            Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_customer) {
            Intent intent = new Intent(MainActivity.this,
                    CustomerActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_product) {
            Intent intent = new Intent(MainActivity.this, ProductActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item
                .getMenuInfo();
        View view = info.targetView;
        final TextView txtId = (TextView) view.findViewById(R.id.txtId);
        final TextView txtOrderId = (TextView) view
                .findViewById(R.id.txtOrderId);
        switch (item.getItemId()) {
            case Menu.FIRST:
                if (txtId != null) {
                    TextView txtProduct = (TextView) info.targetView
                            .findViewById(R.id.txtProduct);
                    AlertDialog.Builder builder = new Builder(MainActivity.this);
                    builder.setMessage("修改产品" + txtProduct.getText() + "的数量");

                    builder.setTitle("提示");
                    final EditText txtTitle = new EditText(this);
                    txtTitle.setInputType(InputType.TYPE_CLASS_NUMBER);
                    builder.setView(txtTitle);
                    builder.setNegativeButton("确认",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    OrderItem item = new OrderItem();
                                    item.id = Integer.parseInt(txtId.getText()
                                            .toString());
                                    item.quantity = Integer.parseInt(txtTitle
                                            .getText().toString());
                                    DBHelper.getInstance(
                                            (KangmeiApplication) getApplication())
                                            .saveOrderItem(item);
                                    initOrderList();
                                    dialog.dismiss();
                                }
                            });

                    builder.create().show();
                } else if (txtOrderId != null) {
                    Intent intent = new Intent(MainActivity.this,
                            OrderActivity.class);
                    intent.putExtra("orderid", txtOrderId.getText().toString());
                    intent.putExtra("operate", "modify");

                    startActivityForResult(intent, REQUEST_CODE);
                }
                break;
            case Menu.FIRST + 1:
                if (txtId != null) {
                    TextView txtProduct = (TextView) info.targetView
                            .findViewById(R.id.txtProduct);

                    AlertDialog.Builder builder = new Builder(MainActivity.this);
                    builder.setMessage("删除后不能恢复，你确定要删除 " + txtProduct.getText()
                            + " 么?");
                    builder.setTitle("提示");

                    builder.setNegativeButton("删除",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    DBHelper.getInstance(
                                            (KangmeiApplication) getApplication())
                                            .deleteOrderItem(
                                                    txtId.getText().toString());
                                    initOrderList();
                                    dialog.dismiss();
                                }
                            });
                    builder.setPositiveButton("不删除",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                }

                else if (txtOrderId != null) {
                    TextView txtCustomer = (TextView) info.targetView
                            .findViewById(R.id.txtCustomer);

                    AlertDialog.Builder builder = new Builder(MainActivity.this);
                    builder.setMessage("删除后不能恢复，你确定要删除 " + txtCustomer.getText()
                            + " 的订单么?");
                    builder.setTitle("提示");

                    builder.setNegativeButton("删除",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    DBHelper.getInstance(
                                            (KangmeiApplication) getApplication())
                                            .deleteOrder(
                                                    txtOrderId.getText().toString());
                                    initOrderList();
                                    dialog.dismiss();
                                }
                            });
                    builder.setPositiveButton("不删除",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                }
                break;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("操作");
        // 添加菜单项
        menu.add(0, Menu.FIRST, 0, "修改");
        menu.add(0, Menu.FIRST + 1, 0, "删除");
    }

    class ExpandableAdapter extends BaseExpandableListAdapter {
        private Context context;
        List<Order> groups;
        List<List<OrderItem>> childs;
        BigDecimal price;
        BigDecimal qty;
        private int[] colors = new int[] { 0x30FF0000, 0x300000FF };

        /*
         * 构造函数: 参数1:context对象 参数2:一级列表数据源 参数3:二级列表数据源
         */
        public ExpandableAdapter(Context context, List<Order> groups,
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
}
