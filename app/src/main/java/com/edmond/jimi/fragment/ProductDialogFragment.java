package com.edmond.jimi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edmond.jimi.Constants;
import com.edmond.jimi.KangmeiApplication;
import com.edmond.jimi.entity.Product;
import com.edmond.jimi.util.DBHelper;
import com.edmond.kangmei.R;

/**
 * Created by apple on 15/9/12.
 */
public class ProductDialogFragment  extends DialogFragment {
    private String mArgument;
    private String operate;
    public static final String ARGUMENT = "argument";
    public static final String OPERATE = "operate";

    EditText txtProduct;
    EditText txtPurchasePrice;
    EditText txtPrice;
    EditText txtImage;
    TextView txtId;
    Button btnOk;
    Button btnCancel;
    Button btnSelect;
    Product product;

    public static ProductDialogFragment newInstance(String operate,String argument){
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        bundle.putString(OPERATE,operate);

        ProductDialogFragment productDialogFragment=new ProductDialogFragment();
        productDialogFragment.setArguments(bundle);
        return productDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_product, container);


        Bundle bundle = getArguments();
        mArgument = bundle.getString(ARGUMENT);
        operate = bundle.getString(OPERATE);


        txtProduct = (EditText) view.findViewById(R.id.txtProduct);
        txtPrice = (EditText) view.findViewById(R.id.txtPrice);
        txtImage = (EditText) view.findViewById(R.id.txtImage);
        txtId = (TextView) view.findViewById(R.id.txtId);
        txtPurchasePrice=(EditText)view.findViewById(R.id.txtPurchasePrice);

        btnOk = (Button) view.findViewById(R.id.btnOk);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnSelect = (Button) view.findViewById(R.id.btnSelect);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (save()) {
                    Toast.makeText(getActivity(), getString(R.string.success),
                            Toast.LENGTH_SHORT).show();

                    getDialog().dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (Constants.OPERATE_MODIFY.equals(operate)) {
            txtProduct.setEnabled(false);
            Product c = DBHelper.getInstance(
                    (KangmeiApplication) getActivity().getApplication()).getProduct(mArgument);
            if (c != null) {
                txtProduct.setText(c.product);
                txtPurchasePrice.setText(String.valueOf(c.purchasePrice));
                txtPrice.setText(String.valueOf(c.price));
                txtImage.setText(c.image);
                txtId.setText(String.valueOf(c.id));

            }
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

        DBHelper.getInstance((KangmeiApplication) getActivity().getApplication())
                .saveProduct(product);

        ProductListFragment productListFragment= (ProductListFragment) getTargetFragment();
        productListFragment.reloadData();
        return true;
    }
}
