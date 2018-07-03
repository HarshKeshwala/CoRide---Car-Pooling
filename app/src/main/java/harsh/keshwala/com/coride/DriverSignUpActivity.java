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

import org.json.JSONArray;
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

public class DriverSignUpActivity extends AppCompatActivity {

    private EditText dFirstName;
    private EditText dLastName;
    private EditText dEmail;
    private EditText dPassword;
    private EditText dConfirmPassword;
    private EditText dPhone;
    private EditText dDob;
    private EditText dLicenseNumber;
    private Button signUpButton;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_sign_up);

        dFirstName = (EditText) findViewById(R.id.driverFirstNameSignUp);
        dLastName = (EditText) findViewById(R.id.driverLastNameSignUp);
        dEmail = (EditText) findViewById(R.id.driverEmailSignUp);
        dPassword = (EditText) findViewById(R.id.driverPasswordSignUp);
        dConfirmPassword = (EditText) findViewById(R.id.driverConfirmPasswordSignUp);
        dPhone = (EditText) findViewById(R.id.driverPhoneSignUp);
        dDob = (EditText) findViewById(R.id.driverDobSignUp);
        dLicenseNumber = (EditText) findViewById(R.id.driverLicenseSignUp);

        signUpButton = (Button) findViewById(R.id.driverSignUpButton);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String firstName, lastName, email, password, confirmPassword, phone, dob, licenseNumber;

                firstName = dFirstName.getText().toString();
                lastName = dLastName.getText().toString();
                email = dEmail.getText().toString();
                password = dPassword.getText().toString();
                confirmPassword = dConfirmPassword.getText().toString();
                phone = dPhone.getText().toString();
                dob = dDob.getText().toString();
                licenseNumber = dLicenseNumber.getText().toString();

                new DriverSignup(firstName, lastName, email, password, confirmPassword, phone, dob, licenseNumber).execute();
            }
        });
    }

    //login with Credentials
    public class DriverSignup extends AsyncTask<String, Void, String> {


        String firstName, lastName, email, password, confirmPassword, phone, dob, licenseNumber;
        public DriverSignup(String firstName, String lastName, String email,
                            String password, String confirmPassword, String phone,
                            String dob, String licenseNumber)   {
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
            pDialog = new ProgressDialog(DriverSignUpActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {

            try {

                String data = "dFirstName="+firstName + "&dLastName=" + lastName + "&dEmail="+ email + "&dPassword=" + password
                        +"&dConfirmPassword=" + confirmPassword + "&dPhone=" + phone + "&dDob=" + dob + "&dLicenseNumber="+ licenseNumber;

                URL url = new URL(Config.URL+"UserClass.php?driverSignUp=true&"+data);
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
            String password = null;
            String phone = null;
            String dob = null;
            String licenseNumber = null;
            String ratings = null;

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
                password = profile.getString("dPassword");
                phone = profile.getString("dPhone");
                dob = profile.getString("dDob");
                licenseNumber = profile.getString("dLicenseNumber");
                ratings = profile.getString("dRatings");

                SharedPreferences.Editor editor = getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE).edit();
                editor.putString("dId",id);
                editor.putString("dFirstName",firstName);
                editor.putString("dLastName",lastName);
                editor.putString("dEmail",email);
                editor.putString("dPassword",password);
                editor.putString("dPhone",phone);
                editor.putString("dDob",dob);
                editor.putString("dLicenseNumber",licenseNumber);
                editor.putString("dRatings",ratings);
                editor.apply();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();

            if(status.equals("Ok") == true) {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
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
