package com.qjy.demos.chart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.text.*;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import com.qjy.demos.chart.adapter.MyChartAdapter;
import com.qjy.demos.chart.adapter.MyImageAdapter;
import com.qjy.demos.chart.bean.ChartMessage;
import com.qjy.demos.chart.service.MyServer;
import com.qjy.demos.chart.service.MyServer.MyBinder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    private MyServer myServer = null;
    private ServiceConnection conn = null;
    private List<ChartMessage> messages = null;
    private MyChartAdapter adapter;
    public static List<String> fileNames;
    private AssetManager manager;
    private AlertDialog dialog;
    private EditText edt_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                myServer = null;

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                myServer = ((MyBinder) service).getService();
            }
        };
        if (myServer == null) {
            bindService(new Intent(MainActivity.this, MyServer.class), conn, BIND_AUTO_CREATE);
        }


        fileNames = new ArrayList<String>();
        manager = getAssets();
        String[] images = new String[0];
        try {
            images = manager.list("img");
            fileNames.addAll(Arrays.asList(images));
        } catch (IOException e) {
            e.printStackTrace();
        }


        ListView listView = (ListView) findViewById(R.id.list_main);
        edt_info = (EditText) findViewById(R.id.edt_info);
        Button btn_send = (Button) findViewById(R.id.btn_send);

        btn_send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (myServer != null && !edt_info.getText().toString().equals("")) {
                    String content = edt_info.getText().toString();
                    edt_info.setText("");
                    initMyMessage(content);
                    String result = myServer.getesult(content);
                    initMessage(result);
                }

            }

        });


        messages = new ArrayList<ChartMessage>();

        adapter = new MyChartAdapter(messages, this);

        listView.setAdapter(adapter);

        edt_info.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("MainActivty", "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("MainActivty", "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.e("MainActivty", "afterTextChanged");
                int position = edt_info.getSelectionEnd();
                SpannableStringBuilder sb = new SpannableStringBuilder();
                sb.append(editable);
                initEdt(sb);
                edt_info.removeTextChangedListener(this);

                edt_info.setText(sb);
                Log.e("MainActivity","edt_info : "+sb);
                Editable edt = edt_info.getText();
                Selection.setSelection(edt,position);
                edt_info.addTextChangedListener(this);
            }
        });


    }

    private void initMyMessage(String content) {
        ChartMessage message = new ChartMessage();
        message.setContent(content);
        message.setFrom("me");
        message.setTo("机器猫");
        message.setTime(System.currentTimeMillis());
        initList(message);
    }

    private void initMessage(String result) {
        ChartMessage message = new ChartMessage();
        message.setContent(result);
        message.setFrom("机器猫");
        message.setTo("me");
        message.setTime(System.currentTimeMillis());
        initList(message);
    }

    private void initList(ChartMessage message) {
        messages.add(message);
        adapter.notifyDataSetChanged();
    }

    public void btnAdd(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_addimg, null);
        GridView gridView = (GridView) view.findViewById(R.id.gridView_dialog);
        List<Bitmap> list = new ArrayList<Bitmap>();
        for (String fileName : fileNames) {
            try {
                InputStream inputStream = manager.open("img/" + fileName);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                list.add(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MyImageAdapter adapter1 = new MyImageAdapter(list, this, new MyImageAdapter.onImageButtonClickListener() {
            @Override
            public void getPosition(int position) {

                String str = edt_info.getText().toString();

                int selectionStart = edt_info.getSelectionStart();
                int selectionEnd = edt_info.getSelectionEnd();
                String str1 = str.substring(0,selectionStart);
                String str2 = str.substring(selectionStart,str.length());
                Log.e("MainActivity","Start : "+selectionStart+" End : "+selectionEnd);

                StringBuilder sb = new StringBuilder();
                sb.append(str1);
                sb.append("</:");
                sb.append(fileNames.get(position));
                sb.append(">");
                int imgEnd = sb.length();
                sb.append(str2);
                edt_info.setText(sb);

                Editable edt = edt_info.getText();

                Selection.setSelection(edt,imgEnd);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        gridView.setAdapter(adapter1);
        builder.setView(view);
        dialog = builder.show();
    }

    private void initEdt(SpannableStringBuilder sb) {
        String str = sb.toString();
        int start = str.indexOf("</:");
        while (start != -1) {
            int end = str.indexOf(">", start);
            if (end != -1) {
                String img = str.substring(start + "</:".length(), end);
                for (String fileName : fileNames) {
                    if (fileName.equals(img)) {
                        try {
                            InputStream inputStream = manager.open("img/" + fileName);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            ImageSpan imgSpan = new ImageSpan(MainActivity.this, bitmap, ImageSpan.ALIGN_BASELINE);

                            Log.e("initEdit","start : "+sb.charAt(start)+" end : "+sb.charAt(end));

                            sb.setSpan(imgSpan, start, end+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            start = str.indexOf("</:", start + 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
