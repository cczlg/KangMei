package com.edmond.jimi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edmond.jimi.Constants;
import com.edmond.jimi.KangmeiApplication;
import com.edmond.jimi.entity.Customer;
import com.edmond.jimi.util.DBHelper;
import com.edmond.kangmei.R;

/**
 * Created by apple on 15/9/10.
 */
public class CustomerDialogFragment  extends DialogFragment {
    private String mArgument;
    private String operate;
    public static final String ARGUMENT = "argument";
    public static final String OPERATE = "operate";
    EditText txtCustomer;
    EditText txtPhone;
    EditText txtScore;
    EditText txtAddress;
    TextView txtId;
    Button btnOk;
    Button btnCancel;
    Customer customer;

    public static CustomerDialogFragment newInstance(String operate,String argument){
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        bundle.putString(OPERATE,operate);

        CustomerDialogFragment customerDialogFragment=new CustomerDialogFragment();
        customerDialogFragment.setArguments(bundle);
        return customerDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dialog_customer, container);

//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setTitle(R.string.title_customer);

        Bundle bundle=getArguments();
        mArgument=bundle.getString(ARGUMENT);
        operate=bundle.getString(OPERATE);

        txtCustomer = (EditText) view.findViewById(R.id.txtCustomer);
        txtId = (TextView) view.findViewById(R.id.txtId);
        txtPhone = (EditText) view.findViewById(R.id.txtPhone);
        txtScore = (EditText) view.findViewById(R.id.txtScore);
        txtAddress = (EditText) view.findViewById(R.id.txtAddress);

        btnOk = (Button) view.findViewById(R.id.btnOk);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);

        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setCustomer();
                if (Constants.OPERATE_NEW.equals(operate)
                        || Constants.OPERATE_MODIFY.equals(operate)) {
                    if (save()) {
                        Toast.makeText(getActivity(), R.string.success,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        return;
                    }
                } else if (Constants.OPERATE_ORDER.equals(operate)) {
                    Customer c = DBHelper.getInstance(
                            (KangmeiApplication) getActivity().getApplication()).getCustomer(
                            customer.name);
                    if (c == null) {
                        save();
                    } else {
                        customer = c;
                    }
                }

                Bundle bundle = new Bundle();
                if (customer != null) {
                    if (customer.name == null
                            || customer.name.trim().length() <= 0) {
                        Toast.makeText(getActivity(), R.string.require_customer_name,
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    bundle.putString("id", String.valueOf(customer.id));
                    bundle.putString("customer", String.valueOf(customer.name));
                    bundle.putString("phone", customer.phone);
                    bundle.putString("score", String.valueOf(customer.score));
                    bundle.putString("address", customer.address);
                }
                Intent intent=new Intent();
                intent.putExtras(bundle);
                getActivity().setResult(getActivity().RESULT_OK, intent);

                getDialog().dismiss();
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
            // txtCustomer.setEnabled(false);
            Customer c = DBHelper.getInstance(
                    (KangmeiApplication) getActivity().getApplication()).getCustomer(
                    mArgument);
            txtId.setText(String.valueOf(c.id));
            txtCustomer.setText(c.name);
            txtPhone.setText(c.phone);
            txtAddress.setText(c.address);
            txtScore.setText(String.valueOf(c.score));
        }
    }

    protected void setCustomer() {
        customer = new Customer();
        customer.id = (txtId.getText() == null || txtId.getText().length() == 0) ? 0
                : Integer.parseInt(txtId.getText().toString());
        customer.name = txtCustomer.getText() == null ? "" : txtCustomer
                .getText().toString();
        customer.phone = txtPhone.getText() == null ? "" : txtPhone.getText()
                .toString();
        customer.score = (txtScore.getText() == null || txtScore.getText()
                .length() == 0) ? 0 : Double.parseDouble(txtScore.getText()
                .toString());
        customer.address = txtAddress.getText() == null ? "" : txtAddress
                .getText().toString();
    }

    protected boolean save() {
        if (customer.name == null || customer.name.trim().length() <= 0) {
            Toast.makeText(getActivity(), R.string.require_customer_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        DBHelper.getInstance((KangmeiApplication) getActivity().getApplication())
                .saveCustomer(customer);

        CustomerListFragment customerListFragment= (CustomerListFragment) getTargetFragment();
        customerListFragment.reloadData();
        return true;
    }

}
