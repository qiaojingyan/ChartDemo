package com.qjy.demos.chart.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.qjy.demos.chart.R;

import java.util.List;

/**
 * Created by qjy on 15-4-16.
 */
public class MyImageAdapter extends BaseAdapter{

    private List<Bitmap> list;
    private Context context;
    private LayoutInflater inflater;
    private onImageButtonClickListener listener;

    public MyImageAdapter(List<Bitmap> list, Context context,onImageButtonClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View ret = null;
        ImageButton btn = null;

        if (view == null) {
            ret = inflater.inflate(R.layout.item_gridview,viewGroup,false);
            btn = (ImageButton) ret.findViewById(R.id.imgbtn_expression);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.getPosition(i);
                }
            });
            ret.setTag(btn);
        }else{
            ret = view;
            btn = (ImageButton) ret.getTag();
        }

        btn.setImageBitmap(list.get(i));

        return ret;
    }

    public interface onImageButtonClickListener{
        public void getPosition(int position);
    }
}
