package com.asiawaters.fieldapprover;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.asiawaters.fieldapprover.classes.KeyValueTableDataAdapter;
import com.asiawaters.fieldapprover.classes.Model_ListMembers;
import com.asiawaters.fieldapprover.classes.Model_TaskListFields;
import com.asiawaters.fieldapprover.classes.Model_TaskMember;
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
import java.util.Arrays;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class DetailsActivity extends AppCompatActivity {
    Model_ListMembers mlm;
    Model_TaskMember taskMembers;
    TableView tableView;
    private static final String[] DATA_TO_SHOW = {"Название поля", "Значение поля"};

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

        tableView = (TableView) findViewById(R.id.tableView);
        tableView.setColumnCount(2);
        tableView.setColumnWeight(1, 2);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(getBaseContext(), DATA_TO_SHOW));


        // Capture our button from layout
        Button ok_btn = (Button) findViewById(R.id.ok_btn);
        Button cancel_btn = (Button) findViewById(R.id.cancel_btn);
        Button cancel_coment_btn = (Button) findViewById(R.id.cancel_coment_btn);
        // Register the onClick listener with the implementation above
        ok_btn.setOnClickListener(mStatListener);
        cancel_btn.setOnClickListener(mStatListener);
        cancel_coment_btn.setOnClickListener(mStatListener);


        mlm = ((FieldApprover) this.getApplication()).getListMember();
        new LoginTask().execute();
    }

    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener mStatListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ok_btn:


                    break;
                case R.id.cancel_btn:



                    break;
                case R.id.cancel_coment_btn:



                    break;
                default:
                    break;
            }

        }
    };

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
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
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
                if (taskMembers != null) {
                    if (taskMembers.getmTaskListFields() != null) {
                        fillCommoninfo();
                        tableView.setDataAdapter(new KeyValueTableDataAdapter(getBaseContext(), Arrays.asList(taskMembers.getmTaskListFields())));
                    }
                }
            }
        }

    }

    public void fillCommoninfo() {

        TextView tv;
        SimpleDateFormat date_format = new SimpleDateFormat("dd-MM-yyyy' T 'HH:mm:ss");

        tv = (TextView) findViewById(R.id.TxtName1);
        tv.setText("Директор");

        tv = (TextView) findViewById(R.id.TxtVle1);
        tv.setText(taskMembers.getDirector());

        tv = (TextView) findViewById(R.id.TxtName2);
        tv.setText("Инициатор");

        tv = (TextView) findViewById(R.id.TxtVle2);
        tv.setText(taskMembers.getInitiatorBP());

        tv = (TextView) findViewById(R.id.TxtName3);
        tv.setText("Событие");

        tv = (TextView) findViewById(R.id.TxtVle3);
        tv.setText(taskMembers.getEvent());

        tv = (TextView) findViewById(R.id.TxtName4);
        tv.setText("События");

        tv = (TextView) findViewById(R.id.TxtVle4);
        tv.setText(taskMembers.getEvents());

        tv = (TextView) findViewById(R.id.TxtName5);
        tv.setText("Дата план");

        tv = (TextView) findViewById(R.id.TxtVle5);
        tv.setText(date_format.format(taskMembers.getDateOfExecutionPlan()));

        tv = (TextView) findViewById(R.id.TxtName6);
        tv.setText("Дата факт");

        tv = (TextView) findViewById(R.id.TxtVle6);
        tv.setText(date_format.format(taskMembers.getDateOfExecutionFact()));

        tv = (TextView) findViewById(R.id.TxtName7);
        tv.setText("Дата к выполнению план");

        tv = (TextView) findViewById(R.id.TxtVle7);
        tv.setText(date_format.format(taskMembers.getDateOfCommencementFact()));

        tv = (TextView) findViewById(R.id.TxtName8);
        tv.setText("Дата к выполнению факт");

        tv = (TextView) findViewById(R.id.TxtVle8);
        tv.setText(date_format.format(taskMembers.getDateOfCommencementFact()));

        tv = (TextView) findViewById(R.id.TxtName9);
        tv.setText("Комментарий");

        tv = (TextView) findViewById(R.id.TxtVle9);
        tv.setText(taskMembers.getmComment());

        tv = (TextView) findViewById(R.id.TxtName10);
        tv.setText("Cтатус задачи");

        tv = (TextView) findViewById(R.id.TxtVle10);
        tv.setText(taskMembers.getStatus());

        tv = (TextView) findViewById(R.id.TxtName11);
        tv.setText("Cостояние задачи");

        tv = (TextView) findViewById(R.id.TxtVle11);
        tv.setText(taskMembers.getStateTask());

    }


}
