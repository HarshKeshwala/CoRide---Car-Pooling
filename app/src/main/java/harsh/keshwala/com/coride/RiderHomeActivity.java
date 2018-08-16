package harsh.keshwala.com.coride;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class RiderHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_home);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs1);

        tabLayout.addTab(tabLayout.newTab().setText("Home").setIcon(R.drawable.home));
        tabLayout.addTab(tabLayout.newTab().setText("Past Trips").setIcon(R.drawable.trip));
        tabLayout.addTab(tabLayout.newTab().setText("Support").setIcon(R.drawable.message));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager1);
        final PagerAdapter pagerAdapter = new TabPageRiderAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.rider_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())   {
            case R.id.viewRiderProfile:
                startActivity(new Intent(this, RiderAccountProfileActivity.class));
                break;
            case R.id.signOut:
                startActivity(new Intent(this, RiderLoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
