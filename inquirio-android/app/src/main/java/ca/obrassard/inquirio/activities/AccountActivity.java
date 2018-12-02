package ca.obrassard.inquirio.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import ca.obrassard.inquirio.DrawerUtils;
import ca.obrassard.inquirio.LoggedUser;
import ca.obrassard.inquirio.R;
import ca.obrassard.inquirio.errorHandling.ErrorUtils;
import ca.obrassard.inquirio.model.User;
import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.LogoutResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    InquirioService service = RetrofitUtil.get();
    TextView txtName;
    TextView txtEmail;
    RatingBar ratingBar;
    TextView nbItemTFound;

    ProgressDialog progressDialog ;

    private void beginLoading() {
        //Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        if (progressDialog == null)
        progressDialog = ProgressDialog.show(AccountActivity.this,
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

        //region [Initialisation des éléments de navigation]
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //endregion

        txtEmail = findViewById(R.id.email);
        txtName = findViewById(R.id.name);
        ratingBar = findViewById(R.id.user_rating);
        nbItemTFound = findViewById(R.id.nbidtem);

        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        beginLoading();
        service.getUserDetail(LoggedUser.data.userID).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                endLoading();
                if (!response.isSuccessful()) {
                    ErrorUtils.showExceptionError(AccountActivity.this, response.errorBody());
                    return;
                }
                User u = response.body();
                txtEmail.setText(u.Email);
                txtName.setText(u.Name);
                ratingBar.setRating((float)u.Rating);
                nbItemTFound.setText(getString(R.string.nbitems, String.valueOf(u.ItemsFoundCount)));
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                endLoading();
                ErrorUtils.showGenServError(AccountActivity.this);
            }
        });
    }

    private void logout (){
        DrawerUtils.logout(this);
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endLoading();
    }

    //endregion
}
