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
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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


public class RiderHomeFragment extends android.support.v4.app.Fragment {

    String TAG = "UpcomingTrip";
    private ProgressDialog pDialog;
    private ListView lv;

    ArrayList<HashMap<String, String>> tripList;

    FloatingActionButton browseTrip;
    Activity context;
    SharedPreferences sharedPreferences;
    String rId;


    public  RiderHomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        View view =  inflater.inflate(R.layout.fragment_rider_home,container,false);
        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();

        tripList = new ArrayList<>();
        lv = (ListView) context.findViewById(R.id.riderTripList);

        sharedPreferences = context.getSharedPreferences(Config.PREF_NAME, Context.MODE_PRIVATE);

        browseTrip = (FloatingActionButton) context.findViewById(R.id.browseTripFloatingActionButton);
        browseTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, RiderBrowseTripActivity.class));
            }
        });


        loadTrips();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView id = (TextView) view.findViewById(R.id.tId);
                Intent intent = new Intent(context,RiderTripDetailsActivity.class);
                intent.putExtra("tId",id.getText().toString());
                context.startActivity(intent);
            }
        });
    }

    public void loadTrips() {
        rId = sharedPreferences.getString("rId","");
        new GetUpcomingTripList(rId).execute();
    }

    public class GetUpcomingTripList extends AsyncTask<Void, Void, Void> {

        String rId;
        public GetUpcomingTripList(String rId)  {
            this.rId = rId;
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
            String jsonStr = sh.makeServiceCall(Config.URL+"/UserClass.php?riderUpcomingTrips=true&rId="+rId);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("trips");

                    tripList.clear();
                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString("tId");
                        String source = c.getString("tSource");
                        String destination = c.getString("tDestination");
                        String date = c.getString("tDate");
                        String time = c.getString("tTime");
                        String expense = c.getString("tExpense");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();


                        // adding each child node to HashMap key => value
                        contact.put("id", id);
                        contact.put("address", source + " to " + destination);
                        contact.put("dateTime", date + " at " + time);
                        contact.put("expense", "$ " + expense);

                        // adding contact to contact list
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
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    context, tripList,
                    R.layout.trip_list, new String[]{"id","address", "dateTime", "expense"},
                    new int[]{R.id.tId, R.id.tAddress, R.id.tDateTime, R.id.tExpense});

            lv.setAdapter(adapter);
        }

    }

}
