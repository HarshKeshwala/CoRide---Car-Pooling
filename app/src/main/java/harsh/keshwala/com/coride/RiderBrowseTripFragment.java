package harsh.keshwala.com.coride;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RiderBrowseTripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RiderBrowseTripFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RiderBrowseTripFragment extends Fragment {
    Activity context;
    public  RiderBrowseTripFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        View view =  inflater.inflate(R.layout.fragment_rider_browse_trip,container,false);
        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
