package com.qjy.demos.chart.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MyServer extends Service{
    
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }
    
    public class MyBinder extends Binder{
        public MyServer getService(){
            return MyServer.this;
        }
    }
    
    public String getesult(String content){
        String send = "你是猪吗？这么难的问题我怎么可能会！";
        if(content.equals("你好")){
            send = "是的，我知道的";
        }else if(content.contains("hello world") || content.contains("Android")){
            send = "你是屌丝程序员吗";
        }else if(content.contains("呵呵")){
            send = "只有女神才可以说呵呵........呵呵";
        }
        return send;
    }

}
