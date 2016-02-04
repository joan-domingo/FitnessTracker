package cat.xojan.fittracker.presentation.home;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.injection.HasComponent;
import cat.xojan.fittracker.injection.component.DaggerHomeComponent;
import cat.xojan.fittracker.injection.component.HomeComponent;
import cat.xojan.fittracker.injection.module.HomeModule;
import cat.xojan.fittracker.presentation.BaseActivity;
import cat.xojan.fittracker.presentation.history.HistoryFragment;

public class HomeActivity extends BaseActivity implements
        MenuAdapter.MenuClickListener,
        HasComponent {

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.left_drawer)
    RecyclerView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
    private HomeComponent mComponent;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeInjector();
        ButterKnife.bind(this);

        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = new String[]{"Activity", "History"};

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new MenuAdapter(mPlanetTitles, this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(
                        this,
                        mDrawerLayout,
                        R.string.drawer_open,
                        R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        selectItem(0);
    }

    private void initializeInjector() {
        mComponent = DaggerHomeComponent.builder()
                .appComponent(getApplicationComponent())
                .baseActivityModule(getActivityModule())
                .homeModule(new HomeModule())
                .build();
        mComponent.inject(this);
    }

    private void selectItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new HistoryFragment();
                break;
            default:

                break;
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(null);
        ft.commit();

        // update selected item title, then close the drawer
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void onClick(View view, int position) {
        selectItem(position);
    }

    @Override
    public HomeComponent getComponent() {
        return mComponent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}