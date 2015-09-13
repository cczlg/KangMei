package com.edmond.jimi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by apple on 15/9/6.
 */
public class CustomerListAdapter  extends SimpleAdapter {
    private int[] colors = new int[] { 0x30FF0000, 0x300000FF };

    public CustomerListAdapter(Context context,
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
