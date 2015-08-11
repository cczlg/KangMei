package com.edmond.jimi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edmond.jimi.util.DBHelper;
import com.edmond.jimi.KangmeiApplication;
import com.edmond.jimi.util.PrefrenceTool;
import com.edmond.kangmei.R;
import com.edmond.jimi.entity.Customer;

public class CustomerDetailActivity extends Activity {
	AutoCompleteTextView txtCustomer;
	EditText txtPhone;
	EditText txtScore;
	EditText txtAddress;
	TextView txtId;
	Button btnOk;
	Button btnCancel;
	Customer customer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_customer);
		String title = PrefrenceTool.getStringValue("kangmei", "apptitle",
				getApplication());
		if (title == null || title.length() == 0) {
			title = getResources().getString(R.string.title_activity_customer);
		} else {
			title = title + getResources().getString(R.string.title_activity_customer);
		}
		setTitle(title);

		final Intent intent = getIntent();
		String customers[] = DBHelper.getInstance(
				(KangmeiApplication) getApplication()).getCustomerNames();
		txtCustomer = (AutoCompleteTextView) findViewById(R.id.txtCustomer);
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, customers);
		txtCustomer.setAdapter(aa);
		txtCustomer.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Customer c = DBHelper.getInstance(
						(KangmeiApplication) getApplication()).getCustomer(
						txtCustomer.getText().toString());
				txtPhone.setText(c.phone);
				txtScore.setText(String.valueOf(c.score));
				txtAddress.setText(c.address);
				txtId.setText(String.valueOf(c.id));
			}
		});
		txtId = (TextView) findViewById(R.id.txtId);
		txtPhone = (EditText) findViewById(R.id.txtPhone);
		txtScore = (EditText) findViewById(R.id.txtScore);
		txtAddress = (EditText) findViewById(R.id.txtAddress);

		btnOk = (Button) findViewById(R.id.btnOk);
		btnCancel = (Button) findViewById(R.id.btnCancel);

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				setCustomer();
				if ("new".equals(intent.getStringExtra("operate"))
						|| "modify".equals(intent.getStringExtra("operate"))) {
					if (save()) {
						Toast.makeText(CustomerDetailActivity.this, "����ɹ�",
								Toast.LENGTH_SHORT).show();
					} else {
						return;
					}
				} else if ("order".equals(intent.getStringExtra("operate"))) {
					Customer c = DBHelper.getInstance(
							(KangmeiApplication) getApplication()).getCustomer(
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
						Toast.makeText(CustomerDetailActivity.this, "�ͻ���Ʋ��ܿ�",
								Toast.LENGTH_LONG).show();
						return;
					}
					bundle.putString("id", String.valueOf(customer.id));
					bundle.putString("customer", String.valueOf(customer.name));
					bundle.putString("phone", customer.phone);
					bundle.putString("score", String.valueOf(customer.score));
					bundle.putString("address", customer.address);
				}

				intent.putExtras(bundle);
				// ����intent
				setResult(RESULT_OK, intent);

				CustomerDetailActivity.this.finish();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CustomerDetailActivity.this.finish();
			}
		});

		if ("modify".equals(intent.getStringExtra("operate"))) {
			// txtCustomer.setEnabled(false);
			Customer c = DBHelper.getInstance(
					(KangmeiApplication) getApplication()).getCustomer(
					intent.getStringExtra("customer"));
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
			Toast.makeText(CustomerDetailActivity.this, "�ͻ���Ʋ��ܿ�",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		DBHelper.getInstance((KangmeiApplication) getApplication())
				.saveCustomer(customer);
		return true;
	}

}
