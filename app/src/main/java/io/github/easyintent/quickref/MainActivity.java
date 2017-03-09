package io.github.easyintent.quickref;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.easyintent.quickref.fragment.AboutFragment;
import io.github.easyintent.quickref.fragment.FavoriteListFragment;
import io.github.easyintent.quickref.fragment.MessageDialogFragment;
import io.github.easyintent.quickref.fragment.ReferenceListFragment;

@EActivity
public class MainActivity extends AppCompatActivity
        implements
            NavigationView.OnNavigationItemSelectedListener,
            MessageDialogFragment.Listener {

    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);

    @ViewById
    protected Toolbar toolbar;

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);

        final MenuItem item = menu.findItem(R.id.search_ref);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint(getString(R.string.lbl_search_reference));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                item.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            mayPopFragment();
        }
    }

    private void mayPopFragment() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 1) {
            navigationView.setCheckedItem(R.id.nav_all);
            showMainFragment();
        } else {
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        showMainFragment();

        switch (item.getItemId()) {
            case R.id.nav_all:
                break;
            case R.id.nav_favorite:
                showFavorites();
                break;
            case R.id.nav_about:
                showAbout();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void search(String query) {
        startActivity(QuickRefActivity.newSearchIntent(this, query));
    }

    private void showFavorites() {
        FragmentManager manager = getSupportFragmentManager();
        FavoriteListFragment fragment = FavoriteListFragment.newInstance();
        manager.beginTransaction()
                .replace(R.id.content_frame, fragment, "favorite_list")
                .addToBackStack("favorite")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

    }

    private void showAbout() {
        clearNavigationSelection();
        FragmentManager manager = getSupportFragmentManager();
        AboutFragment fragment = AboutFragment.newInstance();
        manager.beginTransaction()
                .replace(R.id.content_frame, fragment, "about_fragment")
                .addToBackStack("about")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void clearNavigationSelection() {
        Menu menu = navigationView.getMenu();
        int n = menu.size();
        for (int i=0; i<n; i++) {
            menu.getItem(i).setChecked(false);
        }
    }

    private void initFragment() {
        FragmentManager manager = getSupportFragmentManager();
        ReferenceListFragment fragment = (ReferenceListFragment) manager.findFragmentByTag("reference_list");
        if (fragment != null) {
            return;
        }

        // show main reference list
        fragment = ReferenceListFragment.newListChildrenInstance(null);
        manager.beginTransaction()
                .replace(R.id.content_frame, fragment, "reference_list")
                .addToBackStack("main")
                .commit();
    }

    @Override
    public void onOkClicked(MessageDialogFragment dialogFragment) {
        // nothing to do
    }

    private void showMainFragment() {
        setTitle(getString(R.string.app_name));
        getSupportFragmentManager()
                .popBackStack("main", 0);
    }
}