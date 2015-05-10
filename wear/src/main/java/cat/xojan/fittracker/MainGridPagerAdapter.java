package cat.xojan.fittracker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.wearable.view.FragmentGridPagerAdapter;

import cat.xojan.fittracker.fragment.OpenAppFragment;
import cat.xojan.fittracker.fragment.StartFragment;

public class MainGridPagerAdapter extends FragmentGridPagerAdapter {

    private StartFragment mStartFragment;
    private OpenAppFragment mOpenAppFragment;

    public MainGridPagerAdapter(FragmentManager fm) {
        super(fm);
        initFragments();
    }

    private void initFragments() {
        mStartFragment = mStartFragment == null ? new StartFragment() : mStartFragment;
        mOpenAppFragment = mOpenAppFragment == null ? new OpenAppFragment() : mOpenAppFragment;
    }

    @Override
    public Fragment getFragment(int row, int col) {
        if (col == 0) {
            return mStartFragment;
        } else {
            return mOpenAppFragment;
        }
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount(int i) {
        return 2;
    }
}
