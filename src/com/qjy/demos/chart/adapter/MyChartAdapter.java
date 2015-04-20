package com.qjy.demos.chart.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import com.qjy.demos.chart.MainActivity;
import com.qjy.demos.chart.R;
import com.qjy.demos.chart.bean.ChartMessage;

import android.content.Context;
import android.provider.ContactsContract.Contacts.Data;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 左右chart 布局 Adapter
 *
 * @author Administrator
 */
public class MyChartAdapter extends BaseAdapter {

    private List<ChartMessage> messages;
    private Context context;
    private LayoutInflater inflater;
    private List<String> fileNames;
    private final AssetManager assets;

    public MyChartAdapter(List<ChartMessage> messages, Context context) {
        this.messages = messages;
        this.context = context;

        fileNames = MainActivity.fileNames;

        inflater = LayoutInflater.from(context);
        assets = context.getAssets();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        int ret = 0;

        ChartMessage message = messages.get(position);

        if (message != null) {

            if (message.getFrom().equals("me")) {
                ret = 0;
            } else {
                ret = 1;
            }
        }

        return ret;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret = convertView;
        ChartMessage message = messages.get(position);

        if (message.getFrom().equals("me")) {
            ViewHolder1 viewHolder;
            if (convertView == null) {
                ret = inflater.inflate(R.layout.item_right, parent, false);
                viewHolder = new ViewHolder1();
                viewHolder.img_icon = (ImageView) ret.findViewById(R.id.img_righticon);
                viewHolder.txt_content = (TextView) ret.findViewById(R.id.txt_right_content);
                viewHolder.txt_time = (TextView) ret.findViewById(R.id.txt_right_time);
                ret.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder1) convertView.getTag();
                ret = convertView;
            }

            String str = message.getContent();

            SpannableStringBuilder sb = new SpannableStringBuilder();
            sb.append(str);
            int start = str.indexOf("</:");
            while(start != -1) {
                int end = str.indexOf(">", start);

                if (end != -1) {
                    String img = str.substring(start + "</:".length(), end);
                    for (String fileName : fileNames) {
                        if (fileName.equals(img)) {
                            try {
                                InputStream inputStream = assets.open("img/" + fileName);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                ImageSpan imgSpan = new ImageSpan(context, bitmap);
                                sb.setSpan(imgSpan, start, end + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                start = str.indexOf("</:",start+1);
            }


            viewHolder.txt_content.setText(sb);
            Date date = new Date(message.getTime());
            String dateStr = new SimpleDateFormat("MM-dd HH:MM:SS").format(date);
            viewHolder.txt_time.setText(dateStr);

        } else {
            ViewHolder1 viewHolder;
            if (convertView == null) {
                ret = inflater.inflate(R.layout.item_left, parent, false);
                viewHolder = new ViewHolder1();
                viewHolder.img_icon = (ImageView) ret.findViewById(R.id.img_lefticon);
                viewHolder.txt_content = (TextView) ret.findViewById(R.id.txt_content);
                viewHolder.txt_time = (TextView) ret.findViewById(R.id.txt_time);
                ret.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder1) convertView.getTag();
                ret = convertView;
            }

            String str = message.getContent();

            SpannableStringBuilder sb = new SpannableStringBuilder();
            sb.append(str);

            int start = str.indexOf("</:");

            Log.d("MyChatAdapter", "StartIndex : " + start);

            viewHolder.txt_content.setText(message.getContent());
            Date date = new Date(message.getTime());
            String dateStr = new SimpleDateFormat("MM-dd HH:MM:SS").format(date);
            viewHolder.txt_time.setText(dateStr);
            viewHolder.img_icon.setImageResource(R.drawable.cat);
        }

        return ret;
    }

    class ViewHolder1 {
        ImageView img_icon;
        TextView txt_content;
        TextView txt_time;
    }

    class ViewHolder2 {
        ImageView img_icon;
        TextView txt_content;
        TextView txt_time;
    }

}
