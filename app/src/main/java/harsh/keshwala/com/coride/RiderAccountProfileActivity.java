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

public class RiderAccountProfileActivity extends AppCompatActivity {

    private EditText firstName, lastName, email, phone, dob;
    private SharedPreferences sharedPreferences;
    private Button cancel, save;

    private ProgressDialog pDialog;
    private String rId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_account_profile);

        firstName = (EditText) findViewById(R.id.riderFirstNameProfile);
        lastName = (EditText) findViewById(R.id.riderLastNameProfile);
        email = (EditText) findViewById(R.id.riderEmailProfile);
        phone = (EditText) findViewById(R.id.riderPhoneProfile);
        dob = (EditText) findViewById(R.id.riderDobProfile);

        sharedPreferences = getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE);

        firstName.setText(sharedPreferences.getString("rFirstName", ""));
        lastName.setText(sharedPreferences.getString("rLastName", ""));
        email.setText(sharedPreferences.getString("rEmail", ""));
        phone.setText(sharedPreferences.getString("rPhone", ""));
        dob.setText(sharedPreferences.getString("rDob", ""));

        rId = sharedPreferences.getString("rId", "");

        cancel = (Button) findViewById(R.id.riderProfileUpdateCancel);
        save = (Button) findViewById(R.id.riderProfileUpdate);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RiderAccountProfileActivity.this, DriverHomeActivity.class));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RiderUpdate(rId, firstName.getText().toString(),
                        lastName.getText().toString(),
                        email.getText().toString(),
                        phone.getText().toString(),
                        dob.getText().toString()
                ).execute();
            }
        });
    }

    //login with Credentials
    public class RiderUpdate extends AsyncTask<String, Void, String> {


        String rId,firstName, lastName, email, password, confirmPassword, phone, dob;
        public RiderUpdate(String rId, String firstName, String lastName, String email,
                           String phone,String dob)   {
            this.rId = rId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.phone = phone;
            this.dob = dob;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RiderAccountProfileActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {

            try {

                String data = "rId="+rId+"&rFirstName="+firstName + "&rLastName=" + lastName + "&rEmail="+ email + "&rPhone=" + phone + "&rDob=" + dob;

                URL url = new URL(Config.URL+"UserClass.php?updateRider=true&"+data);
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

            try {
                JSONObject jsonObject = new JSONObject(result);
                status = jsonObject.getString("status");
                message = jsonObject.getString("message");

                //JSONObject profile = jsonObject.

                JSONObject profile = jsonObject.getJSONObject("profile");
                Log.d("LOGIN_RESULT",profile.toString());

                id = profile.getString("rId");
                firstName = profile.getString("rFirstName");
                lastName = profile.getString("rLastName");
                email = profile.getString("rEmail");
                phone = profile.getString("rPhone");
                dob = profile.getString("rDob");

                SharedPreferences.Editor editor = getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE).edit();
                editor.putString("rId",id);
                editor.putString("rFirstName",firstName);
                editor.putString("rLastName",lastName);
                editor.putString("rEmail",email);
                editor.putString("rPhone",phone);
                editor.putString("rDob",dob);
                editor.apply();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();

            if(status.equals("Ok") == true) {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(),RiderHomeActivity.class));
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
