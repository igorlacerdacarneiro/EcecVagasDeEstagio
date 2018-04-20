package com.example.igorl.ececvagasdeestagio.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.example.igorl.ececvagasdeestagio.Fragmentos.TabDisponiveis;
import com.example.igorl.ececvagasdeestagio.Fragmentos.TabEncerradas;

/**
 * Created by igorl on 21/03/2018.
 */

public class FragmentPageAdapterVagas extends FragmentPagerAdapter {

    private String[] mTabTitles;

    public FragmentPageAdapterVagas(FragmentManager fm, String[] mTabTitles) {
        super(fm);
        this.mTabTitles = mTabTitles;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new TabDisponiveis();
            case 1:
                return new TabEncerradas();
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
