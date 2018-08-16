package harsh.keshwala.com.coride;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabAdminAdapter extends FragmentStatePagerAdapter {
    int tabCount;

    public TabAdminAdapter(FragmentManager fm, int numebrOfTabs) {
        super(fm);
        this.tabCount = numebrOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                AdminHomeFragment adminHomeFragment = new AdminHomeFragment();
                return adminHomeFragment;
            case 1:
                AdminRiderFragment adminRiderFragment = new AdminRiderFragment();
                return adminRiderFragment;
            case 2:
                AdminSupportFragment adminSupportFragment = new AdminSupportFragment();
                return  adminSupportFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

}
