package com.example.igorl.ececvagasdeestagio.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.example.igorl.ececvagasdeestagio.Fragmentos.TabAdmins;
import com.example.igorl.ececvagasdeestagio.Fragmentos.TabUsuarios;

/**
 * Created by igorl on 21/03/2018.
 */

public class FragmentPageAdapterUsers extends FragmentPagerAdapter {

    private String[] mTabTitles;

    public FragmentPageAdapterUsers(FragmentManager fm, String[] mTabTitles) {
        super(fm);
        this.mTabTitles = mTabTitles;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new TabUsuarios();
            case 1:
                return new TabAdmins();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return this.mTabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.mTabTitles[position];
    }
}
