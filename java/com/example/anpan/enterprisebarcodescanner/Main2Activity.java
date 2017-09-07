package com.example.anpan.enterprisebarcodescanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anpan.enterprisebarcodescanner.model.TicketDetails;
import com.example.anpan.enterprisebarcodescanner.model.UploadImages;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class Main2Activity extends AppCompatActivity {

    private static final int REQUEST_CAMERA= 1;
    private Uri imageUri;
    private AlertDialog alertDialog1;
    private AlertDialog alertDialog2;
    private AlertDialog alertDialog3;
    private AlertDialog alertDialog4;
    private int count = 1;
    private String successMessage = "Data has been updated successfully";
    private String formattedDate = "";
    private String barcodeValue = "";
    private int hintPosition = 0;
    private Spinner spinner;
    private TicketDetails ticketDetails = null;

    //for web service
    private WebService webService, webService2, webService3;

    //for spinner data
    private ArrayList<String> getDateTypesList;

    private File photo;
    private File photo1;
    private String filePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //get the barcode value from the previous activity
        Bundle bundle = getIntent().getExtras();
         barcodeValue = bundle.getString("barcodeValue");

        //set the barcode value into the textview
        final TextView barcodeText = (TextView)findViewById(R.id.barcodeValue);
        barcodeText.setText(barcodeValue);

        //get the values of bar code types
        Resources resources = getResources();

        //webservice object
        webService = new WebService("http://tempuri.org/GetDateTypes","http://tempuri.org/","GetDateTypes");

        AsyncTasks asyncTasks = new AsyncTasks(new OnDataloadListListener() {
            @Override
            public void onDataloadListReady(ArrayList<String> list) {

                getDateTypesList = list;
                Log.d("Response Text:",list.toString());
                //set the values and hint in Spinner
//        String[] typeValues = resources.getStringArray(R.array.typeValues);
                spinner = (Spinner)findViewById(R.id.typeValues);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Main2Activity.this,
                        android.R.layout.simple_spinner_item, getDateTypesList)
                {
                    @Override
                    public boolean isEnabled(int position){
                        if(position == 0)
                        {
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        //convert the hint item to textview
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if(position == 0){
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                        }
                        else {
                            tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }
                };
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItemText = (String) parent.getItemAtPosition(position);
                        // If user change the default selection
                        // First item is disable and it is used for hint
                        if (position > 0) {
                            // Notify the selected item text
                            //Toast.makeText
                              //      (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                                //    .show();

                            //get the calender date value
                            CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView2);
                            //        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            Date date = new Date(calendarView.getDate());
                            //        formattedDate = simpleDateFormat.format(date);

                            ticketDetails = new TicketDetails(barcodeValue, date, spinner.getSelectedItemPosition() - 1);

                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
//do nothing
                    }
                });
            }
        }, webService, this);

        asyncTasks.execute();

        //webservice 2 for token - barcode
        webService2 = new WebService("http://tempuri.org/AdjustItemTicekts","http://tempuri.org/","AdjustItemTicekts");

        //code for update button
        Button updateButton = (Button) findViewById(R.id.update);

            updateButton.setOnClickListener
                    (new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //do some stuff to update the data



                            if(barcodeText.getText()!=null) {
                                if (spinner.getSelectedItemPosition()!=0) {

                                    AdjustItemTicekt adjustItemTicekt = new AdjustItemTicekt(new OnDataloadListListener() {
                                        @Override
                                        public void onDataloadListReady(ArrayList<String> list) {
                                            alertDialog4 = new AlertDialog.Builder(Main2Activity.this).create();
                                            alertDialog4.setTitle("Data has been successfully updated");
                                            alertDialog4.setMessage("\n"+"Barcode Value: "+barcodeText.getText() + "\n" + formattedDate+"\n"+"Product Type: "+spinner.getSelectedItem().toString());
                                            alertDialog4.setCanceledOnTouchOutside(false);

                                            alertDialog4.setButton
                                                    (DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Button addImagesButton = (Button) findViewById(R.id.addImages);
                                                                    addImagesButton.setEnabled(true);
                                                                }
                                                            }
                                                    );
                                            alertDialog4.show();
                                        }
                                    },webService2,Main2Activity.this, ticketDetails);

                                    adjustItemTicekt.execute();
                                }
                                else {
                                    Toast.makeText
                                            (getApplicationContext(), "Please select a product type.", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                            else {
                                Toast.makeText
                                        (getApplicationContext(), "Invalid barcode value. Please re-scan.", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });

        //code for add images button
        //need to change the validation
        Button addImagesButton = (Button) findViewById(R.id.addImages);
        if(barcodeText.getText()!=null)
        {
            addImagesButton.setOnClickListener
                    (new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            //photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "scan.jpg");
                            //photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "scan.jpg");
                            //photo1 = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                            /*try {
                                photo = File.createTempFile(
                                        "scan",  *//* prefix *//*
                                        ".jpg",         *//* suffix *//*
                                        photo1      *//* directory *//*
                                );
                            }
                            catch
                                    (Exception e)
                            {
                                e.printStackTrace();
                            }
                            filePath = photo.getAbsolutePath();
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, REQUEST_CAMERA);*/

                                        //restoring to previous code
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            photo = new File(Environment.getExternalStorageDirectory(), "scan.jpg");
                            imageUri = Uri.fromFile(photo);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        }
                    });
        }
        else
        {
            //no action
        }

        webService3 = new WebService("http://tempuri.org/UploadTicektImages","http://tempuri.org/","UploadTicektImages");
    }

    //method to scan the bar code
    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        /*File file = new File(filePath);
        imageUri = Uri.fromFile(file);*/
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    //overriden method, that gets activated when camera takes a picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            if(imageUri!=null)
            {
                alertDialog1 = new AlertDialog.Builder(Main2Activity.this).create();
                alertDialog1.setTitle("Do you want to upload the Image to the Bar Code");
                alertDialog1.setCanceledOnTouchOutside(false);
                alertDialog1.setButton
                        (DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //do stuff to upload the picture.

                                        UploadImages uploadImages = new UploadImages(photo,barcodeValue);
                            UploadImage uploadImage = new UploadImage(new OnDataloadListListener() {
                                @Override
                                public void onDataloadListReady(ArrayList<String> list) {
                                    Log.d("Response:", list.toString());

                                    //to check the condition of maximum upload
                                    if(count==2)
                                    {
                                        alertDialog3 = new AlertDialog.Builder(Main2Activity.this).create();
                                        alertDialog3.setTitle("Your Images have been uploaded successfully.");
                                        alertDialog3.setCanceledOnTouchOutside(false);

                                        alertDialog3.setButton
                                                (DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                );
                                        alertDialog3.show();
                                    }

                                    //alert to ask for further upload
                                    if(count<2)
                                    {
                                        count++;
                                        alertDialog2 = new AlertDialog.Builder(Main2Activity.this).create();
                                        alertDialog2.setTitle("Uploaded! Do you want to add another image?");
                                        alertDialog2.setCanceledOnTouchOutside(false);

                                        alertDialog2.setButton
                                                (DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                                photo = new File(Environment.getExternalStorageDirectory(), "scan.jpg");
                                                                imageUri = Uri.fromFile(photo);
                                                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                                                startActivityForResult(intent, REQUEST_CAMERA);

                                                            }
                                                        }
                                                );
                                        alertDialog2.setButton
                                                (DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                                            intent.putExtra("endProgram", "End");
                                                        startActivity(intent);
                                                    }
                                                });
                                        alertDialog2.show();
                                    }

                                }
                            }, webService3, Main2Activity.this, uploadImages);

                                        uploadImage.execute();

                                    }
                                }
                        );
                alertDialog1.setButton
                        (DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel();
                            }
                        });
                alertDialog1.show();
            }
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
