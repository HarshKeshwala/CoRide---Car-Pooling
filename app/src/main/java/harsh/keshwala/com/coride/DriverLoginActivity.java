package harsh.keshwala.com.coride;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class DriverLoginActivity extends AppCompatActivity {

    private TextView driverSignUp;
    private EditText driverEmailEditText;
    private EditText driverPasswordEditText;
    private Button driverLoginButton;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        driverSignUp = (TextView) findViewById(R.id.driverSignUpText);
        driverSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverLoginActivity.this, DriverSignUpActivity.class);
                startActivity(intent);
            }
        });

        driverEmailEditText = (EditText) findViewById(R.id.driverEmailIdLogin);
        driverPasswordEditText = (EditText) findViewById(R.id.driverPasswordLogin);
        driverLoginButton = (Button) findViewById(R.id.driverLoginButton);
        driverLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = driverEmailEditText.getText().toString();
                String password = driverPasswordEditText.getText().toString();

                new LoginPost(email,password).execute();
            }
        });
    }

    //login with Credentials
    public class LoginPost extends AsyncTask<String, Void, String> {

        String email, password;
        public LoginPost(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DriverLoginActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL(Config.URL+"UserClass.php?driverLogin=true&dEmail="+email+"&dPassword="+password);
                Log.d("YYYYYYY",url.toString());
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("dEmail", email);
                postDataParams.put("dPassword", password);
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
                //JSONObject profile = jsonObject.

                JSONObject profile = jsonObject.getJSONObject("user");
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
                Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(),DriverHomeActivity.class));
            }
            else    {
                Toast.makeText(getApplicationContext(),"Incorrect username or password",Toast.LENGTH_LONG).show();
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

