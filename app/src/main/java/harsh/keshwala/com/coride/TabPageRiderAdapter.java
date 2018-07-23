package harsh.keshwala.com.coride;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class TabPageRiderAdapter {

    int tabCount;

    public TabPageRiderAdapter(FragmentManager fm, int numebrOfTabs) {
        super(fm);
        this.tabCount = numebrOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                RiderHomeFragment riderHomeFragment = new RiderHomeFragment();
                return riderHomeFragment;
            case 1:
                RiderBrowseTripFragment riderBrowseTripFragment = new RiderBrowseTripFragment();
                return riderBrowseTripFragment;
            case 2:
                RiderPastTripFragment riderPastTripFragment = new RiderPastTripFragment();
                return riderPastTripFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }


}
