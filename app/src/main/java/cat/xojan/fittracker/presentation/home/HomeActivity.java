package cat.xojan.fittracker.presentation.home;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.injection.HasComponent;
import cat.xojan.fittracker.injection.component.DaggerHomeComponent;
import cat.xojan.fittracker.injection.component.HomeComponent;
import cat.xojan.fittracker.injection.module.HomeModule;
import cat.xojan.fittracker.presentation.BaseActivity;
import cat.xojan.fittracker.presentation.history.HistoryFragment;

public class HomeActivity extends BaseActivity implements HasComponent {

    @Bind(R.id.tabs)
    TabLayout mTabLayout;
    @Bind(R.id.viewpager)
    ViewPager mViewPager;

    private HomeComponent mComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeInjector();
        ButterKnife.bind(this);

        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initializeInjector() {
        mComponent = DaggerHomeComponent.builder()
                .appComponent(getApplicationComponent())
                .baseActivityModule(getActivityModule())
                .homeModule(new HomeModule())
                .build();
        mComponent.inject(this);
    }

    @Override
    public HomeComponent getComponent() {
        return mComponent;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "WORKOUT");
        adapter.addFragment(new HistoryFragment(), "HISTORY");
        adapter.addFragment(new HomeFragment(), "SETTINGS");
        viewPager.setAdapter(adapter);
    }
}