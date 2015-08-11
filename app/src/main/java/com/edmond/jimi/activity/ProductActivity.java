package com.edmond.jimi.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.edmond.jimi.util.DBHelper;
import com.edmond.jimi.KangmeiApplication;
import com.edmond.jimi.util.PrefrenceTool;
import com.edmond.kangmei.R;
import com.edmond.jimi.entity.Product;

public class ProductActivity extends Activity {
    int REQUEST_CODE = 2013;
    ArrayList<Product> list;
    String imagePath;
    ProductListAdapter adapter;
    private int REQUEST_CODE_MODIFY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        String title = PrefrenceTool.getStringValue("kangmei", "apptitle",
                getApplication());
        if (title == null || title.length() == 0) {
            title = getResources().getString(R.string.title_activity_product);
        } else {
            title = title + "--产品";
        }
        setTitle(title);

        imagePath = ((KangmeiApplication) getApplication()).imagepath;

        ListView productList = (ListView) findViewById(R.id.productListView);

        list = DBHelper.getInstance((KangmeiApplication) this.getApplication())
                .getProducts();

        adapter = new ProductListAdapter(ProductActivity.this, list);

        productList.setAdapter(adapter);
        productList.setOnCreateContextMenuListener(ProductActivity.this);
    }

    protected void deleteDialog(final String product) {

        AlertDialog.Builder builder = new Builder(ProductActivity.this);
        builder.setMessage(getString(R.string.confirm_delete));
        builder.setTitle(getString(R.string.warning));

        builder.setPositiveButton(getString(R.string.ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBHelper.getInstance((KangmeiApplication) getApplication())
                        .deleteProduct(product);
                for (Product p : list) {
                    if (product.equals(p.product)) {
                        list.remove(p);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new OnClickListener() {

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
        TextView txtProduct = (TextView) info.targetView
                .findViewById(R.id.txtProduct);
        switch (item.getItemId()) {
            case Menu.FIRST:
                Intent intent = new Intent(ProductActivity.this,
                        ProductDetailActivity.class);
                intent.putExtra("operate", "new");
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case Menu.FIRST + 1:
                intent = new Intent(ProductActivity.this,
                        ProductDetailActivity.class);
                intent.putExtra("operate", "modify");
                intent.putExtra("product", txtProduct.getText().toString());
                startActivityForResult(intent, REQUEST_CODE_MODIFY);
                break;
            case Menu.FIRST + 2:
                deleteDialog(txtProduct.getText().toString());
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_new) {
            Intent intent = new Intent(ProductActivity.this,
                    ProductDetailActivity.class);
            intent.putExtra("operate", "new");
            startActivityForResult(intent, REQUEST_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Product product = new Product();
                product.image = data.getExtras().getString("image");
                product.product = data.getExtras().getString("product");
                product.price = Double.parseDouble(data.getExtras().getString(
                        "price"));
                product.memo = data.getExtras().getString("memo");
                product.purchasePrice=Double.parseDouble(data.getExtras().getString("purchasePrice"));
                list.add(product);

                adapter.notifyDataSetChanged();
            }
        } else if (requestCode == REQUEST_CODE_MODIFY) {
            if (resultCode == RESULT_OK) {
                for (Product product : list) {
                    if (data.getExtras().getString("product") != null
                            && data.getExtras().getString("product")
                            .equals(product.product)) {
                        product.image = data.getExtras().getString("image");
                        product.price = Double.parseDouble(data.getExtras()
                                .getString("price"));
                        product.memo = data.getExtras().getString("memo");
                        product.purchasePrice=Double.parseDouble(data.getExtras().getString("purchasePrice"));
                    }
                }

                adapter.notifyDataSetChanged();
            }
        }
    }

    public class ProductListAdapter extends BaseAdapter {
        private Activity context;
        private List<Product> list;
        private int[] colors = new int[] { 0x30FF0000, 0x300000FF };

        public ProductListAdapter(Activity context, List<Product> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View itemView = inflater.inflate(R.layout.product_list_item, null);
            Product product = list.get(position);
            TextView textView = (TextView) itemView
                    .findViewById(R.id.txtProduct);
//			ImageView imageView = (ImageView) itemView
//					.findViewById(R.id.productImage);
            textView.setText(product.product);
//			imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath
//					+ product.image));
            TextView txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            txtPrice.setText(String.valueOf(product.price));

            TextView txtPurchasePrice=(TextView) itemView.findViewById(R.id.txtPurchasePrice);
            txtPurchasePrice.setText(String.valueOf(product.purchasePrice));

            int colorPos = position % colors.length;
            if (colorPos == 1)
                itemView.setBackgroundColor(Color.argb(250, 255, 255, 255));
            else
                itemView.setBackgroundColor(Color.argb(250, 224, 243, 250));
            return itemView;
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
}
