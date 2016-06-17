package com.asiawaters.fieldapprover;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.asiawaters.fieldapprover.classes.Model_ListMembers;
import com.asiawaters.fieldapprover.classes.Model_TaskListFields;
import com.asiawaters.fieldapprover.classes.Model_TaskMember;
import com.asiawaters.fieldapprover.classes.Utility;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.kobjects.base64.Base64;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    Model_ListMembers mlm;
    Model_TaskMember taskMembers;

    private ListView mListView1, mListView2;

    private String[] data1 = {"Hiren", "Pratik", "Dhruv", "Narendra", "Piyush", "Priyank","Priyank","Priyank","Priyank","Priyank","Priyank","Priyank","Priyank","Priyank","Priyank","Priyank","Priyank","Priyank"};
    private String[] data2 = {"Kirit", "Miral", "Bhushan", "Jiten", "Ajay", "Kamlesh"};
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPreviousActivity();
            }
        });

        mlm = ((FieldApprover) this.getApplication()).getListMember();
        new LoginTask().execute();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private boolean doLogin(String user_id, String password) {

        String NAMESPACE = "Mobile";
        String URL = "http://193.193.245.125:3108/ast2/ws/Mobile";

        boolean result = false;
        final String SOAP_ACTION = "Mobile/MobilePortType/GetStatusRequest";
        final String METHOD_NAME = "GetTask";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("GUIDTask", mlm.getGuidTask());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.implicitTypes = true;
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);
        System.out.println(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        androidHttpTransport.debug = true;

        ArrayList headerProperty = new ArrayList();
        headerProperty.add(new HeaderProperty("Authorization", "Basic " +
                Base64.encode(("aw" + ":" + "123").getBytes())));


        try {
            androidHttpTransport.call(SOAP_ACTION, envelope, headerProperty);
            Log.d("dump Request: ", androidHttpTransport.requestDump);
            Log.d("dump response: ", androidHttpTransport.responseDump);
            SoapObject response = (SoapObject) envelope.getResponse();
            Log.i("myApp", response.toString());
            System.out.println("response" + response);

            if (response.getProperty("List").toString().length() > 0) {
                taskMembers = RetrieveFromSoap(response);
                result = true;
            }

        } catch (SocketException ex) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception e) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + e.getMessage());
            e.printStackTrace();
        }
        return result;

    }

    @Override
    public void onBackPressed() {
        startPreviousActivity();
    }

    public void startPreviousActivity() {
        this.finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public static Model_TaskMember RetrieveFromSoap(SoapObject soap) {
        int ii = 0;
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Model_TaskMember[] tms = new Model_TaskMember[soap.getPropertyCount()];
        Model_TaskMember taskMembers = new Model_TaskMember();
        String Vle = "";
        Model_TaskListFields[] tfls = new Model_TaskListFields[soap.getPropertyCount() - 11];
        for (int i = 0; i < tms.length; i++) {
            switch (soap.getPropertyInfo(i).getName()) {
                case "DateOfExecutionPlan":
                    try {
                        taskMembers.setDateOfExecutionPlan(date_format.parse(soap.getProperty(i).toString()));
                    } catch (ParseException pr) {
                    }
                    break;
                case "DateOfExecutionFact":
                    try {
                        taskMembers.setDateOfExecutionFact(date_format.parse(soap.getProperty(i).toString()));
                    } catch (ParseException pr) {
                    }
                    break;
                case "DateOfCommencementPlan":
                    try {
                        taskMembers.setDateOfCommencementPlan(date_format.parse(soap.getProperty(i).toString()));
                    } catch (ParseException pr) {
                    }
                    break;
                case "DateOfCommencementFact":
                    try {
                        taskMembers.setDateOfCommencementFact(date_format.parse(soap.getProperty(i).toString()));
                    } catch (ParseException pr) {
                    }
                    break;
                case "InitiatorBP":
                    Vle = soap.getProperty(i).toString();
                    if (Vle.equals("anyType{}")) Vle = "";
                    taskMembers.setInitiatorBP(Vle);
                    break;
                case "mComment":
                    Vle = soap.getProperty(i).toString();
                    if (Vle.equals("anyType{}")) Vle = "";
                    taskMembers.setmComment(Vle);
                    break;
                case "Director":
                    Vle = soap.getProperty(i).toString();
                    if (Vle.equals("anyType{}")) Vle = "";
                    taskMembers.setDirector(Vle);
                    break;
                case "Event":
                    Vle = soap.getProperty(i).toString();
                    if (Vle.equals("anyType{}")) Vle = "";
                    taskMembers.setEvent(Vle);
                    break;
                case "StateTask":
                    Vle = soap.getProperty(i).toString();
                    if (Vle.equals("anyType{}")) Vle = "";
                    taskMembers.setStateTask(Vle);
                    break;
                case "Status":
                    Vle = soap.getProperty(i).toString();
                    if (Vle.equals("anyType{}")) Vle = "";
                    taskMembers.setStatus(Vle);
                    break;
                case "Events":
                    SoapObject l0 = (SoapObject) (soap.getProperty(i));
                    taskMembers.setEvents((l0.getProperty(0).toString()));
                    break;
                case "List":
                    SoapObject l2 = (SoapObject) (soap.getProperty(i));
                    tfls[ii] = new Model_TaskListFields();
                    tfls[ii].setKey(l2.getProperty(0).toString());
                    Vle = l2.getProperty(1).toString();
                    if (Vle.equals("anyType{}")) Vle = "";
                    tfls[ii].setValue(Vle);
                    ii++;
                    break;
            }
        }
        taskMembers.setmTaskListFields(tfls);
        return taskMembers;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Details Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.asiawaters.fieldapprover/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Details Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.asiawaters.fieldapprover/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    private class LoginTask extends AsyncTask<Void, Void, Void> {

        private final ProgressDialog dialog = new ProgressDialog(
                DetailsActivity.this);

        protected void onPreExecute() {

            this.dialog.setMessage("Logging in...");
            this.dialog.show();

        }


        protected Void doInBackground(final Void... unused) {

            boolean auth = doLogin("", "");
            System.out.println(auth);

            return null;// don't interact with the ui!
        }


        protected void onPostExecute(Void result) {


            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                TextView txt = (TextView) findViewById(R.id.DetailedText);
                if (taskMembers != null) {
                    txt.setText(taskMembers.toString());
                } else txt.setText("Empty Object");
                txt.invalidate();
            }
        }

    }


}
