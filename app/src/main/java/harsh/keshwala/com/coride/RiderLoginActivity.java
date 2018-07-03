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
import android.widget.TextView;
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

public class RiderLoginActivity extends AppCompatActivity {

    private TextView riderSignUp;
    private EditText riderEmail;
    private EditText riderPassword;
    private Button riderLogin;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_login);

        riderSignUp = (TextView) findViewById(R.id.riderSignUpText);
        riderSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RiderLoginActivity.this, RiderSignUpActivity.class);
                startActivity(intent);
            }
        });

        riderEmail = (EditText) findViewById(R.id.riderEmailIdLogin);
        riderPassword = (EditText) findViewById(R.id.riderPasswordLogin);

        riderLogin = (Button) findViewById(R.id.riderLoginButton);

        riderLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = riderEmail.getText().toString();
                String password = riderPassword.getText().toString();

                new RiderLogin(email,password).execute();
            }
        });
    }

    //login with Credentials
    public class RiderLogin extends AsyncTask<String, Void, String> {

        String email, password;
        public RiderLogin(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RiderLoginActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL(Config.URL+"UserClass.php?riderLogin=true&rEmail="+email+"&rPassword="+password);
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
            String ratings = null;

            try {
                JSONObject jsonObject = new JSONObject(result);
                status = jsonObject.getString("status");
                message = jsonObject.getString("message");
                //JSONObject profile = jsonObject.

                JSONObject profile = jsonObject.getJSONObject("user");
                Log.d("LOGIN_RESULT",profile.toString());

                id = profile.getString("rId");
                firstName = profile.getString("rFirstName");
                lastName = profile.getString("rLastName");
                email = profile.getString("rEmail");
                password = profile.getString("rPassword");
                phone = profile.getString("rPhone");
                dob = profile.getString("rDob");
                ratings = profile.getString("rRatings");

                SharedPreferences.Editor editor = getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE).edit();
                editor.putString("rId",id);
                editor.putString("rFirstName",firstName);
                editor.putString("rLastName",lastName);
                editor.putString("rEmail",email);
                editor.putString("rPassword",password);
                editor.putString("rPhone",phone);
                editor.putString("rDob",dob);
                editor.putString("rRatings",ratings);
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
