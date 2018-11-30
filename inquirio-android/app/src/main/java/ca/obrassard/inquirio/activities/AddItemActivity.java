package ca.obrassard.inquirio.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
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
import ca.obrassard.inquirio.errorHandling.ErrorUtils;
import ca.obrassard.inquirio.model.LostItem;
import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.LostItemCreationRequest;
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

    ProgressDialog progressDialog ;

    private void beginLoading() {
        //Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        if (progressDialog == null)
        progressDialog = ProgressDialog.show(AddItemActivity.this,
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

        service = RetrofitUtil.get();

        //region [Initialisation des éléments de navigation]
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
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
                if (selectedplace == null){
                    Snackbar.make(findViewById(android.R.id.content), "Veuillez séléctionner l'emplacement approximatif de la perte", Snackbar.LENGTH_LONG).show();
                } else {
                    final LostItemCreationRequest item = new LostItemCreationRequest();

                    item.description = txtDescription.getText().toString();
                    item.title = txtTitle.getText().toString();
                    try{
                        item.reward = Double.parseDouble(txtReward.getText().toString());
                    } catch (NumberFormatException e){
                        item.reward = 0;
                    }

                    item.latitude = selectedplace.getLatLng().latitude;
                    item.longitude = selectedplace.getLatLng().longitude;
                    item.locationName = selectedplace.getName().toString();

                    beginLoading();
                    service.addNewItem(item, LoggedUser.token).enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            endLoading();
                            if (!response.isSuccessful()) {
                                ErrorUtils.showExceptionError(AddItemActivity.this, response.errorBody());
                                return;
                            }
                            Integer itemId = response.body();
                            ItemAddedDialog dialog = new ItemAddedDialog();
                            Bundle args = new Bundle();
                            args.putString("itemplace",item.locationName);
                            args.putLong("itemid",itemId);
                            dialog.setArguments(args);
                            dialog.show(getFragmentManager(),"dialog");
                            beginLoading();
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            endLoading();
                            ErrorUtils.showGenServError(AddItemActivity.this);
                        }
                    });
                }
            }
        });
    }

    //region [Méthodes de PlaceAPI]
    public void getLocationWithPlacePicker(){
        beginLoading();
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
        endLoading();
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
