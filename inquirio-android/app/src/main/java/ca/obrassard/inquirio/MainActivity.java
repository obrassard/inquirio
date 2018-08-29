package ca.obrassard.inquirio;

import android.app.DialogFragment;
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
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import ca.obrassard.inquirio.model.LostItem;
import ca.obrassard.inquirio.model.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    LostItemAdapter m_adapter;
    Boolean m_isFirstConnection = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Construction de la toolbar et du tiroir hamburger
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Initialisation du ListView
        ListView lvNearItems = findViewById(R.id.lvNearItems);
        m_adapter = new LostItemAdapter(this);
        lvNearItems.setAdapter(m_adapter);
        lvNearItems.setEmptyView(findViewById(R.id.empty_lv_placeholder));

        //TODO Tests à effacer eventuellement
        User testUser = new User(1,"Olivier Brassard","brassard.oli@gmail.com","5145782504",7);
        m_adapter.add(new LostItem(1,"Macbook Pro Gris Foncé","Description test","Cégep Édouard-montpetit",80, Calendar.getInstance().getTime(),testUser));
        m_adapter.add(new LostItem(2,"Mon chat Newton","Description test","Rue périgord, La Prairie",300, Calendar.getInstance().getTime(),testUser));
        m_adapter.notifyDataSetChanged();

        //Affichage du popup d'accueil si première connexion
        if (m_isFirstConnection){
            DialogFragment welcomeDialog = new WelcomeDialog();
            welcomeDialog.show(getFragmentManager(), "welcomeDialog");
        }
    }

    //Gestion de la fermeture du tiroir
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
