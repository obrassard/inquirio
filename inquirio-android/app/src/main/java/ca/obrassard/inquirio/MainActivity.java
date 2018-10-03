package ca.obrassard.inquirio;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.LocationRequest;
import ca.obrassard.inquirio.transfer.LostItemSummary;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    InquirioService service = RetrofitUtil.getMock();
    LostItemAdapter m_adapter;
    BottomNavigationView bottomNavigationView;
    Boolean m_isFirstConnection = LoggedUser.data.isFirstLogin;

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

        lvNearItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LostItemSummary selecteditem = m_adapter.getItem(position);
                Intent i = new Intent(MainActivity.this.getApplicationContext(),ItemsDetailActivity.class);
                i.putExtra("item.id",selecteditem.itemID);
                startActivity(i);
            }
        });

//        DialogFragment rateTest = new RateUserDialog();
//        Bundle arg = new Bundle();
//        arg.putLong("notifId",1L);
//        rateTest.setArguments(arg);
//        rateTest.show(getFragmentManager(),"rate");

        //Affichage du popup d'accueil si première connexion
        if (m_isFirstConnection){
            DialogFragment welcomeDialog = new WelcomeDialog();
            welcomeDialog.show(getFragmentManager(), "welcomeDialog");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationRequest request = new LocationRequest();
        //TODO : Obtenir les coordonnées GPS de l'Appareil
        service.getNearLostItems(request).enqueue(new Callback<List<LostItemSummary>>() {
            @Override
            public void onResponse(Call<List<LostItemSummary>> call, Response<List<LostItemSummary>> response) {
                List<LostItemSummary> items = response.body();
                m_adapter.clear();
                m_adapter.addAll(items);
                m_adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<LostItemSummary>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Une erreur est survenue lors du chargement des données", Toast.LENGTH_SHORT).show();
            }
        });

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
