package com.example.anpan.enterprisebarcodescanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;


import com.example.anpan.enterprisebarcodescanner.model.TicketDetails;
import com.example.anpan.enterprisebarcodescanner.model.UploadImages;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Yash on 09-06-2017.
 */

public class WebService {

    private String SOAP_ACTION; // = "http://tempuri.org/HelloWorld";

    private String NAMESPACE; // = "http://tempuri.org/";
    private String METHOD_NAME;// = "HelloWorld";

    private static String URL = "http://brentwood.appsondemand.ca/Services/Scanner.asmx?WSDL";
//    private static String URL = "http://10.0.2.2/WebService1.asmx?WSDL";

    public WebService(String SOAP_ACTION, String NAMESPACE, String METHOD_NAME) {
            this.SOAP_ACTION = SOAP_ACTION;
            this.NAMESPACE = NAMESPACE;
            this.METHOD_NAME = METHOD_NAME;
    }

    public ArrayList<String> GetDateTypes(WebService webServiceData) throws Exception {

        ArrayList<String> dateTypes = new ArrayList<>();

        SoapObject request = new SoapObject(webServiceData.NAMESPACE,webServiceData.METHOD_NAME);
        SoapObject result = null;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.headerOut = new Element[1];
        envelope.headerOut[0] = buildAuthHeader();
        Log.i("header", "" + envelope.headerOut[0].toString());
        //envelope.bodyOut = request;

        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        try {
            //this is the actual part that will call the webservice
            androidHttpTransport.call(webServiceData.SOAP_ACTION, envelope);
            result = (SoapObject)envelope.bodyIn;
        } catch (Exception e) {
            e.printStackTrace();
        }


        if(result != null) {
            dateTypes.add("Select an option");

            for(int i = 0; i < result.getPropertyCount(); i++)
            {
                Object property = result.getProperty(i);
                if (property instanceof SoapObject)
                {
                    SoapObject category_list = (SoapObject) property;
                    for(int j=0; j < category_list.getPropertyCount();j++){
                        dateTypes.add(category_list.getProperty(j).toString());
                    }
                }
            }
            return dateTypes;
        }
        else
        {
            dateTypes.add("Data Not Fetched...");
            return dateTypes;
        }

    }

    public ArrayList<String> AdjustItemTicekts(WebService webServiceData, TicketDetails ticketDetails) throws Exception {

        ArrayList<String> dateTypes = new ArrayList<>();

        SoapObject request = new SoapObject(webServiceData.NAMESPACE,webServiceData.METHOD_NAME);
        PropertyInfo propertyInfo = new PropertyInfo();

        Date date = new Date();

        propertyInfo.setName("DateType");
        propertyInfo.setValue(ticketDetails.productType);
        propertyInfo.setType(Integer.class);
        request.addProperty(propertyInfo);

        propertyInfo=new PropertyInfo();
        propertyInfo.setName("dtExpected");
        propertyInfo.setValue(getSOAPDateString(ticketDetails.expectedDate));
        propertyInfo.setType(Object.class);
        request.addProperty(propertyInfo);

        propertyInfo=new PropertyInfo();
        propertyInfo.setName("TicketId");
        propertyInfo.setValue(ticketDetails.ticketValue);
        propertyInfo.setType(String.class);
        request.addProperty(propertyInfo);


        SoapObject result = null;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.headerOut = new Element[1];
        envelope.headerOut[0] = buildAuthHeader();
        Log.i("header", "" + envelope.headerOut[0].toString());
        envelope.bodyOut = request;
        envelope.dotNet = true;

//        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        try {
            //this is the actual part that will call the webservice
            androidHttpTransport.call(webServiceData.SOAP_ACTION, envelope);
            result = (SoapObject)envelope.bodyIn;
        } catch (Exception e) {
            e.printStackTrace();
        }


