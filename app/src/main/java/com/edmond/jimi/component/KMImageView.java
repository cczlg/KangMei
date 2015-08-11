package com.edmond.jimi.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageView;

public class KMImageView extends ImageView {
	String bitMapName;
	EditText editText;

	public KMImageView(Context context) {
		super(context);
	}
	
	public KMImageView(Context context,AttributeSet attrs){
		super(context,attrs);
	}

	public String getBitMapName() {
		return bitMapName;
	}

	public void setBitMapName(String bitMapName) {
		this.bitMapName = bitMapName;
	}

	public EditText getEditText() {
		return editText;
	}

	public void setEditText(EditText editText) {
		this.editText = editText;
	}

}
