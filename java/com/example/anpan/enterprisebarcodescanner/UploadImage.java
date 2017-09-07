package com.example.anpan.enterprisebarcodescanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.anpan.enterprisebarcodescanner.model.UploadImages;

import java.util.ArrayList;

/**
 * Created by Yash on 10-06-2017.
 */

public class UploadImage extends AsyncTask<Void, Void, ArrayList<String>> {

    private ArrayList<String>response = new ArrayList<String>();

    private ProgressDialog progressDialog;
    private WebService webService;
    OnDataloadListListener onDataloadListListener;
    private UploadImages uploadImages = null;

    public UploadImage(OnDataloadListListener onDataloadListListener, WebService webService, Activity mainActivity, UploadImages uploadImages){
        this.onDataloadListListener = onDataloadListListener;
        this.webService = webService;
        this.uploadImages = uploadImages;
        progressDialog = new ProgressDialog(mainActivity);
        progressDialog.setMessage("Uploading");
        progressDialog.setCanceledOnTouchOutside(false);

    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();

        super.onPreExecute();

    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        try {
            response = webService.UploadTicektImages(webService, uploadImages);
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
