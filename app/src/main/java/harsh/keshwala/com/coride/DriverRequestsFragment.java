package harsh.keshwala.com.coride;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class DriverRequestsFragment extends android.support.v4.app.Fragment {

    String TAG = "DriverRequestFragment";
    private ProgressDialog pDialog;
    private ListView lv;

    ArrayList<HashMap<String, String>> tripList;

    Activity context;
    SharedPreferences sharedPreferences;
    String dId;
    Button accept;

    public DriverRequestsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        View view =  inflater.inflate(R.layout.fragment_driver_requests,container,false);
        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();

        tripList = new ArrayList<>();
        lv = (ListView) context.findViewById(R.id.requestList);

        sharedPreferences = context.getSharedPreferences(Config.PREF_NAME, Context.MODE_PRIVATE);

        loadRequests();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView trId = (TextView) view.findViewById(R.id.trId);
                Intent intent = new Intent(context,DriverRequestDetailsActivity.class);
                intent.putExtra("trId",trId.getText().toString());
                context.startActivity(intent);
            }
        });
    }



    public void loadRequests() {
        dId = sharedPreferences.getString("dId","");
        new GetRequests(dId).execute();
    }

    public class GetRequests extends AsyncTask<Void, Void, Void> {

        String dId;
        public GetRequests(String dId)  {
            this.dId = dId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(Config.URL+"/UserClass.php?driverListRequests=true&dId="+dId);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("requests");

                    tripList.clear();
                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String trId = c.getString("trId");
                        String tId = c.getString("tId");
                        String rId = c.getString("rId");
                        String rName = c.getString("rFirstName") + " " +c.getString("rLastName");
                        String address = c.getString("tSource") + " to " + c.getString("tDestination");
                        String date = c.getString("tDate");
                        String expense = c.getString("tExpense");


                        HashMap<String, String> contact = new HashMap<>();

                        contact.put("trId",trId);
                        contact.put("tId",tId);
                        contact.put("rId",rId);
                        contact.put("rName",rName);
                        contact.put("address",address);
                        contact.put("date",date);
                        contact.put("expense",expense);

                        tripList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();


            ListAdapter adapter = new SimpleAdapter(
                    context, tripList,
                    R.layout.request_list, new String[]{"trId","tId","rId","rName","address","date","expense"},
                    new int[]{R.id.trId, R.id.trTId, R.id.trRId, R.id.rName, R.id.rAddress, R.id.rDate,R.id.trExpense});
            lv.setAdapter(adapter);
        }

    }
}
