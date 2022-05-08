package com.example.webshop;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Random;

public class SignupAsyncTask extends AsyncTask<Void, Void, String> {

    private WeakReference<TextView> mTextView;

      public SignupAsyncTask(TextView textView){
        mTextView= new WeakReference<>(textView);
    }

    @Override
    protected String doInBackground(Void... voids) {
        Random random = new Random();
        int number = random.nextInt(11);
        int ms = number*600;

        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "If you have an account, click the cancel button!";
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onPostExecute(String s) {
        //super.onPostExecute(s);
        //mTextView.get().setTextColor(R.color.purple_700);
        // mTextView.get().setText(s);
        mTextView.get().setTextColor(Color.RED);
    }
}


