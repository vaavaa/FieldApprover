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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.asiawaters.fieldapprover.classes.FAExpandableListAdapter;
import com.asiawaters.fieldapprover.classes.Model_ListMembers;
import com.asiawaters.fieldapprover.classes.Model_Person;
import com.asiawaters.fieldapprover.classes.PinnedHeaderExpListView;

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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Model_Person mp;
    private Model_ListMembers[] lst;
    private FAExpandableListAdapter listAdapter;
    private PinnedHeaderExpListView expListView;
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
        expListView = (PinnedHeaderExpListView) findViewById(R.id.lvExp);

//        expListView.setGroupIndicator(null);
//        View h = LayoutInflater.from(this).inflate(R.layout.list_group, (ViewGroup) findViewById(R.id.root), false);
//        expListView.setPinnedHeaderView(h);
//        expListView.setDividerHeight(0);


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
                GetNextStep(groupPosition, childPosition);
                return false;
            }
        });
        if (lst == null) {
            AT = new LoginTask().execute();
        } else {
            if (FA.getUpdateList()) {
                AT = new LoginTask().execute();
            } else {
                FA.setUpdateList(false);
                runUpdateView();
                if (FA.getIdGroup() != -1) {
                    expListView.expandGroup(FA.getIdGroup(), true);
                    if (FA.getIdPosition() != -1)
                        expListView.setSelectedChild(FA.getIdGroup(), FA.getIdPosition(), true);
                }
            }
        }
    }

    public void searchVoid(View v) {
        //Hide Keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        if (listAdapter != null) {
            EditText set = (EditText) findViewById(R.id.search);
            if (set.getText().toString().length() > 0)
                listAdapter.getFilter().filter(set.getText().toString());
            else listAdapter.getFilter().filter("");
        }
    }

    public void allShow(View v) {
        v.setSelected(true);
        findViewById(R.id.IV3).setSelected(false);
        if (lst != null) {
            prepareListData();
            listAdapter = new FAExpandableListAdapter(getBaseContext(), listDataHeader, listDataChild);
            // setting list adapter
            expListView.setAdapter(listAdapter);
            expListView.setOnScrollListener((AbsListView.OnScrollListener) listAdapter);
        }

    }

    public void actionNeed(View v) {
        v.setSelected(true);
        findViewById(R.id.IV2).setSelected(false);
        if (lst != null) {
            prepareListData_();
            listAdapter = new FAExpandableListAdapter(getBaseContext(), listDataHeader, listDataChild);
            // setting list adapter
            expListView.setAdapter(listAdapter);
            expListView.setOnScrollListener((AbsListView.OnScrollListener) listAdapter);
        }

    }


    public HashMap<String, List<String>> sortMap(HashMap<String, List<String>> unsortMap, int order) {
        List<HashMap.Entry<String, List<String>>> list = new LinkedList<HashMap.Entry<String, List<String>>>(unsortMap.entrySet());
        final int f_order = order;
        // put sorted list into map again
        HashMap<String, List<String>> sortedMap = new LinkedHashMap<String, List<String>>();
        for (Iterator<Map.Entry<String, List<String>>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, List<String>> entry = it.next();

            List<String> lstr = entry.getValue();
            Collections.sort(lstr, new Comparator<String>() {
                @SuppressWarnings("unchecked")
                public int compare(String s1, String s2) {
                    if (f_order > 0) return s1.compareTo(s2);
                    else return s2.compareTo(s1);
                }
            });
            entry.setValue(lstr);
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }


    public void prepareListData() {
        if (lst == null) return;
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        for (int i = 0; i < lst.length; i++) {
            if (i == 0)
                listDataHeader.add(lst[i].getTemplate());
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
                    NestedList[i].add(lst[ii].getGuidTask());
            }
            listDataChild.put(listDataHeader.get(i), NestedList[i]);
        }

    }

    public void prepareListData_() {
        if (lst == null) return;
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        for (int i = 0; i < lst.length; i++) {
            if (lst[i].isActive()) {
                if (!listDataHeader.contains(lst[i].getTemplate()))
                    listDataHeader.add(lst[i].getTemplate());
            }
        }

        List<String>[] NestedList = new ArrayList[listDataHeader.size()];

        for (int i = 0; i < listDataHeader.size(); i++) {
            NestedList[i] = new ArrayList<String>();
            for (int ii = 0; ii < lst.length; ii++) {
                if (lst[ii].isActive()) {
                    if (lst[ii].getTemplate().equals(listDataHeader.get(i)))
                        NestedList[i].add(lst[ii].getGuidTask());
                }
                listDataChild.put(listDataHeader.get(i), NestedList[i]);
            }
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
        actionNeed(findViewById(R.id.IV3));
    }

    public void GetNextStep(int groupPosition, int position) {

        String GUIDObject = listDataChild.get(listDataHeader.get(groupPosition)).get(position);
        FA.setListMembers(findMember(GUIDObject));
        startNextActivity();
    }

    private Model_ListMembers findMember(String codeIsIn) {
        for (Model_ListMembers Model : lst) {
            if (Model.getGuidTask().equals(codeIsIn)) {
                return Model;
            }
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (listAdapter != null) {
            listDataChild = sortMap(listDataChild, order);
            listAdapter.notifyDataSetChanged();
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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        request.addProperty("BeginDate", sdf.format(FA.getDateTo()));
        request.addProperty("EndDate", sdf.format(FA.getDateFrom()));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.implicitTypes = true;
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);
        System.out.println(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);
        androidHttpTransport.debug = true;


        ArrayList headerProperty = new ArrayList();
        headerProperty.add(new HeaderProperty("Authorization", "Basic " +
                org.kobjects.base64.Base64.encode((FA.getUser() + ":" + FA.getPassword()).getBytes())));


        try {
            androidHttpTransport.call(SOAP_ACTION, envelope, headerProperty);
            Log.d("dump Request: ", androidHttpTransport.requestDump);
            Log.d("dump response: ", androidHttpTransport.responseDump);
            SoapObject response = (SoapObject) envelope.getResponse();
            Log.i("myApp", response.toString());
            System.out.println("response" + response);
            if (response.getPropertyCount() > 0) {
                if (response.getProperty("List").toString().length() > 0) {
                    Model_ListMembers[] lms = RetrieveFromSoap(response);
                    if (lms != null) {
                        lst = lms;
                        result = true;
                    } else return false;

                } else return false;
            } else result = true;

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
            this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
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
                FA.setUpdateList(false);
                runUpdateView();
                sortList(-1);
            }
        }

    }
}
