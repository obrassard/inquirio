package ca.obrassard.inquirio.activities;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import ca.obrassard.inquirio.DrawerUtils;
import ca.obrassard.inquirio.LoggedUser;
import ca.obrassard.inquirio.activities.adapters.LostItemAdapter;
import ca.obrassard.inquirio.R;
import ca.obrassard.inquirio.activities.dialogs.WelcomeDialog;
import ca.obrassard.inquirio.errorHandling.ErrorUtils;
import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.LocationRequest;
import ca.obrassard.inquirio.transfer.LostItemSummary;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    InquirioService service = RetrofitUtil.get();
    LostItemAdapter m_adapter;
    BottomNavigationView bottomNavigationView;
    private FusedLocationProviderClient mFusedLocationClient;

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
        DrawerUtils.prepareHeader(navigationView);

        bottomNavigationView = findViewById(R.id.navigation);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
                Intent i = new Intent(MainActivity.this.getApplicationContext(), ItemsDetailActivity.class);
                i.putExtra("item.id", selecteditem.itemID);
                startActivity(i);
            }
        });

        //Affichage du popup d'accueil si première connexion
        boolean isFirstConnection = getIntent().getBooleanExtra("firstconnexion",false);
        if (isFirstConnection) {
            DialogFragment welcomeDialog = new WelcomeDialog();
            welcomeDialog.show(getFragmentManager(), "welcomeDialog");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
                == ConnectionResult.SUCCESS){
            loadNearItems();
        } else {
            ErrorUtils.showGPServiceError(this);
        }

    }

    public void loadNearItems(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ErrorUtils.showLocationPermitionError(MainActivity.this);
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location == null) {
                            ErrorUtils.showLocationError(MainActivity.this);
                            return;
                        }

                        LocationRequest request = new LocationRequest();
                        request.latitude = location.getLatitude();
                        request.longitude = location.getLongitude();
                        service.getNearLostItems(request, LoggedUser.token).enqueue(new Callback<List<LostItemSummary>>() {
                            @Override
                            public void onResponse(Call<List<LostItemSummary>> call, Response<List<LostItemSummary>> response) {
                                if (!response.isSuccessful()) {
                                    ErrorUtils.showExceptionError(MainActivity.this, response.errorBody());
                                    return;
                                }

                                List<LostItemSummary> items = response.body();
                                m_adapter.clear();
                                m_adapter.addAll(items);
                                m_adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(Call<List<LostItemSummary>> call, Throwable t) {
                                ErrorUtils.showGenServError(MainActivity.this);
                            }
                        });
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadNearItems();
                } else {
                    ErrorUtils.showLocationPermitionError(MainActivity.this);
                }
            }
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
        DrawerUtils.handleDrawerClick(id,this);
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
