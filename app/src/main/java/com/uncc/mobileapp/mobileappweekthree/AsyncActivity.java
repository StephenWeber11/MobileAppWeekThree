package com.uncc.mobileapp.mobileappweekthree;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Random;

public class AsyncActivity extends AppCompatActivity {
    Handler handler;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);

        Log.d("demo","onCreate thread id is " + Thread.currentThread().getId());

        new DoWorkAsync().execute(10000000);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.d("demo","Message Received!");
                switch(msg.what){
                    case MainActivity.DoWork.STATUS_START:
                        progressDialog.setProgress(0);
                        progressDialog.show();
                        Log.d("demo","Starting");
                        break;
                    case MainActivity.DoWork.STATUS_PROGRESS:
                        progressDialog.setProgress(msg.getData().getInt(MainActivity.DoWork.PROGRESS_KEY));
                        Log.d("demo","In Progress " + msg.getData().getInt(MainActivity.DoWork.PROGRESS_KEY));
                        break;
                    case MainActivity.DoWork.STATUS_STOP:
                        progressDialog.dismiss();
                        Log.d("demo","Stoping");
                        break;
                    default:
                        break;
                }

                return false; //Not been handled by MainActivity - returning true means DONE
            }
        });

    }

    class DoWorkAsync extends AsyncTask<Integer, Integer, Double>{

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(AsyncActivity.this);
            progressDialog.setMessage("DOING ASYNC WORK SON!");
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        //Double received from doInBackground
        @Override
        protected void onPostExecute(Double aDouble) {
            Log.d("demo","onPostExecute aDouble is " + aDouble);
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
                Log.d("demo","onProgressUpdate thread id is " + Thread.currentThread().getId());
                Log.d("demo","onProgressUpdate progress is " + values[0]);
                progressDialog.setProgress(values[0]);
        }

        /* Three methods above execute in the Main Thread */

        //Works in child thread of the Main (UI) thread
        @Override
        protected Double doInBackground(Integer... params) {
            Log.d("demo","doInBackground thread id is " + Thread.currentThread().getId());
            double sum = 0;
            double count = 0;
            Random rand = new Random();
            for (int i = 0; i <= 100; i++) {
                for (int j = 0; j < params[0]; j++) {
                    sum = rand.nextDouble() + sum;
                    count++;
                }
                publishProgress(i);
            }
            return sum/count;
        }
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

            Log.d("demo", "Started work!");
            for (int i = 0; i <= 100; i++) {
                for (int j = 0; j < 10000000; j++) {
                }

                Message message = new Message();
                message.what = STATUS_PROGRESS;

                Bundle bundle = new Bundle();
                bundle.putInt(PROGRESS_KEY, (Integer) i);
                message.setData(bundle);
                handler.sendMessage(message);

            }

            Message stopMsg = new Message();
            stopMsg.what = STATUS_STOP;
            handler.sendMessage(stopMsg);
            Log.d("demo", "Ended work!");
        }
    }
}
