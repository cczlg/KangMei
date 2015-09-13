package com.edmond.jimi.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edmond.jimi.entity.Product;
import com.edmond.kangmei.R;

import java.util.List;

/**
 * Created by apple on 15/9/6.
 */
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
