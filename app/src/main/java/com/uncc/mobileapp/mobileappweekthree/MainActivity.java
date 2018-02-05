package com.uncc.mobileapp.mobileappweekthree;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Managing threads and thread pools
public class MainActivity extends AppCompatActivity {

    /* Thread pool */
    ExecutorService threadPool;

    Handler handler;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("DOING WORK SON!");
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.d("demo","Message Received!");
                switch(msg.what){
                    case DoWork.STATUS_START:
                        progressDialog.setProgress(0);
                        progressDialog.show();
                        Log.d("demo","Starting");
                        break;
                    case DoWork.STATUS_PROGRESS:
                        progressDialog.setProgress(msg.getData().getInt(DoWork.PROGRESS_KEY));
                        Log.d("demo","In Progress " + msg.getData().getInt(DoWork.PROGRESS_KEY));
                        break;
                    case DoWork.STATUS_STOP:
                        progressDialog.dismiss();
                        Log.d("demo","Stoping");
                        break;
                    default:
                        break;
                }

                return false; //Not been handled by MainActivity - returning true means DONE
            }
        });


        threadPool = Executors.newFixedThreadPool(1);

        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Thread thread = new Thread(new DoWork(), "Worker 1");
                thread.start();*/
                threadPool.execute(new DoWork());
            }
        });
    }

    class DoWork implements Runnable{
        static final int STATUS_START = 0x00;
        static final int STATUS_PROGRESS = 0x01;
        static final int STATUS_STOP = 0x02;
        static final String PROGRESS_KEY = "PROGRESS";

        @Override
        public void run() {
            Message startMsg = new Message();
            startMsg.what = STATUS_START;
            handler.sendMessage(startMsg);

            Log.d("demo","Started work!");
            for(int i=0; i<=100; i++){
                for(int j=0; j<10000000; j++){
                }

                Message message = new Message();
                message.what = STATUS_PROGRESS;

                Bundle bundle = new Bundle();
                bundle.putInt(PROGRESS_KEY, (Integer)i);
                message.setData(bundle);
                handler.sendMessage(message);

            }

            Message stopMsg = new Message();
            stopMsg.what = STATUS_STOP;
            handler.sendMessage(stopMsg);
            Log.d("demo","Ended work!");
        }
    }
}
