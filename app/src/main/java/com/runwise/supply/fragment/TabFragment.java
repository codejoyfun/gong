package com.runwise.supply.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.runwise.supply.R;

/**
 * Created by mike on 2017/9/5.
 */

public class TabFragment  extends Fragment {
    private TextView textView;

    public static TabFragment newInstance(String indexName){
        Bundle bundle = new Bundle();
        bundle.putString("index", indexName);
        TabFragment fragment = new TabFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment, null);
        textView = (TextView) view.findViewById(R.id.text);
        textView.setText(getArguments().getString("index"));
        return view;
    }
}
