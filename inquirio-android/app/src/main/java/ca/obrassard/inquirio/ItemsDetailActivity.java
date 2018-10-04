package ca.obrassard.inquirio;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;

import ca.obrassard.inquirio.model.LostItem;
import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.Location;
import ca.obrassard.inquirio.transfer.RequestResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemsDetailActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {


    private MapView mapView;
    private static final String MAP_VIEW_BUNDLE_KEY = "MAPKEY";

    private InquirioService service = RetrofitUtil.getMock();
    private long itemId;
    TextView itemName;
    TextView locationName;
    ImageView statusIcon;
    TextView reward;
    TextView description;
    TextView lostDate;
    Button btnIFoundThis;
    Button btnDeleteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //region [Initialisation des éléments de navigation]
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerUtils.prepareHeader(navigationView);

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

        //endregion

        //Affichage des details de l'item
        itemId = getIntent().getLongExtra("item.id",0);
        service.getItemDetail(itemId).enqueue(new Callback<LostItem>() {
            @Override
            public void onResponse(Call<LostItem> call, Response<LostItem> response) {
                LostItem lostItem = response.body();

                if (lostItem == null){
                    Toast.makeText(ItemsDetailActivity.this, "Une erreur est survenue, veuillez réésayer", Toast.LENGTH_SHORT).show();
                    return;
                }

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
                }

                if(lostItem.itemHasBeenFound){
                    btnIFoundThis.setVisibility(View.GONE);
                    btnDeleteItem.setVisibility(View.GONE);
                    findViewById(R.id.txtfound).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<LostItem> call, Throwable t) {
                Toast.makeText(ItemsDetailActivity.this, "Une erreur est survenue lors du chargement des données", Toast.LENGTH_SHORT).show();
            }
        });

        //Actions des deux boutons
        btnDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.deleteItem(itemId).enqueue(new Callback<RequestResult>() {
                    @Override
                    public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                        if (response.body() == null){
                            Toast.makeText(ItemsDetailActivity.this, "Une erreur est survenue, veuillez réésayer", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        boolean deleteIsSuccessful = response.body().result;
                        if (deleteIsSuccessful){
                            Intent i = new Intent(ItemsDetailActivity.this.getApplicationContext(),MainActivity.class);
                            startActivity(i);
                            Toast.makeText(ItemsDetailActivity.this, "L'item " + itemName.getText()+ " à été supprimé d'Inquirio", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ItemsDetailActivity.this, "Impossible de supprimer l'item. Veuillez réésayer", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RequestResult> call, Throwable t) {
                        Toast.makeText(ItemsDetailActivity.this, "Impossible de supprimer l'item. Veuillex réésayer", Toast.LENGTH_LONG).show();
                       }
                });
            }
        });

        btnIFoundThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ItemsDetailActivity.this.getApplicationContext(),ItemFoundActivity.class);
                i.putExtra("item.id",itemId);
                startActivity(i);
            }
        });
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {


        service.getItemLocation(itemId).enqueue(new Callback<Location>() {
            @Override
            public void onResponse(Call<Location> call, Response<Location> response) {
                Location location = response.body();
                if (location == null){
                    Toast.makeText(ItemsDetailActivity.this, "Une erreur est survenue, veuillez réésayer", Toast.LENGTH_SHORT).show();
                    return;
                }

                locationName.setText(location.Name);

                googleMap.setMinZoomPreference(16);
                final LatLng postion = new LatLng(location.Lattitude, location.Longittude);
                googleMap.addMarker(new MarkerOptions().position(postion));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(postion));

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
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
            public void onFailure(Call<Location> call, Throwable t) {
                Toast.makeText(ItemsDetailActivity.this, "Impossible d'obtenir l'emplacement de l'objet", Toast.LENGTH_SHORT).show();
            }
        });


    }

    //region [Override des methodes de l'activité pour le mapview]
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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
