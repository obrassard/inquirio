package ca.obrassard.inquirio.activities;

import android.Manifest;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.List;

import ca.obrassard.inquirio.DrawerUtils;
import ca.obrassard.inquirio.LoggedUser;
import ca.obrassard.inquirio.activities.adapters.LostItemAdapter;
import ca.obrassard.inquirio.R;
import ca.obrassard.inquirio.activities.dialogs.WelcomeDialog;
import ca.obrassard.inquirio.errorHandling.ErrorUtils;
import ca.obrassard.inquirio.model.LostItem;
import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.FinderContactDetail;
import ca.obrassard.inquirio.transfer.LocationRequest;
import ca.obrassard.inquirio.transfer.LostItemSummary;
import ca.obrassard.inquirio.transfer.RequestResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    InquirioService service = RetrofitUtil.get();
    LostItemAdapter m_adapter;
    BottomNavigationView bottomNavigationView;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean deviceIsLandscape;
    private static final String MAP_VIEW_BUNDLE_KEY = "MAPKEY";
    private int itemId = -1;
    private MapView mapView;
    TextView itemName;
    TextView locationName;
    ImageView statusIcon;
    TextView reward;
    TextView description;
    TextView lostDate;
    Button btnIFoundThis;
    Button btnDeleteItem;
    Button btnContact;
    LinearLayout landscapeDetailLayout;
    LinearLayout landscapeDetailLayoutEmpty;
    GoogleMap m_googleMap;

    ProgressDialog progressDialog ;

    private void beginLoading() {
        if (progressDialog == null)
        progressDialog = ProgressDialog.show(MainActivity.this,
                "Veuillez patienter",null,true);
    }


    private void endLoading() {
        if (progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        deviceIsLandscape = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

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


        if (deviceIsLandscape){

            landscapeDetailLayout = findViewById(R.id.detail_land);
            landscapeDetailLayoutEmpty = findViewById(R.id.detail_land_EMPTY);

            Bundle mapViewBundle = null;
            if (savedInstanceState != null) {
                mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
            }

            mapView = findViewById(R.id.map);
            mapView.onCreate(mapViewBundle);
            mapView.getMapAsync(this);

            itemName = findViewById(R.id.item_name);
            locationName = findViewById(R.id.txtlocation);
            statusIcon = findViewById(R.id.statusicon);
            reward = findViewById(R.id.reward_amount);
            description = findViewById(R.id.description);
            lostDate = findViewById(R.id.lost_date);

            btnIFoundThis = findViewById(R.id.btn_itemfound);
            btnDeleteItem = findViewById(R.id.btn_deleteItem);
            btnContact = findViewById(R.id.btn_contactFinder);

            btnIFoundThis.setVisibility(View.GONE);
            btnDeleteItem.setVisibility(View.GONE);
            btnContact.setVisibility(View.GONE);

            btnDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    beginLoading();
                    service.deleteItem(itemId, LoggedUser.token).enqueue(new Callback<RequestResult>() {
                        @Override
                        public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                            endLoading();
                            if (!response.isSuccessful()) {
                                ErrorUtils.showExceptionError(MainActivity.this, response.errorBody());
                                return;
                            }
                            boolean deleteIsSuccessful = response.body().result;
                            if (deleteIsSuccessful){
                                itemId = -1;
                                landscapeDetailLayout.setVisibility(View.GONE);
                                landscapeDetailLayoutEmpty.setVisibility(View.VISIBLE);
                                Toast.makeText(MainActivity.this, "L'item " + itemName.getText()+ " à été supprimé d'Inquirio", Toast.LENGTH_LONG).show();
                                loadNearItems();
                            } else {
                                Snackbar.make(MainActivity.this.findViewById(android.R.id.content), "Impossible de supprimer l'item. Veuillez réésayer", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<RequestResult> call, Throwable t) {
                            endLoading();
                            ErrorUtils.showGenServError(MainActivity.this);
                        }
                    });
                }
            });

            btnIFoundThis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this.getApplicationContext(),ItemFoundActivity.class);
                    i.putExtra("item.id",itemId);
                    startActivity(i);
                }
            });

            btnContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    beginLoading();
                    service.getFinderContactDetail(itemId,LoggedUser.token).enqueue(new Callback<FinderContactDetail>() {
                        @Override
                        public void onResponse(Call<FinderContactDetail> call, Response<FinderContactDetail> response) {
                            endLoading();
                            if (!response.isSuccessful()) {
                                ErrorUtils.showExceptionError(MainActivity.this, response.errorBody());
                                return;
                            }
                            String telephone = response.body().phoneNumber;
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", telephone, null)));
                        }

                        @Override
                        public void onFailure(Call<FinderContactDetail> call, Throwable t) {
                           endLoading();
                           ErrorUtils.showGenServError(MainActivity.this);
                        }
                    });
                }
            });
        }

        //Initialisation du ListView
        ListView lvNearItems = findViewById(R.id.lvNearItems);
        m_adapter = new LostItemAdapter(this);
        lvNearItems.setAdapter(m_adapter);
        lvNearItems.setEmptyView(findViewById(R.id.empty_lv_placeholder));

        lvNearItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LostItemSummary selecteditem = m_adapter.getItem(position);

                if (!deviceIsLandscape){
                    Intent i = new Intent(MainActivity.this.getApplicationContext(), ItemsDetailActivity.class);
                    i.putExtra("item.id", selecteditem.itemID);
                    startActivity(i);
                } else {
                    itemId = (int)selecteditem.itemID;

                    landscapeDetailLayout.setVisibility(View.VISIBLE);
                    landscapeDetailLayoutEmpty.setVisibility(View.GONE);

                    beginLoading();
                    service.getItemDetail(itemId, LoggedUser.token).enqueue(new Callback<LostItem>() {
                        @Override
                        public void onResponse(Call<LostItem> call, Response<LostItem> response) {
                            //endLoading();
                            if (!response.isSuccessful()) {
                                ErrorUtils.showExceptionError(MainActivity.this, response.errorBody());
                                return;
                            }

                            LostItem lostItem = response.body();
                            setTitle(lostItem.title);
                            itemName.setText(lostItem.title);
                            if (lostItem.itemHasBeenFound){
                                statusIcon.setImageResource(R.drawable.checked);
                            } else {
                                statusIcon.setImageResource(R.drawable.wanted_2);
                            }
                            reward.setText(getString(R.string.cash, lostItem.reward));
                            description.setText(lostItem.description);

                            SimpleDateFormat simpleDateFormat =
                                    new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
                            lostDate.setText(getString(R.string.date,simpleDateFormat.format(lostItem.date)));


                            //Afichage des bons boutons selon l'utilisateur
                            if (lostItem.ownerId == LoggedUser.data.userID){
                                btnIFoundThis.setVisibility(View.GONE);
                                btnDeleteItem.setVisibility(View.VISIBLE);
                            } else {
                                btnIFoundThis.setVisibility(View.VISIBLE);
                                btnDeleteItem.setVisibility(View.GONE);
                                btnContact.setVisibility(View.GONE);
                            }

                            if(lostItem.itemHasBeenFound){
                                btnIFoundThis.setVisibility(View.GONE);
                                btnDeleteItem.setVisibility(View.GONE);
                                findViewById(R.id.txtfound).setVisibility(View.VISIBLE);
                                btnContact.setVisibility(View.VISIBLE);
                            } else {
                                btnContact.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(Call<LostItem> call, Throwable t) {
                            //endLoading();
                            ErrorUtils.showGenServError(MainActivity.this);
                        }
                    });
                    service.getItemLocation(itemId, LoggedUser.token).enqueue(new Callback<ca.obrassard.inquirio.transfer.Location>() {
                        @Override
                        public void onResponse(Call<ca.obrassard.inquirio.transfer.Location> call, Response<ca.obrassard.inquirio.transfer.Location> response) {
                            endLoading();
                            if (!response.isSuccessful()) {
                                ErrorUtils.showExceptionError(MainActivity.this, response.errorBody());
                                return;
                            }
                            ca.obrassard.inquirio.transfer.Location location = response.body();
                            locationName.setText(location.Name);

                            m_googleMap.clear();
                            m_googleMap.setMinZoomPreference(16);
                            final LatLng postion = new LatLng(location.Lattitude, location.Longittude);
                            m_googleMap.addMarker(new MarkerOptions().position(postion));
                            m_googleMap.moveCamera(CameraUpdateFactory.newLatLng(postion));

                            m_googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    Intent i = new Intent(getBaseContext(),MapDetail.class);
                                    i.putExtra("lat",postion.latitude);
                                    i.putExtra("lng",postion.longitude);
                                    startActivity(i);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<ca.obrassard.inquirio.transfer.Location> call, Throwable t) {
                            endLoading();
                            ErrorUtils.showGenServError(MainActivity.this);
                        }
                    });
                }
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
    public void onMapReady(final GoogleMap googleMap) {
        m_googleMap = googleMap;
    }

    //region [Override des methodes de l'activité pour le mapview]
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (!deviceIsLandscape) return;

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!deviceIsLandscape) return;
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!deviceIsLandscape) return;
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (!deviceIsLandscape) return;
        mapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!deviceIsLandscape) return;
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (!deviceIsLandscape) return;
        mapView.onLowMemory();
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

        if (!deviceIsLandscape) return;
        mapView.onResume();

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
                        beginLoading();
                        service.getNearLostItems(request, LoggedUser.token).enqueue(new Callback<List<LostItemSummary>>() {
                            @Override
                            public void onResponse(Call<List<LostItemSummary>> call, Response<List<LostItemSummary>> response) {
                                endLoading();
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
                                endLoading();
                                ErrorUtils.showGenServError(MainActivity.this);
                            }
                        });
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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
