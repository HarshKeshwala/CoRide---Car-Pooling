package harsh.keshwala.com.coride;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class DriverAccountProfileActivity extends AppCompatActivity {

    private EditText firstName, lastName, email, phone, dob, licenseNumber;
    private SharedPreferences sharedPreferences;
    private Button cancel, save;

    private ProgressDialog pDialog;
    private String dId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_account_profile);

        firstName = (EditText) findViewById(R.id.driverFirstNameProfile);
        lastName = (EditText) findViewById(R.id.driverLastNameProfile);
        email = (EditText) findViewById(R.id.driverEmailProfile);
        phone = (EditText) findViewById(R.id.driverPhoneProfile);
        dob = (EditText) findViewById(R.id.driverDobProfile);
        licenseNumber = (EditText) findViewById(R.id.driverLicenseProfile);

        sharedPreferences = getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE);

        firstName.setText(sharedPreferences.getString("dFirstName",""));
        lastName.setText(sharedPreferences.getString("dLastName",""));
        email.setText(sharedPreferences.getString("dEmail",""));
        phone.setText(sharedPreferences.getString("dPhone",""));
        dob.setText(sharedPreferences.getString("dDob",""));
        licenseNumber.setText(sharedPreferences.getString("dLicenseNumber",""));

        dId = sharedPreferences.getString("dId","");

        cancel = (Button) findViewById(R.id.driverProfileUpdateCancel);
        save = (Button) findViewById(R.id.driverProfileUpdate);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DriverAccountProfileActivity.this,DriverHomeActivity.class));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DriverUpdate(dId, firstName.getText().toString(),
                        lastName.getText().toString(),
                        email.getText().toString(),
                        phone.getText().toString(),
                        dob.getText().toString(),
                        licenseNumber.getText().toString()
                ).execute();
            }
        });
    }

    //login with Credentials
    public class DriverUpdate extends AsyncTask<String, Void, String> {


        String dId,firstName, lastName, email, password, confirmPassword, phone, dob, licenseNumber;
        public DriverUpdate(String dId, String firstName, String lastName, String email,
                            String phone,String dob, String licenseNumber)   {
            this.dId = dId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
            this.confirmPassword = confirmPassword;
            this.phone = phone;
            this.dob = dob;
            this.licenseNumber = licenseNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DriverAccountProfileActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {

            try {

                String data = "dId="+dId+"&dFirstName="+firstName + "&dLastName=" + lastName + "&dEmail="+ email + "&dPhone=" + phone + "&dDob=" + dob + "&dLicenseNumber="+ licenseNumber;

                URL url = new URL(Config.URL+"UserClass.php?updateDriver=true&"+data);
                Log.d("YYYYYYY",url.toString());
                JSONObject postDataParams = new JSONObject();
                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(30000 /* milliseconds */);
                conn.setConnectTimeout(30000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        Log.e("+++++", "line: "+line);
                        sb.append(line);
                        //break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e) {
                Log.e("~~~", e.toString());
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("TAG-------",result);
            String status = null;
            String message = null;

            String id = null;
            String firstName = null;
            String lastName = null;
            String email = null;
            String phone = null;
            String dob = null;
            String licenseNumber = null;

            try {
                JSONObject jsonObject = new JSONObject(result);
                status = jsonObject.getString("status");
                message = jsonObject.getString("message");

                //JSONObject profile = jsonObject.

                JSONObject profile = jsonObject.getJSONObject("profile");
                Log.d("LOGIN_RESULT",profile.toString());

                id = profile.getString("dId");
                firstName = profile.getString("dFirstName");
                lastName = profile.getString("dLastName");
                email = profile.getString("dEmail");
                phone = profile.getString("dPhone");
                dob = profile.getString("dDob");
                licenseNumber = profile.getString("dLicenseNumber");

                SharedPreferences.Editor editor = getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE).edit();
                editor.putString("dId",id);
                editor.putString("dFirstName",firstName);
                editor.putString("dLastName",lastName);
                editor.putString("dEmail",email);
                editor.putString("dPhone",phone);
                editor.putString("dDob",dob);
                editor.putString("dLicenseNumber",licenseNumber);
                editor.apply();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();

            if(status.equals("Ok") == true) {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(),DriverHomeActivity.class));
            }
            else    {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while(itr.hasNext()){

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            Log.d("TAG",result.toString());
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }

}
