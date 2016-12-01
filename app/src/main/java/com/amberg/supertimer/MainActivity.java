package com.amberg.supertimer;

import android.support.v4.app.Fragment;

public class MainActivity extends SingleFragmentActivity {
    /* SingleFragmentActivity doing all the Fragment wiring, we just give it
    a fragment to work with */
    @Override
    protected Fragment createFragment() {
        return new MainFragment();
    }
}
