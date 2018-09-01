package ca.obrassard.inquirio;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    LostItemAdapter m_adapter;
    BottomNavigationView bottomNavigationView;
    Boolean m_isFirstConnection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //region [Initialisation des éléments de navigation]
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

        bottomNavigationView = findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent i = new Intent();
                switch (item.getItemId()) {
                    case R.id.navigation_account:
                        LaunchMyAccount();
                        return true;
                    case R.id.navigation_myitem:
                        LaunchMyItems();
                        return true;
                    case R.id.navigation_notif:
                        LaunchMyNotifications();
                        return true;
                }
                return false;
            }
        });

        //Boutton j'ai perdu qqchose
        findViewById(R.id.btn_ilostsomething).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchILostSomething();
            }
        });
        //endregion

        //Initialisation du ListView
        ListView lvNearItems = findViewById(R.id.lvNearItems);
        m_adapter = new LostItemAdapter(this);
        lvNearItems.setAdapter(m_adapter);
        lvNearItems.setEmptyView(findViewById(R.id.empty_lv_placeholder));

        //Affichage du popup d'accueil si première connexion
        if (m_isFirstConnection){
            DialogFragment welcomeDialog = new WelcomeDialog();
            welcomeDialog.show(getFragmentManager(), "welcomeDialog");
        }

    }

    //region [Evennement de navigation]

    private void LaunchILostSomething(){
        Intent intent = new Intent(this, AddItemActivity.class);
        startActivity(intent);
    }

    private void LaunchMyAccount(){
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }

    private void LaunchMyItems(){
        Intent intent = new Intent(this, MyItemsActivity.class);
        startActivity(intent);
    }

    private void  LaunchMyNotifications(){
        Intent intent = new Intent(this, NotificationsActivity.class);
        startActivity(intent);
    }
    //endregion

    //region [Evennement du tirroir]
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent = new Intent();

        if (id == R.id.ham_account) {
            intent = new Intent(this, AccountActivity.class);
        } else if (id == R.id.ham_logout) {
            return false;
        } else if (id == R.id.ham_lostitem) {
            intent = new Intent(this, AddItemActivity.class);
        } else if (id == R.id.ham_myitems) {
            intent = new Intent(this, MyItemsActivity.class);
        } else if (id == R.id.ham_mynotif) {
            intent = new Intent(this, NotificationsActivity.class);
        }
        startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    //endregion
}
