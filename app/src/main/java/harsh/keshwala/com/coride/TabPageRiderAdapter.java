package harsh.keshwala.com.coride;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabPageRiderAdapter extends FragmentStatePagerAdapter {

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
                RiderPastTripFragment riderPastTripFragment = new RiderPastTripFragment();
                return riderPastTripFragment;
            case 2:
                RiderSupportFragment riderSupportFragment = new RiderSupportFragment();
                return  riderSupportFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }


}
