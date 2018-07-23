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
 * {@link RiderHomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RiderHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RiderHomeFragment extends Fragment {

    Activity context;
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
    }
}
