package com.asiawaters.fieldapprover;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {
    private Model_Person mp;
    private TextView selection;
    private Model_ListMembers[] lst;
    private ListView listView;
    private IconicAdapter ia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);
        mp = ((com.asiawaters.fieldapprover.FieldApprover) this.getApplication()).getPerson();
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

        lst = ((com.asiawaters.fieldapprover.FieldApprover) this.getApplication()).getList_values() ;
        new LoginTask().execute();


        selection = (TextView) findViewById(R.id.selection);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ((FieldApprover) getApplication()).setListMembers(lst[position]);
                startNextActivity();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        // SearchView searchView = (SearchView) searchMenuItem.getActionView();

        // searchView.setSearchableInfo(searchManager.
        //        getSearchableInfo(getComponentName()));
        // searchView.setSubmitButtonEnabled(true);
        // searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                ia.getFilter().filter(newText);
//                return true;
//            }
//        });
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
        finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void sortList(int order) {
        Collections.sort(Arrays.asList(lst), new Sorter(order));
        ia.notifyDataSetChanged();
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

    public Dialog openDialog() {
        final Dialog dialog = new Dialog(this); // Context, this, etc.
        dialog.setContentView(R.layout.exit_dialog);
        dialog.setTitle(R.string.dialog_title);
        Button btnok = (Button) dialog.findViewById(R.id.dialog_ok);
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Button btnCancel = (Button) dialog.findViewById(R.id.dialog_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

        return dialog;
    }

    public void startNextActivity() {
        this.finish();
        Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
        startActivity(intent);
    }

    private boolean doLogin(String user_id, String password, String guid) {

        String NAMESPACE = "Mobile";
        String URL = "http://193.193.245.125:3108/ast2/ws/Mobile";

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

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        androidHttpTransport.debug = true;

        ArrayList headerProperty = new ArrayList();
        headerProperty.add(new HeaderProperty("Authorization", "Basic " +
                org.kobjects.base64.Base64.encode(("aw" + ":" + "123").getBytes())));


        try {
            androidHttpTransport.call(SOAP_ACTION, envelope, headerProperty);
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
                listMembers.setActive(Boolean.valueOf(pii.getProperty(5).toString()));

                lms[i] = listMembers;
            }
        }

        return lms;
    }


    private class LoginTask extends AsyncTask<Void, Void, Void> {

        private final ProgressDialog dialog = new ProgressDialog(
                MainActivity.this);

        protected void onPreExecute() {

            this.dialog.setMessage("Logging in...");
            this.dialog.show();

        }


        protected Void doInBackground(final Void... unused) {

            boolean auth = doLogin("", "", mp.getPerson_guid());
            System.out.println(auth);

            return null;// don't interact with the ui!
        }


        protected void onPostExecute(Void result) {


            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                if (lst != null) {
                    ((com.asiawaters.fieldapprover.FieldApprover) getApplication()).setList_values(lst);
                    ia = new IconicAdapter(lst);
                    sortList(1);
                    listView.setAdapter(ia);
                }
            }
        }

    }


    class IconicAdapter extends ArrayAdapter<Model_ListMembers> {

        private int mLastPosition;

        IconicAdapter(Model_ListMembers[] lls) {
            super(MainActivity.this, R.layout.list_row, lls);
        }

        class ViewHolder {
            TextView firstLine;
            TextView secondLine;
            ImageView icon;
        }

        public View getView(int position, View convertView,
                            ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {


                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.list_row, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.firstLine = (TextView) convertView.findViewById(R.id.firstLine);
                viewHolder.secondLine = (TextView) convertView.findViewById(R.id.secondLine);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy' 'HH:mm");
            viewHolder.firstLine.setText(lst[position].getTaskName() + " " + "#" + lst[position].getNumberOfTask());
            viewHolder.secondLine.setText("Date: " + sdf.format(lst[position].getAppointmentDateOfTask()) +
                    "  Upto: " + sdf.format(lst[position].getTargetDatesForTheTask()));
            viewHolder.icon.setImageResource(R.drawable.ic_action_pospone);
            if (lst[position].isActive()) viewHolder.firstLine.setTypeface(null, Typeface.BOLD);
            else viewHolder.firstLine.setTypeface(null, Typeface.NORMAL);
            float initialTranslation = (mLastPosition <= position ? 500f : -500f);

            convertView.setTranslationY(initialTranslation);
            convertView.animate()
                    .setInterpolator(new DecelerateInterpolator(1.0f))
                    .translationY(0f)
                    .setDuration(100l)
                    .setListener(null);
            // Keep track of the last position we loaded
            mLastPosition = position;


            return (convertView);
        }


    }
}
