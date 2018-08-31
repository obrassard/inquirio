package ca.obrassard.inquirio;

import android.app.DialogFragment;
import android.content.Intent;
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
import android.widget.Button;
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

        //Boutton j'ai perdu qqchose
        findViewById(R.id.btn_ilostsomething).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iLostSomething();
            }
        });

        //Bouttons de la barre de menu


        //Affichage du popup d'accueil si première connexion
        if (m_isFirstConnection){
            DialogFragment welcomeDialog = new WelcomeDialog();
            welcomeDialog.show(getFragmentManager(), "welcomeDialog");
        }
    }

    private void iLostSomething(){
        Intent intent = new Intent(this, AddItemActivity.class);
        startActivity(intent);
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

        Intent intent = new Intent();

        if (id == R.id.ham_account || id == R.id.navigation_account) {
            intent = new Intent(this, AccountActivity.class);
        } else if (id == R.id.ham_logout) {
            return false;
        } else if (id == R.id.ham_lostitem) {
            intent = new Intent(this, AddItemActivity.class);
        } else if (id == R.id.ham_myitems || id == R.id.navigation_myitem) {
            intent = new Intent(this, MyItemsActivity.class);
        } else if (id == R.id.ham_mynotif || id == R.id.navigation_notif) {
            intent = new Intent(this, NotificationsActivity.class);
        }
        startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
