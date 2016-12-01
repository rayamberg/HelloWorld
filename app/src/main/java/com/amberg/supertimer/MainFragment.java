package com.amberg.supertimer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * MainFragment contains the code to display the main portion of SuperTimer as a fragment.
 * It is where the timer will actually be stopped, started, and watched.
 */
public class MainFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Begin implementation here */
    }

    /* The following method inflates a view and needs to be overridden for a fragment */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, parent, false);
        return v;
    }
}
