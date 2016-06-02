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

//    @BindView(R.id.sr_refresh)
//    SwipeRefreshLayout sr_refresh;

//    @BindView(R.id.ib_icon)
//    ImageButton ib_icon;
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

    private TextView[] tabs = new TextView[4];
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

//        sr_refresh.setColorSchemeResources(
//                android.R.color.holo_red_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_green_light
//        );

        initFragment();
    }

    private void initEven(){

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        sr_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        sr_refresh.setRefreshing(false);
//                        Toast.makeText(InfoActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
//                    }
//                }, 2000);
//            }
//        });

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


        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                refreeshAllView();
//                Toast.makeText(InfoActivity.this,new Date().toString(), Toast.LENGTH_SHORT).show();
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

        tv_repair.setEnabled(false);
        tv_build.setEnabled(false);
        tv_make.setEnabled(false);

        tabs[0] = tv_travel;
        tabs[1] = tv_repair;
        tabs[2] = tv_build;
        tabs[3] = tv_make;

        vp_page.setOffscreenPageLimit(4);
        pageAdapter adapter = new pageAdapter(getSupportFragmentManager(), fragments);

        vp_page.setAdapter(adapter);
        vp_page.setCurrentItem(0);
    }

    private class pageAdapter extends FragmentPagerAdapter{

        ArrayList<Fragment> fragments;

        public pageAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
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

        if (position >= 0 && position < tabs.length){

            for (int i = 0; i < tabs.length; i++){

                if (i == position) tabs[i].setEnabled(true);
                else tabs[i].setEnabled(false);
            }
        }
    }

    private void refreshView(){

        tv_name.setText(Storage.str_tiduName);
        tv_level.setText("Level: " + DockInfo.level + " (" + DockInfo.exp + "/" + DockInfo.nextExp + ")");
        if (DockInfo.level.equals("150")){
            tv_level.setText("Level: 150 (MAX)");
        }
    }

    private void refreeshAllView(){

        refreshView();

        buildFragment.refreshView();
        repairFragment.refreshView();
        makeFragment.refreshView();
        travelFragemt.refreshView();
    }
}