        if(result != null) {

            for(int i = 0; i < result.getPropertyCount(); i++)
            {
                Object property = result.getProperty(i);
                if (property instanceof SoapObject)
                {
                    SoapObject category_list = (SoapObject) property;
                    for(int j=0; j < category_list.getPropertyCount();j++){
                        dateTypes.add(category_list.getProperty(j).toString());
                    }
                }
            }
            return dateTypes;
        }
        else
        {
            dateTypes.add("Data Not Fetched...");
            return dateTypes;
        }

    }


    public ArrayList<String> UploadTicektImages(WebService webServiceData, UploadImages uploadImages) throws Exception {

        ArrayList<String> dateTypes = new ArrayList<>();

        SoapObject request = new SoapObject(webServiceData.NAMESPACE,webServiceData.METHOD_NAME);
        PropertyInfo propertyInfo = new PropertyInfo();


        Date date = new Date();
        //System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43

        propertyInfo.setName("TicketId");
        propertyInfo.setValue(uploadImages.ticketId);
        propertyInfo.setType(String.class);
        request.addProperty(propertyInfo);

        propertyInfo=new PropertyInfo();
        propertyInfo.setName("f");
        propertyInfo.setValue(getByteFromFile(uploadImages.imageFile));
        propertyInfo.setType(Object.class);
        request.addProperty(propertyInfo);

        SoapObject result = null;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.headerOut = new Element[1];
        envelope.headerOut[0] = buildAuthHeader();
        Log.i("header", "" + envelope.headerOut[0].toString());
        envelope.bodyOut = request;
        envelope.dotNet = true;

        MarshalBase64 marshal = new MarshalBase64();
        marshal.register(envelope);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        try {
            //this is the actual part that will call the webservice
            androidHttpTransport.call(webServiceData.SOAP_ACTION, envelope);
            result = (SoapObject)envelope.bodyIn;
        } catch (Exception e) {
            e.printStackTrace();
        }


        if(result != null) {

            for(int i = 0; i < result.getPropertyCount(); i++)
            {
                Object property = result.getProperty(i);
                dateTypes.add(property.toString());
/*
                if (property instanceof SoapObject)
                {
                    SoapObject category_list = (SoapObject) property;
                    for(int j=0; j < category_list.getPropertyCount();j++){
                        dateTypes.add(category_list.getProperty(j).toString());
                    }
                }
*/
            }
            return dateTypes;
        }
        else
        {
            dateTypes.add("Data Not Fetched...");
            return dateTypes;
        }

    }

    private byte[] getByteFromFile(File imageFile) {


        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(imageFile);
            //bis = new BufferedInputStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

       // BitmapFactory.Options o = new BitmapFactory.Options();
        //o.inJustDecodeBounds = true;
        //int scale = 1;
            //scale = Math.min(o.outWidth/1000, o.outHeight/1000);
          //  scale = (int) Math.pow(2, (int) Math.ceil(Math.log(1000 / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        //o.inSampleSize = scale;
        Bitmap bm = BitmapFactory.decodeStream(fis);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 50 , baos);

        byte[] b = baos.toByteArray();
        return Base64.encode(b, Base64.DEFAULT);
    }


    private Element buildAuthHeader() {
        Element h = new Element().createElement("http://tempuri.org/", "AuthenticationHeader");
        Element username = new Element().createElement("http://tempuri.org/", "Username");
        username.addChild(Node.TEXT, "Cindy");
        h.addChild(Node.ELEMENT, username);
        Element pass = new Element().createElement("http://tempuri.org/", "Password");
        pass.addChild(Node.TEXT, "y0XjvvxEmfpHP5UkYm9YbMBRnIxZZEvm");
        h.addChild(Node.ELEMENT, pass);

        return h;
    }

    private static Object getSOAPDateString(Date itemValue) {
        String lFormatTemplate = "yyyy-MM-dd";
        DateFormat lDateFormat = new SimpleDateFormat(lFormatTemplate);
        String lDate = lDateFormat.format(itemValue);

        return lDate;
    }
}
