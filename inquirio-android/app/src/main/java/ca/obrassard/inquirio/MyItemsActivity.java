package ca.obrassard.inquirio;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.Calendar;

import ca.obrassard.inquirio.model.LostItem;
import ca.obrassard.inquirio.model.User;

public class MyItemsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    MyLostItemAdapter m_adapterLostItems;
    MyFoundItemAdapter m_adapterFoundItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Initialisation des listes
        ListView lvMyLostItems = findViewById(R.id.lv_mylostitems);
        m_adapterLostItems = new MyLostItemAdapter(this);
        lvMyLostItems.setAdapter(m_adapterLostItems);
        lvMyLostItems.setEmptyView(findViewById(R.id.noLostItems));

        ListView lvMyFoundItem = findViewById(R.id.lv_myitemsfound);
        m_adapterFoundItems = new MyFoundItemAdapter(this);
        lvMyFoundItem.setAdapter(m_adapterFoundItems);
        lvMyFoundItem.setEmptyView(findViewById(R.id.noFoundItems));

        //TODO Tests à effacer eventuellement
        User testUser = new User(1,"Olivier Brassard","brassard.oli@gmail.com","5145782504",7);
        m_adapterLostItems.add(new LostItem(1,"Macbook Pro Gris Foncé","Description test","Cégep Édouard-montpetit",80, Calendar.getInstance().getTime(),testUser));
        m_adapterFoundItems.add(new LostItem(2,"Mon chat Newton","Description test","Rue périgord, La Prairie",300, Calendar.getInstance().getTime(),testUser));
        m_adapterFoundItems.add(new LostItem(2,"Disque SSD Kingston","Description test","Labo d'informatique",50, Calendar.getInstance().getTime(),testUser));
        m_adapterFoundItems.notifyDataSetChanged();
        m_adapterLostItems.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Click sur les items du tiroir
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.ham_account) {
            // Handle the camera action
        } else if (id == R.id.ham_logout) {

        } else if (id == R.id.ham_lostitem) {

        } else if (id == R.id.ham_myitems) {

        } else if (id == R.id.ham_mynotif) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
