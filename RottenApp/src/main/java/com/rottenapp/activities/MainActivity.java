package com.rottenapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.astuetz.PagerSlidingTabStrip;
import com.rottenapp.R;
import com.rottenapp.adapters.TabStripAdapter;
import com.rottenapp.fragments.ActivityFragment;
import com.rottenapp.helpers.URLHelper;

public class MainActivity extends BaseTopActivity implements SearchView.OnQueryTextListener {

    private SearchView searchView;
    private TabStripAdapter mTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setupNavDrawer();
        setupViews();

    }

    private void setupViews() {
        ViewPager pager = (ViewPager) findViewById(R.id.mainPager);
        pager.setOffscreenPageLimit(3);
        mTabsAdapter = new TabStripAdapter(getSupportFragmentManager(),this);
        pager.setAdapter(mTabsAdapter);
        pager.setCurrentItem(0);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setDrawerSelectedItem(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_film));
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent myIntent = new Intent(this, PreferencesActivity.class);
                startActivityForResult(myIntent, 1);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                return true;
            case R.id.refresh:
                for (int i=0; i<mTabsAdapter.getCount();i++) {
                    ActivityFragment f = (ActivityFragment)mTabsAdapter.getRegisteredFragment(i);
                    f.refreshData();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                recreate();
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        searchView.clearFocus();
        searchView.setQuery("", false);
        searchView.setIconified(true);
        String URL = URLHelper.getSearchURL(s);
        Intent myIntent = new Intent(this, ListActivity.class);
        myIntent.putExtra("URL",URL);
        myIntent.putExtra("title",getString(R.string.title_activity_search));
        startActivity(myIntent);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

}