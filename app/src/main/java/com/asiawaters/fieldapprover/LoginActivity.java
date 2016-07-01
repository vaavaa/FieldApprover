package com.asiawaters.fieldapprover;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asiawaters.fieldapprover.classes.DBController;
import com.asiawaters.fieldapprover.classes.Model_NetState;
import com.asiawaters.fieldapprover.classes.Model_Person;
import com.asiawaters.fieldapprover.classes.NetListener;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault12;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.SocketException;


public class LoginActivity extends Activity {

    private Model_Person person = null;

    // UI references.
    private EditText mPasswordView;
    private Button mEmailSignInButton;
    private Model_NetState model_netState;
    private NetListener mnetListener;
    private String WDSLPath;

    private ExpandableRelativeLayout mExpandLayout;

    // Database Helper
    private DBController db;
    private com.asiawaters.fieldapprover.FieldApprover FA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FA = ((com.asiawaters.fieldapprover.FieldApprover) getApplication());

        db = new DBController(this);
        db.openDB();
        FA.setDb(db);

        // Set up the login form.
        WDSLPath = FA.getPath_url();
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    new LoginTask().execute();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((((TextView) findViewById(R.id.email)).getText() != null)) {
                    if (isLoginValid(((TextView) findViewById(R.id.email)).getText().toString())) {
                        if ((((TextView) findViewById(R.id.password)).getText() != null)) {
                            if (isPasswordValid(((TextView) findViewById(R.id.password)).getText().toString())) {
                                new LoginTask().execute();
                            } else
                                Toast.makeText(LoginActivity.this, R.string.password, Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(LoginActivity.this, R.string.password, Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(LoginActivity.this, R.string.login, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(LoginActivity.this, R.string.login, Toast.LENGTH_SHORT).show();
            }
        });

        String[] credentials = db.getCredentials();

        if (credentials[0] != null) {
            if (credentials[0].length() > 0) {
                ((TextView) findViewById(R.id.email)).setText(credentials[0].toString());
                ((TextView) findViewById(R.id.password)).setText(credentials[1].toString());
            }
        }

        mExpandLayout
                = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout);

        Button mSettingsButton = (Button) findViewById(R.id.showSettings);
        mSettingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hide Keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if (!mExpandLayout.isExpanded()) mExpandLayout.expand();
                else mExpandLayout.collapse();
            }
        });

        model_netState = FA.getModel_netState();
        mnetListener = FA.getMnetListener();
        RunStatListener();
    }


    public void startNextActivity() {
        this.finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void CheckState() {
        TextView textView = (TextView) findViewById(R.id.HelpText);

        if (mnetListener.get_isOnline()) {
            mEmailSignInButton.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.welcome_box_yellow));
            if (mnetListener.get_isConnected()) {
                mEmailSignInButton.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.welcome_box_green));
                textView.setText("");
            } else {
                textView.setText(R.string.Internet_is_not_available);
            }
        } else {
            mEmailSignInButton.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.welcome_box_red));
            textView.setText(R.string.Connection_is_switched_off);
        }
    }


    private boolean isLoginValid(String email) {
        return email.length() > 0;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }


    public void RunStatListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new CheckTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new CheckTask().execute();
        }
    }

    private String doLogin() {
        String NAMESPACE = "Mobile";
        int timeout = FA.getTimeOut();

        String result = "false";
        final String SOAP_ACTION = "Mobile";
        final String METHOD_NAME = "Autorization";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("Login", ((TextView) findViewById(R.id.email)).getText().toString());
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.implicitTypes = true;
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);
        System.out.println(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(WDSLPath, timeout);
        androidHttpTransport.debug = true;

//        ArrayList headerProperty = new ArrayList();
//        headerProperty.add(new HeaderProperty("Authorization", "Basic " +
//                org.kobjects.base64.Base64.encode(("aw" + ":" + "123").getBytes())));


        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);//, headerProperty);
            Log.d("dump Request: ", androidHttpTransport.requestDump);
            Log.d("dump response: ", androidHttpTransport.responseDump);
            SoapObject response = (SoapObject) envelope.getResponse();
            Log.i("myApp", response.toString());
            System.out.println("response" + response);

            if (response.getProperty("GUIDUser").toString().length() > 0) {
                //Заполнили данные о человеке
                Model_Person person = new Model_Person();
                person.setPerson_guid(response.getProperty("GUIDUser").toString());
                person.setPerson_name(response.getProperty("Name").toString());
                person.setPerson_surname(response.getProperty("Surname").toString());
                person.setPerson_midname(response.getProperty("MiddleName").toString());
                person.setPerson_organisation(response.getProperty("Organization").toString());
                person.setEmail(response.getProperty("email").toString());
                person.setPerson_position(response.getProperty("Position").toString());
                FA.setPerson(person);
                result = "true";
            }

        } catch (SocketException ex) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + ex.getMessage());
            ex.printStackTrace();
            result = ex.getMessage();
        } catch (Exception e) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + e.getMessage());
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;

    }

    private class LoginTask extends AsyncTask<Void, Void, String> {

        private final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);

        protected void onPreExecute() {
            this.dialog.setMessage(getBaseContext().getResources().getString(R.string.LoggingIn));
            this.dialog.show();
        }

        protected String doInBackground(final Void... unused) {

            String auth = doLogin();
            return auth;// doesn't interact with the ui!
        }

        protected void onPostExecute(String result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (result != null) {
                if (result.equals("true")) {
                    if (((CheckBox) findViewById(R.id.chSave)).isChecked())
                        db.setCredentials(((TextView) findViewById(R.id.email)).getText().toString(),
                                ((TextView) findViewById(R.id.password)).getText().toString());
                    else db.setCredentials("", "");

                    startNextActivity();
                } else {
                    if (result.indexOf("Пользователь не найден") > 0)
                        Toast.makeText(getBaseContext(), R.string.UserNotFind, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getBaseContext(), R.string.ServerIsNotResponding, Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

    private class CheckTask extends AsyncTask<Void, Void, Boolean> {

        protected void onPreExecute() {
        }

        protected void onProgressUpdate(Void... progress) {
            CheckState();
        }

        protected Boolean doInBackground(final Void... unused) {
            do {
                try {
                    Thread.sleep(2000);
                    publishProgress();
                } catch (InterruptedException Ex) {
                }
            } while (!this.isCancelled());
            return true;
        }

        protected void onPostExecute() {
        }

    }
}



