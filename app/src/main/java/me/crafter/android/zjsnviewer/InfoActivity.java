package me.crafter.android.zjsnviewer;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.crafter.android.zjsnviewer.infofragment.BuildFragment;
import me.crafter.android.zjsnviewer.infofragment.MakeFragment;
import me.crafter.android.zjsnviewer.infofragment.RepairFragment;
import me.crafter.android.zjsnviewer.infofragment.TravelFragemt;

public class InfoActivity extends FragmentActivity {

    @BindView(R.id.include2)
    Toolbar bar;

    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_level)
    TextView tv_level;

    @BindView(R.id.tv_travel)
    TextView tv_travel;
    @BindView(R.id.tv_repair)
    TextView tv_repair;
    @BindView(R.id.tv_build)
    TextView tv_build;
    @BindView(R.id.tv_make)
    TextView tv_make;
    @BindView(R.id.vp_page)
    ViewPager vp_page;

    private ArrayList<TextView> tabs;
    private TravelFragemt travelFragemt;
    private BuildFragment buildFragment;
    private MakeFragment makeFragment;
    private RepairFragment repairFragment;

    private Handler handler;
    private Runnable runnable;
    private int RUN_TIME = 30*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);

        initView();
        initEven();
        refreshView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private void initView(){

        bar.setTitle(R.string.pref_header_info);

        initFragment();
    }

    private void initEven(){

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        vp_page.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                setTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for(final TextView textView : tabs){

            View view = (View) textView.getParent();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = (int) textView.getTag();
                    vp_page.setCurrentItem(position);
                    setTab(position);
                }
            });
        }

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                refreshAllView();
                handler.postDelayed(runnable,RUN_TIME);
            }
        };
        handler.postDelayed(runnable,RUN_TIME);
    }

    private void initFragment(){

        travelFragemt = new TravelFragemt();
        repairFragment = new RepairFragment();
        buildFragment = new BuildFragment();
        makeFragment = new MakeFragment();

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(travelFragemt);
        fragments.add(repairFragment);
        fragments.add(buildFragment);
        fragments.add(makeFragment);

        tv_travel.setTag(0);
        tv_repair.setTag(1);
        tv_build.setTag(2);
        tv_make.setTag(3);
        tv_repair.setEnabled(false);
        tv_build.setEnabled(false);
        tv_make.setEnabled(false);

        tabs = new ArrayList<>();
        tabs.add(tv_travel);
        tabs.add(tv_repair);
        tabs.add(tv_build);
        tabs.add(tv_make);

        vp_page.setOffscreenPageLimit(4);
        pageAdapter adapter = new pageAdapter(getSupportFragmentManager(), fragments);

        vp_page.setAdapter(adapter);
        vp_page.setCurrentItem(0);
    }

    private class pageAdapter extends FragmentPagerAdapter{

        ArrayList<Fragment> fragments;

        pageAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    private void setTab(int position) {

        if (position >= 0 && position < tabs.size()){

            for (int i = 0; i < tabs.size(); i++){

                if (i == position) tabs.get(i).setEnabled(true);
                else tabs.get(i).setEnabled(false);
            }
        }
    }

    private void refreshView(){

        tv_name.setText(Storage.str_tiduName);
        tv_level.setText("Level: " + DockInfo.level + " (" + DockInfo.exp + "/" + DockInfo.nextExp + ")");
        if (DockInfo.level.equals("150")){
            tv_level.setText(R.string.max);
        }
    }

    private void refreshAllView(){

        refreshView();

        buildFragment.refreshView();
        repairFragment.refreshView();
        makeFragment.refreshView();
        travelFragemt.refreshView();
    }
}
