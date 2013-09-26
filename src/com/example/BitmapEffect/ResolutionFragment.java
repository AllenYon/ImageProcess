package com.example.BitmapEffect;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created with IntelliJ IDEA.
 * User: Link
 * Date: 13-9-25
 * Time: PM11:44
 * To change this template use File | Settings | File Templates.
 */
public class ResolutionFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resolutions, null,false);
        return view;
    }
}
