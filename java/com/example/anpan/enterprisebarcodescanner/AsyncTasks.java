package com.example.anpan.enterprisebarcodescanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by Yash on 10-06-2017.
 */

public class AsyncTasks extends AsyncTask<Void, Void, ArrayList<String>> {

    private ArrayList<String>response = new ArrayList<String>();

    private ProgressDialog progressDialog;
    private WebService webService;
    OnDataloadListListener onDataloadListListener;

    public AsyncTasks(OnDataloadListListener onDataloadListListener, WebService webService, Activity mainActivity){
        this.onDataloadListListener = onDataloadListListener;
        this.webService = webService;
        progressDialog = new ProgressDialog(mainActivity);
        progressDialog.setMessage("Loading");

    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();

        super.onPreExecute();

    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        try {
            response = webService.GetDateTypes(webService);
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {

        if(onDataloadListListener != null){

            onDataloadListListener.onDataloadListReady(strings);
            progressDialog.cancel();
        }

        super.onPostExecute(strings);

    }

}

interface OnDataloadListListener{

   void onDataloadListReady(ArrayList<String> list);

}
