package com.edmond.jimi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.SimpleAdapter;

import com.edmond.jimi.entity.Customer;

import java.util.List;
import java.util.Map;

/**
 * Created by apple on 15/9/6.
 */
public class CustomerListAdapter  extends SimpleAdapter implements SectionIndexer{
    private int[] colors = new int[] { 0x30FF0000, 0x300000FF };
    private Context context=null;
    private List<Customer> customers=null;
    /**
     * 布局解析器
     */
    private LayoutInflater layoutInflater = null;


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

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }
}
