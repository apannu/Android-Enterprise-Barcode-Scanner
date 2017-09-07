package com.example.anpan.enterprisebarcodescanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.anpan.enterprisebarcodescanner.model.TicketDetails;

import java.util.ArrayList;

/**
 * Created by Yash on 10-06-2017.
 */

public class AdjustItemTicekt extends AsyncTask<Void, Void, ArrayList<String>> {

    private ArrayList<String>response = new ArrayList<String>();

    private ProgressDialog progressDialog;
    private WebService webService;
    OnDataloadListListener onDataloadListListener;
    private TicketDetails ticketDetails = null;

    public AdjustItemTicekt(OnDataloadListListener onDataloadListListener, WebService webService, Activity mainActivity
    , TicketDetails ticketDetails){
        this.onDataloadListListener = onDataloadListListener;
        this.webService = webService;
        this.ticketDetails = ticketDetails;
        progressDialog = new ProgressDialog(mainActivity);
        progressDialog.setMessage("Updating");

    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();

        super.onPreExecute();

    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        try {
            response = webService.AdjustItemTicekts(webService, ticketDetails);
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
