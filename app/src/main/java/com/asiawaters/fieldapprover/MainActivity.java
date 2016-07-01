package com.asiawaters.fieldapprover;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.asiawaters.fieldapprover.classes.FAExpandableListAdapter;
import com.asiawaters.fieldapprover.classes.Model_ListMembers;
import com.asiawaters.fieldapprover.classes.Model_Person;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Model_Person mp;
    private Model_ListMembers[] lst;
    private FAExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private FieldApprover FA;
    private AsyncTask AT;

    private String WDSLPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FA = ((com.asiawaters.fieldapprover.FieldApprover) getApplication());

        //prepare tool bar
        mp = FA.getPerson();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeApp();
            }
        });

        WDSLPath = FA.getPath_url();
        lst = FA.getList_values();


        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {


            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                FA.setIdGroup(groupPosition);
                FA.setIdPosition(childPosition);
                GetNextStep(childPosition);
                return false;
            }
        });
        if (lst == null) {
            AT =   new LoginTask().execute();
        } else {
            runUpdateView();
            if (FA.getIdGroup() != -1) {
                expListView.expandGroup(FA.getIdGroup(), true);
                if (FA.getIdPosition() != -1)
                    expListView.setSelectedChild(FA.getIdGroup(), FA.getIdPosition(), true);
            }
        }
    }

    public void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        for (int i = 0; i < lst.length; i++) {
            if (i == 0) listDataHeader.add(lst[i].getTemplate());
            else {
                if (!listDataHeader.contains(lst[i].getTemplate()))
                    listDataHeader.add(lst[i].getTemplate());
            }
        }
        List<String>[] NestedList = new ArrayList[listDataHeader.size()];

        for (int i = 0; i < listDataHeader.size(); i++) {
            NestedList[i] = new ArrayList<String>();
            for (int ii = 0; ii < lst.length; ii++) {
                if (lst[ii].getTemplate().equals(listDataHeader.get(i)))
                    NestedList[i].add(lst[ii].getTaskName());
            }
            listDataChild.put(listDataHeader.get(i), NestedList[i]);
        }

    }

    public void startLogingActivity() {

        this.finish();
        Intent intent;
        intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void runUpdateView() {
        FA.setList_values(lst);
        // preparing list data
        prepareListData();
        listAdapter = new FAExpandableListAdapter(getBaseContext(), listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    public void GetNextStep(int position) {
        FA.setListMembers(lst[position]);
        startNextActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_desc) {
            sortList(1);
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_asc) {
            sortList(-1);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void closeApp() {
        if (lst != null) {
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else startLogingActivity();
    }

    public void sortList(int order) {
        Collections.sort(Arrays.asList(lst), new Sorter(order));
    }

    static class Sorter implements Comparator<Model_ListMembers> {
        int order = -1;

        Sorter(int order) {
            this.order = order;
        }

        public int compare(Model_ListMembers ob1, Model_ListMembers ob2) {
            int result = 0;
            if (order > 0) result = Boolean.compare(ob2.isActive(), ob1.isActive());
            else result = Boolean.compare(ob1.isActive(), ob2.isActive());
            return result;
        }

    }


    @Override
    public void onBackPressed() {
        closeApp();
    }

    public void startNextActivity() {
        this.finish();
        Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
        startActivity(intent);
    }

    private boolean doLogin(String user_id, String password, String guid) {

        String NAMESPACE = "Mobile";
        String URL = WDSLPath;
        int timeout = FA.getTimeOut();

        boolean result = false;
        final String SOAP_ACTION = "Mobile/MobilePortType/GetStatusRequest";
        final String METHOD_NAME = "PoluchitSpisokZadachZaPeriod";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("GuidUser", guid);
        request.addProperty("BeginDate", "2016-01-01");
        request.addProperty("EndDate", "2016-09-09");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.implicitTypes = true;
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);
        System.out.println(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);
        androidHttpTransport.debug = true;


//        ArrayList headerProperty = new ArrayList();
//        headerProperty.add(new HeaderProperty("Authorization", "Basic " +
//                org.kobjects.base64.Base64.encode(("aw" + ":" + "123").getBytes())));


        try {
            androidHttpTransport.call(SOAP_ACTION, envelope); //, headerProperty);
            Log.d("dump Request: ", androidHttpTransport.requestDump);
            Log.d("dump response: ", androidHttpTransport.responseDump);
            SoapObject response = (SoapObject) envelope.getResponse();
            Log.i("myApp", response.toString());
            System.out.println("response" + response);

            if (response.getProperty("List").toString().length() > 0) {
                Model_ListMembers[] lms = RetrieveFromSoap(response);
                if (lms != null) {
                    lst = lms;
                    result = true;
                } else return false;

            } else return false;

        } catch (SocketException ex) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception e) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + e.getMessage());
            e.printStackTrace();
        }
        return result;

    }

    public static Model_ListMembers[] RetrieveFromSoap(SoapObject soap) {
        Model_ListMembers[] lms = null;
        if (soap.getPropertyCount() > 0) {
            lms = new Model_ListMembers[soap.getPropertyCount()];
            for (int i = 0; i < lms.length; i++) {
                SoapObject pii = (SoapObject) soap.getProperty(i);
                Model_ListMembers listMembers = new Model_ListMembers();
                listMembers.setNumberOfTask(pii.getProperty(0).toString());

                SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                try {
                    listMembers.setAppointmentDateOfTask(date_format.parse(pii.getProperty(1).toString()));
                } catch (ParseException pr) {
                }

                listMembers.setTaskName(pii.getProperty(2).toString());
                try {
                    listMembers.setTargetDatesForTheTask(date_format.parse(pii.getProperty(3).toString()));
                } catch (ParseException pr) {
                }

                listMembers.setGuidTask(pii.getProperty(4).toString());
                listMembers.setActive(!Boolean.valueOf(pii.getProperty(5).toString()));
                listMembers.setTemplate(pii.getProperty("Template").toString());
                listMembers.setActiveBP(Boolean.valueOf(pii.getProperty("ActiveBP").toString()));
                lms[i] = listMembers;
            }
        }

        return lms;
    }

    private class LoginTask extends AsyncTask<Void, Void, Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(
                MainActivity.this);


        protected void onPreExecute() {
            this.dialog.setMessage(getBaseContext().getResources().getString(R.string.LoggingIn));
            this.dialog.show();
            this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
                @Override
                public void onCancel(DialogInterface dialog) {
                    AT.cancel(true);
                    closeApp();
                }
            });


        }


        protected Boolean doInBackground(final Void... unused) {

            boolean auth = doLogin("", "", mp.getPerson_guid());
            System.out.println(auth);

            return auth;// don't interact with the ui!
        }


        protected void onPostExecute(Boolean result) {


            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (!result) {
                Toast.makeText(getBaseContext(), R.string.timeout, Toast.LENGTH_SHORT).show();
                startLogingActivity();
            } else if (lst != null) {
                runUpdateView();
            }


        }

    }
}
