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

public class DriverAddCarActivity extends AppCompatActivity {

    private EditText carModel, carNumber, carModelYear;
    private Button addCar;
    private SharedPreferences sharedPreferences;
    private String dId;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_add_car);

        carModel = (EditText) findViewById(R.id.carModelName);
        carNumber = (EditText) findViewById(R.id.carVehicleNumber);
        carModelYear = (EditText) findViewById(R.id.carModelYear);

        sharedPreferences = getSharedPreferences(Config.PREF_NAME,MODE_PRIVATE);
        dId = sharedPreferences.getString("dId","");

        addCar = (Button) findViewById(R.id.addCarButton);

        addCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddCar(dId,
                        carModel.getText().toString(),
                        carModelYear.getText().toString(),
                        carNumber.getText().toString()).execute();
            }
        });
    }

    //login with Credentials
    public class AddCar extends AsyncTask<String, Void, String> {


        String dId, carModel, carModelYear, carNumber;
        public AddCar(String dId, String carModel, String carModelYear, String carNumber)   {
            this.dId = dId;
            this.carModel = carModel;
            this.carModelYear = carModelYear;
            this.carNumber = carNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DriverAddCarActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {

            try {

                String data = "dId="+dId + "&carModel=" + carModel + "&modelYear="+ carModelYear + "&vehicleNumber=" + carNumber;

                URL url = new URL(Config.URL+"UserClass.php?driverAddCar=true&"+data);
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
