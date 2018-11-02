package ca.obrassard.inquirio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import ca.obrassard.inquirio.DrawerUtils;
import ca.obrassard.inquirio.activities.dialogs.ItemAddedDialog;
import ca.obrassard.inquirio.LoggedUser;
import ca.obrassard.inquirio.R;
import ca.obrassard.inquirio.model.LostItem;
import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddItemActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtSelectedLocation;
    EditText txtTitle;
    EditText txtDescription;
    EditText txtReward;
    Place selectedplace;
    InquirioService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        service = RetrofitUtil.getMock();

        //region [Initialisation des éléments de navigation]
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
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
        //endregion

        //Intialisation des vues personnalisées
        txtSelectedLocation = findViewById(R.id.selectedlocation);
        txtSelectedLocation.setText("");

        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtReward = findViewById(R.id.txtReward);

        Button btn_setLocation = findViewById(R.id.btn_addlocation);
        btn_setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationWithPlacePicker();
            }
        });

        Button btn_send = findViewById(R.id.btnSend);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtTitle.getText().toString().trim().equals("")){
                    Toast.makeText(AddItemActivity.this, "Veuillez fournir le titre de votre item", Toast.LENGTH_SHORT).show();
                }
                else if (txtDescription.getText().toString().trim().equals("")){
                    Toast.makeText(AddItemActivity.this, "Veuillez entrer une description", Toast.LENGTH_SHORT).show();
                }
                else if (txtReward.getText().toString().trim().equals("")){
                    Toast.makeText(AddItemActivity.this, "Veuillez spécifier une récompense (ou 0$)", Toast.LENGTH_SHORT).show();
                }
                else if (selectedplace == null){
                    Toast.makeText(AddItemActivity.this, "Veuillez séléctionner l'emplacement approximatif de la perte", Toast.LENGTH_SHORT).show();
                } else {
                    final LostItem item = new LostItem();

                    item.description = txtDescription.getText().toString();
                    item.itemHasBeenFound = false;
                    item.ownerId = LoggedUser.data.userID;
                    item.title = txtTitle.getText().toString();
                    item.reward = Double.parseDouble(txtReward.getText().toString());
                    item.latitude = selectedplace.getLatLng().latitude;
                    item.longitude = selectedplace.getLatLng().longitude;
                    item.locationName = selectedplace.getName().toString();

                    service.addNewItem(item).enqueue(new Callback<Long>() {
                        @Override
                        public void onResponse(Call<Long> call, Response<Long> response) {
                            Long itemId = response.body();
                            if (itemId == null){
                                Toast.makeText(AddItemActivity.this, "Une erreur est survenue, veuillez réésayer", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            ItemAddedDialog dialog = new ItemAddedDialog();
                            Bundle args = new Bundle();
                            args.putString("itemplace",item.locationName);
                            args.putLong("itemid",itemId);
                            dialog.setArguments(args);
                            dialog.show(getFragmentManager(),"dialog");
                        }

                        @Override
                        public void onFailure(Call<Long> call, Throwable t) {
                            Toast.makeText(AddItemActivity.this, "Une erreur est survenue, veuillez réésayer", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //region [Méthodes de PlaceAPI]
    public void getLocationWithPlacePicker(){
        int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e("MAP", "GooglePlayServicesRepairableException : "+ e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("MAP", "GooglePlayServicesNotAvailableException : "+ e.getMessage());
        }
    }

    //Recuperation de l'emplacement séléctionné dans le placepicker
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                selectedplace = PlacePicker.getPlace(this,data);
                txtSelectedLocation.setText(selectedplace.getName());
            }
        }
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
