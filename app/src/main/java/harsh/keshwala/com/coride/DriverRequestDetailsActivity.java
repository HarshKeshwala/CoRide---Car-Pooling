package harsh.keshwala.com.coride;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class DriverRequestDetailsActivity extends AppCompatActivity {


    private String trId,dId;
    private TextView source, destination, date, time, expense;
    private TextView riderName, riderEmail, tId, rId;
    private ProgressDialog pDialog;
    private Button accept, reject;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_request_details);


        trId = getIntent().getExtras().getString("trId","");

        source = (TextView) findViewById(R.id.rSource);
        destination  = (TextView) findViewById(R.id.rDestination);
        date  = (TextView) findViewById(R.id.rDate);
        time  = (TextView) findViewById(R.id.rTime);
        expense  = (TextView) findViewById(R.id.rExpense);
        riderName  = (TextView) findViewById(R.id.rRiderName);
        riderEmail  = (TextView) findViewById(R.id.rEmail);
        tId = (TextView) findViewById(R.id.trdTId);
        rId = (TextView) findViewById(R.id.trdRId);
        sharedPreferences = getSharedPreferences(Config.PREF_NAME,MODE_PRIVATE);
        dId = sharedPreferences.getString("dId","");

        accept = (Button) findViewById(R.id.acceptButton);
        reject = (Button) findViewById(R.id.rejectButton);

        new RequestDetails(trId).execute();

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AcceptRequest(tId.getText().toString(),trId,dId,rId.getText().toString()).execute();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RejectRequest(tId.getText().toString(),trId,dId).execute();
            }
        });
    }

    //login with Credentials
    public class RequestDetails extends AsyncTask<String, Void, String> {

        String trId;
        public RequestDetails(String tId) {
            this.trId = tId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DriverRequestDetailsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL(Config.URL+"UserClass.php?requestDetails=true&trId="+trId);
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
            try {
                JSONObject jsonObject = new JSONObject(result);
                source.setText(jsonObject.getString("tSource"));
                destination.setText(jsonObject.getString("tDestination"));
                date.setText(jsonObject.getString("tDate"));
                time.setText(jsonObject.getString("tTime"));
                expense.setText(jsonObject.getString("tExpense") + " CAD");
                riderName.setText(jsonObject.getString("rFirstName") + " " + jsonObject.getString("rLastName"));
                riderEmail.setText(jsonObject.getString("rEmail"));
                tId.setText(jsonObject.getString("tId"));
                rId.setText(jsonObject.getString("rId"));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();
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

    public class AcceptRequest extends AsyncTask<String, Void, String> {
        String tId, trId, dId, rId;
        public AcceptRequest(String tId, String trId, String dId, String rId)   {
            this.tId = tId;
            this.trId = trId;
            this.dId = dId;
            this.rId = rId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DriverRequestDetailsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {
            try {
                String data = "tId="+ tId + "&dId=" + dId + "&trId=" + trId + "&rId=" + rId;

                URL url = new URL(Config.URL+"UserClass.php?acceptTripRequest=true&"+data);
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
            try {
                JSONObject jsonObject = new JSONObject(result);
                status = jsonObject.getString("status");
                message = jsonObject.getString("message");
                //JSONObject profile = jsonObject.
                Log.d("LOGIN_RESULT",message);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();
            if(status.equals("Ok") == true) {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            }
            else    {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            }
        }
    }

    public class RejectRequest extends AsyncTask<String, Void, String> {
        String tId, trId, dId;
        public RejectRequest(String tId, String trId, String dId)   {
            this.tId = tId;
            this.trId = trId;
            this.dId = dId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DriverRequestDetailsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {
            try {
                String data = "tId="+ tId + "&dId=" + dId + "&trId=" + trId;

                URL url = new URL(Config.URL+"UserClass.php?rejectTripRequest=true&"+data);
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
            try {
                JSONObject jsonObject = new JSONObject(result);
                status = jsonObject.getString("status");
                message = jsonObject.getString("message");
                //JSONObject profile = jsonObject.
                Log.d("LOGIN_RESULT",message);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();
            if(status.equals("Ok") == true) {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            }
            else    {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            }
        }
    }
}
