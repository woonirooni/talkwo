package firebase.wooni.talkwo.views;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import firebase.wooni.talkwo.R;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mTabLayout.setupWithViewPager(mViewPager);
        setUpViewPager();
    }

    private void setUpViewPager() {
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new ChatFragment(),"채팅");
        pagerAdapter.addFragment(new FriendFragment(),"친구");
        mViewPager.setAdapter(pagerAdapter);

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter{

       private  List<Fragment> fragmentList = new ArrayList<>();
       private  List<String> frStringTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return frStringTitleList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            frStringTitleList.add(title);


        }
    }
}
