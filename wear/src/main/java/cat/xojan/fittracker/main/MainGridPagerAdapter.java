package cat.xojan.fittracker.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.wearable.view.FragmentGridPagerAdapter;

import cat.xojan.fittracker.main.fragment.HistoryFragment;
import cat.xojan.fittracker.main.fragment.OpenAppFragment;
import cat.xojan.fittracker.main.fragment.StartFragment;

public class MainGridPagerAdapter extends FragmentGridPagerAdapter {

    private StartFragment mStartFragment;
    private HistoryFragment mHistoryFragment;
    private OpenAppFragment mOpenAppFragment;

    public MainGridPagerAdapter(FragmentManager fm) {
        super(fm);
        initFragments();
    }

    private void initFragments() {
        mStartFragment = mStartFragment == null ? new StartFragment() : mStartFragment;
        mHistoryFragment = mHistoryFragment == null ? new HistoryFragment() : mHistoryFragment;
        mOpenAppFragment = mOpenAppFragment == null ? new OpenAppFragment() : mOpenAppFragment;
    }

    @Override
    public Fragment getFragment(int row, int col) {
        if (col == 0) {
            return mStartFragment;
        } else if (col == 1) {
            return mHistoryFragment;
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
        return 3;
    }
}
