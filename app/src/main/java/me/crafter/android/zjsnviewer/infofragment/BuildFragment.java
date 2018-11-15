package me.crafter.android.zjsnviewer.infofragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.crafter.android.zjsnviewer.DockInfo;
import me.crafter.android.zjsnviewer.R;


public class BuildFragment extends Fragment {

    @BindView(R.id.textView1)
    TextView textView1;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.textView4)
    TextView textView4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.widget__build, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshView();
    }

    public void refreshView(){

        String[] info = DockInfo.getBuildBoard();
        textView1.setText(info[0]);
        textView2.setText(info[1]);
        textView3.setText(info[2]);
        textView4.setText(info[3]);
    }
}
