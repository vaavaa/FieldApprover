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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asiawaters.fieldapprover.classes.Model_NetState;
import com.asiawaters.fieldapprover.classes.Model_Person;
import com.asiawaters.fieldapprover.classes.NetListener;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.SocketException;
import java.util.ArrayList;


public class LoginActivity extends Activity {

    private Model_Person person = null;

    // UI references.
    private EditText mPasswordView;
    private Button mEmailSignInButton;
    private Model_NetState model_netState;
    private NetListener mnetListener;
    private String WDSLPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        WDSLPath = ((com.asiawaters.fieldapprover.FieldApprover) this.getApplication()).getPath_url();
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
                            } else Toast.makeText(LoginActivity.this, R.string.password, Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(LoginActivity.this, R.string.password, Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(LoginActivity.this, R.string.login, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(LoginActivity.this, R.string.login, Toast.LENGTH_SHORT).show();
            }
        });

        model_netState = ((FieldApprover) getApplication()).getModel_netState();
        mnetListener = ((FieldApprover) getApplication()).getMnetListener();
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

    private boolean doLogin() {
        String NAMESPACE = "Mobile";

        boolean result = false;
        final String SOAP_ACTION = "Mobile";
        final String METHOD_NAME = "Autorization";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("Login", "Саликов А.К.");
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.implicitTypes = true;
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);
        System.out.println(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(WDSLPath);
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
                ((FieldApprover) this.getApplication()).setPerson(person);
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

    private class LoginTask extends AsyncTask<Void, Void, Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);

        protected void onPreExecute() {
            this.dialog.setMessage(getBaseContext().getResources().getString(R.string.LoggingIn));
            this.dialog.show();
        }

        protected Boolean doInBackground(final Void... unused) {

            boolean auth = doLogin();
            return auth;// doesn't interact with the ui!
        }

        protected void onPostExecute(Boolean result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (result) startNextActivity();
            else {
                Toast.makeText(getBaseContext(), R.string.ServerIsNotResponding, Toast.LENGTH_SHORT).show();
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



