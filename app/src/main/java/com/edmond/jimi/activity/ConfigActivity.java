package com.edmond.jimi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.edmond.jimi.util.PrefrenceTool;
import com.edmond.kangmei.R;

public class ConfigActivity extends Activity {
	EditText txtSalesman;
	EditText txtPhone;
	EditText txtDevPhone;
	Button btnOk;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);
		String title = PrefrenceTool.getStringValue("kangmei", "apptitle",
				getApplication());
		if (title == null || title.length() == 0) {
			title = getResources().getString(R.string.title_activity_config);
		}
		setTitle(title);

		txtSalesman = (EditText) findViewById(R.id.txtSalesMan);
		txtSalesman.setText(PrefrenceTool.getStringValue("kangmei", "salesman",
				getApplicationContext()));
		txtPhone = (EditText) findViewById(R.id.txtPhone);
		txtPhone.setText(PrefrenceTool.getStringValue("kangmei", "phone",
				getApplicationContext()));
		txtDevPhone = (EditText) findViewById(R.id.txtDevPhone);
		txtDevPhone.setText(PrefrenceTool.getStringValue("kangmei",
				"deliveryphone", getApplicationContext()));

		btnOk = (Button) findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				save();
				ConfigActivity.this.finish();
			}
		});
	}

	private void save() {
		PrefrenceTool.saveValue("kangmei", "salesman", txtSalesman.getText()
				.toString(), getApplicationContext());
		PrefrenceTool.saveValue("kangmei", "phone", txtPhone.getText()
				.toString(), getApplicationContext());
		PrefrenceTool.saveValue("kangmei", "deliveryphone", txtDevPhone
				.getText().toString(), getApplicationContext());
	}

	public void onBackPressed() {
		super.onBackPressed();
	}
}
