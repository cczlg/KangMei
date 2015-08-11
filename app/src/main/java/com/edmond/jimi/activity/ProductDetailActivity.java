package com.edmond.jimi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edmond.jimi.util.DBHelper;
import com.edmond.jimi.component.FileDialog;
import com.edmond.jimi.KangmeiApplication;
import com.edmond.jimi.util.PrefrenceTool;
import com.edmond.jimi.entity.Product;
import com.edmond.kangmei.R;

public class ProductDetailActivity extends Activity {
    EditText txtProduct;
    EditText txtPurchasePrice;
    EditText txtPrice;
    EditText txtImage;
    TextView txtId;
    Button btnOk;
    Button btnCancel;
    Button btnSelect;
    Product product;
    int REQUEST_SAVE = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_product);
        String title = PrefrenceTool.getStringValue("kangmei", "apptitle",
                getApplication());
        if (title == null || title.length() == 0) {
            title = getResources().getString(R.string.title_activity_customer);
        } else {
            title = title + getResources().getString(R.string.title_activity_customer);
        }
        setTitle(title);

        txtProduct = (EditText) findViewById(R.id.txtProduct);
        txtPrice = (EditText) findViewById(R.id.txtPrice);
        txtImage = (EditText) findViewById(R.id.txtImage);
        txtId = (TextView) findViewById(R.id.txtId);
        txtPurchasePrice=(EditText)findViewById(R.id.txtPurchasePrice);

        btnOk = (Button) findViewById(R.id.btnOk);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSelect = (Button) findViewById(R.id.btnSelect);

        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (save()) {
                    Toast.makeText(ProductDetailActivity.this, getString(R.string.success),
                            Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    if (product != null) {
                        bundle.putString("product", product.product);
                        bundle.putString("price", String.valueOf(product.price));
                        bundle.putString("memo", product.memo);
                        bundle.putString("image", product.image);
                        bundle.putString("purchasePrice", String.valueOf(product.purchasePrice));
                    }
                    Intent intent = getIntent();
                    intent.putExtras(bundle);
                    // ·µ»Øintent
                    setResult(RESULT_OK, intent);
                    ProductDetailActivity.this.finish();
                }
            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                ProductDetailActivity.this.finish();
            }
        });

        Intent intent = getIntent();
        if ("modify".equals(intent.getStringExtra("operate"))) {
            txtProduct.setEnabled(false);
            Product c = DBHelper.getInstance(
                    (KangmeiApplication) getApplication()).getProduct(
                    intent.getStringExtra("product"));
            if (c != null) {
                txtProduct.setText(c.product);
                txtPurchasePrice.setText(String.valueOf(c.purchasePrice));
                txtPrice.setText(String.valueOf(c.price));
                txtImage.setText(c.image);
                txtId.setText(String.valueOf(c.id));

            }
        }

        btnSelect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), FileDialog.class);
                intent.putExtra(FileDialog.START_PATH, "/sdcard/kangmei/image");

                // can user select directories or not
                intent.putExtra(FileDialog.CAN_SELECT_DIR, true);

                // alternatively you can set file filter
                // intent.putExtra(FileDialog.FORMAT_FILTER, new String[] {
                // "png" });

                startActivityForResult(intent, REQUEST_SAVE);
            }
        });
    }

    public synchronized void onActivityResult(final int requestCode,
                                              int resultCode, final Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_SAVE) {
                System.out.println("Saving...");
            }

            String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
            System.out.println(filePath);
            txtImage.setText(filePath);

        } else if (resultCode == Activity.RESULT_CANCELED) {

        }

    }

    protected boolean save() {
        product = new Product();
        product.id = (txtId.getText() == null || txtId.getText().length() == 0) ? 0
                : Integer.parseInt(txtId.getText().toString());
        product.product = txtProduct.getText() == null ? "" : txtProduct
                .getText().toString();
        product.price = (txtPrice.getText() == null || txtPrice.getText()
                .length() == 0) ? 0 : Double.parseDouble(txtPrice.getText()
                .toString());
        product.purchasePrice = (txtPurchasePrice.getText() == null || txtPurchasePrice.getText()
                .length() == 0) ? 0 : Double.parseDouble(txtPurchasePrice.getText()
                .toString());
        product.image = txtImage.getText() == null ? "" : txtImage.getText()
                .toString();

        DBHelper.getInstance((KangmeiApplication) getApplication())
                .saveProduct(product);
        return true;
    }
}
