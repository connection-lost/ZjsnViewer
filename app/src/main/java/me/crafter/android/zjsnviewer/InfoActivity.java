package me.crafter.android.zjsnviewer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.crafter.android.zjsnviewer.infofragment.BuildFragment;
import me.crafter.android.zjsnviewer.infofragment.MakeFragment;
import me.crafter.android.zjsnviewer.infofragment.RepairFragment;
import me.crafter.android.zjsnviewer.infofragment.TravelFragemt;

public class InfoActivity extends FragmentActivity {

    @BindView(R.id.dl_drawer)
    DrawerLayout dl_drawer;

    @BindView(R.id.toobar)
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

    @BindView(R.id.ib_icon)
    ImageButton ib_icon;

    @BindView(R.id.tv_drawer_name)
    TextView tv_drawer_name;
    @BindView(R.id.tv_drawer_level)
    TextView tv_drawer_level;
    @BindView(R.id.tv_build_time)
    TextView tv_build_time;
    @BindView(R.id.tv_setting)
    TextView tv_setting;

    @BindView(R.id.sw_title_on)
    Switch sw_title_on;
    @BindView(R.id.sw_title_auto_run)
    Switch sw_title_auto_run;
    @BindView(R.id.tv_web)
    TextView tv_web;
    private Context context;

    private ArrayList<TextView> tabs;
    private TravelFragemt travelFragemt;
    private BuildFragment buildFragment;
    private MakeFragment makeFragment;
    private RepairFragment repairFragment;

    private Handler handler;
    private Runnable runnable;
    private int RUN_TIME = 60*1000;
    private int FIRST_TIME = 2*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);

        initData();
        initView();
        initEven();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean on = preferences.getBoolean("on", true);
        Boolean auto = preferences.getBoolean("auto_run", true);
        sw_title_on.setChecked(on);
        sw_title_auto_run.setChecked(auto);

        handler.postDelayed(runnable,FIRST_TIME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    public void initData(){

        context = this;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String username = prefs.getString("username", "none");
        String password = prefs.getString("password", "none");
        if (username.contains("none") &&  password.contains("none")) Toast.makeText(context, R.string.username_hint, Toast.LENGTH_SHORT).show();
    }

    private void initView(){

        refreshView();
        initFragment();
    }

    private void initEven(){

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ib_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DockInfo.updateInterval = 0;
                    final ProgressDialog progressDialog = ProgressDialog.show(context,"",getString(R.string.loading));
                    UpdateTask task = new UpdateTask(context);
                    task.setUpdateTaskStateChange(new UpdateTask.onUpdateTaskStateChange() {

                        @Override
                        public void AfterTask() {

                            progressDialog.dismiss();
                            refreshAllView();
                        }
                    });
                    task.execute();
//                    new UpdateTask(v.getContext()).execute();

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

        tv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ZjsnViewer.class);
                startActivity(intent);
            }
        });

        sw_title_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                if (!preferences.edit().putBoolean("on", isChecked).commit()){

                    sw_title_on.setChecked(!isChecked);
                }
            }
        });

        sw_title_auto_run.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                if (!preferences.edit().putBoolean("auto_run", isChecked).commit()){

                    sw_title_auto_run.setChecked(!isChecked);
                }
            }
        });

        tv_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(InfoActivity.this, WebActivity.class);
                intent.putExtra("URL","http://js.ntwikis.com/");
                intent.putExtra("JS","http://js.ntwikis.com/jsp/apps/cancollezh/charactors/buildtime.jsp");
                startActivity(intent);
            }
        });
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
        tv_drawer_name.setText(Storage.str_tiduName);
        tv_level.setText("Level: " + DockInfo.level + " (" + DockInfo.exp + "/" + DockInfo.nextExp + ")");
        tv_drawer_level.setText("Level: " + DockInfo.level + " (" + DockInfo.exp + "/" + DockInfo.nextExp + ")");
        if (DockInfo.level.equals("150")){
            tv_level.setText(R.string.max);
            tv_drawer_level.setText(R.string.max);
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
